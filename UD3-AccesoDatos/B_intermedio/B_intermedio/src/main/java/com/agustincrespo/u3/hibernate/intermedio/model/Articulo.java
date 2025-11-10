package com.agustincrespo.u3.hibernate.intermedio.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.agustincrespo.u3.hibernate.intermedio.persistence.converter.LocalDateTimeToStringConverter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

	@Column(name = "contenido", nullable = false)
	private String contenido;

	@CreationTimestamp
	@Column(name = "fecha_publicacion", updatable = false, nullable = false)
	@Convert(converter = LocalDateTimeToStringConverter.class)
	private LocalDateTime fechaPublicacion;

	// --- RELACIÓN 1:N (Lado "N", DUEÑO) ---
	/**
	 * Define la relación N:1 con Usuario.<br>
	 * Muchos Articulos pertenecen a un Usuario (autor).<br>
	 * Esta es la entidad "dueña" de la relación, ya que contiene la clave foránea.
	 *
	 * '@JoinColumn(name = "autor_id")': Especifica que la columna 'autor_id' en
	 * esta tabla (articulos) es la clave foránea.<br>
	 * 'nullable = false': Coincide con el SQL (un artículo DEBE tener autor).
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "autor_id", nullable = false)
	private Usuario autor;

	// --- RELACIÓN N:M (Lado DUEÑO) ---
	/**
	 * Define la relación N:M con Categoria.<br>
	 * Esta es la entidad "dueña" de la relación (la elección es ARBITRARIA, pero
	 * una de las dos debe serlo).<br>
	 *
	 * 'cascade': Usamos PERSIST y MERGE. No queremos que al borrar un artículo se
	 * borren sus categorías (podrían estar usadas por otros artículos).<br>
	 * 
	 * '@JoinTable': Configura la tabla intermedia.<br>
	 * 'name = "articulo_categoria"': Nombre de la tabla pivote en el SQL.<br>
	 * 'joinColumns': La columna FK de esta entidad (Articulo).<br>
	 * 'inverseJoinColumns': La columna FK de la otra entidad (Categoria).
	 *
	 */
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "articulo_categoria", joinColumns = @JoinColumn(name = "articulo_id"), inverseJoinColumns = @JoinColumn(name = "categoria_id"))
	private Set<Categoria> categorias = new HashSet<>();

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

	public Set<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(Set<Categoria> categorias) {
		this.categorias = categorias;
		// FIXME: Añadir lógica para sincronizar la relación bidireccional
	}

	// --- Métodos Helper para N:M ---

	/**
	 * Método helper para añadir una categoría Y sincronizar la relación N:M
	 * bidireccional.
	 */
	public void addCategoria(Categoria categoria) {
		this.categorias.add(categoria);
		categoria.getArticulos().add(this);
	}

	/**
	 * Método helper para eliminar una categoría Y sincronizar la relación N:M
	 * bidireccional.
	 */
	public void removeCategoria(Categoria categoria) {
		this.categorias.remove(categoria);
		categoria.getArticulos().remove(this);
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
