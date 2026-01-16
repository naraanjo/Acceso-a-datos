package com.agustincrespo.u3.hibernate.avanzado.service;

import java.util.List;

import com.agustincrespo.u3.hibernate.avanzado.dto.ArticuloDTO;

// Para no explicar otra vez todo, esta interfaz sigue la misma estructura que UsuarioService.

public interface ArticuloService {

	ArticuloDTO findById(int id);

	List<ArticuloDTO> findAll();

	void create(ArticuloDTO dto);

	ArticuloDTO update(ArticuloDTO dto);

	void delete(int id);

	List<ArticuloDTO> findByAutor(int autorId);
}