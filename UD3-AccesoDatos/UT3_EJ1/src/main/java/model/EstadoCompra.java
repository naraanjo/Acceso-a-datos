package model;

public enum EstadoCompra {
    PENDIENTE("PENDIENTE"),
    ENVIADO("ENVIADO"),
    ENTREGADO("ENTREGADO"),
    ELIMINADO("ELIMINADO");
    
    private final String estado;
    
    EstadoCompra(String estado) {
        this.estado = estado;
    }
    
    public String getValue() {
        return estado;
    }
}