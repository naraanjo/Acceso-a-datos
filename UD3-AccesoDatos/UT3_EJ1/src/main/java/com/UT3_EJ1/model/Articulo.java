package com.UT3_EJ1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Representa un artículo disponible en el inventario de la tienda.
 * <p>
 * Un artículo contiene información descriptiva, precio actual y stock
 * disponible. Mantiene la relación con los registros de compras que lo
 * incluyan.
 * </p>
 * <p>
 * <strong>Relación:</strong> 1:N con {@link ArticuloCompra}.
 * </p>
 *
 * @author Álvaro Naranjo
 * @version 1.0.0-SNAPSHOT
 * @since 27/11/2025
 */
@Entity
@Table(name = "articulo")
public class Articulo {

	/**
	 * Identificador único del artículo. Generado automáticamente por la base de
	 * datos.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	/**
	 * Nombre del artículo.
	 * <p>
	 * Campo obligatorio, longitud máxima 100 caracteres.
	 * </p>
	 */
	@Column(name = "nombre", nullable = false, length = 100)
	private String nombre;

	/**
	 * Descripción detallada del artículo.
	 * <p>
	 * Campo obligatorio, almacenado como TEXT en la base de datos.
	 * </p>
	 */
	@Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
	private String descripcion;

	/**
	 * Precio actual de venta del artículo.
	 * <p>
	 * Campo obligatorio, precisión de 10 dígitos y 2 decimales.
	 * </p>
	 */
	@Column(name = "precio_actual", nullable = false, precision = 10, scale = 2)
	private BigDecimal precioActual;

	/**
	 * Cantidad de stock disponible.
	 * <p>
	 * Campo obligatorio, no puede ser negativo.
	 * </p>
	 * <p>
	 * Tipo primitivo: No es necesario @NotNull
	 * </p>
	 */
	@Column(name = "stock", nullable = false)
	private int stock;

	/**
	 * Esta del articulo activo | Inactivo, por defecto activo
	 * <p>
	 * Campo obligatorio, no puede ser null
	 * </p>
	 * <p>
	 * Tipo primitivo: No es necesario @NotNull
	 * </p>
	 */
	@Column(name = "estado", nullable = false)
	private boolean estado = true;

	/**
	 * Conjunto de relaciones con compras donde aparece este artículo.
	 * <p>
	 * Relación mapeada por el atributo {@code articulo} en la clase
	 * {@link ArticuloCompra}. Se utiliza carga perezosa (LAZY) y operaciones en
	 * cascada (ALL).
	 * </p>
	 */
	@OneToMany(mappedBy = "articulo", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ArticuloCompra> articulosCompra = new HashSet<>();

	/**
	 * * Constructor vacío requerido por JPA.
	 */
	public Articulo() {
	}

	/**
	 * Crea un nuevo artículo con los datos especificados.
	 *
	 * @param nombre       nombre del artículo
	 * @param descripcion  descripción del artículo
	 * @param precioActual precio de venta actual
	 * @param stock        cantidad inicial de stock
	 */
	public Articulo(String nombre, String descripcion, BigDecimal precioActual, int stock) {
		setNombre(nombre);
		setDescripcion(descripcion);
		setPrecioActual(precioActual);
		setStock(stock);
	}

	// --- Getters y Setters ---

	/**
	 * Obtiene el identificador del artículo.
	 * 
	 * @return el identificador único del artículo
	 */
	public int getId() {
		return id;
	}

	// No se recomienda cambiar ID autogenerado
	// public void setId(int id) { this.id = id; }

	/**
	 * Obtiene el nombre del artículo.
	 * 
	 * @return el nombre del artículo
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece el nombre del artículo.
	 * <p>
	 * Si el valor es nulo o vacío, se establece "SinNombre" por defecto.
	 * </p>
	 * 
	 * @param nombre el nuevo nombre del artículo
	 */
	public void setNombre(String nombre) {
		this.nombre = (nombre != null && !nombre.isBlank()) ? nombre.trim() : "SinNombre";
	}

	/**
	 * Obtiene la descripción del artículo.
	 * 
	 * @return la descripción detallada
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Establece la descripción del artículo.
	 * <p>
	 * Si el valor es nulo o vacío, se establece una cadena vacía.
	 * </p>
	 * 
	 * @param descripcion la nueva descripción
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = (descripcion != null && !descripcion.isBlank()) ? descripcion.trim() : "";
	}

	/**
	 * Obtiene el precio actual del artículo.
	 * 
	 * @return el precio como BigDecimal
	 */
	public BigDecimal getPrecioActual() {
		return precioActual;
	}

	/**
	 * Establece el precio actual del artículo.
	 * <p>
	 * Si el precio es negativo o nulo, se establece a 0.
	 * </p>
	 * 
	 * @param precioActual el nuevo precio
	 */
	public void setPrecioActual(BigDecimal precioActual) {
		this.precioActual = (precioActual != null && precioActual.compareTo(BigDecimal.ZERO) >= 0) ? precioActual
				: BigDecimal.ZERO;
	}

	/**
	 * Obtiene el estado del artículo.
	 * <p>
	 * Indica si el artículo está activo o inactivo en el inventario.
	 * </p>
	 *
	 * @return {@code true} si el artículo está activo, {@code false} si está
	 *         inactivo.
	 */
	public boolean getEstado() {
		return estado;
	}

	/**
	 * Establece el estado del artículo.
	 * <p>
	 * Permite marcar el artículo como activo o inactivo.
	 * </p>
	 *
	 * @param estado {@code true} para activo, {@code false} para inactivo.
	 */
	public void setEstado(boolean estado) {
		this.estado = estado;
	}

	/**
	 * Obtiene el stock disponible.
	 * 
	 * @return la cantidad de stock
	 */
	public int getStock() {
		return stock;
	}

	/**
	 * Establece el stock disponible.
	 * <p>
	 * Si el stock es negativo, se establece a 0.
	 * </p>
	 * 
	 * @param stock la nueva cantidad de stock
	 */
	public void setStock(int stock) {
		this.stock = (stock >= 0) ? stock : 0;
	}

	/**
	 * Obtiene el conjunto de relaciones de compra asociadas a este artículo.
	 * 
	 * @return un conjunto de objetos {@link ArticuloCompra}
	 */
	public Set<ArticuloCompra> getArticulosCompra() {
		return articulosCompra;
	}

	/**
	 * Establece el conjunto de relaciones de compra.
	 * 
	 * @param articulosCompra el nuevo conjunto de relaciones
	 */
	public void setArticulosCompra(Set<ArticuloCompra> articulosCompra) {
		this.articulosCompra = (articulosCompra != null) ? articulosCompra : new HashSet<>();
	}

	/**
	 * Añade un registro de compra asociado a este artículo, manteniendo la
	 * coherencia bidireccional.
	 * <p>
	 * Este método asegura que la relación se actualice en ambos extremos.
	 * </p>
	 * 
	 * @param articuloCompra el registro de compra a añadir
	 */
	public void addArticuloCompra(ArticuloCompra articuloCompra) {

		if (articuloCompra != null) {
			articulosCompra.add(articuloCompra);
			if (articuloCompra.getArticulo() != this) {
				articuloCompra.setArticulo(this);
			}
		}

	}

	// --- CRUD ---

	/**
	 * Crea un nuevo artículo en la base de datos.
	 *
	 * @param articulo Artículo a persistir
	 * @param em       EntityManager utilizado para la operación
	 * @throws IllegalArgumentException si articulo es null
	 * @throws RuntimeException         si ocurre un error durante la persistencia
	 */
	public static void crear(Articulo articulo, EntityManager em) {
		validarArticuloNoNull(articulo);
		validarEntityManager(em);

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.persist(articulo);
			tx.commit();
		
		} catch (Exception e) {
			if (tx.isActive())
				tx.rollback();
			throw e;
		}
	}

	/**
	 * Obtiene un artículo por su identificador.
	 *
	 * @param id Identificador del artículo
	 * @param em EntityManager utilizado para la operación
	 * @return Articulo si existe, null si no se encuentra
	 */
	public static Articulo selectArticulo(int id, EntityManager em) {
		validarId(id);
		validarEntityManager(em);
		return em.find(Articulo.class, id);
	}

	/**
	 * Recupera todos los artículos de la base de datos.
	 *
	 * @param em EntityManager utilizado para la operación
	 * @return Lista de artículos, vacía si no hay registros
	 */
	public static List<Articulo> obtenerTodos(EntityManager em) {
		validarEntityManager(em);
		return em.createQuery("SELECT a FROM Articulo a", Articulo.class).getResultList();
	}

	/**
	 * Actualiza un artículo existente.
	 * <p>
	 * Permite modificar nombre, descripción, precio, stock y estado activo.
	 * </p>
	 *
	 * @param articulo Artículo con los datos actualizados
	 * @param em       EntityManager utilizado para la operación
	 * @throws IllegalArgumentException si articulo es null
	 * @throws RuntimeException         si ocurre un error durante la actualización
	 */
	public static void actualizar(Articulo articulo, EntityManager em) {
		validarArticuloNoNull(articulo);
		validarEntityManager(em);

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.merge(articulo);
			tx.commit();
		} catch (Exception e) {
			if (tx.isActive())
				tx.rollback();
			throw e;
		}
	}

	// --- ELIMINACIÓN LÓGICA CON PROCEDURE ---

	/**
	 * "Elimina" un artículo utilizando el procedimiento almacenado
	 * {@code eliminar_articulo}.
	 * <p>
	 * El procedimiento establece {@code activo = false}, {@code stock = 0},
	 * {@code precio_actual = 0} y limpia nombre y descripción.
	 * </p>
	 *
	 * @param id Identificador del artículo a eliminar
	 * @param em EntityManager utilizado para la operación
	 * @throws IllegalArgumentException                 si id <= 0
	 * @throws jakarta.persistence.PersistenceException si ocurre un error al
	 *                                                  ejecutar el procedure
	 */
	public void eliminar(int id, EntityManager em) {
		validarId(id);
		validarEntityManager(em);

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Query q = em.createNativeQuery("CALL eliminar_articulo(:id)");
			q.setParameter("id", id);
			q.executeUpdate();
			tx.commit();
		} catch (Exception e) {
			if (tx.isActive())
				tx.rollback();
			throw e;
		}
	}

	// --- MÉTODOS DE VALIDACIÓN ---
	private static void validarArticuloNoNull(Articulo articulo) {
		if (articulo == null) {
			throw new IllegalArgumentException("El artículo no puede ser null");
		}
	}

	private static void validarId(int id) {
		if (id <= 0) {
			throw new IllegalArgumentException("El id del artículo debe ser mayor que 0");
		}
	}

	private static void validarEntityManager(EntityManager em) {
		if (em == null) {
			throw new IllegalArgumentException("El EntityManager no puede ser null");
		}
	}

	/**
	 * Retorna una representación en cadena del artículo.
	 * 
	 * @return una cadena con los detalles principales del artículo
	 */
	@Override
	public String toString() {
		return "Articulo{id=" + id + ", nombre='" + nombre + "', precioActual=" + precioActual + ", stock=" + stock
				+ "}";
	}
}