package com.agustincrespo.u3.hibernate.intermedio;

import java.util.Set;

import com.agustincrespo.u3.hibernate.intermedio.model.Articulo;
import com.agustincrespo.u3.hibernate.intermedio.model.Categoria;
import com.agustincrespo.u3.hibernate.intermedio.model.Perfil;
import com.agustincrespo.u3.hibernate.intermedio.model.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

public class App {

	public static void main(String[] args) {
		// Configurar el EntityManagerFactory
		// Carga la configuración del persistence.xml
		EntityManagerFactory emf = null;
		EntityManager em = null;

		try {
			emf = Persistence.createEntityManagerFactory("usuarios-jpa-pu");
			em = emf.createEntityManager();

			// ----------------------------------------------------
			// LEER DATOS EXISTENTES (Usando LAZY loading)
			// ----------------------------------------------------
			System.out.println("--- LEYENDO DATOS EXISTENTES ---");

			// A) Consultar Usuario y su Perfil (1:1)
			// El perfil es LAZY. No se carga hasta que no hacemos .getPerfil()
			System.out.println("\n[CONSULTA] Buscando a 'ana_dev' (ID 1)...");
			Usuario ana = em.find(Usuario.class, 1);
			System.out.println("-> Usuario encontrado: " + ana.getNombre());
			System.out.println("-> Biografía (LAZY 1:1): " + ana.getPerfil().getBiografia());

			// B) Consultar Usuario y sus Artículos (1:N)
			// Los artículos son LAZY. No se cargan hasta que no hacemos .getArticulos()
			System.out.println("\n[CONSULTA] Buscando artículos de 'ana_dev'...");
			Set<Articulo> articulosDeAna = ana.getArticulos();
			System.out.println("-> 'ana_dev' ha escrito " + articulosDeAna.size() + " artículos.");
			for (Articulo art : articulosDeAna) {
				System.out.println("   - " + art.getTitulo());
			}

			// C) Consultar Artículo, su Autor (N:1) y Categorías (N:M)
			// El autor Y las categorías con LAZY loading.
			System.out.println("\n[CONSULTA] Buscando Artículo ID 3 ('Guía de Docker')...");
			Articulo dockerArt = em.find(Articulo.class, 3);
			System.out.println("-> Artículo encontrado: " + dockerArt.getTitulo());
			System.out.println("-> Autor (LAZY N:1): " + dockerArt.getAutor().getNombre());
			System.out.println("-> Categorías (LAZY N:M):");
			for (Categoria cat : dockerArt.getCategorias()) {
				System.out.println("   - " + cat.getNombre());
			}

			// ----------------------------------------------------
			// CREAR DATOS NUEVOS
			// ----------------------------------------------------
			System.out.println("\n--- CREANDO DATOS NUEVOS (usando una transacción) ---");
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			try {
				// Creo un usuario SIN perfil y SIN artículos
				Usuario userSimple = new Usuario();
				userSimple.setNombre("usuario_temporal" + System.currentTimeMillis());
				userSimple.setEmail(System.currentTimeMillis() + "@agustincrespo.com");
				userSimple.setPasswordHash("hash_temporal");

				// Al hacer persist, se genera el INSERT en la BBDD
				// Aquí no hay CascadeType, así que solo se guarda el Usuario
				em.persist(userSimple);
				System.out.println("\n[CREANDO] " + userSimple.getNombre());

				// Creo un usuario CON perfil, artículos y categorías (nuevas y existentes)
				Usuario userPro = new Usuario();
				userPro.setNombre("usuario_pro_temp");
				userPro.setEmail("pro@ejemplo.com");
				userPro.setPasswordHash("hash_pro");

				Perfil perfilPro = new Perfil();
				perfilPro.setBiografia("Biografía del usuario pro");
				perfilPro.setSitioWeb("https://pro.temp");

				// Usamos el helper para sincronizar la 1:1
				userPro.setPerfil(perfilPro);

				System.out.println("[CREANDO] " + userPro.getNombre() + " con su perfil.");

				// Se crea una nueva categoría
				Categoria catNueva = new Categoria();
				catNueva.setNombre("Nueva Categoría Temporal " + System.currentTimeMillis());

				// Se carga una categoría existente
				Categoria catExistente = em.find(Categoria.class, 1); // Tecnología
				catExistente.setNombre("Tecnología y Ciencia"); // Modifico su nombre para ver el UPDATE

				// Se crea un artículo y lo asigno al 'userPro'
				Articulo artNuevo = new Articulo();
				artNuevo.setTitulo("Mi Artículo Temporal sobre Hibernate " + System.currentTimeMillis());
				artNuevo.setContenido("Contenido del artículo ...");

				userPro.addArticulo(artNuevo);

				// Asigno categorías (N:M) al nuevo artículo
				artNuevo.addCategoria(catNueva); // Asigna la categoría nueva
				artNuevo.addCategoria(catExistente); // Asigna la categoría existente

				System.out.println("[CREANDO] Artículo '" + artNuevo.getTitulo() + "'");
				System.out.println("[ASIGNANDO] Artículo a " + catNueva.getNombre());
				System.out.println("[ASIGNANDO] Artículo a " + catExistente.getNombre());

				// Persistir los objetos nuevos
				// Gracias a CascadeType.ALL y CascadeType.PERSIST, al guardar 'userPro', se
				// guardarán 'perfilPro' y 'artNuevo'.
				// Y al guardarse 'artNuevo', se guardará 'catNueva' y se actualizará
				// 'catExistente'.
				em.persist(userPro);

				System.out.println("\n[FLUSH] Forzando SQL INSERTs (pero sin commit)...");
				// Forzamos que Hibernate envíe el SQL a la BBDD
				// Verás los INSERTs en la consola (por 'show_sql=true')
				em.flush();

				System.out.println("\n[ROLLBACK] Deshaciendo todos los cambios...");
				tx.rollback();
				System.out.println("¡Rollback completado! La base de datos está intacta.");

			} catch (PersistenceException e) {
				System.err.println("!!! ERROR EN LA TRANSACCIÓN !!!");
				e.printStackTrace();
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
			}

		} catch (Exception e) {
			System.err.println("!!! ERROR AL INICIAR JPA !!!");
			e.printStackTrace();
		} finally {
			// RECUERDA que hay que cerrar los recursos
			if (em != null) {
				em.close();
			}
			if (emf != null) {
				emf.close();
			}
			System.out.println("\n--- APLICACIÓN FINALIZADA ---");
		}
	}
}