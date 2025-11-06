package com.agustincrespo.u2.D_crud_ws.persistence;

import com.agustincrespo.u2.D_crud_ws.model.Perfil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de persistencia (DAO) para la entidad Perfil.
 * Gestiona todas las operaciones CRUD (Crear, Leer, Actualizar, Borrar)
 * para los perfiles de usuario.
 * <p>
 * Esta clase también proporciona métodos con visibilidad de paquete
 * (ej. createWithConnection) para ser utilizados por
 * {@link UsuariosPersistence} dentro de transacciones.</p>
 */
public class PerfilesPersistence {

	/**
	 * Constructor privado para evitar la instanciación de la clase de utilidad.
	 */
	private PerfilesPersistence() {
	}

	/**
	 * Busca y devuelve un Perfil por su ID (que es el ID del usuario).
	 *
	 * @param usuarioId El ID del usuario (clave primaria) del perfil a buscar.
	 * @return El objeto Perfil si se encuentra, o null si no existe.
	 * @throws SQLException Si ocurre un error de acceso a la base de datos.
	 */
	public static final Perfil readById(int usuarioId) throws SQLException {
		String sql = "SELECT usuario_id, biografia, sitio_web, ubicacion FROM perfiles WHERE usuario_id = ?";
		
		try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, usuarioId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapRow(rs);
				}
			}
		}
		return null; // No encontrado
	}

	/**
	 * Recupera todos los Perfiles de la base de datos.
	 *
	 * @return Una lista de objetos Perfil. La lista estará vacía si no hay perfiles.
	 * @throws SQLException Si ocurre un error de acceso a la base de datos.
	 */
	public static final List<Perfil> readAll() throws SQLException {
		String sql = "SELECT usuario_id, biografia, sitio_web, ubicacion FROM perfiles";
		List<Perfil> list = new ArrayList<>();

		try (Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(mapRow(rs));
			}
		}
		return list;
	}

	/**
	 * Crea un nuevo Perfil en la base de datos.
	 * 
	 * <p>Esta operación utiliza su propia conexión y autocommit. No debe usarse
	 * si se requiere una transacción junto con la creación de un Usuario.</p>
	 *
	 * @param perfil El objeto Perfil a crear. Debe tener un usuarioId válido.
	 * @return El mismo objeto Perfil que se pasó como argumento.
	 * @throws SQLException Si ocurre un error de base de datos (ej. clave duplicada).
	 * @see #createWithConnection(Perfil, Connection)
	 */
	public static final Perfil create(Perfil perfil) throws SQLException {
		try (Connection conn = ConnectionFactory.getConnection()) {
			return createWithConnection(perfil, conn);
		}
	}

	/**
	 * Crea un nuevo Perfil utilizando una conexión existente.
	 * 
	 * <p>Este método (con visibilidad de paquete) está diseñado para ser utilizado por
	 * {@link UsuariosPersistence} para asegurar que la creación del perfil
	 * ocurra dentro de la misma transacción que la creación del usuario.</p>
	 *
	 * @param perfil El objeto Perfil a crear.
	 * @param conn   La conexión transaccional existente.
	 * @return El mismo objeto Perfil.
	 * @throws SQLException Si ocurre un error de SQL durante la inserción.
	 */
	static final Perfil createWithConnection(Perfil perfil, Connection conn) throws SQLException {
		String sql = "INSERT INTO perfiles (usuario_id, biografia, sitio_web, ubicacion) VALUES (?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, perfil.getUsuarioId());
			ps.setString(2, perfil.getBiografia());
			ps.setString(3, perfil.getSitioWeb());
			ps.setString(4, perfil.getUbicacion());
			ps.executeUpdate();
		}
		return perfil;
	}

	/**
	 * Actualiza un Perfil existente en la base de datos.
	 * 
	 * <p>Esta operación utiliza su propia conexión y autocommit. No debe usarse
	 * si se requiere una transacción junto con la actualización de un Usuario.</p>
	 *
	 * @param perfil El objeto Perfil con los datos a actualizar.
	 * @return true si el perfil fue actualizado (fila afectada > 0), false en caso contrario.
	 * @throws SQLException Si ocurre un error de acceso a la base de datos.
	 * @see #updateWithConnection(Perfil, Connection)
	 */
	public static final boolean update(Perfil perfil) throws SQLException {
		try (Connection conn = ConnectionFactory.getConnection()) {
			return updateWithConnection(perfil, conn);
		}
	}

	/**
	 * Actualiza un Perfil existente utilizando una conexión existente.
	 * 
	 * <p>Este método (con visibilidad de paquete) está diseñado para ser utilizado por
	 * {@link UsuariosPersistence} para asegurar que la actualización del perfil
	 * ocurra dentro de la misma transacción que la actualización del usuario.</p>
	 *
	 * @param perfil El objeto Perfil a actualizar.
	 * @param conn   La conexión transaccional existente.
	 * @return true si el perfil fue actualizado (fila afectada > 0), false en caso contrario.
	 * @throws SQLException Si ocurre un error de SQL durante la actualización.
	 */
	static final boolean updateWithConnection(Perfil perfil, Connection conn) throws SQLException {
		String sql = "UPDATE perfiles SET biografia = ?, sitio_web = ?, ubicacion = ? WHERE usuario_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, perfil.getBiografia());
			ps.setString(2, perfil.getSitioWeb());
			ps.setString(3, perfil.getUbicacion());
			ps.setInt(4, perfil.getUsuarioId());
			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * Elimina un Perfil de la base de datos.
	 * 
	 * <p>Esta operación utiliza su propia conexión y autocommit.</p>
	 *
	 * @param perfil El Perfil a eliminar (solo se utiliza su usuarioId).
	 * @return true si el perfil fue eliminado, false en caso contrario.
	 * @throws SQLException Si ocurre un error de acceso a la base de datos.
	 */
	public static final boolean delete(Perfil perfil) throws SQLException {
		String sql = "DELETE FROM perfiles WHERE usuario_id = ?";

		try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, perfil.getUsuarioId());
			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * Elimina un Perfil por su ID (usuarioId) utilizando una conexión existente.
	 * 
	 * <p>Este método (con visibilidad de paquete) está diseñado para ser utilizado por
	 * {@link UsuariosPersistence} dentro de una transacción.</p>
	 *
	 * @param usuarioId El ID del perfil a eliminar.
	 * @param conn      La conexión transaccional existente.
	 * @return true si el perfil fue eliminado, false en caso contrario.
	 * @throws SQLException Si ocurre un error de SQL durante la eliminación.
	 */
	static final boolean deleteByIdWithConnection(int usuarioId, Connection conn) throws SQLException {
		String sql = "DELETE FROM perfiles WHERE usuario_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, usuarioId);
			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * Método helper privado para mapear una fila de un ResultSet a un objeto Perfil.
	 *
	 * @param rs El ResultSet posicionado en la fila actual.
	 * @return Un objeto Perfil completamente populado.
	 * @throws SQLException Si ocurre un error al leer los datos del ResultSet.
	 */
	private static Perfil mapRow(ResultSet rs) throws SQLException {
		Perfil p = new Perfil();
		// rs.getInt() devuelve 0 si el valor es NULL, lo cual coincide
		// con la lógica de normalización de los modelos.
		p.setUsuarioId(rs.getInt("usuario_id"));
		p.setBiografia(rs.getString("biografia"));
		p.setSitioWeb(rs.getString("sitio_web"));
		p.setUbicacion(rs.getString("ubicacion"));
		return p;
	}
}