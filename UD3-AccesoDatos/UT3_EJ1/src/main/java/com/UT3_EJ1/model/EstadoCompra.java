package com.UT3_EJ1.model;

/**
 * Enumeración que representa los posibles estados en los que se puede encontrar una compra.
 * <p>
 * Los estados siguen el ciclo de vida natural de un pedido:
 * <ul>
 * <li>{@link #PENDIENTE}: La compra ha sido creada pero no procesada.</li>
 * <li>{@link #ENVIADO}: El pedido ha salido del almacén.</li>
 * <li>{@link #ENTREGADO}: El cliente ha recibido el pedido.</li>
 * <li>{@link #ELIMINADO}: Estado especial para indicar que la compra ha sido "borrada"</li>
 * </ul>
 * </p>
 *
 * @author Álvaro Naranjo
 * @version 1.0.0-SNAPSHOT
 * @since 27/11/2025
 */
public enum EstadoCompra {
    
    /**
     * El pedido ha sido registrado pero aún no se ha enviado.
     */
    PENDIENTE("PENDIENTE"),
    
    /**
     * El pedido ha sido procesado y enviado al cliente.
     */
    ENVIADO("ENVIADO"),
    
    /**
     * El cliente ha recibido el pedido correctamente.
     */
    ENTREGADO("ENTREGADO"),
    
    /**
     * Estado que indica que la compra ha sido marcada como eliminada o anonimizada.
     * <p>Utilizado principalmente por triggers de base de datos al eliminar un cliente.</p>
     */
    ELIMINADO("ELIMINADO");
    
    /**
     * Valor textual del estado almacenado internamente.
     */
    private final String estado;
    
    /**
     * Constructor privado para los valores del enum.
     * @param estado cadena de texto representativa del estado
     */
    EstadoCompra(String estado) {
        this.estado = estado;
    }
    
    /**
     * Obtiene el valor textual del estado.
     * @return la cadena que representa el estado
     */
    public String getValue() {
        return estado;
    }
}