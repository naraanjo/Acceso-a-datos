package com.agustincrespo.u2.E_crud_desktop.persistence;

// CAMBIO: Imports necesarios para HikariCP
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Factory class que gestiona un pool de conexiones (HikariCP).
 * 
 * <p>
 * Esta clase centraliza la lógica de conexión, leyendo la configuración desde
 * DbConfig para inicializar un pool de conexiones (DataSource) que proveerá
 * conexiones a la capa de persistencia.
 * </p>
 */
public class ConnectionFactory {

	private static final HikariDataSource dataSource;

	/**
	 * Bloque estático de inicialización. Se ejecuta UNA SOLA VEZ cuando la clase es
	 * cargada por la JVM.<br>
	 * Aquí es donde configuramos e iniciamos el pool de conexiones.
	 */
	static {
		try {
			// Cargamos las propiedades desde tu DbConfig
			Properties props = DbConfig.getProperties();

			// Creamos la configuración de Hikari
			HikariConfig config = new HikariConfig();

			// --- Configuración JDBC ---
			// Tomamos la URL y el Driver de db.config
			config.setJdbcUrl(props.getProperty("url"));
			config.setDriverClassName(props.getProperty("driver"));

			// Pasamos las propiedades 'user' y 'password' (aunque en SQLite estén vacías)
			config.setUsername(props.getProperty("user"));
			config.setPassword(props.getProperty("password"));

			// --- Configuración del Pool ---
			// Leemos propiedades del pool desde db.config, o usamos valores por defecto
			// (10 es un buen número para una app de escritorio)
			config.setMaximumPoolSize(Integer.parseInt(props.getProperty("pool.maxSize", "10")));
			config.setMinimumIdle(Integer.parseInt(props.getProperty("pool.minIdle", "2")));
			config.setIdleTimeout(Long.parseLong(props.getProperty("pool.idleTimeout", "600000"))); // 10 min
			config.setConnectionTimeout(Long.parseLong(props.getProperty("pool.connectionTimeout", "30000"))); // 30 sec

			// --- Optimizaciones específicas para SQLite ---
			// Esto mejora MUCHO el rendimiento de PreparedStatement en SQLite
			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

			// Finalmente, inicializamos el pool (DataSource)
			dataSource = new HikariDataSource(config);

		} catch (SQLException e) {
			System.err.println("Error fatal al inicializar el pool de conexiones: " + e.getMessage());
			// Si el pool no puede iniciar, la aplicación no puede funcionar.
			throw new ExceptionInInitializerError(e);
		}
	}

	/**
	 * Constructor privado para prevenir la instanciación de esta clase de utilidad.
	 */
	private ConnectionFactory() {
		// Clase de utilidad
	}

	/**
	 * Obtiene una conexión del pool.
	 * 
	 * <p>
	 * Cuando llames a 'connection.close()', la conexión no se cerrará, sino que se
	 * devolverá automáticamente al pool.
	 *
	 * @return Una java.sql.Connection gestionada por el pool.
	 * @throws SQLException Si el pool no puede proveer una conexión.
	 */
	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	/**
	 * Cierra el pool de conexiones.
	 * 
	 * <p>
	 * Debes llamar a este método cuando tu aplicación de escritorio se esté
	 * cerrando para liberar todos los recursos.
	 */
	public static void closePool() {
		if (dataSource != null) {
			System.out.println("Cerrando pool de conexiones...");
			dataSource.close();
			System.out.println("Pool de conexiones cerrado.");
		}
	}
}
