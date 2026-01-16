package com.agustincrespo.u3.hibernate.avanzado.service;

import java.util.List;

import com.agustincrespo.u3.hibernate.avanzado.dto.UsuarioCambioPasswordDTO;
import com.agustincrespo.u3.hibernate.avanzado.dto.UsuarioDTO;
import com.agustincrespo.u3.hibernate.avanzado.dto.UsuarioRegistroDTO;

/**
 * Interfaz de servicio que define las operaciones de negocio relacionadas con
 * la entidad Usuario.
 *
 * <p>Responsabilidades principales:</p>
 * <ul>
 * <li>Proporcionar métodos CRUD (Create, Read, Update, Delete) utilizando DTOs.</li>
 * <li>Aplicar reglas de negocio: validación, hashing de contraseñas y gestión de auditoría.</li>
 * <li>Coordinar la demarcación de transacciones.</li>
 * </ul>
 */
public interface UsuarioService {

    /**
     * Registra un nuevo usuario en el sistema.
     * <p>
     * Este método se encarga de:
     * <ul>
     * <li>Validar los datos de entrada.</li>
     * <li>Hashear la contraseña (que viene en texto plano) antes de guardarla.</li>
     * <li>Establecer valores por defecto (activo = true, fecha de creación).</li>
     * <li>Persistir la entidad resultante.</li>
     * </ul>
     * </p>
     *
     * @param dto DTO de registro que contiene nombre, email y contraseña en texto plano.
     * @throws IllegalArgumentException si el DTO es null o contiene datos inválidos.
     */
    void create(UsuarioRegistroDTO dto);

    /**
     * Gestiona el cambio seguro de contraseña para un usuario existente.
     * <p>
     * Valida que la contraseña actual proporcionada coincida con el hash almacenado
     * antes de aplicar y guardar el nuevo hash.
     * </p>
     *
     * @param dto DTO que contiene el ID del usuario, la contraseña antigua y la nueva.
     * @throws IllegalArgumentException si el usuario no existe o la contraseña antigua no coincide.
     */
    void changePassword(UsuarioCambioPasswordDTO dto);

    /**
     * Obtiene y devuelve un usuario por su identificador único.
     *
     * @param id Identificador numérico del usuario (debe ser mayor que 0).
     * @return Un UsuarioDTO con los datos públicos del usuario, o {@code null} si no existe.
     * @throws IllegalArgumentException si el id proporcionado no es válido.
     */
    UsuarioDTO findById(int id);

    /**
     * Recupera el listado completo de usuarios del sistema.
     *
     * @return Una lista de UsuarioDTO. Si no hay usuarios, devuelve una lista vacía (nunca {@code null}).
     */
    List<UsuarioDTO> findAll();

    /**
     * Actualiza la información básica de un usuario existente.
     * <p>
     * Solo actualiza campos permitidos (como nombre, email o estado activo).
     * <strong>No actualiza la contraseña</strong> ni datos de auditoría inmutables.
     * </p>
     *
     * @param dto El UsuarioDTO con los datos modificados. Debe incluir el ID del usuario.
     * @return El UsuarioDTO actualizado reflejando los cambios persistidos.
     * @throws IllegalArgumentException si el DTO es null o el usuario no existe.
     */
    UsuarioDTO update(UsuarioDTO dto);

    /**
     * Elimina un usuario del sistema por su identificador.
     *
     * @param id Identificador del usuario a eliminar.
     * @throws IllegalArgumentException si el id no es válido.
     */
    void delete(int id);

}