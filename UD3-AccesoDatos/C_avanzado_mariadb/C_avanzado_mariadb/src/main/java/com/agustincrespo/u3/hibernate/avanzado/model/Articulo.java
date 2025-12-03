package com.agustincrespo.u3.hibernate.avanzado.model;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "articulos")
public class Articulo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "titulo", nullable = false)
	private String titulo;

	@Column(name = "contenido", nullable = false, columnDefinition = "LONGTEXT")
	@Basic(fetch = FetchType.LAZY)
	private String contenido;

	@CreationTimestamp
	@Column(name = "fecha_publicacion", updatable = false, nullable = false)
	private LocalDateTime fechaPublicacion;

	// --- RELACIÓN 1:N (Lado "N", DUEÑO) ---
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "autor_id", nullable = false)
	private Usuario autor;

	public Articulo() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getContenido() {
		return contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	public LocalDateTime getFechaPublicacion() {
		return fechaPublicacion;
	}

	public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
		this.fechaPublicacion = fechaPublicacion;
	}

	public Usuario getAutor() {
		return autor;
	}

	public void setAutor(Usuario autor) {
		this.autor = autor;
	}

	// --- toString, equals, hashCode ---

	@Override
	public String toString() {
		return "Articulo{" + "id=" + id + ", titulo='" + titulo + "'" + ", fechaPublicacion=" + fechaPublicacion
				+ ", autor_id=" + (autor != null ? autor.getId() : "N/A") + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass() && !(o instanceof Articulo))
			return false;

		Articulo articulo = (Articulo) o;
		if (id == 0 && articulo.id == 0) {
			return this.titulo.equals(articulo.titulo) && this.fechaPublicacion.equals(articulo.fechaPublicacion);
			// return super.equals(o);
		}
		return id == articulo.id;
	}

	@Override
	public int hashCode() {
		return id != 0 ? Objects.hash(id) : System.identityHashCode(this);
	}
}