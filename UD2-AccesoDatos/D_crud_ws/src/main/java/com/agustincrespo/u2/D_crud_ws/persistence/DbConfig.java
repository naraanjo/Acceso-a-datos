package com.agustincrespo.u2.D_crud_ws.persistence;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DbConfig es un singleton que carga la configuración de la base de datos desde
 * el fichero "db.config" y expone el contenido en un objeto
 * java.util.Properties.
 *
 * Uso: Properties props = DbConfig.getInstance().getProperties();
 *
 * Notas: - El fichero buscado es "db.config" en el directorio de trabajo. - Si
 * no se puede leer el fichero se lanza SQLException con información. - Se
 * proporciona el método reload() para forzar la recarga en tiempos de ejecución
 * (por ejemplo en tests).
 */
public final class DbConfig {

	private static final Properties properties = new Properties();

	/**
	 * Constructor privado para prevenir la instanciación de esta clase de utilidad.
	 */
	private DbConfig() {
	}

	/**
	 * Carga la configuración desde el fichero "db.config" ubicado en el directorio
	 * de trabajo.
	 *
	 * Este método intenta abrir el fichero mediante FileInputStream y cargar sus
	 * pares clave=valor en la propiedad estática properties.
	 *
	 * Comportamiento: - Si el fichero se carga correctamente, properties contendrá
	 * las claves y valores definidos en el fichero. - Si se produce cualquier error
	 * al leer o parsear el fichero, se escribe un mensaje en System.err y se lanza
	 * SQLException con la causa original.
	 *
	 * Nota: es un método privado estático usado internamente por getProperties y
	 * reload. No realiza validaciones adicionales sobre las claves/valores
	 * cargados; el consumidor debe leer las propiedades esperadas (p. ej. db.url,
	 * db.driver, db.user, db.password).
	 *
	 * @throws SQLException si no se puede abrir o leer el fichero de configuración
	 */
	private static void cargarConfiguracion() throws SQLException {
		try (InputStream in = new FileInputStream("db.config")) {
			DbConfig.properties.load(in);
		} catch (Exception e) {
			System.err.println("Error al cargar db.config: " + e.getMessage());
			throw new SQLException("No se ha podido cargar la configuración de la base de datos, archivo 'db.config'",
					e);
		}
	}

	/**
	 * Devuelve las propiedades cargadas desde db.config. Si aún no se han cargado,
	 * intenta cargarlas en ese momento.
	 *
	 * Comportamiento: - Si las properties ya estaban cargadas devuelve la instancia
	 * compartida. - Si no estaban cargadas, llama a cargarConfiguracion() y
	 * devuelve las properties resultantes. - Si cargarConfiguracion() falla se
	 * propaga SQLException al llamante.
	 *
	 * Uso típico: Properties props = DbConfig.getProperties();
	 *
	 * @return las propiedades cargadas desde el fichero db.config
	 * @throws SQLException si no se puede leer el fichero de configuración
	 */
	public static Properties getProperties() throws SQLException {
		if (DbConfig.properties.isEmpty()) {
			DbConfig.cargarConfiguracion();
		}
		return DbConfig.properties;
	}

	/**
	 * Fuerza la recarga de la configuración desde el fichero db.config.
	 *
	 * Este método borra las properties cargadas y vuelve a ejecutar la carga. Se
	 * utiliza cuando se espera que el fichero haya cambiado en tiempo de ejecución
	 * (p. ej. en pruebas). Si la recarga falla se lanza SQLException para que el
	 * llamante pueda manejar el error.
	 *
	 * @throws SQLException si no se puede leer el fichero al recargar
	 */
	public static void reload() throws SQLException {
		DbConfig.properties.clear();
		DbConfig.cargarConfiguracion();
	}
}