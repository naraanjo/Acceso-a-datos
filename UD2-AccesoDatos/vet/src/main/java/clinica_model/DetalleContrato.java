package clinica_model;

/**
 * Representa la información laboral y salarial única de cada veterinario (Relación 1:1).
 * Implementa programación defensiva para evitar valores nulos e inconsistentes.
 */
public class DetalleContrato {

	// Atributos de la tabla DetalleContrato
	private int id;
	private double salarioBase;
	private int horarioSemanal; // Horas trabajadas a la semana
	private int veterinarioLicencia;


	private Veterinario veterinario; // Relación 1:1

	// Constructor Vacío (Defensivo)
	public DetalleContrato() {
		this.id = 0;
		this.salarioBase = 0.0;
		this.horarioSemanal = 0;
		this.veterinarioLicencia = 0;
		this.veterinario = null;
	}

	// Constructor Completo (Defensivo)
	public DetalleContrato(int id, double salarioBase, int horarioSemanal, int veterinarioLicencia) {
		// Validaciones en el constructor
		this.id = (id > 0) ? id : 0;
		this.salarioBase = (salarioBase >= 0.0) ? salarioBase : 0.0;
		this.horarioSemanal = (horarioSemanal > 0 && horarioSemanal <= 60) ? horarioSemanal : 0; // Ejemplo de validación lógica
		this.veterinarioLicencia = (veterinarioLicencia > 0) ? veterinarioLicencia : 0;
		this.veterinario = null;
	}

	// --- Getters y Setters con Programación Defensiva ---

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = (id > 0) ? id : this.id; // Mantiene el valor antiguo si el nuevo es inválido
	}

	public double getSalarioBase() {
		return salarioBase;
	}

	public void setSalarioBase(double salarioBase) {
		// Debe ser mayor o igual a cero
		this.salarioBase = (salarioBase >= 0.0) ? salarioBase : 0.0;
	}

	public int getHorarioSemanal() {
		return horarioSemanal;
	}

	public void setHorarioSemanal(int horarioSemanal) {
		// Validación lógica: asumimos un horario semanal razonable (ej: 1 a 60 horas)
		this.horarioSemanal = (horarioSemanal > 0 && horarioSemanal <= 60) ? horarioSemanal : 0;
	}

	public int getVeterinarioLicencia() {
		return veterinarioLicencia;
	}

	public void setVeterinarioLicencia(int veterinarioLicencia) {
		// La FK debe ser positiva
		this.veterinarioLicencia = (veterinarioLicencia > 0) ? veterinarioLicencia : 0;
	}

	// --- Getters y Setters de Relación ---

	public Veterinario getVeterinario() {
		return veterinario;
	}

	public void setVeterinario(Veterinario veterinario) {
		this.veterinario = veterinario; // Puede ser null si no se carga el detalle del veterinario
	}
	

}
