package model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Representa la clave primaria compuesta de la entidad {@link ArticuloCompra}.
 * Esta clase se marca como {@link Embeddable} para ser utilizada dentro de otra
 * entidad mediante {@link jakarta.persistence.EmbeddedId}.
 *
 *
 *
 * @author Álvaro Naranjo
 */
@Embeddable
public class ArticuloCompraId implements Serializable {

	private static final long serialVersionUID = 3346885694770211343L;

	/**
     * Identificador del artículo en la relación N:M.
     * Corresponde con la columna id_articulo en la base de datos.
     */
    @Column(name = "id_articulo")
    private int idArticulo;

    /**
     * Identificador de la compra en la relación N:M.
     * Corresponde con la columna id_compra en la base de datos.
     */
    @Column(name = "id_compra")
    private int idCompra;

    /**
     * Constructor por defecto obligatorio para JPA.
     */
    public ArticuloCompraId() {
    }

    /**
     * Constructor que permite crear un identificador compuesto asignando
     * directamente los valores de idArticulo e idCompra.
     *
     * @param idArticulo identificador del artículo
     * @param idCompra   identificador de la compra
     */
    public ArticuloCompraId(int idArticulo, int idCompra) {
        this.idArticulo = idArticulo;
        this.idCompra = idCompra;
    }

    // --- Getters y Setters ---

    public int getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    // --- equals y hashCode (CRUCIAL para claves compuestas) ---

    /**
     * Compara esta clave compuesta con otra para determinar si representan
     * la misma combinación de artículo y compra. 
     *
     * @param o objeto a comparar
     * @return true si ambos identificadores son iguales, false en caso contrario
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
     *
     * @return valor hash calculado con los campos de la clave
     */
    @Override
    public int hashCode() {
        return Objects.hash(idArticulo, idCompra);
    }
}
