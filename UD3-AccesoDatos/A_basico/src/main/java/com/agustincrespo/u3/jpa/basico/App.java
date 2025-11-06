package com.agustincrespo.u3.jpa.basico;
import com.agustincrespo.u3.jpa.basico.model.Perfil;
import com.agustincrespo.u3.jpa.basico.model.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
public class App {

    public static void main(String[] args) {
        // Crear el "Factory". Esto lee el persistence.xml y prepara todo.
        // Se crea UNA SOLA VEZ para toda la aplicación.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("usuarios-jpa-pu");

        // Obtenemos el "EntityManager".
        // Es el objeto que gestiona todo.
        // Sería el equivalente a 'Connection' que hemos usado antes.
        EntityManager em = emf.createEntityManager();

        System.out.println("Iniciando demostración JPA...");

        try {
            // --- LECTURA (Find) ---
            System.out.println("--- Buscando usuario con ID 1 ---");
            
            Usuario ana = em.find(Usuario.class, 1);
            if (ana != null) {
                System.out.println("Encontrado: " + ana);
                // Gracias al FetchType.LAZY, la siguiente línea lanzará
                // un 'SELECT' para buscar el perfil, SOLO si es necesario.
                System.out.println("Su perfil: " + ana.getPerfil());
            }

            // --- CREACIÓN (Persist) ---
            System.out.println("\n--- Creando un nuevo usuario con perfil ---");
            
            // Todas las operaciones de escritura DEBEN estar en una transacción.
            em.getTransaction().begin();

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre("jp_user");
            nuevoUsuario.setEmail("jp@agustincrespo.com");
            nuevoUsuario.setPasswordHash("hashJPA");
            
            Perfil nuevoPerfil = new Perfil();
            nuevoPerfil.setBiografia("Probando JPA 1-1");
            nuevoPerfil.setSitioWeb("jpa.com");

            // ¡IMPORTANTE! Sincronizamos la relación en AMBOS lados
            nuevoUsuario.setPerfil(nuevoPerfil);
            
            // ¡Así se guarda! No más INSERT.
            // Hibernate guardará al Usuario Y al Perfil gracias al CascadeType.ALL.
            em.persist(nuevoUsuario);
            
            em.getTransaction().commit();
            System.out.println("Usuario guardado: " + nuevoUsuario);

            // --- BORRADO (Remove) ---
            System.out.println("\n--- Borrando usuario " + nuevoUsuario.getId() + " ---");
            em.getTransaction().begin();
            
            // Antes de borrar, el objeto debe estar "gestionado" por el EntityManager
            Usuario usuarioABorrar = em.find(Usuario.class, nuevoUsuario.getId());
            if (usuarioABorrar != null) {
                // ¡Así se borra! Hibernate borrará el Usuario Y el Perfil.
                em.remove(usuarioABorrar);
            }
            
            em.getTransaction().commit();
            System.out.println("Usuario borrado.");

        } catch (Exception e) {
            // si algo falla, revertimos la transacción
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            // cerramos todo al final.
            if (em.isOpen()) {
                em.close();
            }
            if (emf.isOpen()) {
                emf.close();
            }
        }
    }
}
