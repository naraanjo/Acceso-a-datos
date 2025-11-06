package com.agustincrespo.u2.D_crud_ws.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Factory class para crear y configurar conexiones a la base de datos.
 * 
 * Esta clase centraliza la lógica de conexión, leyendo la configuración
 * desde DBConfig para proveer objetos Connection a la capa de persistencia.
 * No está pensada para ser instanciada.
 */
public class ConnectionFactory {

    /**
     * Constructor privado para prevenir la instanciación de esta clase de utilidad.
     */
    private ConnectionFactory() {
        // Clase de utilidad
    }

    /**
     * Obtiene una nueva conexión a la base de datos basada en la configuración
     * de db.config.
     *
     * @return Una nueva java.sql.Connection.
     * @throws SQLException Si DbConfig no se puede leer o si DriverManager
     * falla al intentar conectar.
     */
    public static Connection getConnection() throws SQLException {
        Properties props = DbConfig.getProperties();

        String url = props.getProperty("url");

        /*
         * Nota: No necesitamos Class.forName(props.getProperty("driver"))
         * porque los drivers modernos JDBC 4.0+ (como el de SQLite)
         * se registran automáticamente usando el Service Provider Interface (SPI).
         */
        return DriverManager.getConnection(url, props);
    }
}