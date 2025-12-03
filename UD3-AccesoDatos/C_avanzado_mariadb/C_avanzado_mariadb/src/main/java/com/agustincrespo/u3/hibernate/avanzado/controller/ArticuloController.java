package com.agustincrespo.u3.hibernate.avanzado.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.agustincrespo.u3.hibernate.avanzado.dao.ArticuloDAO;
import com.agustincrespo.u3.hibernate.avanzado.dto.ArticuloDTO;
import com.agustincrespo.u3.hibernate.avanzado.model.Articulo;
import com.agustincrespo.u3.hibernate.avanzado.service.ArticuloService;

public class ArticuloController {

	// El controller, se relacion con el service
	private final ArticuloService articuloService;

	public ArticuloController(ArticuloService articuloService) {
		this.articuloService = articuloService;
	}

	public ArticuloDTO findById(int id) {

		// Llamo al service
		return articuloService.findById(id);
	}

	public List<ArticuloDTO> findAll() {
		// Llamada al service
		return articuloService.findAll();
	}

	// Insert
	public void save(ArticuloDTO dto) {

		// Llamada al service
		articuloService.save(dto);
	}

	public void update(ArticuloDTO dto) {
		// Llamada al service --> update
		articuloService.update(dto);
	}

	public void delete(int id) {
		articuloService.delete(id);
	}
}
