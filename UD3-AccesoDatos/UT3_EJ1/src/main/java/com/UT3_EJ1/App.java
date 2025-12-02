package com.UT3_EJ1;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.UT3_EJ1.model.*;

/**
 * Clase principal de la aplicación que gestiona la base de datos de la tienda.
 * <p>
 * Crea datos iniciales de clientes, artículos y compras, permite mostrar
 * información completa, actualizar entidades y limpiar la base de datos.
 * </p>
 */
public class App {
	
	// IMPORTANTE |-> Recomiendo ver el archivo "informacion-modelado", ahi justifico todo y las decisiones
	// tomadas
	
	// Scripts |-> src/main/resources/scripts

    private static final String PERSISTENCE_UNIT_NAME = "shop-ec";
    private static EntityManagerFactory emf;

    /**
     * Método principal de la aplicación.
     * 
     * @param args Argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Exception e) {
            System.err.println("Error FATAL: No se pudo conectar a la base de datos.");
            e.printStackTrace();
            return;
        }

        EntityManager em = emf.createEntityManager();
        System.out.println("Conexión establecida con la base de datos\n");

        try {
            limpiarBd(em);               // Limpiar base de datos
            crearDatosIniciales(em);     // Crear datos iniciales

            mostrarClienteCompleto("11111111D", em); // Mostrar un cliente
            mostrarTodosLosClientes(em);             // Mostrar todos los clientes
            eliminarCliente("22222222D", em);       // Eliminar un cliente
            mostrarTodasLasCompras(em);             // Mostrar todas las compras
            mostrarTodosLosArticulos(em);           // Mostrar todos los artículos
            mostrarTodosLosTickets(em);             // Mostrar todas las líneas de compra

            // --- Ejemplo de actualización de compra ---
            System.out.println("\n===== ACTUALIZACIÓN DE UNA COMPRA =====");
            Compra compraParaModificar = Compra.obtenerTodos(em).stream().findFirst().orElse(null);
            if (compraParaModificar != null) {
                compraParaModificar.setEstado(EstadoCompra.ENTREGADO);
                Compra.actualizar(compraParaModificar, em);
            }

            // --- Ejemplo de actualización de artículo ---
            System.out.println("\n===== ACTUALIZACIÓN DE UN ARTICULO =====");
            Articulo articuloParaModificar = Articulo.obtenerTodos(em).stream().findFirst().orElse(null);
            if (articuloParaModificar != null) {
                articuloParaModificar.setEstado(false);
                Articulo.actualizar(articuloParaModificar, em);
            }

            // --- Ejemplo de actualización de información fiscal ---
            System.out.println("\n===== ACTUALIZACIÓN DE INFORMACION FISCAL =====");
            InformacionFiscal infoFscalActualizar = InformacionFiscal.obtenerTodos(em).stream().findFirst().orElse(null);
            if (infoFscalActualizar != null) {
                infoFscalActualizar.setDireccionFiscal("cambio_direccion");
                InformacionFiscal.actualizar(infoFscalActualizar, em);
            }

            limpiarBd(em); // Limpiar base de datos al finalizar

        } catch (Exception e) {
            System.err.println("Error inesperado en el flujo principal: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em.isOpen())
                em.close();
            if (emf.isOpen())
                emf.close();
            System.out.println("Conexión cerrada.");
        }
    }

    // -------------------- MÉTODOS DE INICIALIZACIÓN --------------------

    /**
     * Limpia todas las tablas de la base de datos.
     * 
     * @param em EntityManager activo
     */
    private static void limpiarBd(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery("DELETE FROM ArticuloCompra").executeUpdate();
            em.createQuery("DELETE FROM Compra").executeUpdate();
            em.createQuery("DELETE FROM Cliente").executeUpdate();
            em.createQuery("DELETE FROM Articulo").executeUpdate();
            em.createQuery("DELETE FROM InformacionFiscal").executeUpdate();
            tx.commit();
            System.out.println("Base de datos limpiada completamente.\n");
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Crea datos iniciales: información fiscal, clientes, artículos y compras.
     * 
     * @param em EntityManager activo
     */
    private static void crearDatosIniciales(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // --- Información Fiscal ---
            InformacionFiscal info1 = new InformacionFiscal("11111111D", "Calle Falsa 1", "+34123456780");
            InformacionFiscal info2 = new InformacionFiscal("22222222D", "Calle Verdadera 2", "+34987654320");
            InformacionFiscal info3 = new InformacionFiscal("33333333D", "Calle Real 3", "+34611223340");

            // --- Clientes ---
            Cliente cliente1 = new Cliente("11111111D", "Juan Perez", "juanD@mail.com", LocalDateTime.now(), info1);
            Cliente cliente2 = new Cliente("22222222D", "Maria Lopez", "mariaD@mail.com", LocalDateTime.now(), info2);
            Cliente cliente3 = new Cliente("33333333D", "Luis Garcia", "luisD@mail.com", LocalDateTime.now(), info3);
            em.persist(cliente1);
            em.persist(cliente2);
            em.persist(cliente3);

            // --- Artículos ---
            Articulo articulo1 = new Articulo("Laptop", "Laptop Gamer", new BigDecimal("1200.00"), 10);
            Articulo articulo2 = new Articulo("Ratón", "Ratón Inalámbrico", new BigDecimal("25.00"), 50);
            Articulo articulo3 = new Articulo("Teclado", "Teclado Mecánico", new BigDecimal("80.00"), 30);
            em.persist(articulo1);
            em.persist(articulo2);
            em.persist(articulo3);

            // --- Compras ---
            Compra compra1 = new Compra(LocalDateTime.now(), EstadoCompra.PENDIENTE, info1.getDireccionFiscal(), BigDecimal.ZERO, cliente1);
            em.persist(compra1);
            em.flush();
            compra1.addArticuloCompra(new ArticuloCompra(articulo1, compra1, articulo1.getPrecioActual(), 1));
            compra1.addArticuloCompra(new ArticuloCompra(articulo2, compra1, articulo2.getPrecioActual(), 2));
            calcularPrecioTotal(compra1);
            em.persist(compra1);

            Compra compra2 = new Compra(LocalDateTime.now(), EstadoCompra.PENDIENTE, info2.getDireccionFiscal(), BigDecimal.ZERO, cliente2);
            em.persist(compra2);
            em.flush();
            compra2.addArticuloCompra(new ArticuloCompra(articulo2, compra2, articulo2.getPrecioActual(), 1));
            compra2.addArticuloCompra(new ArticuloCompra(articulo3, compra2, articulo3.getPrecioActual(), 1));
            calcularPrecioTotal(compra2);
            em.persist(compra2);

            Compra compra3 = new Compra(LocalDateTime.now(), EstadoCompra.PENDIENTE, info3.getDireccionFiscal(), BigDecimal.ZERO, cliente3);
            em.persist(compra3);
            em.flush();
            compra3.addArticuloCompra(new ArticuloCompra(articulo1, compra3, articulo1.getPrecioActual(), 1));
            compra3.addArticuloCompra(new ArticuloCompra(articulo3, compra3, articulo3.getPrecioActual(), 2));
            calcularPrecioTotal(compra3);
            em.persist(compra3);

            tx.commit();
            System.out.println("Datos iniciales creados correctamente.\n");
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw e;
        }
    }

    /**
     * Calcula el precio total de una compra sumando el precio de cada artículo multiplicado por la cantidad.
     * 
     * @param compra Compra a calcular
     */
    private static void calcularPrecioTotal(Compra compra) {
        BigDecimal total = compra.getArticulosCompra().stream()
                .map(ac -> ac.getPrecioCompra().multiply(BigDecimal.valueOf(ac.getUnidades())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        compra.setPrecioTotal(total);
    }

    // -------------------- MÉTODOS DE IMPRESIÓN --------------------

    /**
     * Muestra toda la información de un cliente, incluyendo compras y artículos.
     * 
     * @param nifCif NIF/CIF del cliente
     * @param em     EntityManager activo
     */
    private static void mostrarClienteCompleto(String nifCif, EntityManager em) {
        System.out.println("===== EJEMPLO SELECT CLIENTE COMPLETO =====");
        Cliente cliente = Cliente.selectCliente(nifCif, em);
        if (cliente != null) {
            System.out.println(cliente.getInformacionFiscal());
            System.out.println(cliente);
            System.out.println("Compras del cliente:");
            cliente.getCompras().forEach(c -> {
                System.out.println("Compra ID: " + c.getId());
                System.out.println("Dirección: " + c.getDireccion());
                System.out.println("Estado: " + c.getEstado());
                System.out.println("Precio Total: " + c.getPrecioTotal());
                System.out.println("Artículos:");
                c.getArticulosCompra().forEach(ac -> System.out.println(
                        "  - " + ac.getArticulo().getNombre() + " | Unidades: " + ac.getUnidades()
                                + " | Precio: " + ac.getPrecioCompra()));
                System.out.println("--------------------------");
            });
        }
    }

    /**
     * Muestra todos los clientes registrados.
     * 
     * @param em EntityManager activo
     */
    private static void mostrarTodosLosClientes(EntityManager em) {
        System.out.println("===== LISTA DE TODOS LOS CLIENTES =====");
        InformacionFiscal.obtenerTodos(em).forEach(c -> System.out.println(c + " | NIF/CIF: " + c.getNifCif()));
    }

    /**
     * Elimina un cliente por NIF/CIF y muestra la lista actualizada de clientes.
     * 
     * @param nifCif NIF/CIF del cliente
     * @param em     EntityManager activo
     */
    private static void eliminarCliente(String nifCif, EntityManager em) {
        System.out.println("\nEliminando cliente " + nifCif + "...");
        InformacionFiscal.eliminar(nifCif, em);
        System.out.println("Lista de clientes después de la eliminación:");
        mostrarTodosLosClientes(em);
    }

    /**
     * Muestra todas las compras registradas.
     * 
     * @param em EntityManager activo
     */
    private static void mostrarTodasLasCompras(EntityManager em) {
        System.out.println("===== LISTA DE TODAS LAS COMPRAS =====");
        Compra.obtenerTodos(em).forEach(System.out::println);
    }

    /**
     * Muestra todos los artículos registrados.
     * 
     * @param em EntityManager activo
     */
    private static void mostrarTodosLosArticulos(EntityManager em) {
        System.out.println("===== LISTA DE TODOS LOS ARTICULOS =====");
        Articulo.obtenerTodos(em).forEach(System.out::println);
    }

    /**
     * Muestra todas las líneas de compra (tickets).
     * 
     * @param em EntityManager activo
     */
    private static void mostrarTodosLosTickets(EntityManager em) {
        System.out.println("===== LISTA DE TODOS LOS TICKETS =====");
        ArticuloCompra.obtenerTodos(em).forEach(System.out::println);
    }
}
