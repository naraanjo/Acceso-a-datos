package com.agustincrespo.u3.hibernate.avanzado.dao;

import java.util.List;

import com.agustincrespo.u3.hibernate.avanzado.model.Usuario;

/**
 * DAO para la entidad {@link Usuario}.
 * <p>
 * Proporciona las operaciones básicas de persistencia (CRUD) para gestionar
 * objetos Usuario desde la capa de acceso a datos.<br>
 * Las implementaciones deben encargarse del manejo del EntityManager,
 * transacciones y del tratamiento de excepciones. Además, deben respetar los
 * contratos documentados en los métodos (p. ej. devolver lista vacía en lugar
 * de {@code null}).
 * </p>
 */
public interface UsuarioDAO {

	/**
	 * Busca un {@link Usuario} por su identificador primario.
	 *
	 * @param id Identificador (clave primaria) del usuario a buscar.
	 * @return el Usuario correspondiente al id, o {@code null} si no existe.
	 */
	Usuario findById(int id);

	/**
	 * Recupera todos los {@link Usuario} almacenados.
	 * <p>
	 * Si no hay resultados, debe devolverse una lista vacía (nunca {@code null}).
	 * </p>
	 *
	 * @return lista con todos los usuarios.
	 */
	List<Usuario> findAll();

	/**
	 * Persiste un nuevo {@link Usuario} en la fuente de datos.
	 * <p>
	 * La implementación debe validar el parámetro y asignar cualquier identificador
	 * generado al objeto proporcionado cuando proceda.
	 * </p>
	 *
	 * @param usuario entidad Usuario a persistir (no debe ser {@code null}).
	 */
	void save(Usuario usuario);

	/**
	 * Actualiza un {@link Usuario} existente.
	 * <p>
	 * El objeto pasado debe contener un identificador válido que permita localizar
	 * la entidad a actualizar. La implementación debe devolver la instancia
	 * gestionada resultante tras la operación de merge/actualización.
	 * </p>
	 *
	 * @param usuario entidad Usuario con los cambios a sincronizar.
	 * @return la instancia gestionada que representa el usuario actualizado.
	 */
	Usuario update(Usuario usuario);

	/**
	 * Elimina el {@link Usuario} indicado de la persistencia.
	 * <p>
	 * La implementación debe aceptar tanto instancias gestionadas como instancias
	 * detachadas; en el caso de recibir una instancia detachada, puede realizar un
	 * merge o buscar la entidad por id antes de eliminarla.
	 * </p>
	 *
	 * @param usuario entidad Usuario a eliminar (no debe ser {@code null}).
	 */
	void delete(Usuario usuario);
}