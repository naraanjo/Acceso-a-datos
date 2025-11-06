package com.agustincrespo.u2.E_crud_desktop.model;

import java.util.Objects;

public class Perfil {
	private int usuarioId; // TODO fíjate en esto
	private String biografia;
	private String sitioWeb;
	private String ubicacion;

	/**
	 * Constructor por defecto.
	 * 
	 * <p>Inicializa usuarioId a 0 y cadenas a vacío.
	 */
	public Perfil() {
		this.usuarioId = 0;
		this.biografia = "";
		this.sitioWeb = "";
		this.ubicacion = "";
	}

	/**
	 * Constructor completo.
	 *
	 * @param usuarioId ID del usuario asociado (no debe ser negativo)
	 * @param biografia Biografía del usuario
	 * @param sitioWeb  Sitio web del usuario
	 * @param ubicacion Ubicación del usuario
	 */
	public Perfil(int usuarioId, String biografia, String sitioWeb, String ubicacion) {
		this.setUsuarioId(usuarioId); // Usa el setter para la validación
		this.setBiografia(biografia);
		this.setSitioWeb(sitioWeb);
		this.setUbicacion(ubicacion);
	}

	public int getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(int usuarioId) {
		this.usuarioId = (usuarioId < 0) ? 0 : usuarioId;
	}

	public String getBiografia() {
		return biografia;
	}

	public void setBiografia(String biografia) {
		this.biografia = normalizeString(biografia);
	}

	public String getSitioWeb() {
		return sitioWeb;
	}

	public void setSitioWeb(String sitioWeb) {
		this.sitioWeb = normalizeString(sitioWeb);
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = normalizeString(ubicacion);
	}

	@Override
	public String toString() {
		return "Perfil{" + "usuarioId=" + usuarioId + ", biografia='" + biografia + '\'' + ", sitioWeb='" + sitioWeb
				+ '\'' + ", ubicacion='" + ubicacion + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Perfil perfil = (Perfil) o;
		return this.usuarioId == perfil.usuarioId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(usuarioId);
	}

	/**
	 * Helper de validación: cadenas nulas o vacías -> ""; quita espacios.
	 */
	private static String normalizeString(String s) {
		return (s == null || s.isBlank()) ? "" : s.trim();
	}
}
