package model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Representa un artículo disponible en el inventario de la tienda. Un artículo
 * contiene información descriptiva, precio actual y stock disponible. Mantiene
 * la relación con los registros de compras que lo incluyan.
 *
 * Relación: 1:N con {@link ArticuloCompra}.
 *
 * Autor: Álvaro Naranjo
 */
@Entity
@Table(name = "articulo")
public class Articulo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "nombre", nullable = false, length = 100)
	private String nombre;

	@Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
	private String descripcion;

	@Column(name = "precio_actual", nullable = false, precision = 10, scale = 2)
	private BigDecimal precioActual;

	@Column(name = "stock", nullable = false)
	private int stock;
	
	@Column(name = "activo", nullable = false)
	private boolean activo = true;


	@OneToMany(mappedBy = "articulo", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ArticuloCompra> articulosCompra = new HashSet<>();

	/** Constructor vacío requerido por JPA */
	public Articulo() {
	}

	public Articulo(String nombre, String descripcion, BigDecimal precioActual, int stock) {
		setNombre(nombre);
		setDescripcion(descripcion);
		setPrecioActual(precioActual);
		setStock(stock);
	}

	// --- Getters y Setters ---
	public int getId() {
		return id;
	}

	// No se recomienda cambiar ID autogenerado
	// public void setId(int id) { this.id = id; }

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = (nombre != null && !nombre.isBlank()) ? nombre.trim() : "SinNombre";
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = (descripcion != null && !descripcion.isBlank()) ? descripcion.trim() : "";
	}

	public BigDecimal getPrecioActual() {
		return precioActual;
	}

	public void setPrecioActual(BigDecimal precioActual) {
		this.precioActual = (precioActual != null && precioActual.compareTo(BigDecimal.ZERO) >= 0) ? precioActual
				: BigDecimal.ZERO;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = (stock >= 0) ? stock : 0;
	}

	public Set<ArticuloCompra> getArticulosCompra() {
		return articulosCompra;
	}

	public void setArticulosCompra(Set<ArticuloCompra> articulosCompra) {
		this.articulosCompra = (articulosCompra != null) ? articulosCompra : new HashSet<>();
	}
	
	public boolean isActivo() {
	    return activo;
	}

	public void setActivo(boolean activo) {
	    this.activo = activo;
	}

	/**
	 * Añade un registro de compra asociado a este artículo, manteniendo la
	 * coherencia bidireccional.
	 */
	public void addArticuloCompra(ArticuloCompra articuloCompra) {
		if (articuloCompra == null)
			return;
		articulosCompra.add(articuloCompra);
		if (articuloCompra.getArticulo() != this) {
			articuloCompra.setArticulo(this);
		}
	}

	/**
	 * "Elimina" un artículo de la compra marcándolo como inactivo,
	 * sin eliminar físicamente la relación de la base de datos.
	 */
	public void removeArticuloCompra(ArticuloCompra articuloCompra) {
	    if (articuloCompra == null) return;

	    // Marcamos el artículo como inactivo
	    if (articuloCompra.getArticulo() != null) {
	        articuloCompra.getArticulo().setActivo(false);
	    }
	    
	    // Opcional: si quieres quitarlo de la colección en memoria pero no de la BD
	    articulosCompra.remove(articuloCompra);
	}

	@Override
	public String toString() {
		return "Articulo{id=" + id + ", nombre='" + nombre + "', precioActual=" + precioActual + ", stock=" + stock
				+ "}";
	}
}
