package com.UT3_EJ1.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entidad que representa una orden de compra realizada por un cliente.
 * <p>
 * Gestiona los detalles del pedido como la fecha, estado, dirección de envío y el precio total.
 * </p>
 * <p>
 * <strong>Relaciones:</strong>
 * <ul>
 * <li>N:1 con {@link Cliente}: Cada compra pertenece a un único cliente.</li>
 * <li>1:N con {@link ArticuloCompra}: Una compra puede contener múltiples líneas de artículos.</li>
 * </ul>
 * </p>
 *
 * @author Álvaro Naranjo
 * @version 1.0.0-SNAPSHOT
 * @since 27/11/2025
 */
@Entity
@Table(name = "compra")
public class Compra {

	/**
	 * Identificador único de la compra.
	 * Generado automáticamente por la estrategia de identidad de la base de datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	/**
	 * Fecha y hora exacta en la que se realizó la compra.
	 * <p>Campo obligatorio.</p>
	 */
	@Column(name = "fecha_realizada", nullable = false)
	private LocalDateTime fechaRealizada;

	/**
	 * Estado actual de la compra (ej. PENDIENTE, ENVIADO).
	 * <p>Se almacena como una cadena de texto (STRING) en la base de datos.</p>
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "estado", nullable = false, length = 20)
	private EstadoCompra estado;

	/**
	 * Dirección de envío asociada a esta compra específica.
	 * <p>Campo obligatorio.</p>
	 */
	@Column(name = "direccion", nullable = false)
	private String direccion;

	/**
	 * Precio total calculado de la compra.
	 * <p>Campo obligatorio con precisión de 10 dígitos y 2 decimales.</p>
	 */
	@Column(name = "precio_total", nullable = false, precision = 10, scale = 2)
	private BigDecimal precioTotal;

	// --- RELACIÓN N:1 con Cliente ---
	
	/**
	 * Cliente que ha realizado la compra.
	 * <p>Relación de muchos a uno (N:1), carga perezosa (LAZY) y (nullable = true), para que
	 * al eliminar un cliente se borre su informacion de las compras -ON DELETE SET NULL-</p>
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cliente_nif_cif", nullable = true)
	private Cliente cliente;

	// --- RELACIÓN 1:N con ArticuloCompra ---
	
	/**
	 * Conjunto de líneas de compra (artículos) asociadas a este pedido.
	 * <p>Relación uno a muchos (1:N) con operaciones en cascada (ALL) y eliminación de huérfanos.</p>
	 */
	@OneToMany(mappedBy = "compra", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ArticuloCompra> articulosCompra = new HashSet<>();

	/**
	 * Constructor vacío requerido por JPA.
	 */
	public Compra() {
	}

	/**
	 * Crea una nueva compra con los datos especificados y la asocia a un cliente.
	 * <p>Valida y establece la relación bidireccional con el cliente.</p>
	 *
	 * @param fechaRealizada fecha de la compra
	 * @param estado estado inicial del pedido
	 * @param direccion dirección de envío
	 * @param precioTotal importe total
	 * @param cliente cliente que realiza la compra
	 */
	public Compra(LocalDateTime fechaRealizada, EstadoCompra estado, String direccion, BigDecimal precioTotal,
			Cliente cliente) {
		
		setFechaRealizada(fechaRealizada);
		setEstado(estado);
		setDireccion(direccion);
		setPrecioTotal(precioTotal);
		setCliente(cliente); // bidireccional
	}

	// --- Getters y Setters ---
	
	/**
	 * Obtiene el identificador de la compra.
	 * @return el ID de la compra
	 */
	public int getId() {
		return id;
	}

	// No  setter de ID si es autogenerado
	// public void setId(int id) { this.id = id; }

	/**
	 * Obtiene la fecha en la que se realizó la compra.
	 * @return fecha y hora de la compra
	 */
	public LocalDateTime getFechaRealizada() {
		return fechaRealizada;
	}

	/**
	 * Establece la fecha de la compra.
	 * <p>Si el valor es nulo, se establece la fecha y hora actual.</p>
	 * @param fechaRealizada nueva fecha
	 */
	public void setFechaRealizada(LocalDateTime fechaRealizada) {
		this.fechaRealizada = (fechaRealizada != null) ? fechaRealizada : LocalDateTime.now();
	}

	/**
	 * Obtiene el estado actual del pedido.
	 * @return el estado como enum {@link EstadoCompra}
	 */
	public EstadoCompra getEstado() {
		return estado;
	}

	/**
	 * Establece el estado del pedido.
	 * <p>Si el valor es nulo, se establece a {@code PENDIENTE} por defecto.</p>
	 * @param estado nuevo estado
	 */
	public void setEstado(EstadoCompra estado) {
		this.estado = (estado != null) ? estado : EstadoCompra.PENDIENTE; // valor por defecto
	}

	/**
	 * Obtiene la dirección de envío.
	 * @return la dirección como cadena
	 */
	public String getDireccion() {
		return direccion;
	}

	/**
	 * Establece la dirección de envío.
	 * <p>Si el valor es nulo o vacío, se establece una cadena vacía.</p>
	 * @param direccion nueva dirección
	 */
	public void setDireccion(String direccion) {
		this.direccion = (direccion != null && !direccion.isBlank()) ? direccion.trim() : "";
	}

	/**
	 * Obtiene el precio total de la compra.
	 * @return el precio total
	 */
	public BigDecimal getPrecioTotal() {
		return precioTotal;
	}

	/**
	 * Establece el precio total de la compra.
	 * <p>Si el valor es nulo, se establece a 0.</p>
	 * @param precioTotal nuevo precio total
	 */
	public void setPrecioTotal(BigDecimal precioTotal) {
		this.precioTotal = (precioTotal != null) ? precioTotal : BigDecimal.ZERO;
	}

	/**
	 * Obtiene el cliente asociado a la compra.
	 * @return la entidad Cliente
	 */
	public Cliente getCliente() {
		return cliente;
	}

	/**
	 * Asocia un cliente a la compra y mantiene la coherencia bidireccional.
	 * <p>
	 * Añade esta compra a la lista de compras del cliente si aún no está presente,
	 * utilizando el método {@code addCompra} para evitar problemas con listas inmutables.
	 * </p>
	 *
	 * @param cliente el cliente a asociar
	 * @throws IllegalArgumentException si el cliente es nulo
	 */
	public void setCliente(Cliente cliente) {
		  this.cliente = cliente;

		    // Solo sincronizamos la lista del cliente si no es null
		    if (cliente != null && !cliente.getCompras().contains(this)) {
		        cliente.addCompra(this); 
		    }
	}

	/**
	 * Obtiene el conjunto de artículos incluidos en la compra.
	 * @return un conjunto inmodificable de {@link ArticuloCompra}
	 */
	public Set<ArticuloCompra> getArticulosCompra() {
		return Collections.unmodifiableSet(articulosCompra);
	}

	

	// --- Métodos Helper ---
	
	/**
	 * Añade una línea de artículo a la compra y sincroniza la relación.
	 * <p>Establece esta compra como propietaria en la entidad {@link ArticuloCompra}.</p>
	 * @param articuloCompra la línea de compra a añadir
	 */
	public void addArticuloCompra(ArticuloCompra articuloCompra) {
		if (articuloCompra == null)
			return;
		articulosCompra.add(articuloCompra);
		if (articuloCompra.getCompra() != this) {
			articuloCompra.setCompra(this);
		}
	}

	/**
	 * Elimina una línea de artículo de la compra.
	 * <p>Desvincula la relación estableciendo la compra a null en la entidad hija.</p>
	 * @param articuloCompra la línea de compra a eliminar
	 */
	public void removeArticuloCompra(ArticuloCompra articuloCompra) {
		if (articuloCompra == null)
			return;
		articulosCompra.remove(articuloCompra);
		if (articuloCompra.getCompra() == this) {
			articuloCompra.setCompra(null);
		}
	}

	 // --- CRUD ---

    /**
     * Crea una nueva compra en la base de datos.
     * <p>
     * La compra puede contener artículos asociados mediante {@link Compra#getArticulosCompra()}.
     * </p>
     *
     * @param compra Compra a persistir
     * @param em     EntityManager utilizado para la operación
     * @throws IllegalArgumentException si compra es null
     * @throws RuntimeException         si ocurre un error durante la persistencia
     */
    public static void crear(Compra compra, EntityManager em) {
        validarCompraNoNull(compra);
        validarEntityManager(em);

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(compra);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    /**
     * Obtiene una compra por su identificador.
     *
     * @param id Identificador de la compra
     * @param em EntityManager utilizado para la operación
     * @return Compra si existe, null si no se encuentra
     */
    public Compra selectCompra(int id, EntityManager em) {
        validarId(id);
        validarEntityManager(em);
        return em.find(Compra.class, id);
    }

    /**
     * Recupera todas las compras de la base de datos.
     *
     * @param em EntityManager utilizado para la operación
     * @return Lista de compras, vacía si no hay registros
     */
    public static  List<Compra> obtenerTodos(EntityManager em) {
        validarEntityManager(em);
        return em.createQuery("SELECT c FROM Compra c", Compra.class).getResultList();
    }

    /**
     * Actualiza los datos de una compra existente.
     * <p>
     * Permite actualizar dirección, estado, precio total y artículos asociados.
     * No se modifica la asociación con el cliente directamente mediante este método.
     * </p>
     *
     * @param compra Compra con los datos actualizados
     * @param em     EntityManager utilizado para la operación
     * @throws IllegalArgumentException si compra es null
     * @throws RuntimeException         si ocurre un error durante la actualización
     */
    public  static void actualizar(Compra compra, EntityManager em) {
        validarCompraNoNull(compra);
        validarEntityManager(em);

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(compra);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    // --- MÉTODOS DE VALIDACIÓN ---

    private static void validarCompraNoNull(Compra compra) {
        if (compra == null) {
            throw new IllegalArgumentException("La compra no puede ser null");
        }
    }

    private void validarId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El id de la compra debe ser mayor que 0");
        }
    }

    private static void validarEntityManager(EntityManager em) {
        if (em == null) {
            throw new IllegalArgumentException("El EntityManager no puede ser null");
        }
    }
   
	/**
	 * Retorna una representación en cadena de la compra.
	 * @return cadena con los detalles básicos de la compra
	 */
	@Override
	public String toString() {
		return "Compra{id=" + id + ", fechaRealizada=" + fechaRealizada + ", estado=" + estado + ", direccion='"
				+ direccion + '\'' + ", precioTotal=" + precioTotal + '}';
	}
}