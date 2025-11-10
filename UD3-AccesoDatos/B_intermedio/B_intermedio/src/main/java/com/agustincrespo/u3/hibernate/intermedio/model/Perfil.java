package com.agustincrespo.u3.hibernate.intermedio.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "perfiles")
public class Perfil {

	@Id
	@Column(name = "usuario_id")
	private int id; // Clave primaria compartida con 'Usuario'

	@Column(name = "biografia")
	private String biografia;

	@Column(name = "sitio_web")
	private String sitioWeb;

	@Column(name = "ubicacion")
	private String ubicacion;

	/**
	 * Define la relación 1:1 dueña con Usuario.
	 * '@MapsId': Le dice a JPA que la clave primaria de esta entidad ('id')
	 * es también una clave foránea, y su valor debe ser copiado
	 * desde la entidad 'usuario'.
	 * '@JoinColumn': Especifica que la columna 'usuario_id' es la que
	 * mapea esta relación.
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	public Perfil() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBiografia() {
		return biografia;
	}

	public void setBiografia(String biografia) {
		this.biografia = biografia;
	}

	public String getSitioWeb() {
		return sitioWeb;
	}

	public void setSitioWeb(String sitioWeb) {
		this.sitioWeb = sitioWeb;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public String toString() {
		return "Perfil{" + "id=" + id + ", biografia='" + biografia + '\'' + ", sitioWeb='" + sitioWeb + '\''
				+ ", ubicacion='" + ubicacion + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass() && !(o instanceof Perfil))
			return false;
		Perfil perfil = (Perfil) o;
		if (id == 0 && perfil.id == 0) {
			return super.equals(o);
		}
		return id == perfil.id;
	}

	@Override
	public int hashCode() {
		return id != 0 ? Objects.hash(id) : System.identityHashCode(this);
	}
}
