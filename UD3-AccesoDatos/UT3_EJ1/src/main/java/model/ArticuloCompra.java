package model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Representa la tabla intermedia de la relación N:M entre {@link Articulo} y
 * {@link Compra}. Contiene información adicional de la relación como precio y
 * unidades.
 *
 * Clave primaria compuesta: {@link ArticuloCompraId}.
 * 
 * Coherencia bidireccional con {@link Compra}.
 * 
 * Autor: Álvaro Naranjo
 */
@Entity
@Table(name = "articulo_compra")
public class ArticuloCompra {

	@EmbeddedId
	private ArticuloCompraId id = new ArticuloCompraId();

	@Column(name = "precio_compra", nullable = false, precision = 10, scale = 2)
	private BigDecimal precioCompra;

	@Column(name = "unidades", nullable = false)
	private int unidades;

	// --- Relaciones ---
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_articulo", insertable = false, updatable = false)
	private Articulo articulo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_compra", insertable = false, updatable = false)
	private Compra compra;

	/** Constructor vacío requerido por JPA */
	public ArticuloCompra() {
	}

	/**
	 * Constructor seguro con sincronización bidireccional
	 */
	public ArticuloCompra(Articulo articulo, Compra compra, BigDecimal precioCompra, int unidades) {
		setArticulo(articulo); // package-private
		setCompra(compra); // package-private
		this.precioCompra = (precioCompra != null) ? precioCompra : BigDecimal.ZERO;
		this.unidades = (unidades >= 0) ? unidades : 0;
	}

	// --- Getters y setters ---
	public ArticuloCompraId getId() {
		return id;
	}

	public void setId(ArticuloCompraId id) {
		this.id = (id != null) ? id : new ArticuloCompraId();
	}

	public BigDecimal getPrecioCompra() {
		return precioCompra;
	}

	public void setPrecioCompra(BigDecimal precioCompra) {
		this.precioCompra = (precioCompra != null) ? precioCompra : BigDecimal.ZERO;
	}

	public int getUnidades() {
		return unidades;
	}

	public void setUnidades(int unidades) {
		this.unidades = (unidades >= 0) ? unidades : 0;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	// VISIBILIDAD --> package-private
	 void setArticulo(Articulo articulo) {
		if (articulo == null) {
			throw new IllegalArgumentException("El artículo no puede ser null en ArticuloCompra");
		}
		
		this.articulo = articulo;
		this.id.setIdArticulo(articulo.getId()); // Actualizamos la clave compuesta
		
		// Sincronización bidireccional
		if (!articulo.getArticulosCompra().contains(this)) {
			articulo.addArticuloCompra(this);
		}
	}

	public Compra getCompra() {
		return compra;
	}

	// VISIBILIDAD --> package-private
	 void setCompra(Compra compra) {
		if (compra == null) {
			throw new IllegalArgumentException("La compra no puede ser null en ArticuloCompra");
		}
		this.compra = compra;
		this.id.setIdCompra(compra.getId());

		// Coherencia bidireccional
		if (!compra.getArticulosCompra().contains(this)) {
			compra.addArticuloCompra(this);
		}
	}

	@Override
	public String toString() {
		return "ArticuloCompra{" + "articulo=" + (articulo != null ? articulo.getId() : "null") + ", compra="
				+ (compra != null ? compra.getId() : "null") + ", precioCompra=" + precioCompra + ", unidades="
				+ unidades + '}';
	}
}
