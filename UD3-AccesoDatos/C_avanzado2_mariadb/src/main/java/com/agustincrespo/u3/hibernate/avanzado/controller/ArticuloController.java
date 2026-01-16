package com.agustincrespo.u3.hibernate.avanzado.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.agustincrespo.u3.hibernate.avanzado.dao.ArticuloDAO;
import com.agustincrespo.u3.hibernate.avanzado.dto.ArticuloDTO;
import com.agustincrespo.u3.hibernate.avanzado.model.Articulo;

public class ArticuloController {

    private final ArticuloDAO articuloDao;

    public ArticuloController(ArticuloDAO articuloDao) {
        this.articuloDao = articuloDao;
    }

    public ArticuloDTO findById(int id) {
        Articulo a = articuloDao.findById(id);
        return ArticuloDTO.fromEntity(a);
    }

    public List<ArticuloDTO> findAll() {
        return articuloDao.findAll().stream().map(ArticuloDTO::fromEntity).collect(Collectors.toList());
    }

    public void create(ArticuloDTO dto) {
        Articulo a = dto.toEntity();
        articuloDao.save(a);
    }

    public ArticuloDTO update(ArticuloDTO dto) {
        Articulo a = dto.toEntity();
        Articulo updated = articuloDao.update(a);
        return ArticuloDTO.fromEntity(updated);
    }

    public void delete(int id) {
        Articulo a = articuloDao.findById(id);
        if (a != null) {
            articuloDao.delete(a);
        }
    }
}
