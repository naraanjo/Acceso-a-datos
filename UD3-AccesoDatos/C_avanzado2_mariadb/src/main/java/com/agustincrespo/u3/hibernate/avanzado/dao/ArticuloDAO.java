package com.agustincrespo.u3.hibernate.avanzado.dao;

import java.util.List;

import com.agustincrespo.u3.hibernate.avanzado.model.Articulo;

/**
 * DAO para la entidad {@link Articulo}.
 * 
 * <p>
 * Esta interfaz declara las operaciones básicas de persistencia (CRUD) que
 * deben proporcionar las implementaciones para gestionar objetos
 * {@link Articulo}.<br>
 * Las implementaciones se encargan de los detalles de persistencia (por ejemplo
 * EntityManager, transacciones y tratamiento de excepciones) y deben cumplir
 * los contratos descritos en los Javadoc de cada método.
 * </p>
 */
public interface ArticuloDAO {

	/**
	 * Busca un Articulo por su clave primaria.
	 *
	 * @param id Identificador (clave primaria) del Articulo a buscar.
	 * @return el {@link Articulo} correspondiente al id, o {@code null} si no
	 *         existe.
	 */
	Articulo findById(int id);

	/**
	 * Recupera todos los Articulo existentes en la fuente de datos.
	 *
	 * @return una lista con todos los {@link Articulo}; nunca debe devolverse
	 *         {@code null} (si no hay resultados debe retornarse una lista vacía).
	 */
	List<Articulo> findAll();

	/**
	 * Persiste un nuevo {@link Articulo} en la base de datos.
	 *
	 * La implementación debe asegurarse de asignar cualquier identificador generado
	 * al objeto proporcionado cuando corresponda.
	 *
	 * @param articulo entidad {@link Articulo} a persistir (no debe ser
	 *                 {@code null}).
	 */
	void save(Articulo articulo);

	/**
	 * Actualiza un {@link Articulo} existente.
	 *
	 * @param articulo entidad {@link Articulo} con los cambios a sincronizar.<br>
	 *                 Debe tener un identificador válido que identifique la entidad
	 *                 a actualizar.
	 * @return la instancia gestionada que representa el {@link Articulo}
	 *         actualizado.
	 */
	Articulo update(Articulo articulo);

	/**
	 * Elimina el {@link Articulo} indicado de la persistencia.
	 *
	 * La implementación debe aceptar tanto instancias gestionadas como instancias
	 * detachadas; si se recibe una instancia detachada, puede que la implementación
	 * primero la haga managed antes de eliminarla.
	 *
	 * @param articulo entidad {@link Articulo} a eliminar (no debe ser
	 *                 {@code null}).
	 */
	void delete(Articulo articulo);

	/**
	 * Busca una lista de {@link Articulo} para un usuario concreto.
	 * 
	 * @param autorId clave del {@link Usuario} autor que escribió esos
	 *                {@link Articulo}.
	 * @return una lista con todos los {@link Articulo} de los que ese
	 *         {@link Usuario} es autor. No se devolverá {@code null} si no escribió
	 *         ningún {@link Articulo}, sino que debe retornarse una lista vacía).
	 */
	List<Articulo> findByAutorId(int autorId);
}