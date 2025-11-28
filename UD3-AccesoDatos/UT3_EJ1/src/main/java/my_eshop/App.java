package my_eshop;

import jakarta.persistence.*;
import model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;


/*
 * IMPORTANTE
 * 
 * Recomiendo leer al menos el archivo (informacion-modelado)
 * Ahi explico las decisiones tomadas, y su explicación 
 * 
 * IMPORTANTE*/
public class App {

	
    private static final String PERSISTENCE_UNIT_NAME = "shop-ec";
    private static EntityManagerFactory emf;

    public static void main(String[] args) {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        System.out.println("Conexión establecida con la base de datos");

        try {
            // Puedes cambiar estos valores antes de ejecutar varias veces
            String nifCliente = "0000001";
            String nombreCliente = "Juan Pérez Modificado";
            String emailCliente = "juan_mod@test.com";
            String direccionFiscal = "Calle Mayor 99";
            String telefono = "600999888";

            crearDatosIniciales(em, nifCliente, nombreCliente, emailCliente, direccionFiscal, telefono);

            Long idCompra = realizarCompra(em, nifCliente);

            bajaLogicaArticulo(em, 1L);

            imprimirEstadoCompra(em, idCompra);

            eliminarCliente(em, nifCliente);

            verificarAnonimizacion(em, idCompra);

        } catch (Exception e) {
            System.err.println(" Error inesperado en la aplicación: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em.isOpen()) em.close();
            if (emf.isOpen()) emf.close();
            System.out.println(" Conexión cerrada.");
        }
    }

    // -------------------------- MÉTODOS --------------------------

    private static void crearDatosIniciales(EntityManager em, String nif, String nombre, String email,
                                            String direccion, String telefono) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            System.out.println("\n--- 1. CREANDO O ACTUALIZANDO DATOS INICIALES ---");

            // Cliente
            Cliente cliente = new Cliente(nif,nombre,email,LocalDate.now());
          

            // Info fiscal
            InformacionFiscal info = new InformacionFiscal(nif,direccion,telefono);
         
            cliente.setInformacionFiscal(info);

            // Artículos
            Articulo art1 = new Articulo("Teclado Mecánico", "Switch Blue", new BigDecimal("59.99"), 10);
            Articulo art2 = new Articulo("Ratón Gaming", "RGB 16000dpi", new BigDecimal("29.50"), 25);
            Articulo art3 = new Articulo("Monitor 24", "Full HD IPS", new BigDecimal("120.00"), 5);

            try {
                // Merge permite insertar o actualizar según exista la PK
                em.merge(cliente);
                Arrays.asList(art1, art2, art3).forEach(em::merge);
            } catch (PersistenceException pe) {
                if (pe.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                    System.err.println("⚠ Ya existen registros con esa PK. Se actualizan los datos.");
                } else {
                    throw pe;
                }
            }

            tx.commit();
            System.out.println(" Datos iniciales procesados correctamente.");
        } catch (IllegalArgumentException iae) {
            if (tx.isActive()) tx.rollback();
            System.err.println(" Valor inválido al crear datos iniciales: " + iae.getMessage());
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    private static Long realizarCompra(EntityManager em, String nifCliente) {
        EntityTransaction tx = em.getTransaction();
        Long compraId = null;
        try {
            tx.begin();
            System.out.println("\n--- 2. REALIZANDO COMPRA ---");

            Cliente cliente = em.find(Cliente.class, nifCliente);
            Articulo art1 = em.find(Articulo.class, 1L);
            Articulo art2 = em.find(Articulo.class, 2L);

            if (cliente == null || art1 == null || art2 == null) {
                System.err.println(" No se encontraron los datos para la compra.");
                return null;
            }

            Compra compra = new Compra(LocalDateTime.now(),
                    EstadoCompra.PENDIENTE,
                    cliente.getInformacionFiscal().getDireccionFiscal(),
                    BigDecimal.ZERO,
                    cliente);

            em.persist(compra);
            em.flush();

            ArticuloCompra linea1 = new ArticuloCompra(art1, compra, art1.getPrecioActual(), 2);
            ArticuloCompra linea2 = new ArticuloCompra(art2, compra, art2.getPrecioActual(), 1);

            compra.addArticuloCompra(linea1);
            compra.addArticuloCompra(linea2);

            BigDecimal total = compra.getArticulosCompra().stream()
                    .map(ac -> ac.getPrecioCompra().multiply(BigDecimal.valueOf(ac.getUnidades())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            compra.setPrecioTotal(total);

            tx.commit();
            compraId = (long) compra.getId();

            System.out.println(" Compra realizada con ID: " + compraId + " | Total: " + total + "€");
        } catch (IllegalArgumentException iae) {
            if (tx.isActive()) tx.rollback();
            System.err.println(" Error al crear la compra: " + iae.getMessage());
        } catch (PersistenceException pe) {
            if (tx.isActive()) tx.rollback();
            if (pe.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                System.err.println(" Violación de restricción de clave foránea o duplicada al realizar la compra.");
            } else {
                throw pe;
            }
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
        return compraId;
    }

    private static void bajaLogicaArticulo(EntityManager em, Long idArticulo) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            System.out.println("\n--- 3. BAJA LÓGICA DE ARTÍCULO ---");

            Articulo art = em.find(Articulo.class, idArticulo);
            if (art != null) {
                art.setActivo(false);
                System.out.println(" Artículo '" + art.getNombre() + "' marcado como NO ACTIVO.");
            }

            tx.commit();
        } catch (IllegalArgumentException iae) {
            if (tx.isActive()) tx.rollback();
            System.err.println(" Error en baja lógica: " + iae.getMessage());
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    private static void eliminarCliente(EntityManager em, String nif) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            System.out.println("\n--- 5. ELIMINANDO CLIENTE ---");

            Cliente cliente = em.find(Cliente.class, nif);
            if (cliente != null) {
                em.remove(cliente);
                System.out.println(" Cliente eliminado. El trigger debería anonimizar sus compras.");
            } else {
                System.out.println(" Cliente no encontrado.");
            }

            tx.commit();
        } catch (PersistenceException pe) {
            if (tx.isActive()) tx.rollback();
            System.err.println(" Error de persistencia al eliminar cliente: " + pe.getMessage());
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    private static void imprimirEstadoCompra(EntityManager em, Long idCompra) {
        if (idCompra == null) return;
        Compra c = em.find(Compra.class, idCompra.intValue());
        if (c != null) {
            System.out.println("   Estado Compra [" + idCompra + "]: " + c.getEstado() +
                    " | Cliente: " + (c.getCliente() != null ? c.getCliente().getNombre() : "NULL"));
        }
    }

    private static void verificarAnonimizacion(EntityManager em, Long idCompra) {
        if (idCompra == null) return;
        System.out.println("\n--- 6. VERIFICANDO RESULTADO ---");

        em.clear();

        Compra c = em.find(Compra.class, idCompra.intValue());
        if (c != null) {
            System.out.println("Compra ID: " + c.getId());
            System.out.println("Estado: " + c.getEstado());
            System.out.println("Dirección: '" + c.getDireccion() + "'");
            System.out.println("Precio Total: " + c.getPrecioTotal());

            if (c.getCliente() == null) {
                System.out.println(" ÉXITO: La compra no tiene cliente asociado (Anonimizada).");
            } else {
                System.out.println(" ERROR: La compra aún tiene cliente: " + c.getCliente().getNifCif());
            }
        }
    }
}
