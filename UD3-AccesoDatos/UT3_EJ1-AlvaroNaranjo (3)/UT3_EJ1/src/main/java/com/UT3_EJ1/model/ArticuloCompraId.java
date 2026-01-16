package com.UT3_EJ1.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Representa la clave primaria compuesta de la entidad {@link ArticuloCompra}.
 * <p>
 * Esta clase implementa {@link Serializable} tal como requiere la especificación JPA para claves compuestas.
 * Se marca como {@link Embeddable} para ser incrustada en la entidad propietaria
 * mediante la anotación {@link jakarta.persistence.EmbeddedId}.
 * </p>
 * <p>
 * Define la identidad única de cada línea de compra mediante la combinación
 * de los identificadores de {@link Articulo} y {@link Compra}.
 * </p>
 *
 * @author Álvaro Naranjo
 * @version 1.0.0-SNAPSHOT
 * @since 27/11/2025
 */
@Embeddable
public class ArticuloCompraId implements Serializable {

	private static final long serialVersionUID = 3346885694770211343L;

	/**
     * Identificador del artículo en la relación N:M.
     * <p>Corresponde con la columna {@code id_articulo} en la base de datos.</p>
     */
    @Column(name = "id_articulo")
    private int idArticulo;

    /**
     * Identificador de la compra en la relación N:M.
     * <p>Corresponde con la columna {@code id_compra} en la base de datos.</p>
     */
    @Column(name = "id_compra")
    private int idCompra;

    /**
     * Constructor por defecto obligatorio para la especificación JPA.
     */
    public ArticuloCompraId() {
    }

    /**
     * Constructor que permite crear un identificador compuesto asignando
     * directamente los valores de las claves foráneas.
     *
     * @param idArticulo identificador numérico del artículo
     * @param idCompra   identificador numérico de la compra
     */
    public ArticuloCompraId(int idArticulo, int idCompra) {
        this.idArticulo = idArticulo;
        this.idCompra = idCompra;
    }

    // --- Getters y Setters ---

    /**
     * Obtiene el identificador del artículo.
     * @return el ID del artículo
     */
    public int getIdArticulo() {
        return idArticulo;
    }

    /**
     * Establece el identificador del artículo.
     * @param idArticulo el nuevo ID del artículo
     */
    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }

    /**
     * Obtiene el identificador de la compra.
     * @return el ID de la compra
     */
    public int getIdCompra() {
        return idCompra;
    }

    /**
     * Establece el identificador de la compra.
     * @param idCompra el nuevo ID de la compra
     */
    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    // --- equals y hashCode (CRUCIAL para claves compuestas) ---

    /**
     * Compara esta clave compuesta con otra para determinar si representan
     * la misma combinación de artículo y compra.
     * <p>
     * Es fundamental para el correcto funcionamiento de JPA en la gestión de entidades
     * dentro del contexto de persistencia.
     * </p>
     *
     * @param o objeto a comparar
     * @return {@code true} si ambos identificadores son iguales, {@code false} en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ArticuloCompraId that = (ArticuloCompraId) o;
        return idArticulo == that.idArticulo && idCompra == that.idCompra;
    }

    /**
     * Genera un código hash basado en los componentes de la clave.
     * <p>
     * Garantiza una distribución uniforme en colecciones basadas en hash.
     * </p>
     *
     * @return valor hash calculado con {@code idArticulo} e {@code idCompra}
     */
    @Override
    public int hashCode() {
        return Objects.hash(idArticulo, idCompra);
    }
}