package com.agustincrespo.u3.hibernate.avanzado.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.agustincrespo.u3.hibernate.avanzado.model.Usuario;

/**
 * Data Transfer Object (DTO) para la entidad {@link Usuario}.
 * <p>
 * Representa una vista ligera del usuario que se puede enviar o recibir desde
 * capas externas (p. ej. controladores REST, vistas, clientes) sin exponer toda
 * la entidad JPA ni sus relaciones completas.
 * </p>
 * <p>
 * Lógica de funcionamiento del DTO en este caso:
 * <ul>
 * <li>fromEntity(Usuario u) construye un UsuarioDTO a partir de una entidad
 * {@code Usuario}. Devuelve {@code null} si la entidad es {@code null}.</li>
 * <li>toEntity() crea una nueva instancia de {@code Usuario} con los campos
 * presentes en el DTO. No realiza búsquedas en la base de datos ni rellena
 * relaciones.</li>
 * <li>Por razones de seguridad, el campo {@code passwordHash} de la entidad se
 * deja a {@code null} cuando se crea desde el DTO, evitando así transmitir o
 * sobrescribir hashes accidentalmente desde la capa de transporte.</li>
 * <li>El DTO contiene solo datos esenciales (id, nombre, email, activo,
 * fechaCreacion) útiles para mostrar listados o transferir información sin
 * cargar dependencias pesadas ni datos sensibles.</li>
 * </ul>
 * </p>
 */
public class UsuarioDTO {
	public int id;
	public String nombre;
	public String email;
	public boolean activo;
	public LocalDateTime fechaCreacion;

	public UsuarioDTO() {
	}

	/**
	 * Construye un {@code UsuarioDTO} a partir de la entidad proporcionada.
	 * <p>
	 * Devuelve {@code null} si la entidad {@code u} es {@code null}.
	 * </p>
	 *
	 * @param u la entidad Usuario de origen
	 * @return un UsuarioDTO con los datos copiados, o {@code null}
	 */
	public static UsuarioDTO fromEntity(Usuario u) {
		if (u == null)
			return null;
		UsuarioDTO d = new UsuarioDTO();
		d.id = u.getId();
		d.nombre = u.getNombre();
		d.email = u.getEmail();
		d.activo = u.isActivo();
		d.fechaCreacion = u.getFechaCreacion();
		return d;
	}
	
	public static List<UsuarioDTO> fromEntityList(List<Usuario> usuarios) {
	    return usuarios.stream()
	            .map(UsuarioDTO::fromEntity)
	            .collect(Collectors.toList());
	}


	/**
	 * Convierte este DTO en una nueva entidad {@link Usuario}.
	 * <p>
	 * Esta operación crea una instancia nueva y asigna los valores simples copiados
	 * desde el DTO. No se gestionan relaciones ni se realiza ningún lookup en la
	 * base de datos. El campo {@code passwordHash} se deja a {@code null}
	 * intencionalmente: la contraseña/hashes deben manejarse por separado y con
	 * cuidado en capas específicas (registro o cambio de contraseña).
	 * </p>
	 *
	 * @return una nueva instancia de Usuario con los campos básicos asignados
	 */
	public  Usuario toEntity() {
		Usuario u = new Usuario();
		u.setId(this.id);
		u.setNombre(this.nombre);
		u.setEmail(this.email);
		u.setPasswordHash(null);
		u.setActivo(this.activo);
		u.setFechaCreacion(this.fechaCreacion);
		return u;
	}
}