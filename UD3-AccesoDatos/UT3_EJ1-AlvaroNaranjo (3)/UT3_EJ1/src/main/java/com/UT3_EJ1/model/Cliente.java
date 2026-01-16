package com.UT3_EJ1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad que representa a un cliente del sistema. Su identificador coincide
 * con el NIF/CIF del cliente.
 * <p>
 * <strong>Relación 1:1 con {@link InformacionFiscal}:</strong>
 * <ul>
 * <li>Cliente es la entidad débil (HIJA) en esta relación.</li>
 * <li>Comparte su clave primaria con InformacionFiscal.</li>
 * <li>Debe persistirse DESPUÉS de InformacionFiscal para evitar errores de
 * integridad.</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Relación 1:N con {@link Compra}:</strong>
 * <ul>
 * <li>Un cliente puede realizar varias compras.</li>
 * <li>Se usa {@code cascade = PERSIST, MERGE} para que al crear o modificar un
 * cliente, sus compras se persistan/actualicen automáticamente.</li>
 * </ul>
 * </p>
 *
 * @author Álvaro Naranjo
 * @version 1.0.0-SNAPSHOT
 * @since 27/11/2025
 */
@Entity
@Table(name = "cliente")
public class Cliente {

	/**
	 * Identificador único del cliente.
	 * <p>
	 * Coincide con el NIF/CIF. Además actúa como clave foránea hacia
	 * {@link InformacionFiscal} debido a la relación derivada mediante
	 * {@code @MapsId}.
	 * </p>
	 */
	@Id
	@Column(name = "nif_cif", length = 20)
	private  String nifCif;

	/**
	 * Nombre completo del cliente.
	 * <p>
	 * Campo obligatorio, longitud máxima 100 caracteres.
	 * </p>
	 */
	@Column(name = "nombre", nullable = false, length = 100)
	private String nombre;

	/**
	 * Correo electrónico del cliente.
	 * <p>
	 * Campo obligatorio y único en el sistema.
	 * </p>
	 */
	@Email(message = "Debe ser un email valido")
	@Column(name = "email", nullable = false, length = 100, unique = true)
	private String email;

	/**
	 * Fecha y hora de registro del cliente en el sistema.
	 * <p>
	 * Campo obligatorio.
	 * </p>
	 */
	@Column(name = "fecha_registro", nullable = false)
	private LocalDateTime fechaRegistro;

	// --- RELACIÓN 1:1 (Lado débil) ---

	/**
	 * Información fiscal asociada al cliente.
	 * <p>
	 * Relación 1:1 donde el Cliente utiliza la misma PK que la Información Fiscal.
	 * </p>
	 */
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@MapsId
	@JoinColumn(name = "nif_cif", referencedColumnName = "nif_cif")
	private InformacionFiscal informacionFiscal;

	// --- RELACIÓN 1:N con Compra ---

	/**
	 * Lista de compras realizadas por el cliente.
	 * <p>
	 * Representa el lado "1" de la relación 1:N.
	 * </p>
	 */
	@OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
	private List<Compra> compras = new ArrayList<>();

	/**
	 * Constructor vacío requerido por la especificación JPA.
	 */
	public Cliente() {
	}

	/**
	 * Crea un cliente con su identificador y sus datos básicos.
	 * <p>
	 * Realiza validaciones para asegurar que el NIF/CIF no sea nulo.
	 * </p>
	 *
	 * @param nifCif        identificador del cliente (NIF/CIF)
	 * @param nombre        nombre completo del cliente
	 * @param email         correo electrónico
	 * @param fechaRegistro fecha y hora de registro
	 * @throws IllegalArgumentException si el nifCif es nulo o vacío
	 */
	public Cliente(String nifCif, String nombre, String email, LocalDateTime fechaRegistro, InformacionFiscal informacionFiscal) {

		// Garantiza que nunca se cree un cliente invalido
		if (nifCif == null || nifCif.isBlank()) {
			throw new IllegalArgumentException("El NIF/CIF no puede ser null o vacío");
		}
		this.nifCif = nifCif.trim(); // PK: asignación directa, inmutable

		// Uso de setters |-> validaciones
		setNombre(nombre);
		setEmail(email);
		setFechaRegistro(fechaRegistro);
		setInformacionFiscal(informacionFiscal);
	}

	// --- Getters y Setters ---

	/**
	 * Obtiene el NIF/CIF del cliente.
	 * 
	 * @return el identificador del cliente
	 */
	public String getNifCif() {
		return nifCif;
	}

	/**
	 * Obtiene el nombre del cliente.
	 * 
	 * @return el nombre completo
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece el nombre del cliente.
	 * <p>
	 * Si el valor es nulo, se establece una cadena vacía.
	 * </p>
	 * 
	 * @param nombre el nuevo nombre
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre != null ? nombre.trim() : "";
	}

	/**
	 * Obtiene el correo electrónico.
	 * 
	 * @return el email del cliente
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Establece el correo electrónico.
	 * <p>
	 * Si el valor es nulo, se establece un numero generado (para evitar errores), evitando
	 * email iguales al ser unique, sin dejarlo en (""). 
	 * </p>
	 * 
	 * @param email el nuevo email
	 */
	public void setEmail(String email) {
		this.email = (email != null && !email.isBlank()) ? email.trim() :  UUID.randomUUID().toString();;
	}

	/**
	 * Obtiene la fecha de registro.
	 * 
	 * @return la fecha y hora de registro
	 */
	public LocalDateTime getFechaRegistro() {
		return fechaRegistro;
	}

	/**
	 * Establece la fecha de registro.
	 * <p>
	 * Si el valor es nulo, se establece la fecha y hora actual.
	 * </p>
	 * 
	 * @param fechaRegistro la nueva fecha de registro
	 */
	public void setFechaRegistro(LocalDateTime fechaRegistro) {
		this.fechaRegistro = fechaRegistro != null ? fechaRegistro : LocalDateTime.now();
	}

	/**
	 * Obtiene la información fiscal asociada.
	 * 
	 * @return la entidad {@link InformacionFiscal}
	 */
	public InformacionFiscal getInformacionFiscal() {
		return informacionFiscal;
	}

	/**
	 * Establece la información fiscal y sincroniza la relación bidireccional.
	 * <p>
	 * Asigna la entidad en este lado y llama al método {@code setCliente} en la
	 * entidad {@link InformacionFiscal} para mantener la coherencia en memoria.
	 * </p>
	 * 
	 * @param informacionFiscal la nueva información fiscal
	 */
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

	/**
	 * Obtiene la lista de compras del cliente como una lista inmodificable.
	 * <p>
	 * Para añadir compras, utilice el método {@link #addCompra(Compra)}.
	 * </p>
	 * 
	 * @return una lista de solo lectura de las compras
	 */
	public List<Compra> getCompras() {
		return Collections.unmodifiableList(compras);
	}

	// --- Métodos Helper ---

	/**
	 * Vincula una compra a este cliente actualizando ambos lados de la relación.
	 * <p>
	 * Añade la compra a la lista interna y establece este cliente como propietario
	 * en la entidad {@link Compra}.
	 * </p>
	 * 
	 * @param compra compra asociada al cliente
	 */
	public void addCompra(Compra compra) {
		if (compra != null && !compras.contains(compra)) {
			compras.add(compra);
			compra.setCliente(this);
		}
	}
	
    // --- CRUD CON VALIDACIONES ---

    /**
     * Crea un nuevo registro de cliente en la base de datos.
     * <p>
     * Requiere que la {@link InformacionFiscal} asociada ya exista en la base de datos.
     * </p>
     *
     * @param cliente Objeto {@link Cliente} a persistir
     * @param em      EntityManager utilizado para la operación
     * @throws RuntimeException         Si ocurre algún error durante la persistencia
     * @throws IllegalArgumentException Si cliente o su info fiscal son null
     */
    public static void crear(Cliente cliente, EntityManager em) {
        validarClienteNoNull(cliente);
        validarEntityManager(em);

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(cliente); // Se asume que infoFiscal ya existe
            tx.commit();
        }
        catch (PersistenceException e) {
            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                System.err.println("Ya existe un registro con ese NIF/CIF: " + cliente.getNifCif());
            } else {
                e.printStackTrace();
            }
        }catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    /**
     * Obtiene un cliente por su NIF/CIF.
     *
     * @param nifCif NIF/CIF del cliente
     * @param em     EntityManager utilizado para la operación
     * @return Cliente si existe, null si no se encuentra
     */
    public static Cliente selectCliente(String nifCif, EntityManager em) {
        validarNifCif(nifCif);
        validarEntityManager(em);
        return em.find(Cliente.class, nifCif);
    }

    /**
     * Recupera todos los clientes de la base de datos.
     *
     * @param em EntityManager utilizado para la operación
     * @return Lista de clientes, vacía si no hay registros
     */
    public List<Cliente> obtenerTodos(EntityManager em) {
        validarEntityManager(em);
        return em.createQuery("SELECT c FROM Cliente c", Cliente.class).getResultList();
    }

  

    /**
     * Elimina un cliente según su NIF/CIF.
     * <p>
     * Esto NO eliminará la información fiscal asociada si la relación está definida
     * con {@code CascadeType.ALL} únicamente en Cliente (depende de tu entidad).
     * </p>
     *
     * @param nifCif NIF/CIF del cliente a eliminar
     * @param em     EntityManager utilizado para la operación
     * @throws RuntimeException         Si ocurre algún error durante la eliminación
     * @throws IllegalArgumentException Si nifCif es null o vacío
     */
    public static void eliminar(String nifCif, EntityManager em) {
        validarNifCif(nifCif);
        validarEntityManager(em);

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Cliente cliente = em.find(Cliente.class, nifCif);
            if (cliente != null) {
                em.remove(cliente);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    // --- MÉTODOS DE VALIDACIÓN ---

    private static void validarClienteNoNull(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser null");
        }
        if (cliente.getNifCif() == null || cliente.getNifCif().isBlank()) {
            throw new IllegalArgumentException("El NIF/CIF del cliente no puede ser null o vacío");
        }
        if (cliente.getInformacionFiscal() == null) {
            throw new IllegalArgumentException("El cliente debe tener información fiscal asociada");
        }
    }

    private static void validarNifCif(String nifCif) {
        if (nifCif == null || nifCif.isBlank()) {
            throw new IllegalArgumentException("El NIF/CIF no puede ser null o vacío");
        }
    }

    private static void validarEntityManager(EntityManager em) {
        if (em == null) {
            throw new IllegalArgumentException("El EntityManager no puede ser null");
        }
    }
    

	/**
	 * Retorna una representación en cadena del cliente.
	 * 
	 * @return una cadena con los datos principales (NIF, nombre, email)
	 */
	@Override
	public String toString() {
		return "Cliente{nifCif='" + nifCif + "', nombre='" + nombre + "', email='" + email + "'}";
	}

	/**
	 * Compara este cliente con otro objeto para determinar igualdad.
	 * <p>
	 * La igualdad se basa exclusivamente en el identificador (NIF/CIF).
	 * </p>
	 * 
	 * @param o objeto a comparar
	 * @return {@code true} si tienen el mismo NIF/CIF, {@code false} en caso
	 *         contrario
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Cliente cliente = (Cliente) o;
		return Objects.equals(nifCif, cliente.nifCif);
	}

	/**
	 * Genera el código hash del cliente.
	 * <p>
	 * Basado exclusivamente en el identificador (NIF/CIF).
	 * </p>
	 * 
	 * @return el valor hash
	 */
	@Override
	public int hashCode() {
		return Objects.hash(nifCif);
	}
}