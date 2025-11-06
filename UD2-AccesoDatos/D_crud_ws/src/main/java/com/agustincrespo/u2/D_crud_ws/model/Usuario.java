package com.agustincrespo.u2.D_crud_ws.model;

import java.util.Objects;

public class Usuario {
	private int id;
	private String nombre;
	private String email;
	private String passwordHash;
	// formato "YYYY-MM-DD HH:MM:SS", luego se puede mapear a LocalDateTime
	private String fechaCreacion;
	private int estaActivo;
	private Perfil perfil; // opcional 1:1

	public Usuario() {
		this.id = 0;
		this.nombre = "";
		this.email = "";
		this.passwordHash = "";
		this.fechaCreacion = "";
		this.estaActivo = 1;
		this.perfil = null;
	}

	public Usuario(Integer id, String nombre, String email, String passwordHash, String fechaCreacion,
			Integer estaActivo) {
		this.id = normalizeInteger(id);
		this.nombre = normalizeString(nombre);
		this.email = normalizeString(email);
		this.passwordHash = normalizeString(passwordHash);
		this.fechaCreacion = normalizeString(fechaCreacion);
		this.estaActivo = normalizeInteger(estaActivo);
		this.perfil = null;
	}

	// getters & setters
	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = normalizeInteger(id);
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

	public Integer getEstaActivo() {
		return estaActivo;
	}

	public void setEstaActivo(Integer estaActivo) {
		this.estaActivo = normalizeInteger(estaActivo);
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
				+ fechaCreacion + '\'' + ", estaActivo=" + estaActivo + ", perfil=" + perfil + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Usuario usuario = (Usuario) o;
		return Objects.equals(id, usuario.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	// Helpers de validación: cadenas nulas -> ""; enteros negativos -> 0
	private static String normalizeString(String s) {
		return (s == null || s.isBlank()) ? "" : s.trim();
	}

	private static Integer normalizeInteger(Integer i) {
		return (i == null || i < 0) ? 0 : i;
	}
}