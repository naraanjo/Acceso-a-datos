package com.agustincrespo.u3.hibernate.intermedio.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "categorias")
public class Categoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "nombre", nullable = false, unique = true)
	private String nombre;

	// --- RELACIÓN N:M (Lado Inverso) ---
	/**
	 * Define la relación N:M inversa con Articulo.<br>
	 * 'mappedBy = "categorias"': Le dice a JPA que esta relación ya está
	 * configurada por el campo 'categorias' en la clase Articulo (la entidad
	 * dueña), y aquí ya no hay mucho que volver a configurar.
	 */
	@ManyToMany(mappedBy = "categorias")
	private Set<Articulo> articulos = new HashSet<>();

	public Categoria() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Set<Articulo> getArticulos() {
		return articulos;
	}

	public void setArticulos(Set<Articulo> articulos) {
		this.articulos = articulos;
	}

	// --- toString, equals, hashCode ---

	@Override
	public String toString() {
		return "Categoria{" + "id=" + id + ", nombre='" + nombre + "'}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass() && !(o instanceof Categoria))
			return false;

		Categoria otraCategoria = (Categoria) o;
		if (id == 0 && otraCategoria.id == 0)
			return super.equals(o);

		// Podemos comparar por 'nombre' ya que es UNIQUE
		if (id == 0)
			return Objects.equals(nombre, otraCategoria.nombre);

		return id == otraCategoria.id;
	} // FIXME: revisar la logica de equals

	@Override
	public int hashCode() {
		if (id != 0)
			return Objects.hash(this.id);

		// 'nombre' es un identificador natural bueno si el ID no está asignado
		if (nombre != null && !nombre.isEmpty())
			return Objects.hash(nombre);

		return System.identityHashCode(this); // Por defecto, usar la identidad del objeto
	}
}
