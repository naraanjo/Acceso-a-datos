package com.UT3_EJ1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

/**
 * Representa la entidad asociativa (tabla intermedia) de la relación N:M entre {@link Articulo} y {@link Compra}.
 * <p>
 * Esta entidad gestiona los atributos específicos de la relación, como el precio congelado
 * en el momento de la compra y la cantidad de unidades adquiridas.
 * Utiliza una clave primaria compuesta definida en {@link ArticuloCompraId}.
 * </p>
 * <p>
 * Mantiene la coherencia bidireccional con las entidades padre.
 * </p>
 *
 * @author Álvaro Naranjo
 * @version 1.0.0-SNAPSHOT
 * @since 27/11/2025
 */
@Entity
@Table(name = "articulo_compra")
public class ArticuloCompra {

	/**
	 * Clave primaria compuesta de la entidad.
	 * Instanciada por defecto para evitar nulos en operaciones de persistencia.
	 */
	@EmbeddedId
	private ArticuloCompraId id = new ArticuloCompraId();

	/**
	 * Precio unitario del artículo en el momento exacto de la compra.
	 * <p>Permite mantener un histórico de precios, independiente del precio actual del catálogo.</p>
	 */
	
	@Column(name = "precio_compra", nullable = false, precision = 10, scale = 2)
	private BigDecimal precioCompra;
	/**
	 * Cantidad de unidades del artículo incluidas en esta compra.
	 */
	@Column(name = "unidades", nullable = false)
	private int unidades;

	// --- Relaciones ---
	
	/**
	 * Artículo asociado a esta línea de compra.
	 * <p>Parte de la relación N:M. Mapeado con {@code insertable=false, updatable=false} 
	 * porque la gestión de la clave foránea se realiza a través de {@link #id}.</p>
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_articulo", insertable = false, updatable = false)
	private Articulo articulo;

	/**
	 * Compra a la que pertenece esta línea.
	 * <p>Parte de la relación N:M. Mapeado con {@code insertable=false, updatable=false} 
	 * porque la gestión de la clave foránea se realiza a través de {@link #id}.</p>
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_compra", insertable = false, updatable = false)
	private Compra compra;

	/** * Constructor vacío requerido por la especificación JPA.
	 */
	public ArticuloCompra() {
	}

	/**
	 * Crea una nueva línea de compra con sincronización bidireccional automática.
	 *
	 * @param articulo el artículo comprado
	 * @param compra la compra a la que pertenece
	 * @param precioCompra el precio unitario en el momento de la compra
	 * @param unidades la cantidad de unidades
	 */
	public ArticuloCompra(Articulo articulo, Compra compra, BigDecimal precioCompra, int unidades) {
		setArticulo(articulo); // package-private
		setCompra(compra); // package-private
		this.precioCompra = (precioCompra != null) ? precioCompra : BigDecimal.ZERO;
		this.unidades = (unidades >= 0) ? unidades : 0;
	}

	// --- Getters y setters ---
	
	/**
	 * Obtiene el identificador compuesto de la relación.
	 * @return el objeto de clave primaria compuesta
	 */
	public ArticuloCompraId getId() {
		return id;
	}

	/**
	 * Establece el identificador compuesto.
	 * @param id el nuevo identificador compuesto
	 */
	public void setId(ArticuloCompraId id) {
		this.id = (id != null) ? id : new ArticuloCompraId();
	}

	/**
	 * Obtiene el precio de compra registrado.
	 * @return el precio como BigDecimal
	 */
	public BigDecimal getPrecioCompra() {
		return precioCompra;
	}

	/**
	 * Establece el precio de compra.
	 * <p>Si el valor es nulo, se establece a 0.</p>
	 * @param precioCompra el nuevo precio
	 */
	public void setPrecioCompra(BigDecimal precioCompra) {
		this.precioCompra = (precioCompra != null) ? precioCompra : BigDecimal.ZERO;
	}

	/**
	 * Obtiene la cantidad de unidades.
	 * @return el número de unidades
	 */
	public int getUnidades() {
		return unidades;
	}

	/**
	 * Establece la cantidad de unidades.
	 * <p>Si el valor es negativo, se establece a 0.</p>
	 * @param unidades la nueva cantidad
	 */
	public void setUnidades(int unidades) {
		this.unidades = (unidades >= 0) ? unidades : 0;
	}

	/**
	 * Obtiene el artículo asociado.
	 * @return la entidad Articulo
	 */
	public Articulo getArticulo() {
		return articulo;
	}

	/**
	 * Establece el artículo y sincroniza la relación.
	 * <p>
	 * Actualiza tanto la referencia al objeto como la parte correspondiente de la clave compuesta.
	 * Mantiene la coherencia bidireccional añadiendo esta línea a la lista del artículo si es necesario.
	 * </p>
	 * <p><strong>Visibilidad:</strong> package-private para proteger la integridad de la clave compuesta.</p>
	 *
	 * @param articulo el artículo a asociar
	 * @throws IllegalArgumentException si el artículo es null
	 */
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

	/**
	 * Obtiene la compra asociada.
	 * @return la entidad Compra
	 */
	public Compra getCompra() {
		return compra;
	}

	/**
	 * Establece la compra y sincroniza la relación.
	 * <p>
	 * Actualiza tanto la referencia al objeto como la parte correspondiente de la clave compuesta.
	 * Mantiene la coherencia bidireccional añadiendo esta línea a la lista de la compra si es necesario.
	 * </p>
	 * <p><strong>Visibilidad:</strong> package-private para proteger la integridad de la clave compuesta.</p>
	 *
	 * @param compra la compra a asociar
	 * @throws IllegalArgumentException si la compra es null
	 */
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

	    /**
	     * Recupera todas las líneas de compra (relaciones Artículo-Compra) existentes en la base de datos.
	     * * @param em el EntityManager activo para realizar la consulta
	     * @return una lista con todos los objetos ArticuloCompra
	     */
	    public static List<ArticuloCompra> obtenerTodos(EntityManager em) {
	        if (em == null) {
	            throw new IllegalArgumentException("El EntityManager no puede ser null");
	        }
	        
	        // Consulta  Seleccionamos la entidad completa 'ac'
	        return em.createQuery("SELECT ac FROM ArticuloCompra ac", ArticuloCompra.class)
	                 .getResultList();
	    }
	    
	/**
	 * Retorna una representación en cadena de la línea de compra.
	 * @return una cadena con los IDs relacionados y los valores de la línea
	 */
	@Override
	public String toString() {
		return "ArticuloCompra{" + "articulo=" + (articulo != null ? articulo.getId() : "null") + ", compra="
				+ (compra != null ? compra.getId() : "null") + ", precioCompra=" + precioCompra + ", unidades="
				+ unidades + '}';
	}
}