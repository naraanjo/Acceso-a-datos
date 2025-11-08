package clinica_persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import clinica_model.Certificacion;
import clinica_model.Veterinario;
import errores.Errores;

/**
 * Clase de persistencia y gestión de Veterinarios.
 * * Contiene las operaciones CRUD y utilidades adicionales para mostrar, listar y
 * gestionar relaciones con certificaciones.
 */
public class VeterinarioPersistence {

	



	// ========================= CRUD ==============================
	

	/**
	 * Inserta un nuevo veterinario junto con su contrato y certificaciones en la base de datos.
	 * Utiliza una transacción para asegurar la consistencia.
	 *
	 * @param veterinario El objeto Veterinario a insertar.
	 * @return {@code true} si la inserción (veterinario, contrato y certificaciones) fue exitosa y se hizo commit, {@code false} en caso contrario.
	 */
	public static boolean create(Veterinario veterinario) {
		String sqlVeterinario = "INSERT INTO Veterinario (num_licencia, nombre, apellido, fecha_contratacion) VALUES (?, ?, ?, ?)";
		String sqlContrato = "INSERT INTO detalleContrato (salario_base, horario_semanal, veterinario_licencia) VALUES (?, ?, ?)";

		Connection connection = null;
		boolean insertado = false;

		if (veterinario == null || veterinario.getNum_licencia() <= 0) {
			return false;
		}

		try {
			connection = DatabaseConnection.getConnection();
			connection.setAutoCommit(false);

			PreparedStatement stmtVeterinario = connection.prepareStatement(sqlVeterinario);
			stmtVeterinario.setInt(1, veterinario.getNum_licencia());
			stmtVeterinario.setString(2, veterinario.getNombre());
			stmtVeterinario.setString(3, veterinario.getApellido());
			stmtVeterinario.setString(4, veterinario.getFecha_contratacion());
			insertado = stmtVeterinario.executeUpdate() > 0;
			stmtVeterinario.close();

			if (insertado) {
				PreparedStatement stmtContrato = connection.prepareStatement(sqlContrato);
				stmtContrato.setDouble(1, veterinario.getSalarioBase());
				stmtContrato.setDouble(2, veterinario.getHorarioSemanal());
				stmtContrato.setInt(3, veterinario.getNum_licencia());
				insertado = stmtContrato.executeUpdate() > 0;
				stmtContrato.close();
			}

			// --- Inserción y sincronización de certificaciones ---
			if (insertado) {
				Map<Integer, Certificacion> certificacionesParaReindexar = new HashMap<>();

				int tempId = -1;
				List<Map.Entry<Integer, Certificacion>> entradas = new ArrayList<>(veterinario.getCertificacionesMap().entrySet());
				for (Map.Entry<Integer, Certificacion> entry : entradas) {
					if (entry.getKey() == 0) {
						veterinario.getCertificacionesMap().remove(entry.getKey());
						veterinario.getCertificacionesMap().put(tempId--, entry.getValue());
					}
				}

				Iterator<Map.Entry<Integer, Certificacion>> it = veterinario.getCertificacionesMap().entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<Integer, Certificacion> entry = it.next();
					Certificacion c = entry.getValue();
					Integer oldKey = entry.getKey();

					if (c != null && (oldKey <= 0 || c.getId() == 0)) {
						c.setVeterinario_licencia(veterinario.getNum_licencia());

						if (CertificacionPersistence.create(c)) {
							it.remove();
							certificacionesParaReindexar.put(oldKey, c);
						} else {
							insertado = false;
							break;
						}
					}
				}

				for (Map.Entry<Integer, Certificacion> entry : certificacionesParaReindexar.entrySet()) {
					veterinario.reindexarCertificacion(entry.getKey(), entry.getValue());
				}
			}

			if (insertado)
				connection.commit();
			else
				connection.rollback();

		} catch (SQLException e) {
			Errores.notificarError("crear veterinario (o certificaciones)", e);
			insertado = false;
			try {
				if (connection != null)
					connection.rollback();
			} catch (SQLException e1) {
				Errores.notificarError("hacer rollback", e1);
			}
		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
				} catch (SQLException e) {
					Errores.notificarError("restaurar autoCommit", e);
				}
			}
		}

		return insertado;
	}

	/** * Recupera todos los veterinarios del sistema. 
	 * Para cada veterinario, recupera sus datos principales, los detalles de su contrato 
	 * y las IDs de sus certificaciones asociadas.
	 *
	 * @return Una lista de objetos Veterinario, cada uno con su contrato y IDs de certificaciones cargados.
	 */
	public static List<Veterinario> readAll() {
		List<Veterinario> veterinarios = new ArrayList<>();
		String sqlVeterinario = "SELECT num_licencia, nombre, apellido, fecha_contratacion FROM Veterinario";
		String sqlContrato = "SELECT salario_base, horario_semanal FROM detalleContrato WHERE veterinario_licencia = ?";
		String sqlCertificaciones = "SELECT id FROM Certificacion WHERE veterinario_licencia = ?";

		Connection connection = null;

		try {
			connection = DatabaseConnection.getConnection();

			PreparedStatement pstmt = connection.prepareStatement(sqlVeterinario);
			ResultSet rsVeterinario = pstmt.executeQuery();

			while (rsVeterinario.next()) {
				Veterinario v = new Veterinario(
						rsVeterinario.getInt("num_licencia"),
						rsVeterinario.getString("nombre"),
						rsVeterinario.getString("apellido"),
						rsVeterinario.getString("fecha_contratacion"),
						0, 0.0, 0.0);
				veterinarios.add(v);
			}
			rsVeterinario.close();
			pstmt.close();

			PreparedStatement pstmtContrato = connection.prepareStatement(sqlContrato);
			PreparedStatement pstmtCertificaciones = connection.prepareStatement(sqlCertificaciones);

			for (Veterinario v : veterinarios) {
				pstmtContrato.setInt(1, v.getNum_licencia());
				ResultSet rsContrato = pstmtContrato.executeQuery();
				if (rsContrato.next()) {
					v.setSalarioBase(rsContrato.getDouble("salario_base"));
					v.setHorarioSemanal(rsContrato.getDouble("horario_semanal"));
				}
				rsContrato.close();

				pstmtCertificaciones.setInt(1, v.getNum_licencia());
				ResultSet rsCertificaciones = pstmtCertificaciones.executeQuery();
				ArrayList<Integer> certificacionesIds = new ArrayList<>();
				while (rsCertificaciones.next()) {
					certificacionesIds.add(rsCertificaciones.getInt("id"));
				}
				v.addCertificaciones(certificacionesIds);
				rsCertificaciones.close();
			}

			pstmtContrato.close();
			pstmtCertificaciones.close();

		} catch (SQLException e) {
			Errores.notificarError("leer todos los veterinarios", e);
			veterinarios = new ArrayList<>();
		}

		return veterinarios;
	}

	/** * Recupera un veterinario por su número de licencia. 
	 * También carga los detalles de su contrato y las IDs de sus certificaciones.
	 *
	 * @param num_licencia El número de licencia del veterinario a recuperar.
	 * @return El objeto Veterinario si se encuentra, o {@code null} si no existe o si ocurre un error.
	 */
	public static Veterinario readById(int num_licencia) {
		String sqlVeterinario = "SELECT num_licencia, nombre, apellido, fecha_contratacion FROM Veterinario WHERE num_licencia = ?";
		String sqlContrato = "SELECT id, salario_base, horario_semanal FROM detalleContrato WHERE veterinario_licencia = ?";
		String sqlCertificaciones = "SELECT id FROM Certificacion WHERE veterinario_licencia = ?";

		Veterinario veterinario = null;
		Connection connection = null;

		try {
			connection = DatabaseConnection.getConnection();

			PreparedStatement stmt = connection.prepareStatement(sqlVeterinario);
			stmt.setInt(1, num_licencia);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				String nombre = rs.getString("nombre");
				String apellido = rs.getString("apellido");
				String fecha_contratacion = rs.getString("fecha_contratacion");

				veterinario = new Veterinario(num_licencia, nombre, apellido, fecha_contratacion, 0, 0.0, 0.0);
			}
			rs.close();
			stmt.close();

			if (veterinario != null) {
				PreparedStatement stmtContrato = connection.prepareStatement(sqlContrato);
				stmtContrato.setInt(1, num_licencia);
				ResultSet rsContrato = stmtContrato.executeQuery();
				if (rsContrato.next()) {
					veterinario.setSalarioBase(rsContrato.getDouble("salario_base"));
					veterinario.setHorarioSemanal(rsContrato.getDouble("horario_semanal"));
				}
				rsContrato.close();
				stmtContrato.close();

				PreparedStatement stmtCertificaciones = connection.prepareStatement(sqlCertificaciones);
				stmtCertificaciones.setInt(1, num_licencia);
				ResultSet rsCertificaciones = stmtCertificaciones.executeQuery();
				ArrayList<Integer> certificacionesIds = new ArrayList<>();
				while (rsCertificaciones.next()) {
					certificacionesIds.add(rsCertificaciones.getInt("id"));
				}
				veterinario.addCertificaciones(certificacionesIds);
				rsCertificaciones.close();
				stmtCertificaciones.close();
			}
		} catch (SQLException e) {
			Errores.notificarError("leer veterinario por ID", e);
		}

		return veterinario;
	}

	/**
	 * Actualiza los datos principales de un veterinario y los detalles de su contrato.
	 * Si el contrato no existe en la base de datos, lo inserta.
	 * Utiliza una transacción para asegurar la integridad de la operación.
	 *
	 * @param veterinario El objeto Veterinario con los datos actualizados.
	 * @return {@code true} si la actualización del veterinario y su contrato fue exitosa, {@code false} en caso contrario.
	 */
	public static boolean update(Veterinario veterinario) {
		if (veterinario == null || veterinario.getNum_licencia() <= 0) {
			return false;
		}

		String sqlVeterinarioUpdate = "UPDATE Veterinario SET nombre = ?, apellido = ?, fecha_contratacion = ? WHERE num_licencia = ?";
		String sqlContratoUpdate = "UPDATE DetalleContrato SET salario_base = ?, horario_semanal = ? WHERE veterinario_licencia = ?";
		String sqlContratoInsert = "INSERT INTO DetalleContrato (veterinario_licencia, salario_base, horario_semanal) VALUES (?, ?, ?)";

		Connection connection = null;
		boolean actualizado = false;

		try {
			connection = DatabaseConnection.getConnection();
			connection.setAutoCommit(false);

			PreparedStatement stmt = connection.prepareStatement(sqlVeterinarioUpdate);
			stmt.setString(1, veterinario.getNombre());
			stmt.setString(2, veterinario.getApellido());
			stmt.setString(3, veterinario.getFecha_contratacion());
			stmt.setInt(4, veterinario.getNum_licencia());
			actualizado = stmt.executeUpdate() > 0;
			stmt.close();

			if (actualizado) {
				PreparedStatement stmtContrato = connection.prepareStatement(sqlContratoUpdate);
				stmtContrato.setDouble(1, veterinario.getSalarioBase());
				stmtContrato.setDouble(2, veterinario.getHorarioSemanal());
				stmtContrato.setInt(3, veterinario.getNum_licencia());
				boolean contratoUpdate = stmtContrato.executeUpdate() > 0;
				stmtContrato.close();

				if (!contratoUpdate) {
					// El contrato no existía, intentar insertarlo
					PreparedStatement stmtContratoInsert = connection.prepareStatement(sqlContratoInsert);
					stmtContratoInsert.setInt(1, veterinario.getNum_licencia());
					stmtContratoInsert.setDouble(2, veterinario.getSalarioBase());
					stmtContratoInsert.setDouble(3, veterinario.getHorarioSemanal());
					actualizado = stmtContratoInsert.executeUpdate() > 0;
					stmtContratoInsert.close();
				}
			}

			if (actualizado)
				connection.commit();
			else
				connection.rollback();

		} catch (SQLException e) {
			Errores.notificarError("actualizar veterinario", e);
			actualizado = false;
			try {
				if (connection != null)
					connection.rollback();
			} catch (SQLException e1) {
				Errores.notificarError("hacer rollback", e1);
			}
		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
				} catch (SQLException e) {
					Errores.notificarError("restaurar autoCommit", e);
				}
			}
		}

		return actualizado;
	}

	/** * Elimina un veterinario de la base de datos por su número de licencia. 
	 * Primero elimina las certificaciones asociadas, luego el contrato y finalmente el registro del veterinario,
	 * todo dentro de una transacción.
	 *
	 * @param num_licencia El número de licencia del veterinario a eliminar.
	 * @return {@code true} si el veterinario fue encontrado y eliminado exitosamente, {@code false} en caso contrario.
	 */
	public static boolean delete(int num_licencia) {
		// Validación del parámetro
		if (num_licencia <= 0) {
			return false;
		}

		// Sentencias SQL (primero certificaciones, luego contrato, luego veterinario)
		String sqlDeleteCertificaciones = "DELETE FROM Certificacion WHERE veterinario_licencia = ?";
		String sqlDeleteContrato = "DELETE FROM DetalleContrato WHERE veterinario_licencia = ?";
		String sqlDeleteVeterinario = "DELETE FROM Veterinario WHERE num_licencia = ?";

		Connection connection = null;
		boolean eliminado = false;

		try {
			connection = DatabaseConnection.getConnection();
			connection.setAutoCommit(false);

			// Eliminar primero las certificaciones asociadas
			PreparedStatement stmtCertificaciones = connection.prepareStatement(sqlDeleteCertificaciones);
			stmtCertificaciones.setInt(1, num_licencia);
			stmtCertificaciones.executeUpdate();
			stmtCertificaciones.close();

			// Eliminar el contrato del veterinario
			PreparedStatement stmtContrato = connection.prepareStatement(sqlDeleteContrato);
			stmtContrato.setInt(1, num_licencia);
			stmtContrato.executeUpdate(); // No importa si no existe, puede devolver 0
			stmtContrato.close();

			//  Eliminar el veterinario principal
			PreparedStatement stmtVet = connection.prepareStatement(sqlDeleteVeterinario);
			stmtVet.setInt(1, num_licencia);
			eliminado = stmtVet.executeUpdate() > 0; // true si se eliminó al menos 1 fila
			stmtVet.close();

			// Confirmar o revertir transacción según el resultado
			if (eliminado) {
				connection.commit();
				System.out.println("Veterinario y todos sus datos asociados eliminados exitosamente.");
			} else {
				connection.rollback();
				System.out.println("No se encontró el veterinario para eliminar.");
			}

		} catch (SQLException e) {
			// Manejo de errores SQL
			Errores.notificarError("eliminar veterinario y datos asociados", e);
			eliminado = false;
			try {
				if (connection != null)
					connection.rollback();
			} catch (SQLException e1) {
				Errores.notificarError("hacer rollback en delete", e1);
			}
		} finally {
			// Restaurar autoCommit a su estado original
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
				} catch (SQLException e) {
					Errores.notificarError("restaurar autoCommit", e);
				}
			}
		}

		// Devuelve el resultado de la eliminación
		return eliminado;
	}

	/** * Elimina un veterinario de la base de datos a partir de un objeto {@code Veterinario}. 
	 * Internamente llama a {@code delete(int num_licencia)}.
	 *
	 * @param veterinario El objeto Veterinario a eliminar.
	 * @return {@code true} si el veterinario fue eliminado, {@code false} si el objeto es nulo o la eliminación falló.
	 */
	public static boolean delete(Veterinario veterinario) {
		if (veterinario != null)
			return delete(veterinario.getNum_licencia());
		else
			return false;
	}

	

	/** * Muestra por consola los detalles de un veterinario dado, incluyendo su información 
	 * principal, contrato y una lista de sus certificaciones (recuperadas de la BD).
	 *
	 * @param v El objeto Veterinario cuyos detalles se van a mostrar.
	 */
	public static void mostrarDetallesVeterinario(Veterinario v) {
		if (v == null) {
			System.out.println("Veterinario no encontrado o nulo.");
			return;
		}

		System.out.println("\n--- DETALLES VETERINARIO ---");
		System.out.println("Licencia: " + v.getNum_licencia());
		System.out.println("Nombre: " + v.getNombre() + " " + v.getApellido());
		System.out.println("Fecha de contratación: " + v.getFecha_contratacion());
		System.out.println("Salario base: " + v.getSalarioBase());
		System.out.println("Horario semanal: " + v.getHorarioSemanal());

		List<Certificacion> certificaciones = CertificacionPersistence.readByVeterinarioLicencia(v.getNum_licencia());
		if (certificaciones.isEmpty()) {
			System.out.println("Sin certificaciones registradas.");
		} else {
			System.out.println("\n--- CERTIFICACIONES ---");
			for (Certificacion c : certificaciones) {
				System.out.println("* " + c.getNombre_especialidad() + " - " + c.getInstitucion_emisora());
			}
		}
	}

	/** * Recupera y muestra en consola los detalles de todos los veterinarios registrados en el sistema.
	 * Llama a {@code readAll()} y luego a {@code mostrarDetallesVeterinario(Veterinario v)} para cada uno.
	 */
	public static void mostrarTodosVeterinarios() {
		List<Veterinario> lista = readAll();
		if (lista.isEmpty()) {
			System.out.println("No hay veterinarios registrados.");
			return;
		}

		System.out.println("\n=== LISTA DE VETERINARIOS ===");
		for (Veterinario v : lista) {
			mostrarDetallesVeterinario(v);
		}
	}

	/** * Recupera y muestra en consola los detalles de un veterinario específico según su licencia. 
	 * Llama a {@code readById(int licencia)} y luego a {@code mostrarDetallesVeterinario(Veterinario v)}.
	 *
	 * @param licencia El número de licencia del veterinario a mostrar.
	 */
	public static void mostrarVeterinarioPorLicencia(int licencia) {
		Veterinario v = readById(licencia);
		mostrarDetallesVeterinario(v);
	}

	/** * Muestra en consola una lista de los veterinarios que poseen al menos una certificación.
	 * Se listan solo nombre, apellido y licencia.
	 */
	public static void mostrarVeterinariosConCertificaciones() {
		List<Veterinario> lista = readAll();
		System.out.println("\n=== VETERINARIOS CON CERTIFICACIONES ===");
		for (Veterinario v : lista) {
			List<Certificacion> certs = CertificacionPersistence.readByVeterinarioLicencia(v.getNum_licencia());
			if (!certs.isEmpty()) {
				System.out.println("- " + v.getNombre() + " " + v.getApellido() + " (" + v.getNum_licencia() + ")");
			}
		}
	}


}