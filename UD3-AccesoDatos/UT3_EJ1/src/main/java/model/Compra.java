package model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa una orden de compra realizada por un cliente.
 */
@Entity
@Table(name = "compra")
public class Compra {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "fecha_realizada", nullable = false)
	private LocalDateTime fechaRealizada;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado", nullable = false, length = 20)
	private EstadoCompra estado;

	@Column(name = "direccion", nullable = false)
	private String direccion;

	@Column(name = "precio_total", nullable = false, precision = 10, scale = 2)
	private BigDecimal precioTotal;

	// --- RELACIÓN N:1 con Cliente ---
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cliente_nif_cif", nullable = false)
	private Cliente cliente;

	// --- RELACIÓN 1:N con ArticuloCompra ---
	@OneToMany(mappedBy = "compra", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ArticuloCompra> articulosCompra = new HashSet<>();

	// --- Constructores ---
	public Compra() {
	}

	/**
	 * Constructor: evita nulls, valida relación con Cliente
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
	public int getId() {
		return id;
	}

	// No  setter de ID si es autogenerado
	// public void setId(int id) { this.id = id; }

	public LocalDateTime getFechaRealizada() {
		return fechaRealizada;
	}

	public void setFechaRealizada(LocalDateTime fechaRealizada) {
		this.fechaRealizada = (fechaRealizada != null) ? fechaRealizada : LocalDateTime.now();
	}

	public EstadoCompra getEstado() {
		return estado;
	}

	public void setEstado(EstadoCompra estado) {
		this.estado = (estado != null) ? estado : EstadoCompra.PENDIENTE; // valor por defecto
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = (direccion != null && !direccion.isBlank()) ? direccion.trim() : "";
	}

	public BigDecimal getPrecioTotal() {
		return precioTotal;
	}

	public void setPrecioTotal(BigDecimal precioTotal) {
		this.precioTotal = (precioTotal != null) ? precioTotal : BigDecimal.ZERO;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		if (cliente == null) {
			throw new IllegalArgumentException("Cliente no puede ser null");
		}
		this.cliente = cliente;

		// Coherencia bidireccional: añade esta compra al cliente si no está ya
		if (!cliente.getCompras().contains(this)) {
			cliente.addCompra(this); 
		}
	}

	public Set<ArticuloCompra> getArticulosCompra() {
		return articulosCompra;
	}

	public void setArticulosCompra(Set<ArticuloCompra> articulosCompra) {
		if (articulosCompra != null) {
			this.articulosCompra = articulosCompra;
		}
	}

	// --- Métodos Helper ---
	public void addArticuloCompra(ArticuloCompra articuloCompra) {
		if (articuloCompra == null)
			return;
		articulosCompra.add(articuloCompra);
		if (articuloCompra.getCompra() != this) {
			articuloCompra.setCompra(this);
		}
	}

	public void removeArticuloCompra(ArticuloCompra articuloCompra) {
		if (articuloCompra == null)
			return;
		articulosCompra.remove(articuloCompra);
		if (articuloCompra.getCompra() == this) {
			articuloCompra.setCompra(null);
		}
	}

	@Override
	public String toString() {
		return "Compra{id=" + id + ", fechaRealizada=" + fechaRealizada + ", estado=" + estado + ", direccion='"
				+ direccion + '\'' + ", precioTotal=" + precioTotal + '}';
	}
}
