package com.agustincrespo.u3.hibernate.avanzado.dto;

import java.time.LocalDateTime;

import com.agustincrespo.u3.hibernate.avanzado.model.Articulo;

/**
 * Data Transfer Object (DTO) para la entidad {@link Articulo}.
 * <p>
 * Representa una versión ligera de un artículo adecuada para transporte entre
 * capas (p. ej. controladores REST y clientes). Contiene los campos básicos
 * necesarios para mostrar o editar un artículo sin exponer la entidad JPA
 * completa ni sus relaciones complejas.
 * </p>
 * <p>
 * Uso de los métodos de mapeo:
 * <ul>
 * <li><code>fromEntity(Articulo a)</code> construye un {@code ArticuloDTO} a
 * partir de una entidad. Devuelve {@code null} si la entidad es
 * {@code null}.</li>
 * <li><code>toEntity()</code> crea una nueva instancia de {@code Articulo} con
 * los campos presentes en el DTO. No realiza búsquedas ni asigna relaciones
 * (por ejemplo, no asigna el autor más allá de guardar su id en
 * {@code autorId}).</li>
 * </ul>
 * </p>
 */
public class ArticuloDTO {
	public int id;
	public String titulo;
	public String contenido;
	public LocalDateTime fechaPublicacion;
	public int autorId;

	public ArticuloDTO() {
	}

	/**
	 * Construye un ArticuloDTO a partir de la entidad proporcionada.
	 * <p>
	 * Devuelve {@code null} si la entidad {@code a} es {@code null}.
	 * </p>
	 *
	 * @param a la entidad Articulo de origen
	 * @return un ArticuloDTO con los datos copiados, o {@code null}
	 */
	public static ArticuloDTO fromEntity(Articulo a) {
		if (a == null)
			return null;
		ArticuloDTO d = new ArticuloDTO();
		d.id = a.getId();
		d.titulo = a.getTitulo();
		d.contenido = a.getContenido();
		d.fechaPublicacion = a.getFechaPublicacion();
		d.autorId = a.getAutor() != null ? a.getAutor().getId() : 0;
		return d;
	}

	/**
	 * Convierte este DTO en una nueva entidad {@link Articulo}.
	 * <p>
	 * Crea una instancia nueva y asigna los campos simples del DTO. No se resuelven
	 * relaciones ni se realiza ningún acceso a la base de datos para enlazar
	 * entidades relacionadas. Si es necesario asociar el autor por objeto, la capa
	 * superior debe hacerlo (p. ej. buscar el Usuario por {@code autorId} y
	 * asignarlo a la entidad resultante).
	 * </p>
	 *
	 * @return una nueva instancia de Articulo con los campos básicos asignados
	 */
	public Articulo toEntity() {
		Articulo a = new Articulo();
		a.setId(this.id);
		a.setTitulo(this.titulo);
		a.setContenido(this.contenido);
		a.setFechaPublicacion(this.fechaPublicacion);
		return a;
	}
}