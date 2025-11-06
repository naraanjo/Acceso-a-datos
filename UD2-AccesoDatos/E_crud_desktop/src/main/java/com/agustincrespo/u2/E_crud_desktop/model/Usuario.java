package com.agustincrespo.u2.E_crud_desktop.model;

import java.util.Objects;

public class Usuario {
	private int id;
	private String nombre;
	private String email;
	private String passwordHash;
	private String fechaCreacion;
	private boolean activo;
	private Perfil perfil; // opcional 1:1

	/**
	 * Constructor por defecto.
	 * 
	 * <p>Inicializa id a 0, activo a true, y cadenas a vacío.
	 */
	public Usuario() {
		this.id = 0;
		this.nombre = "";
		this.email = "";
		this.passwordHash = "";
		this.fechaCreacion = "";
		this.activo = true;
		this.perfil = null;
	}

	/**
	 * Constructor completo.
	 *
	 * @param id            ID del usuario (no debe ser negativo)
	 * @param nombre        Nombre
	 * @param email         Email
	 * @param passwordHash  Hash de la contraseña
	 * @param fechaCreacion Fecha (como String)
	 * @param activo    Estado (boolean)
	 */
	public Usuario(int id, String nombre, String email, String passwordHash, String fechaCreacion,
			boolean estaActivo) {
		this.setId(id);
		this.setNombre(nombre);
		this.setEmail(email);
		this.setPasswordHash(passwordHash);
		this.setFechaCreacion(fechaCreacion);
		this.setActivo(estaActivo);
		this.perfil = null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = (id < 0) ? 0 : id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = normalizeString(nombre);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = normalizeString(email);
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = normalizeString(passwordHash);
	}

	public String getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(String fechaCreacion) {
		this.fechaCreacion = normalizeString(fechaCreacion);
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean estaActivo) {
		this.activo = estaActivo;
	}

	/**
	 * Setter sobrecargado para compatibilidad con la capa de persistencia.
	 * Convierte el 'int' (0 o 1) de la BBDD a 'boolean'.
	 *
	 * @param activo 0 (false) o 1 (true)
	 */
	public void setEstaActivo(int estaActivo) {
		this.activo = (estaActivo != 0);
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	@Override
	public String toString() {
		return "Usuario{" + "id=" + id + ", nombre='" + nombre + '\'' + ", email='" + email + '\'' + ", fechaCreacion='"
				+ fechaCreacion + '\'' + ", activo=" + activo + ", perfil=" + perfil + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Usuario usuario = (Usuario) o;
		// CAMBIO: Comparación de primitivos 'int'
		return this.id == usuario.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	/**
	 * Helper de validación: cadenas nulas o vacías -> ""; quita espacios.
	 */
	private static String normalizeString(String s) {
		return (s == null || s.isBlank()) ? "" : s.trim();
	}

	// CAMBIO: normalizeInteger eliminado ya que no se aceptan nulos.
}
