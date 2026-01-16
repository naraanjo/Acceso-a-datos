package com.agustincrespo.u3.hibernate.avanzado.controller;

import java.util.List;

import com.agustincrespo.u3.hibernate.avanzado.dto.UsuarioDTO;
import com.agustincrespo.u3.hibernate.avanzado.dto.UsuarioRegistroDTO;
import com.agustincrespo.u3.hibernate.avanzado.service.UsuarioService;

public class UsuarioController {

	private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
    public void create(UsuarioRegistroDTO entradaDto) {
        usuarioService.create(entradaDto); 
    }

    public UsuarioDTO findById(int id) {
        // El servicio ya devuelve un DTO, el controlador solo lo pasa
        return usuarioService.findById(id);
    }

    public List<UsuarioDTO> findAll() {
        // Delegamos directamente, el servicio ya hizo la conversión
        return usuarioService.findAll();
    }

    public UsuarioDTO update(UsuarioDTO dto) {
        return usuarioService.update(dto);
    }

    public void delete(int id) {
        usuarioService.delete(id);
    }
}
