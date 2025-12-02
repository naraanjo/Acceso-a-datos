package com.UT3_EJ1.model;

import java.util.List;

import jakarta.persistence.*;

/**
 * Representa la información fiscal asociada a un cliente.
 * <p>
 * Esta entidad actúa como la entidad "padre" o fuerte en la relación 1:1 con
 * {@link Cliente}. Sin embargo, a nivel de negocio, es inseparable del cliente.
 * Contiene los datos legales necesarios para la facturación.
 * </p>
 * <p>
 * <strong>Relación 1:1 con {@link Cliente}:</strong>
 * <ul>
 * <li>La relación es bidireccional.</li>
 * <li>Esta entidad debe persistirse antes que la entidad {@link Cliente} si se
 * gestionan manualmente, aunque el {@code CascadeType} en Cliente suele
 * facilitar esto.</li>
 * </ul>
 * </p>
 *
 * @author Álvaro Naranjo
 * @version 1.0.0-SNAPSHOT
 * @since 27/11/2025
 */
@Entity
@Table(name = "informacion_fiscal")
public class InformacionFiscal {

	/**
	 * NIF o CIF del cliente.
	 * <p>
	 * Es la clave primaria de esta entidad y se utiliza para identificar de manera
	 * única la información fiscal. No es autogenerada, debe ser suministrada
	 * externamente.
	 * </p>
	 */
	@Id
	@Column(name = "nif_cif", length = 20)
	private String nifCif;

	/**
	 * Dirección fiscal completa del cliente.
	 * <p>
	 * Campo obligatorio para la facturación.
	 * </p>
	 */
	@Column(name = "direccion_fiscal", nullable = false)
	private String direccionFiscal;

	/**
	 * Teléfono de contacto principal.
	 * <p>
	 * Campo obligatorio, longitud máxima 20 caracteres.
	 * </p>
	 */
	@Column(name = "telefono", nullable = false, length = 20)
	private String telefono;

	/**
	 * Cliente asociado a esta información fiscal.
	 * <p>
	 * Relación uno a uno. Se utiliza carga perezosa (LAZY) para no recuperar los
	 * datos del cliente a menos que sea necesario. Las operaciones de persistencia
	 * y fusión se propagan.
	 * </p>
	 */
	@OneToOne(mappedBy = "informacionFiscal", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE, CascadeType.REMOVE })
	private Cliente cliente;

	/**
	 * Constructor vacío requerido por la especificación JPA.
	 */
	public InformacionFiscal() {
	}

	/**
	 * Crea una nueva instancia de información fiscal con los datos obligatorios.
	 *
	 * @param nifCif          NIF o CIF del cliente (Clave Primaria)
	 * @param direccionFiscal Dirección fiscal completa
	 * @param telefono        Teléfono de contacto
	 */
	public InformacionFiscal(String nifCif, String direccionFiscal, String telefono) {
		this.nifCif = nifCif;
		setDireccionFiscal(direccionFiscal);
		setTelefono(telefono);
	}

	// --- Getters y Setters ---

	/**
	 * Obtiene el NIF/CIF del cliente.
	 * 
	 * @return el NIF o CIF
	 */
	public String getNifCif() {
		return nifCif;
	}

	// Nota: No se suele incluir un setter para la PK si esta no debe cambiar,

	/**
	 * Obtiene la dirección fiscal.
	 * 
	 * @return la dirección fiscal
	 */
	public String getDireccionFiscal() {
		return direccionFiscal;
	}

	/**
	 * Establece la dirección fiscal del cliente.
	 * <p>
	 * Si el valor es nulo, se establece una cadena vacía para evitar nulos en BD.
	 * </p>
	 * 
	 * @param direccionFiscal la nueva dirección fiscal
	 */
	public void setDireccionFiscal(String direccionFiscal) {
		this.direccionFiscal = direccionFiscal != null ? direccionFiscal.trim() : "";
	}

	/**
	 * Obtiene el teléfono de contacto.
	 * 
	 * @return el teléfono
	 */
	public String getTelefono() {
		return telefono;
	}

	/**
	 * Establece el teléfono de contacto.
	 * <p>
	 * Valida que sea un teléfono válido: opcional '+' al inicio, solo dígitos al
	 * final, longitud entre 9 y 15 dígitos. Los caracteres no numéricos se
	 * eliminan.
	 * </p>
	 *
	 * @param telefono el nuevo teléfono
	 * @throws IllegalArgumentException si el teléfono no cumple el formato
	 */
	public void setTelefono(String telefono) {
		if (telefono == null) {
			throw new IllegalArgumentException("El teléfono no puede ser null");
		}

		// Eliminamos espacios al inicio y final
		String t = telefono.trim();

		// Permitimos '+' al inicio
		boolean tieneMas = t.startsWith("+");

		// Eliminamos todos los caracteres que no sean dígitos
		t = t.replaceAll("\\D", "");

		// Reconstruimos con '+' si estaba al inicio
		if (tieneMas) {
			t = "+" + t;
		}

		// Longitud válida (sin contar '+')
		int lengthSinMas = tieneMas ? t.length() - 1 : t.length();
		if (lengthSinMas < 9 || lengthSinMas > 15) {
			throw new IllegalArgumentException("El teléfono debe tener entre 9 y 15 dígitos (sin contar '+')");
		}

		this.telefono = t;
	}

	/**
	 * Obtiene el cliente asociado a esta información fiscal.
	 * 
	 * @return la entidad Cliente asociada
	 */
	public Cliente getCliente() {
		return cliente;
	}

	/**
	 * Establece el cliente asociado y sincroniza la relación bidireccional.
	 * <p>
	 * Este método asegura que ambos lados de la relación (InfoFiscal y Cliente)
	 * apunten el uno al otro correctamente en memoria.
	 * </p>
	 *
	 * @param cliente el cliente a asociar
	 * @throws IllegalArgumentException si el cliente es null
	 */
	public void setCliente(Cliente cliente) {
		if (cliente == null)
			throw new IllegalArgumentException("Cliente no puede ser null"); // No permitimos null

		this.cliente = cliente;

		// Sincronizamos el otro lado de la relación para mantener la coherencia
		if (cliente.getInformacionFiscal() != this) {
			cliente.setInformacionFiscal(this);
		}

	}

	/**
	 * Retorna una representación en cadena de la información fiscal.
	 * 
	 * @return cadena con los datos fiscales básicos
	 */
	@Override
	public String toString() {
		return "InformacionFiscal{nifCif='" + nifCif + "', direccionFiscal='" + direccionFiscal + "', telefono='"
				+ telefono + "'}";
	}

	// OPERACIONES CRUD

	/**
	 * Valida que el EntityManager no sea null.
	 *
	 * @param em EntityManager a validar
	 * @throws IllegalArgumentException si es null
	 */
	private static void validarEntityManager(EntityManager em) {
		if (em == null) {
			throw new IllegalArgumentException("El EntityManager no puede ser null");
		}
	}

	// --- CRUD CON VALIDACIONES ---



	public static InformacionFiscal selectClienteCompleto(String nifCif, EntityManager em) {
	    validarNifCif(nifCif);
	    validarEntityManager(em);

	    try {
	        return em.createQuery(
	            "SELECT i FROM InformacionFiscal i " +
	            "JOIN FETCH i.cliente c " +
	            "LEFT JOIN FETCH c.compras " +
	            "WHERE i.nifCif = :nifCif", InformacionFiscal.class)
	            .setParameter("nifCif", nifCif)
	            .getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
	}


	public static List<InformacionFiscal> obtenerTodos(EntityManager em) {
		validarEntityManager(em);
		return em.createQuery("SELECT i FROM InformacionFiscal i", InformacionFiscal.class).getResultList();
	}

	public static void actualizar(InformacionFiscal infoFiscal, EntityManager em) {
		validarInfoFiscalNoNull(infoFiscal);
		validarEntityManager(em);

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.merge(infoFiscal);
			tx.commit();
		} catch (Exception e) {
			if (tx.isActive())
				tx.rollback();
			throw e;
		}
	}

	public static void eliminar(String nifCif, EntityManager em) {
		validarNifCif(nifCif);
		validarEntityManager(em);

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			InformacionFiscal infoFiscal = em.find(InformacionFiscal.class, nifCif);
			if (infoFiscal != null) {
				em.remove(infoFiscal);
			}
			tx.commit();
		} catch (Exception e) {
			if (tx.isActive())
				tx.rollback();
			throw e;
		}
	}

	/**
	 * Valida que el NIF/CIF proporcionado no sea null ni vacío.
	 *
	 * @param nifCif NIF/CIF a validar
	 * @throws IllegalArgumentException si es null o vacío
	 */
	private static void validarNifCif(String nifCif) {
		if (nifCif == null || nifCif.isBlank()) {
			throw new IllegalArgumentException("El NIF/CIF no puede ser null o vacío");
		}
	}

	/**
	 * Valida que el objeto {@link InformacionFiscal} no sea null y tenga NIF/CIF.
	 *
	 * @param infoFiscal Objeto a validar
	 * @throws IllegalArgumentException si es null o nifCif es null o vacío
	 */
	private static void validarInfoFiscalNoNull(InformacionFiscal infoFiscal) {
		if (infoFiscal == null) {
			throw new IllegalArgumentException("La información fiscal no puede ser null");
		}
		if (infoFiscal.getNifCif() == null || infoFiscal.getNifCif().isBlank()) {
			throw new IllegalArgumentException("El NIF/CIF no puede ser null o vacío");
		}
	}
}