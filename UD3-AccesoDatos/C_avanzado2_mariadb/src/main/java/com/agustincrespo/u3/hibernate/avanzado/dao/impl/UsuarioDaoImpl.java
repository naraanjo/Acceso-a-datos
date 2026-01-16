package com.agustincrespo.u3.hibernate.avanzado.dao.impl;

import java.util.List;

import com.agustincrespo.u3.hibernate.avanzado.dao.UsuarioDAO;
import com.agustincrespo.u3.hibernate.avanzado.model.Usuario;

import jakarta.persistence.EntityManager;

public class UsuarioDaoImpl implements UsuarioDAO {

	private final EntityManager em;

	public UsuarioDaoImpl(EntityManager em) {
		this.em = em;
	}

	@Override
	public Usuario findById(int id) {
		return em.find(Usuario.class, id);
	}

	@Override
	public List<Usuario> findAll() {
		return em.createQuery("FROM Usuario", Usuario.class).getResultList();
	}

	@Override
	public void save(Usuario usuario) {
		em.persist(usuario);
	}

	@Override
	public Usuario update(Usuario usuario) {
		return em.merge(usuario);
	}

	@Override
	public void delete(Usuario usuario) {
		Usuario managed = usuario;
		if (!em.contains(usuario)) {
			managed = em.merge(usuario);
		}
		em.remove(managed);
	}
	
	@Override
	public int countArticulos(Usuario usuario) {
		
		if(usuario == null) {
			throw new IllegalArgumentException("El usuario no existe");
		}
		
		// Consulta para sacar los articulos de ese usuario
		 String consultaCount = "SELECT COUNT(a) FROM Articulo a WHERE a.autor.id = :id";
		    
		 return em.createQuery(consultaCount, Integer.class) // Tipo long -> resultado
		                        .setParameter("id", usuario.getId()) // Parametro
		                        .getSingleResult() // Devuelve un unico resultado
		                        .intValue(); // En int
	}
}
