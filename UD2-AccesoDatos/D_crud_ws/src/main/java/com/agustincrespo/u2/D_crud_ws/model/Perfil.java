package com.agustincrespo.u2.D_crud_ws.model;

import java.util.Objects;

public class Perfil {
	private int usuarioId;
	private String biografia;
	private String sitioWeb;
	private String ubicacion;

	public Perfil() {
		this.usuarioId = 0;
		this.biografia = "";
		this.sitioWeb = "";
		this.ubicacion = "";
	}

	public Perfil(Integer usuarioId, String biografia, String sitioWeb, String ubicacion) {
		this.usuarioId = normalizeInteger(usuarioId);
		this.biografia = normalizeString(biografia);
		this.sitioWeb = normalizeString(sitioWeb);
		this.ubicacion = normalizeString(ubicacion);
	}

	public Integer getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Integer usuarioId) {
		this.usuarioId = normalizeInteger(usuarioId);
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
		return Objects.equals(usuarioId, perfil.usuarioId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(usuarioId);
	}

	// Helpers de validación: cadenas nulas -> ""; enteros negativos -> 0
	private static String normalizeString(String s) {
		return (s == null || s.isBlank()) ? "" : s.trim();
	}

	private static Integer normalizeInteger(Integer i) {
		return (i == null || i < 0) ? 0 : i;
	}
}