package com.agustincrespo.u2.E_crud_desktop.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.agustincrespo.u2.E_crud_desktop.model.Perfil;
import com.agustincrespo.u2.E_crud_desktop.model.Usuario;

/**
 * Clase de persistencia (DAO) para la entidad Usuario. Gestiona todas las
 * operaciones CRUD (Crear, Leer, Actualizar, Borrar) para los usuarios y sus
 * perfiles asociados en la base de datos. Esta es una clase de utilidad y no
 * debe ser instanciada.
 */
public class UsuariosPersistence {

	/**
	 * Constructor privado para evitar la instanciación de la clase de utilidad.
	 */
	private UsuariosPersistence() {
	}

	/**
	 * Busca y devuelve un Usuario por su ID, incluyendo su Perfil si existe.
	 * Realiza un LEFT JOIN con la tabla de perfiles.
	 *
	 * @param id El ID (clave primaria) del usuario a buscar.
	 * @return El objeto Usuario completo (con Perfil) si se encuentra, o null si no
	 *         se encuentra ningún usuario con ese ID.
	 * @throws SQLException Si ocurre un error de acceso a la base de datos.
	 */
	public static final Usuario readById(int id) throws SQLException {
		String sql = "SELECT u.id, u.nombre, u.email, u.password_hash, u.fecha_creacion, u.esta_activo, "
				+ "p.usuario_id, p.biografia, p.sitio_web, p.ubicacion "
				+ "FROM usuarios u LEFT JOIN perfiles p ON u.id = p.usuario_id WHERE u.id = ?";

		try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapRow(rs);
				}
			}
		}
		return null; // No encontrado
	}

	/**
	 * Recupera todos los Usuarios de la base de datos, incluyendo sus Perfiles si
	 * existen. Realiza un LEFT JOIN con la tabla de perfiles.
	 *
	 * @return Una lista de objetos Usuario. La lista estará vacía si no hay
	 *         usuarios.
	 * @throws SQLException Si ocurre un error de acceso a la base de datos.
	 */
	public static final List<Usuario> readAll() throws SQLException {
		String sql = "SELECT u.id, u.nombre, u.email, u.password_hash, u.fecha_creacion, u.esta_activo, "
				+ "p.usuario_id, p.biografia, p.sitio_web, p.ubicacion "
				+ "FROM usuarios u LEFT JOIN perfiles p ON u.id = p.usuario_id";
		List<Usuario> list = new ArrayList<>();

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
	 * Crea un nuevo Usuario en la base de datos. Si el objeto Usuario contiene un
	 * Perfil, también lo crea. Esta operación es transaccional (atómica).
	 *
	 * @param usuario El objeto Usuario a crear. El ID del usuario se actualizará en
	 *                este objeto tras la inserción.
	 * @return El mismo objeto Usuario, actualizado con el ID generado por la base
	 *         de datos.
	 * @throws SQLException Si ocurre un error de base de datos o si la transacción
	 *                      falla.
	 */
	public static final Usuario create(Usuario usuario) throws SQLException {
		String sql = "INSERT INTO usuarios (nombre, email, password_hash, esta_activo) VALUES (?, ?, ?, ?)";
		Connection conn = null;
		try {
			conn = ConnectionFactory.getConnection();
			conn.setAutoCommit(false); // Mantenemos la transacción por si el perfil falla

			try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				ps.setString(1, usuario.getNombre());
				ps.setString(2, usuario.getEmail());
				ps.setString(3, usuario.getPasswordHash());
				ps.setInt(4, usuario.isActivo()? 1 : 0);
				ps.executeUpdate();
				try (ResultSet gk = ps.getGeneratedKeys()) {
					if (gk.next()) {
						usuario.setId(gk.getInt(1)); // El ID generado se asigna en el modelo
					}
				}
			}
			// Si el usuario tiene un perfil, lo creamos también
			if (usuario.getPerfil() != null) {
				Perfil p = usuario.getPerfil();
				p.setUsuarioId(usuario.getId());
				PerfilesPersistence.create(p);
			}

			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
		} finally {
			if (conn != null)
				conn.setAutoCommit(true); // Restauramos el auto-commit
		}
		return usuario;
	}

	/**
	 * Actualiza un Usuario existente en la base de datos. También sincroniza el
	 * estado del Perfil: - Si el perfil existe en el modelo, lo actualiza o crea. -
	 * Si el perfil es null en el modelo, lo elimina de la base de datos. Esta
	 * operación es transaccional (atómica).
	 *
	 * @param usuario El objeto Usuario con los datos a actualizar. Debe tener un ID
	 *                válido.
	 * @return true si el usuario fue actualizado, false en caso contrario (por
	 *         ejemplo, si el ID no existía).
	 * @throws SQLException Si ocurre un error de base de datos o si la transacción
	 *                      falla.
	 */
	public static final boolean update(Usuario usuario) throws SQLException {
		String sql = "UPDATE usuarios SET nombre = ?, email = ?, password_hash = ?, esta_activo = ? WHERE id = ?";

		try (Connection conn = ConnectionFactory.getConnection()) {
			conn.setAutoCommit(false);
			boolean userUpdated;

			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setString(1, usuario.getNombre());
				ps.setString(2, usuario.getEmail());
				ps.setString(3, usuario.getPasswordHash());
				ps.setInt(4, usuario.isActivo()? 1 : 0);
				ps.setInt(5, usuario.getId());
				userUpdated = ps.executeUpdate() > 0;
			}

			Perfil perfil = usuario.getPerfil();
			if (perfil == null) {
				// Si el usuario pone su perfil a null, lo borramos de la BBDD
				PerfilesPersistence.delete(new Perfil(usuario.getId(), null, null, null));
			} else {
				// Si el perfil existe, intentamos actualizarlo o crearlo
				perfil.setUsuarioId(usuario.getId());
				boolean updated = PerfilesPersistence.update(perfil);
				if (!updated) {
					PerfilesPersistence.create(perfil);
				}
			}
			conn.commit();
			return userUpdated;
		}
	}

	/**
	 * Elimina un Usuario de la base de datos. Confía en la configuración 'ON DELETE
	 * CASCADE' de la clave foránea en la tabla 'perfiles' para eliminar
	 * automáticamente el perfil asociado.
	 *
	 * @param usuario El Usuario a eliminar (solo se utiliza su ID).
	 * @return true si el usuario fue eliminado, false en caso contrario.
	 * @throws SQLException Si ocurre un error de acceso a la base de datos.
	 */
	public static final boolean delete(Usuario usuario) throws SQLException {
		// La BBDD se encarga de borrar el perfil gracias a 'ON DELETE CASCADE'.
		String sql = "DELETE FROM usuarios WHERE id = ?";

		try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, usuario.getId());
			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * Método helper privado para mapear una fila de un ResultSet a un objeto
	 * Usuario. Este método lee todas las columnas del SELECT (incluyendo el LEFT
	 * JOIN) y construye el objeto Usuario, asignando su Perfil si este existe.
	 *
	 * @param rs El ResultSet posicionado en la fila actual.
	 * @return Un objeto Usuario completamente populado.
	 * @throws SQLException Si ocurre un error al leer los datos del ResultSet.
	 */
	private static Usuario mapRow(ResultSet rs) throws SQLException {
		Usuario u = new Usuario();

		u.setId(rs.getInt("id"));
		u.setNombre(rs.getString("nombre"));
		u.setEmail(rs.getString("email"));
		u.setPasswordHash(rs.getString("password_hash"));
		u.setFechaCreacion(rs.getString("fecha_creacion"));
		u.setEstaActivo(rs.getInt("esta_activo"));

		int perfilUsuarioId = rs.getInt("usuario_id");

		if (!rs.wasNull()) {
			// Si no era NULL, significa que el LEFT JOIN encontró un perfil.
			Perfil p = new Perfil();
			p.setUsuarioId(perfilUsuarioId);
			p.setBiografia(rs.getString("biografia"));
			p.setSitioWeb(rs.getString("sitio_web"));
			p.setUbicacion(rs.getString("ubicacion"));
			u.setPerfil(p);
		}
		// Si era NULL, u.getPerfil(), el perfil se queda como null (valor por defecto).

		return u;
	}
}