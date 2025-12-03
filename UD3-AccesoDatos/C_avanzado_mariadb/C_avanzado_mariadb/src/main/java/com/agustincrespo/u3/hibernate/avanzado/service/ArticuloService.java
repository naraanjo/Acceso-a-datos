package com.agustincrespo.u3.hibernate.avanzado.service;

import java.util.List;

import com.agustincrespo.u3.hibernate.avanzado.dao.ArticuloDAO;
import com.agustincrespo.u3.hibernate.avanzado.dto.ArticuloDTO;
import com.agustincrespo.u3.hibernate.avanzado.model.Articulo;

public class ArticuloService {

	// El servicio recibe un dao
	private final ArticuloDAO articuloDao;

	public ArticuloService(ArticuloDAO articuloDao) {
		this.articuloDao = articuloDao;
	}

	// Metodo pra buscar un articulo por Id
	public ArticuloDTO findById(int id) {

		// Llamo al dao para que lo busque
		Articulo articuloModel = articuloDao.findById(id);
		// Paso de model a dto
		return ArticuloDTO.fromEntity(articuloModel);
	}

	// Metodo para buscar todos los articulos
	public List<ArticuloDTO> findAll() {

		// Llamo al dao para que busque todos los articulos
		List<Articulo> listaArticulosModel = articuloDao.findAll();
		// Paso la lista de articulos model a dto
		return ArticuloDTO.fromEntityList(listaArticulosModel);
	}

	// Metodo para insertar un articulo
	public void save(ArticuloDTO articuloDTO) {

		if (articuloDTO != null) {
			// Paso de dto a model
			Articulo articuloModel = articuloDTO.toEntity();
			// Inserto ese articulo --> llamada al dao
			articuloDao.save(articuloModel);
		}
	}

	// Actualiza un articulo
	public void update(ArticuloDTO articuloDTO) {

		if (articuloDTO != null) {
			// Paso de dto a model
			Articulo articuloModel = articuloDTO.toEntity();
			// Llamo al dao --> Update bd
			articuloDao.update(articuloModel);
		}
	}

	// Delete de un articulo
	public void delete(int id) {

		// Buso el articulo
		Articulo art = articuloDao.findById(id);

		if (art != null) {
			// Llamo al dao --> delete bd
			articuloDao.delete(art);

		}

	}
}
