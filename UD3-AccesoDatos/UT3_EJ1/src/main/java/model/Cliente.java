package model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidad que representa a un cliente del sistema. Su identificador coincide
 * con el NIF/CIF del cliente.
 *
 * Relación 1:1 con {@link InformacionFiscal}
 * 
 * Cliente es la entidad débil (HIJA) en esta relación Comparte su clave
 * primaria con InformacionFiscal Debe persistirse DESPUÉS de InformacionFiscal
 * para evitar errores
 * 
 *
 * Relación 1:N con {@link Compra}:
 * 
 * Un cliente puede realizar varias compras Se usa
 * {@code cascade = PERSIST, MERGE} para que al crear o modificar un cliente,
 * sus compras se persistan/actualicen automáticamente
 * 
 *
 * @author Álvaro Naranjo
 */
@Entity
@Table(name = "cliente")
public class Cliente {

	/**
	 * Identificador del cliente. Coincide con el NIF/CIF del cliente. Además actúa
	 * como clave foránea hacia {@link InformacionFiscal}.
	 */
	@Id
	@Column(name = "nif_cif", length = 20)
	private String nifCif;

	/** Nombre completo del cliente. No puede ser nulo. */
	@Column(name = "nombre", nullable = false, length = 100)
	private String nombre;

	/** Correo electrónico del cliente. No puede ser nulo. */
	@Column(name = "email", nullable = false, length = 100)
	private String email;

	/** Fecha de registro del cliente en el sistema. */
	@Column(name = "fecha_registro", nullable = false)
	private LocalDate fechaRegistro;

	// --- RELACIÓN 1:1 (Lado débil) ---
	/**
	 * Relación 1:1 con la entidad {@link InformacionFiscal}. Usa la misma columna
	 * para PK y FK.
	 */
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@MapsId
	@JoinColumn(name = "nif_cif", referencedColumnName = "nif_cif")
	private InformacionFiscal informacionFiscal;

	// --- RELACIÓN 1:N con Compra ---
	/**
	 * Lista de compras realizadas por el cliente. Representa el lado "1" de la
	 * relación 1:N.
	 */
	@OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
	private List<Compra> compras = new ArrayList<>();

	/** Constructor vacío */
	public Cliente() {
	}

	/**
	 * Crea un cliente con su identificador y sus datos básicos.
	 *
	 * @param nifCif        identificador del cliente
	 * @param nombre        nombre del cliente
	 * @param email         correo electrónico
	 * @param fechaRegistro fecha de registro
	 */
	public Cliente(String nifCif, String nombre, String email, LocalDate fechaRegistro) {
		
		// Garantiza que nunca se cree un cliente invalido
	    if (nifCif == null || nifCif.isBlank()) {
	        throw new IllegalArgumentException("El NIF/CIF no puede ser null o vacío");
	    }
	    this.nifCif = nifCif.trim();  // PK: asignación directa, inmutable

	    //Uso de setters |-> validaciones
	    setNombre(nombre);
	    setEmail(email);
	    setFechaRegistro(fechaRegistro);
	    
	}

	// --- Getters y Setters ---
	public String getNifCif() {
		return nifCif;
	}



	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre != null ? nombre.trim() : "";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email != null ? email.trim() : "";
	}

	public LocalDate getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(LocalDate fechaRegistro) {
		this.fechaRegistro = fechaRegistro != null ? fechaRegistro : LocalDate.now();
	}

	public InformacionFiscal getInformacionFiscal() {
		return informacionFiscal;
	}

	public void setInformacionFiscal(InformacionFiscal informacionFiscal) {
		// 1. Asignamos la relación en este lado
		this.informacionFiscal = informacionFiscal;

		// 2. Sincronizamos el OTRO lado de la relación
		// Solo si el objeto no es nulo y aún no nos tiene asignados (para evitar bucle
		// infinito)
		if (informacionFiscal != null && informacionFiscal.getCliente() != this) {
			informacionFiscal.setCliente(this);
		}
	}

	// Evito modificaciones del exterior
	public List<Compra> getCompras() {
	    return Collections.unmodifiableList(compras);
	}

	public void setCompras(List<Compra> compras) {
		this.compras = compras;
	}

	// --- Métodos Helper ---

	/**
	 * Vincula una compra a este cliente actualizando ambos lados de la relación.
	 *
	 * @param compra compra asociada al cliente
	 */
	public void addCompra(Compra compra) {
		compras.add(compra);
		compra.setCliente(this);
	}

	@Override
	public String toString() {
		return "Cliente{nifCif='" + nifCif + "', nombre='" + nombre + "', email='" + email + "'}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Cliente cliente = (Cliente) o;
		return Objects.equals(nifCif, cliente.nifCif);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nifCif);
	}
}
