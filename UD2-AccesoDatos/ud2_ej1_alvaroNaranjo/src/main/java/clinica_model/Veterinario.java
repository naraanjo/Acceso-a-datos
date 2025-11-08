package clinica_model;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map; 

import clinica_persistence.CertificacionPersistence;

/**
 * Clase Modelo Veterinario UNIFICADA.
 * Contiene los atributos del Veterinario (Tabla principal)
 * y del DetalleContrato (Relación 1:1).
 * <p>
 * Relación 1:N entre Veterinario y Certificación — cada veterinario puede tener muchas certificaciones.
 * </p>
 */
public class Veterinario implements Iterable<Certificacion> {

	private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	// --- ATRIBUTOS DE LA TABLA Veterinario---
	private int num_licencia;
	private String nombre;
	private String apellido;
	private String fecha_contratacion; // String (YYYY-MM-DD) validado.

	// --- ATRIBUTOS DE LA TABLA DetalleContrato ---
	private int contratoId;
	private double salarioBase;
	private double horarioSemanal; // Horas trabajadas a la semana

	// --- RELACIÓN 1:N CON CERTIFICACION ---
	private HashMap<Integer, Certificacion> certificacionesMap;

	/**
	 * Constructor vacío.
	 * Inicializa los atributos con valores por defecto y el mapa de certificaciones vacío.
	 */
	public Veterinario() {
		this.num_licencia = 0;
		this.nombre = "";
		this.apellido = "";
		this.fecha_contratacion = "";

		this.salarioBase = 0.0;
		this.horarioSemanal = 0.0;

		this.certificacionesMap = new HashMap<>();
	}

	/**
	 * Constructor completo del Veterinario.
	 *
	 * @param num_licencia        Número de licencia del veterinario.
	 * @param nombre              Nombre del veterinario.
	 * @param apellido            Apellido del veterinario.
	 * @param fecha_contratacion  Fecha de contratación (formato YYYY-MM-DD).
	 * @param contratoId          Identificador del contrato.
	 * @param salarioBase         Salario base del veterinario.
	 * @param horarioSemanal      Horas trabajadas a la semana.
	 */
	public Veterinario(int num_licencia, String nombre, String apellido, String fecha_contratacion, int contratoId,
			double salarioBase, double horarioSemanal) {

		this.num_licencia = (num_licencia > 0) ? num_licencia : 0;
		this.nombre = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : "";
		this.apellido = (apellido != null && !apellido.trim().isEmpty()) ? apellido.trim() : "";
		setFecha_contratacion(fecha_contratacion);

		this.contratoId = (contratoId >= 0) ? contratoId : 0;
		this.salarioBase = (salarioBase >= 0.0) ? salarioBase : 0.0;
		this.horarioSemanal = (horarioSemanal > 0 && horarioSemanal <= 60) ? horarioSemanal : 0;

		this.certificacionesMap = new HashMap<>();
	}

	/**
	 * Obtiene el número de licencia del veterinario.
	 * @return Número de licencia.
	 */
	public int getNum_licencia() {
		return num_licencia;
	}

	/**
	 * Establece el número de licencia del veterinario.
	 * @param num_licencia Número de licencia (debe ser mayor que 0).
	 */
	public void setNum_licencia(int num_licencia) {
		this.num_licencia = (num_licencia > 0) ? num_licencia : 0;
	}

	/**
	 * Obtiene el nombre del veterinario.
	 * @return Nombre del veterinario.
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Establece el nombre del veterinario.
	 * @param nombre Nombre del veterinario (no debe ser nulo ni vacío).
	 */
	public void setNombre(String nombre) {
		this.nombre = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : "";
	}

	/**
	 * Obtiene el apellido del veterinario.
	 * @return Apellido del veterinario.
	 */
	public String getApellido() {
		return apellido;
	}

	/**
	 * Establece el apellido del veterinario.
	 * @param apellido Apellido del veterinario (no debe ser nulo ni vacío).
	 */
	public void setApellido(String apellido) {
		this.apellido = (apellido != null && !apellido.trim().isEmpty()) ? apellido.trim() : "";
	}

	/**
	 * Obtiene la fecha de contratación.
	 * @return Fecha de contratación (formato YYYY-MM-DD).
	 */
	public String getFecha_contratacion() {
		return fecha_contratacion;
	}

	/**
	 * Establece la fecha de contratación.
	 * @param fecha_contratacion Fecha de contratación (se valida el formato y la validez lógica).
	 */
	public void setFecha_contratacion(String fecha_contratacion) {
		this.fecha_contratacion = validateAndFormatDate(fecha_contratacion);
	}

	/**
	 * Valida si la cadena de fecha cumple con el formato YYYY-MM-DD y es lógicamente válida.
	 * Si es válida, devuelve la cadena formateada; de lo contrario, devuelve cadena vacía.
	 *
	 * @param fecha_contratacion_str Cadena de fecha a validar.
	 * @return Fecha validada o cadena vacía si es inválida.
	 */
	private String validateAndFormatDate(String fecha_contratacion_str) {
		if (fecha_contratacion_str == null || fecha_contratacion_str.trim().isEmpty()) {
			return "";
		}

		try {
			LocalDate date = LocalDate.parse(fecha_contratacion_str.trim(), ISO_DATE_FORMATTER);
			return date.format(ISO_DATE_FORMATTER);
		} catch (DateTimeParseException e) {
			return "";
		}
	}

	/**
	 * Obtiene el ID del contrato.
	 * @return Identificador del contrato.
	 */
	public int getContratoId() {
		return contratoId;
	}

	/**
	 * Establece el ID del contrato.
	 * @param contratoId Identificador del contrato (debe ser mayor o igual a 0).
	 */
	public void setContratoId(int contratoId) {
		this.contratoId = (contratoId >= 0) ? contratoId : 0;
	}

	/**
	 * Obtiene el salario base del veterinario.
	 * @return Salario base.
	 */
	public double getSalarioBase() {
		return salarioBase;
	}

	/**
	 * Establece el salario base del veterinario.
	 * @param salarioBase Salario base (debe ser mayor o igual a 0).
	 */
	public void setSalarioBase(double salarioBase) {
		this.salarioBase = (salarioBase >= 0.0) ? salarioBase : 0.0;
	}

	/**
	 * Obtiene el horario semanal (horas trabajadas por semana).
	 * @return Horas trabajadas a la semana.
	 */
	public double getHorarioSemanal() {
		return horarioSemanal;
	}

	/**
	 * Establece el horario semanal.
	 * @param horarioSemanal Horas trabajadas por semana (entre 1 y 60 horas).
	 */
	public void setHorarioSemanal(double horarioSemanal) {
		this.horarioSemanal = (horarioSemanal > 0 && horarioSemanal <= 60) ? horarioSemanal : 0;
	}

	// Relación 1:N

	/**
	 * Añade una certificación al veterinario por su ID.
	 * @param certificacionId ID de la certificación a añadir.
	 */
	public void addCertificacion(Integer certificacionId) {
		this.certificacionesMap.put(certificacionId, null);
	}

	/**
	 * Añade múltiples certificaciones por sus IDs.
	 * @param certificacionesIds Lista de IDs de certificaciones.
	 */
	public void addCertificaciones(Integer... certificacionesIds) {
		for (Integer cId : certificacionesIds) {
			this.certificacionesMap.put(cId, null);
		}
	}

	/**
	 * Añade una colección de IDs de certificaciones.
	 * @param certificacionesIds Colección de IDs de certificaciones.
	 */
	public void addCertificaciones(Collection<Integer> certificacionesIds) {
		for (Integer cId : certificacionesIds) {
			this.certificacionesMap.put(cId, null);
		}
	}

	/**
	 * Elimina una certificación del veterinario por su ID.
	 * @param certificacionId ID de la certificación a eliminar.
	 */
	public void removeCertificacion(Integer certificacionId) {
		this.certificacionesMap.remove(certificacionId);
	}

	/**
	 * Elimina múltiples certificaciones por sus IDs.
	 * @param certificacionesIds Colección de IDs de certificaciones a eliminar.
	 */
	public void removeCertificaciones(Collection<Integer> certificacionesIds) {
		for (Integer cId : certificacionesIds) {
			this.certificacionesMap.remove(cId);
		}
	}

	/**
	 * Elimina todas las certificaciones del veterinario.
	 */
	public void clearCertificaciones() {
		this.certificacionesMap.clear();
	}

	/**
	 * Obtiene el número total de certificaciones asociadas al veterinario.
	 * @return Número de certificaciones.
	 */
	public int getNumeroCertificaciones() {
		return this.certificacionesMap.size();
	}

	/**
	 * Obtiene la colección de objetos Certificación almacenados en el HashMap.
	 * @return Colección de objetos {@link Certificacion}.
	 */
	public Collection<Certificacion> getCertificacionesMapValues() {
		return this.certificacionesMap.values();
	}

	/**
	 * Obtiene el HashMap completo de las certificaciones.
	 * @return Mapa con los IDs como claves y objetos {@link Certificacion} como valores.
	 */
	public Map<Integer, Certificacion> getCertificacionesMap() {
	    return this.certificacionesMap;
	}

	/**
	 * Reindexa una certificación en el HashMap, reemplazando una entrada temporal (ID 0)
	 * con su nuevo ID autogenerado.
	 *
	 * @param idTemporal ID temporal (debe ser 0).
	 * @param certificacion Objeto {@link Certificacion} con el ID real.
	 */
	public void reindexarCertificacion(Integer idTemporal, Certificacion certificacion) {
		if (this.certificacionesMap.containsKey(idTemporal) && certificacion.getId() > 0) {
			this.certificacionesMap.remove(idTemporal);
			this.certificacionesMap.put(certificacion.getId(), certificacion);
		}
	}

	/**
	 * Obtiene un iterador de IDs de certificaciones asociadas al veterinario.
	 * @return Iterador de enteros con los IDs de las certificaciones.
	 */
	public Iterator<Integer> getCertificacionesIds() {
		return new ArrayList<>(this.certificacionesMap.keySet()).iterator();
	}

	/**
	 * Obtiene un iterador de objetos {@link Certificacion}, cargándolos de forma perezosa.
	 * @return Iterador de objetos {@link Certificacion}.
	 */
	public Iterator<Certificacion> getCertificaciones() {
		for (Integer cId : this.certificacionesMap.keySet()) {
			if (this.certificacionesMap.get(cId) == null) {
				Certificacion c = CertificacionPersistence.readById(cId);
				if (c != null) {
					c.setVeterinario_licencia(this.num_licencia);
					this.certificacionesMap.put(cId, c);
				}
			}
		}
		return new ArrayList<>(this.certificacionesMap.values()).iterator();
	}

	/**
	 * Implementación del método {@link Iterable#iterator()}.
	 * Permite iterar directamente sobre las certificaciones del veterinario.
	 *
	 * @return Iterador de objetos {@link Certificacion}.
	 */
	@Override
    public Iterator<Certificacion> iterator() {
        return getCertificaciones();
    }

	/**
	 * Devuelve una representación en texto del veterinario.
	 * @return Cadena con los datos principales del veterinario.
	 */
	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("Licencia: ").append(num_licencia)
	      .append(" | Nombre: ").append(nombre).append(" ").append(apellido)
	      .append(" | Contratación: ").append(fecha_contratacion)
	      .append(" | Salario: $").append(salarioBase)
	      .append(" | Horas/Semana: ").append(horarioSemanal);
	    return sb.toString();
	}

}
