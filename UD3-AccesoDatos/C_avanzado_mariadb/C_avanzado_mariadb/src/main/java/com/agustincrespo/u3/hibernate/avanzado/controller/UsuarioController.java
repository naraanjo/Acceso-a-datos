package com.agustincrespo.u3.hibernate.avanzado.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.persister.collection.mutation.UpdateRowsCoordinatorTablePerSubclass;

import com.agustincrespo.u3.hibernate.avanzado.dao.UsuarioDAO;
import com.agustincrespo.u3.hibernate.avanzado.dto.UsuarioDTO;
import com.agustincrespo.u3.hibernate.avanzado.model.Usuario;
import com.agustincrespo.u3.hibernate.avanzado.service.UsuarioService;

// La clase controller SIEMPRE, debe recibir un UsuarioService

public class UsuarioController {

    private final UsuarioService usuarioService;

    // Construcor de controller, recibe un service
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public UsuarioDTO findById(int id) {
        return usuarioService.findById(id); // Pasar a la clase service
    }

    public List<UsuarioDTO> findAll() {
        return usuarioService.findAll(); // Pasar a la clase service
    }

    public void save(UsuarioDTO dto) {
        usuarioService.save(dto); // Llama al service 
    }

    public void update(UsuarioDTO dto) {
         usuarioService.update(dto); // Llama al service
    }

    public void delete(int id) {
    
    		usuarioService.delete(id);
    	
    }
}
