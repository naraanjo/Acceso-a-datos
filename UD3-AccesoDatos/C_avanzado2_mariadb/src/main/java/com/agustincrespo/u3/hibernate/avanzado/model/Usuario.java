package com.agustincrespo.u3.hibernate.avanzado.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "nombre", nullable = false, unique = true)
	private String nombre;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@CreationTimestamp
	@Column(name = "fecha_creacion", updatable = false, nullable = false)
	private LocalDateTime fechaCreacion;

	@Column(name = "activo", nullable = false)
	private boolean activo;

	// --- RELACIÓN 1:N ---
	@OneToMany(mappedBy = "autor", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<Articulo> articulos = new HashSet<>();

	public Usuario() {
		this.activo = true;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	/* Métodos para la relación 1:N */
	public Set<Articulo> getArticulos() {
		return articulos;
	}

	public void setArticulos(Set<Articulo> articulos) {
		this.articulos = articulos;
	}

	/**
	 * Añadir un artículo y sincronizar la relación 1:N bidireccional.
	 */
	public void addArticulo(Articulo articulo) {
		articulos.add(articulo);
		articulo.setAutor(this);
	}

	/**
	 * Eliminar un artículo y sincronizar la relación 1:N bidireccional.
	 */
	public void removeArticulo(Articulo articulo) {
		articulos.remove(articulo);
		articulo.setAutor(null);
	}

	// TODO ¿cómo mejorar la eficiencia de esto?
	public int getNumArticulos() {
		return articulos != null ? articulos.size() : 0;
	}

	// --- toString, equals, hashCode ---

	@Override
	public String toString() {
		return "Usuario{" + "id=" + id + ", nombre='" + nombre + '\'' + ", email='" + email + '\'' + ", fechaCreacion='"
				+ fechaCreacion + '\'' + ", activo=" + activo + ", num_articulos="
				+ (articulos != null ? articulos.size() : 0) + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass() && !(o instanceof Usuario))
			return false;

		Usuario usuario = (Usuario) o;

		// Si ambos son nuevos (id == 0), comparamos referencias
		if (id == 0 && usuario.id == 0)
			return super.equals(o);

		return id == usuario.id;
	}

	@Override
	public int hashCode() {
		// Usamos el ID si está persistido (id != 0)
		return id != 0 ? Objects.hash(id) : System.identityHashCode(this);
	}
}