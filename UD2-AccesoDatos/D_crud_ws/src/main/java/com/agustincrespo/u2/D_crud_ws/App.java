package com.agustincrespo.u2.D_crud_ws;

import com.agustincrespo.u2.D_crud_ws.model.Perfil;
import com.agustincrespo.u2.D_crud_ws.model.Usuario;
import com.agustincrespo.u2.D_crud_ws.persistence.UsuariosPersistence;

import java.sql.SQLException;
import java.util.List;

/**
 * Aplicación de demostración para el CRUD de Usuarios y Perfiles.
 */
public class App {

	public static void main(String[] args) {
		System.out.println("Iniciando demostración CRUD de Usuarios...");

		Usuario userConPerfil = null;
		Usuario userSinPerfil = null;

		try {
			System.out.println("\n--- LECTURA INICIAL DE DATOS ---");
			List<Usuario> usuariosActuales = UsuariosPersistence.readAll();
			System.out.println("Usuarios actuales en la BBDD (" + usuariosActuales.size() + "):");
			for (Usuario u : usuariosActuales) {
				System.out.println(u);
			}

			System.out.println("\n--- CREACIÓN DE NUEVOS USUARIOS ---");
			// Usuario 1 (con perfil)
			userConPerfil = new Usuario(0, "nuevo_usuario_1", "conperfil@ejemplo.com", "pass123", null, 1);
			Perfil perfil = new Perfil(0, "Biografía del nuevo usuario", "https://nuevo.com", "Internet");
			userConPerfil.setPerfil(perfil);
			userConPerfil = UsuariosPersistence.create(userConPerfil);
			System.out.println("Usuario Creado (con perfil): " + userConPerfil);

			// Usuario 2 (sin perfil)
			userSinPerfil = new Usuario(0, "nuevo_usuario_2", "sinperfil@ejemplo.com", "pass456", null, 1);
			userSinPerfil = UsuariosPersistence.create(userSinPerfil);
			System.out.println("Usuario Creado (sin perfil): " + userSinPerfil);

			// --- MODIFICACIÓN DE DATOS ---
			System.out.println("\n--- MODIFICACIÓN DE DATOS ---");

			// Modificamos al usuario 2 (sin perfil) para AÑADIRLE uno
			System.out.println("\n-> Modificando 'nuevo_usuario_2' para AÑADIR un perfil...");
			userSinPerfil.setNombre("usuario_2_modificado");
			userSinPerfil.setEstaActivo(0); // Lo desactivamos
			Perfil perfilNuevo = new Perfil(0, "Biografía añadida después", null, "Mundo");
			userSinPerfil.setPerfil(perfilNuevo);
			UsuariosPersistence.update(userSinPerfil);
			Usuario user2Modificado = UsuariosPersistence.readById(userSinPerfil.getId());
			System.out.println("Resultado (Usuario 2): " + user2Modificado);

			// Modificamos al usuario 1 (con perfil) para QUITARLE el perfil
			System.out.println("\n-> Modificando 'nuevo_usuario_1' para QUITAR su perfil...");
			userConPerfil.setPerfil(null);
			UsuariosPersistence.update(userConPerfil);
			Usuario user1Modificado = UsuariosPersistence.readById(userConPerfil.getId());
			System.out.println("Resultado (Usuario 1): " + user1Modificado);
		} catch (SQLException e) {
			System.err.println("ERROR DE BASE DE DATOS: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// Limpiar los datos creados en esta demo
			System.out.println("\n--- BORRADO Y LIMPIEZA ---");
			try {
				if (userConPerfil != null && userConPerfil.getId() > 0) {
					System.out.println("Borrando usuario " + userConPerfil.getId() + "...");
					UsuariosPersistence.delete(userConPerfil);
				}
				if (userSinPerfil != null && userSinPerfil.getId() > 0) {
					System.out.println("Borrando usuario " + userSinPerfil.getId() + "...");
					UsuariosPersistence.delete(userSinPerfil);
				}
				System.out.println("Limpieza completada.");

				System.out.println("\n--- LECTURA FINAL DE DATOS ---");
				List<Usuario> usuariosFinal = UsuariosPersistence.readAll();
				System.out.println("Usuarios restantes en la BBDD (" + usuariosFinal.size() + "):");
				usuariosFinal.forEach(System.out::println);

			} catch (SQLException e) {
				System.err.println("ERROR DURANTE LA LIMPIEZA: " + e.getMessage());
			}
		}
	}
}
