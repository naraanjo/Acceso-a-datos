package com.agustincrespo.u3.hibernate.avanzado.dao.impl;

import java.util.List;

import com.agustincrespo.u3.hibernate.avanzado.dao.ArticuloDAO;
import com.agustincrespo.u3.hibernate.avanzado.model.Articulo;

import jakarta.persistence.EntityManager;

public class ArticuloDaoImpl implements ArticuloDAO {

	private final EntityManager em;

	public ArticuloDaoImpl(EntityManager em) {
		this.em = em;
	}

	@Override
	public Articulo findById(int id) {
		return em.find(Articulo.class, id);
	}

	@Override
	public List<Articulo> findAll() {
		return em.createQuery("FROM Articulo", Articulo.class).getResultList();
	}

	@Override
	public void save(Articulo articulo) {
		em.persist(articulo);
	}

	@Override
	public Articulo update(Articulo articulo) {
		return em.merge(articulo);
	}

	@Override
	public void delete(Articulo articulo) {
		Articulo managed = articulo;
		if (!em.contains(articulo)) {
			managed = em.merge(articulo);
		}
		em.remove(managed);
	}
}
