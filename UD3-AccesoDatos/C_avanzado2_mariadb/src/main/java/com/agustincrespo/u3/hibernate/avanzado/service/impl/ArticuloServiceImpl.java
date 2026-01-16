package com.agustincrespo.u3.hibernate.avanzado.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.agustincrespo.u3.hibernate.avanzado.dao.ArticuloDAO;
import com.agustincrespo.u3.hibernate.avanzado.dao.UsuarioDAO;
import com.agustincrespo.u3.hibernate.avanzado.dto.ArticuloDTO;
import com.agustincrespo.u3.hibernate.avanzado.model.Articulo;
import com.agustincrespo.u3.hibernate.avanzado.model.Usuario;
import com.agustincrespo.u3.hibernate.avanzado.service.ArticuloService;

public class ArticuloServiceImpl implements ArticuloService {

	private final ArticuloDAO articuloDao;
	private final UsuarioDAO usuarioDao;

	public ArticuloServiceImpl(ArticuloDAO articuloDao, UsuarioDAO usuarioDao) {
		this.articuloDao = articuloDao;
		this.usuarioDao = usuarioDao;
	}

	@Override
	public ArticuloDTO findById(int id) {
		Articulo a = articuloDao.findById(id);
		return ArticuloDTO.fromEntity(a);
	}

	@Override
	public List<ArticuloDTO> findAll() {
		return articuloDao.findAll().stream().map(ArticuloDTO::fromEntity).collect(Collectors.toList());
	}

	@Override
	public void create(ArticuloDTO dto) {
		if (dto == null)
			throw new IllegalArgumentException("El DTO no puede ser null");

		Usuario autor = usuarioDao.findById(dto.autorId);
		if (autor == null)
			throw new IllegalArgumentException("No existe un usuario con ID: " + dto.autorId);

		Articulo articulo = dto.toEntity();

		autor.addArticulo(articulo);

		articuloDao.save(articulo);
	}

	@Override
	public ArticuloDTO update(ArticuloDTO dto) {
		if (dto == null)
			throw new IllegalArgumentException("DTO null");

		Articulo existing = articuloDao.findById(dto.id);
		if (existing == null) {
			throw new IllegalArgumentException("Artículo no encontrado");
		}

		existing.setTitulo(dto.titulo);
		existing.setContenido(dto.contenido);
		// La fecha de publicación no se suele cambiar en un update normal, 
		// Depende del cómo se configure y defina la capa de negocio.

		// cambio en el autor, por caché de memoria de Hibernate
		if (dto.autorId != 0 && dto.autorId != existing.getAutor().getId()) {
			Usuario nuevoAutor = usuarioDao.findById(dto.autorId);
			if (nuevoAutor == null) {
				throw new IllegalArgumentException("Nuevo autor no encontrado");
			}

			existing.getAutor().removeArticulo(existing); // Desvincular del viejo
			nuevoAutor.addArticulo(existing); // Vincular al nuevo
		}

		Articulo updated = articuloDao.update(existing);
		return ArticuloDTO.fromEntity(updated);
	}

	@Override
	public void delete(int id) {
		
		// Puede dar error por FK
		Articulo a = articuloDao.findById(id);
		if (a == null) {
			 throw new IllegalArgumentException("El articulo no puede ser null");
		}
		
		// Desvincular la relacion existente
		// Al autor le quito el articulo
		if(a.getAutor() != null) {
			a.getAutor().removeArticulo(a);
			
			// Hago la eliminacion |-> Una vez he desvinculado todas sus relaciones
			articuloDao.delete(a);
		}
		
		
	}

	// Se debe agregar por arquitectura, para exponer el DAO hacia la capa superior
	@Override
	public List<ArticuloDTO> findByAutor(int autorId) {
		if (usuarioDao.findById(autorId) == null)
			return new ArrayList<ArticuloDTO>();

		List<Articulo> articulos = articuloDao.findByAutorId(autorId);

		return articulos.stream().map(ArticuloDTO::fromEntity).collect(Collectors.toList());
	}
}