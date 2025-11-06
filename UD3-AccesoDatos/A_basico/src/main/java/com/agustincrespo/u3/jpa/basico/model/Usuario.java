package com.agustincrespo.u3.jpa.basico.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime; // Usamos tipos de Java modernos
import java.util.Objects;

// JPA: @Entity le dice a JPA que esta clase debe ser gestionada y mapeada a una tabla.
@Entity
// JPA: @Table especifica el nombre exacto de la tabla en la BBDD.
@Table(name = "usuarios")
public class Usuario {

	// JPA: @Id marca este campo como la clave primaria (PK) de la tabla.
	// JPA: @GeneratedValue le dice a JPA cómo se genera esta PK.
	//  |->'IDENTITY' significa que confiamos en la BBDD (ej. AUTOINCREMENT de SQLite).
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id") // Mapea al nombre de la columna (aunque se llamen igual)
	private int id;

	// JPA: @Column es opcional si se llaman igual, pero es buena práctica
	// usarlo para definir restricciones (unique, nullable).
	@Column(name = "nombre", nullable = false, unique = true)
	private String nombre;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	// JPA: Mapeamos la columna de BBDD "fecha_creacion"
	// @CreationTimestamp es una anotación de Hibernate (no de JPA puro)
	// que inserta automáticamente la fecha y hora al crear.
	@CreationTimestamp
	@Column(name = "fecha_creacion", updatable = false, nullable = false)
	private LocalDateTime fechaCreacion; // ¡Mejor usar LocalDateTime que String!

	// JPA: Aquí mapeamos el 'boolean' de Java al 'INTEGER' (0 o 1) de SQLite.
	// @JdbcTypeCode le dice a Hibernate cómo hacer la conversión.
	@JdbcTypeCode(SqlTypes.INTEGER)
	@Column(name = "esta_activo", nullable = false)
	@ColumnDefault("1") // Valor por defecto en la BBDD
	private boolean activo;

	/*
	 * --- ¡LA RELACIÓN! ---
	 *
	 * JPA: @OneToOne define la relación 1 a 1.
	 *
	 * mappedBy = "usuario"
	 *   Indica que este es el lado INVERSO de la relación: la clave foránea
	 *   está en la otra entidad (Perfil) en su campo 'usuario'.
	 *
	 * cascade = CascadeType.ALL
	 *   Si persisto o borro un Usuario, Hibernate también persistirá o borrará
	 *   su Perfil asociado.
	 *
	 * orphanRemoval = true
	 *   Si hago usuario.setPerfil(null) se eliminará el Perfil huérfano de la BBDD.
	 *
	 * fetch = FetchType.LAZY
	 *   No se carga el Perfil hasta que se solicite explícitamente con
	 *   usuario.getPerfil(). Es una optimización importante.
	 */
	@OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Perfil perfil;

	public Usuario() {
		this.activo = true; // Valor por defecto en Java
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

	/**
	 * Método helper para sincronizar la relación 1:1 bidireccional. Siempre que
	 * asignamos un Perfil, también asignamos 'este' Usuario al Perfil.
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

	@Override
	public String toString() {
		return "Usuario{" + "id=" + id + ", nombre='" + nombre + '\'' + ", email='" + email + '\'' + ", fechaCreacion='"
				+ fechaCreacion + '\'' + ", activo=" + activo + ", id_perfil="
				+ (perfil != null ? perfil.getId() : "N/A") + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Usuario usuario = (Usuario) o;
		return id == usuario.id && id != 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}