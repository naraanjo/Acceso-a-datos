package com.agustincrespo.u3.hibernate.intermedio.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.agustincrespo.u3.hibernate.intermedio.persistence.converter.LocalDateTimeToStringConverter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
	@Convert(converter = LocalDateTimeToStringConverter.class)
	private LocalDateTime fechaCreacion;

	@JdbcTypeCode(SqlTypes.INTEGER)
	@Column(name = "esta_activo", nullable = false)
	@ColumnDefault("1")
	private boolean activo;

	// --- RELACIÓN 1:1 ---
	@OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Perfil perfil;

	// --- RELACIÓN 1:N ---
	/**
	 * Define la relación 1:N con Articulo.<br>
	 * Un Usuario tiene muchos Articulos.<br>
	 * 
	 * 'mappedBy = "autor"': Indica que la clave foránea es gestionada por el campo
	 * 'autor' en la clase Articulo.<br>
	 * 'cascade = CascadeType.ALL': Si se borra un Usuario, se borran todos sus
	 * artículos (coincide con ON DELETE CASCADE del SQL).<br>
	 * 'fetch = FetchType.LAZY': ¡CRÍTICO! No cargar los artículos a menos que se
	 * pidan explícitamente (ej. usuario.getArticulos()).
	 */
	@OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
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

	public Perfil getPerfil() {
		return perfil;
	}

	/*
	 * Método helper para sincronizar la relación 1:1 bidireccional.
	 */
	public void setPerfil(Perfil perfil) {
		if (perfil == null) {
			if (this.perfil != null) {
				this.perfil.setUsuario(null);
			}
		} else {
			perfil.setUsuario(this);
		}
		this.perfil = perfil;
	}

	/* Métodos para la relación 1:N */
	public Set<Articulo> getArticulos() {
		return articulos;
	}

	public void setArticulos(Set<Articulo> articulos) {
		this.articulos = articulos;
	}

	/**
	 * Método helper para añadir un artículo y sincronizar la relación 1:N
	 * bidireccional.
	 */
	public void addArticulo(Articulo articulo) {
		articulos.add(articulo);
		articulo.setAutor(this);
	}

	/**
	 * Método helper para eliminar un artículo y sincronizar la relación 1:N
	 * bidireccional.
	 */
	public void removeArticulo(Articulo articulo) {
		articulos.remove(articulo);
		articulo.setAutor(null);
	}

	public int getNumArticulos() {
		return articulos != null ? articulos.size() : 0;
	}

	// --- toString, equals, hashCode ---

	@Override
	public String toString() {
		return "Usuario{" + "id=" + id + ", nombre='" + nombre + '\'' + ", email='" + email + '\'' + ", fechaCreacion='"
				+ fechaCreacion + '\'' + ", activo=" + activo + ", id_perfil="
				+ (perfil != null ? perfil.getId() : "N/A") + ", num_articulos="
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
