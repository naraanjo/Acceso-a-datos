package com.agustincrespo.u3.hibernate.avanzado.service;

import java.util.List;

import com.agustincrespo.u3.hibernate.avanzado.dao.UsuarioDAO;
import com.agustincrespo.u3.hibernate.avanzado.dto.UsuarioDTO;
import com.agustincrespo.u3.hibernate.avanzado.model.Usuario;

// Tiene siempre al Dao por parametros
// LOGICA DE NEGOCIO Y PASO DE DTO-MODEL <-> MODEL-DTO
public class UsuarioService {

	private final UsuarioDAO usuarioDao;

	// Constructor del service - recibe DAO
	public UsuarioService(UsuarioDAO usuarioDao) {
		this.usuarioDao = usuarioDao;
	}

	// Buscar un usuario por Id
	public UsuarioDTO findById(int id) {

		// Llama al DAO, que devuelve usuarioModel
		Usuario usuarioEncontrado = usuarioDao.findById(id);
		// Paso de entity A DTO
		return UsuarioDTO.fromEntity(usuarioEncontrado);
	}

	// Sacar la lista con todos los usuarios
	public List<UsuarioDTO> findAll() {

		// Llamo al dao para que me devuelve model, todos los usuarios
		List<Usuario> usuariosModelLista = usuarioDao.findAll();
		// Devuelvo la lista pasando de MODEL-DTO
		return UsuarioDTO.fromEntityList(usuariosModelLista);

	}

	// Inserta un nuevo registro en la bd
	public void save(UsuarioDTO usuarioDto) {

		if (usuarioDto != null) {
			// Paso el dto a model
			Usuario usarioModel = usuarioDto.toEntity();

			// Llamo al dao para que inserte el usuario
			usuarioDao.save(usarioModel);
		}
	}

	// Actualiza a un usuario
	public void update(UsuarioDTO usuarioDTO) {

		if (usuarioDTO != null) {
			// Paso el dto a model
			Usuario usuarioModel = usuarioDTO.toEntity();
			// Llamo al dao para que actualice el usuario
			usuarioDao.update(usuarioModel);
		}

	}
	
	// Elimino a un usuario
	public void delete(int id) {
		
		// Obtengo a ese usuario
		Usuario usuario = usuarioDao.findById(id);
		
		// Llamo al dao y elimino
		if(usuario != null) {
			usuarioDao.delete(usuario);
		}
		
	}

}
