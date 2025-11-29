package com.UT3_EJ1;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.hibernate.exception.ConstraintViolationException;

import com.UT3_EJ1.model.*;


/*
 * IMPORTANTE
 * 
 * Recomiendo leer al menos el archivo (informacion-modelado)
 * Ahi explico las decisiones tomadas, y su explicación 
 * TODAS LAS VALIDACIONES REALIZADAS EN LOS SET
 * 
 * IMPORTANTE*/

/**
 * Clase principal de la aplicación para probar la persistencia y la lógica de negocio.
 * <p>
 * Realiza una serie de operaciones CRUD y llamadas a procedimientos almacenados para verificar
 * el correcto funcionamiento del modelo de datos y las restricciones.
 * </p>
 *
 * @author Álvaro Naranjo
 */
public class App {

    private static final String PERSISTENCE_UNIT_NAME = "shop-ec";
    private static EntityManagerFactory emf;

    public static void main(String[] args) {
        // Inicialización segura del EntityManagerFactory
        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Exception e) {
            System.err.println(" Error FATAL: No se pudo conectar a la base de datos.");
            e.printStackTrace();
            return;
        }

        EntityManager em = emf.createEntityManager();
        System.out.println(" Conexión establecida con la base de datos");

        try {
            // Datos de prueba configurables
            String nifCliente = "0002001024";
            String nombreCliente = "Juan Pérez";
            String emailCliente = "juanb@test.com";
            String direccionFiscal = "Calle Mayor 1";
            String telefono = "603123453";

            // --- EJECUCIÓN DE PRUEBAS ---

            // 1. Crear o actualizar datos maestros (Maneja duplicados)
            crearDatosIniciales(em, nifCliente, nombreCliente, emailCliente, direccionFiscal, telefono);

            // 2. Realizar una compra
            Long idCompra = realizarCompra(em, nifCliente);

            if (idCompra != null) {
              
                // 4. Baja "especial" usando Procedure (si existe en BD)
                deleteArticuloProcedure(em, 33L); // Asumiendo ID 33 existe

                // 5. Consultar estado antes de borrar cliente
                imprimirEstadoCompra(em, idCompra);

                // 6. Eliminar cliente (Dispara Trigger de BD)
                eliminarCliente(em, nifCliente);

                // 7. Verificar que la compra se anonimizó
                verificarAnonimizacion(em, idCompra);
            }

        } catch (Exception e) {
            System.err.println(" Error inesperado en el flujo principal: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em.isOpen()) em.close();
            if (emf.isOpen()) emf.close();
            System.out.println(" Conexión cerrada.");
        }
    }

    // -------------------------- MÉTODOS DE NEGOCIO --------------------------

    /**
     * Crea los datos iniciales (Cliente, InfoFiscal, Artículos).
     * Usa 'merge' para permitir re-ejecuciones sin error de duplicados.
     * (OBVIAMENTE PARA PRUEBAS - NO EN UN ENTORNO REAL)
     */
    private static void crearDatosIniciales(EntityManager em, String nif, String nombre, String email,
                                            String direccion, String telefono) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            System.out.println("\n--- 1. GESTIONANDO DATOS INICIALES ---");

            // Preparación de objetos (Validaciones de setters saltarían aquí)
            Cliente cliente = new Cliente(nif, nombre, email, LocalDateTime.now());
            InformacionFiscal info = new InformacionFiscal(nif, direccion, telefono);
            cliente.setInformacionFiscal(info);

            Articulo art1 = new Articulo("Teclado Mecánico", "Switch Blue", new BigDecimal("59.99"), 10);
            Articulo art2 = new Articulo("Ratón Gaming", "RGB 16000dpi", new BigDecimal("29.50"), 25);
            Articulo art3 = new Articulo("Monitor 24", "Full HD IPS", new BigDecimal("120.00"), 5);

            // Usamos merge para insertar o actualizar si ya existe
            em.merge(cliente);
            Arrays.asList(art1, art2, art3).forEach(em::merge);

            tx.commit();
            System.out.println(" Datos iniciales procesados correctamente.");

        } catch (IllegalArgumentException iae) {
            if (tx.isActive()) tx.rollback();
            System.err.println(" Error de validación de datos: " + iae.getMessage());
        } catch (PersistenceException pe) {
            if (tx.isActive()) tx.rollback();
            // Detectar violación de PK si ocurriera (aunque merge lo evita)
            if (pe.getCause() instanceof ConstraintViolationException) {
                System.err.println(" Violación de restricción (probablemente clave duplicada).");
            } else {
                throw pe;
            }
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    /**
     * Realiza una compra para el cliente especificado.
     */
    private static Long realizarCompra(EntityManager em, String nifCliente) {
        EntityTransaction tx = em.getTransaction();
        Long compraId = null;
        try {
            tx.begin();
            System.out.println("\n--- 2. REALIZANDO COMPRA ---");

            Cliente cliente = em.find(Cliente.class, nifCliente);
            Articulo art1 = em.find(Articulo.class, 34); 
            Articulo art2 = em.find(Articulo.class, 35);

            if (cliente == null || art1 == null || art2 == null) {
                System.err.println(" No se encontraron los datos necesarios (Cliente o Artículos).");
                if (tx.isActive()) tx.rollback();
                return null;
            }

            // Crear Compra
            Compra compra = new Compra(
                    LocalDateTime.now(),
                    EstadoCompra.PENDIENTE,
                    cliente.getInformacionFiscal().getDireccionFiscal(),
                    BigDecimal.ZERO,
                    cliente);

            // Persistir compra primero para generar ID
            em.persist(compra);
            
            // Crear líneas de compra
            ArticuloCompra linea1 = new ArticuloCompra(art1, compra, art1.getPrecioActual(), 2);
            ArticuloCompra linea2 = new ArticuloCompra(art2, compra, art2.getPrecioActual(), 1);

            compra.addArticuloCompra(linea1);
            compra.addArticuloCompra(linea2);

            // Calcular total con Streams
            BigDecimal total = compra.getArticulosCompra().stream()
                    .map(ac -> ac.getPrecioCompra().multiply(BigDecimal.valueOf(ac.getUnidades())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            compra.setPrecioTotal(total);

            tx.commit();
            compraId = (long) compra.getId(); // Casting seguro
            System.out.println(" Compra realizada con ID: " + compraId + " | Total: " + total + "€");

        } catch (IllegalArgumentException iae) {
            if (tx.isActive()) tx.rollback();
            System.err.println(" Datos inválidos en la compra: " + iae.getMessage());
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println(" Error al procesar la compra: " + e.getMessage());
        }
        return compraId;
    }

 

    /**
     * Llama a un procedimiento almacenado en la BD para eliminar/transformar un artículo.
     */
    private static void deleteArticuloProcedure(EntityManager em, Long idArticulo) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            System.out.println("\n--- 4. BAJA DE ARTÍCULO (PROCEDURE) ---");

            // Llamada nativa al procedimiento
            Query query = em.createNativeQuery("CALL eliminar_articulo(:id)");
            query.setParameter("id", idArticulo.intValue());
            query.executeUpdate();

            System.out.println(" Procedimiento ejecutado para artículo ID: " + idArticulo);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println(" Error al ejecutar procedure: " + e.getMessage());
        }
    }

    /**
     * Elimina un cliente. Esto debería disparar el Trigger en la BD.
     */
    private static void eliminarCliente(EntityManager em, String nif) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            System.out.println("\n--- 5. ELIMINANDO CLIENTE ---");

            Cliente cliente = em.find(Cliente.class, nif);
            if (cliente != null) {
                em.remove(cliente);
                System.out.println(" Cliente eliminado. Esperando ejecución del Trigger...");
            } else {
                System.out.println(" Cliente no encontrado.");
            }
            tx.commit();
        } catch (PersistenceException pe) {
            if (tx.isActive()) tx.rollback();
            System.err.println(" Error SQL al eliminar cliente: " + pe.getMessage());
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    private static void imprimirEstadoCompra(EntityManager em, Long idCompra) {
        if(idCompra == null) return;
        Compra c = em.find(Compra.class, idCompra.intValue());
        if (c != null) {
            String clienteNombre = (c.getCliente() != null) ? c.getCliente().getNombre() : "NULL";
            System.out.println("   Estado Compra [" + idCompra + "]: " + c.getEstado() +
                    " | Cliente: " + clienteNombre);
        }
    }

    private static void verificarAnonimizacion(EntityManager em, Long idCompra) {
        if(idCompra == null) return;
        System.out.println("\n--- 6. VERIFICANDO RESULTADO DEL TRIGGER ---");

        em.clear(); // Limpiamos caché para leer datos reales de BD

        Compra c = em.find(Compra.class, idCompra.intValue());
        if (c != null) {
            System.out.println("Compra ID: " + c.getId());
            System.out.println("Estado: " + c.getEstado()); // Esperado: ELIMINADO
            System.out.println("Dirección: '" + c.getDireccion() + "'"); // Esperado: ''
            
            if (c.getCliente() == null) {
                System.out.println(" ÉXITO: La compra no tiene cliente asociado (Anonimizada por Trigger).");
            } else {
                System.out.println(" ERROR: La compra aún apunta al cliente: " + c.getCliente().getNifCif());
            }
        }
    }
}