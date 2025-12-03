package com.agustincrespo.u3.hibernate.avanzado;

import java.util.List;

import com.agustincrespo.u3.hibernate.avanzado.controller.ArticuloController;
import com.agustincrespo.u3.hibernate.avanzado.controller.UsuarioController;
import com.agustincrespo.u3.hibernate.avanzado.dao.impl.ArticuloDaoImpl;
import com.agustincrespo.u3.hibernate.avanzado.dao.impl.UsuarioDaoImpl;
import com.agustincrespo.u3.hibernate.avanzado.dto.ArticuloDTO;
import com.agustincrespo.u3.hibernate.avanzado.dto.UsuarioDTO;
import com.agustincrespo.u3.hibernate.avanzado.service.ArticuloService;
import com.agustincrespo.u3.hibernate.avanzado.service.UsuarioService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class App {

    public static void main(String[] args) {

        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            // Crear EntityManager
            emf = Persistence.createEntityManagerFactory("usuarios_db2");
            em = emf.createEntityManager();

            // Crear DAOs
            UsuarioDaoImpl usuarioDao = new UsuarioDaoImpl(em);
            ArticuloDaoImpl articuloDao = new ArticuloDaoImpl(em);

            // Crear Services
            UsuarioService usuarioService = new UsuarioService(usuarioDao);
            ArticuloService articuloService = new ArticuloService(articuloDao);

            // Crear Controllers
            UsuarioController usuarioController = new UsuarioController(usuarioService);
            ArticuloController articuloController = new ArticuloController(articuloService);

            // Listar todos los usuarios
            System.out.println("--- USUARIOS ---");
            List<UsuarioDTO> usuarios = usuarioController.findAll();
            for (UsuarioDTO u : usuarios) {
                System.out.println("Usuario DTO: id=" + u.id + " nombre=" + u.nombre + " email=" + u.email);
            }

            // Listar todos los artículos
            System.out.println("--- ARTICULOS ---");
            List<ArticuloDTO> articulos = articuloController.findAll();
            for (ArticuloDTO a : articulos) {
                System.out.println("Articulo DTO: id=" + a.id + " titulo=" + a.titulo + " autorId=" + a.autorId);
            }

            // Crear un nuevo usuario usando Controller
            UsuarioDTO nuevoUsuario = new UsuarioDTO();
            nuevoUsuario.nombre = "nuevo_usuario_demo_" + System.currentTimeMillis();
            nuevoUsuario.email = "demo+" + System.currentTimeMillis() + "@example.com";

            try {
                usuarioController.save(nuevoUsuario);
                System.out.println("Usuario creado: " + nuevoUsuario.nombre);
            } catch (Exception e) {
                System.err.println("Error al crear usuario: " + e.getMessage());
            }

            // Buscar un usuario por id
            try {
                UsuarioDTO buscado = usuarioController.findById(1); // ejemplo id 1
                if (buscado != null) {
                    System.out.println("Usuario encontrado: " + buscado.nombre);
                } else {
                    System.out.println("Usuario con id=1 no encontrado");
                }
            } catch (Exception e) {
                System.err.println("Error al buscar usuario: " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
        }
    }
}
