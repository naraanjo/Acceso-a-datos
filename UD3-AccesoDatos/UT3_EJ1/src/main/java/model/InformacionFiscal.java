package model;

import jakarta.persistence.*;

/**
 * Representa la información fiscal de un cliente. Esta entidad es la entidad
 * padre en una relación 1:1 con {@link Cliente}. Debe ser persistida antes que
 * la entidad {@link Cliente}.
 *
 * Contiene datos como el NIF/CIF, dirección fiscal y teléfono.
 * 
 * @author Álvaro Naranjo
 */
@Entity
@Table(name = "informacion_fiscal")
public class InformacionFiscal {

	/**
	 * NIF o CIF del cliente. Es la clave primaria de esta entidad y se utiliza para
	 * identificar de manera única la información fiscal de un cliente.
	 * 
	 */
	@Id
	@Column(name = "nif_cif", length = 20)
	private String nifCif;

	/**
	 * Dirección fiscal del cliente. Campo obligatorio.
	 * 
	 */
	@Column(name = "direccion_fiscal", nullable = false)
	private String direccionFiscal;

	/**
	 * Teléfono de contacto del cliente.
	 * 
	 */
	@Column(name = "telefono", nullable = false, length = 20)
	private String telefono;

	/**
	 * Relación uno a uno con la entidad {@link Cliente}. Se utiliza carga perezosa
	 * 
	 */
	@OneToOne(mappedBy = "informacionFiscal", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	private Cliente cliente;

	/**
	 * Constructor por defecto.
	 */
	public InformacionFiscal() {
	}

	/**
	 * Constructor con parámetros.
	 *
	 * @param nifCif          NIF o CIF del cliente.
	 * @param direccionFiscal Dirección fiscal del cliente.
	 * @param telefono        Teléfono de contacto del cliente.
	 */
	public InformacionFiscal(String nifCif, String direccionFiscal, String telefono) {
		this.nifCif = nifCif;
		this.direccionFiscal = direccionFiscal;
		this.telefono = telefono;
	}

	// --- Getters y Setters ---

	/**
	 * Obtiene el NIF/CIF del cliente.
	 *
	 * @return NIF/CIF
	 */
	public String getNifCif() {
		return nifCif;
	}



	/**
	 * Obtiene la dirección fiscal del cliente.
	 *
	 * @return Dirección fiscal
	 */
	public String getDireccionFiscal() {
		return direccionFiscal;
	}

	/**
	 * Establece la dirección fiscal del cliente.
	 *
	 * @param direccionFiscal Dirección fiscal
	 */
	public void setDireccionFiscal(String direccionFiscal) {
		this.direccionFiscal = direccionFiscal != null ? direccionFiscal.trim() : "";
	}

	/**
	 * Obtiene el teléfono de contacto del cliente.
	 *
	 * @return Teléfono
	 */
	public String getTelefono() {
		return telefono;
	}

	/**
	 * Establece el teléfono de contacto del cliente.
	 *
	 * @param telefono Teléfono
	 */
	public void setTelefono(String telefono) {
		this.telefono = telefono != null ? telefono.trim() : "";
	}

	/**
	 * Obtiene el cliente asociado a esta información fiscal.
	 *
	 * @return Cliente
	 */
	public Cliente getCliente() {
		return cliente;
	}

	/**
	 * Establece el cliente asociado a esta información fiscal.
	 *
	 * @param cliente Cliente
	 */
	public void setCliente(Cliente cliente) {
		if (cliente == null)
			throw new IllegalArgumentException("Cliente no puede ser null"); // No permitimos null

		this.cliente = cliente;

		// Sincronizamos el otro lado de la relación
		if (cliente.getInformacionFiscal() != this) {
			cliente.setInformacionFiscal(this);
		}

	}

	/**
	 * Representación en forma de cadena de la información fiscal.
	 *
	 * @return String con NIF/CIF, dirección fiscal y teléfono
	 */
	@Override
	public String toString() {
		return "InformacionFiscal{nifCif='" + nifCif + "', direccionFiscal='" + direccionFiscal + "', telefono='"
				+ telefono + "'}";
	}
}
