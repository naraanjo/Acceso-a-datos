package clinica_persistence;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton para manejar la conexión a la base de datos.
 * NO ES SEGURO PARA ENTORNOS MULTIHILO.
 */
public class DatabaseConnection {

    private static Connection connection;

    private DatabaseConnection() throws SQLException {
        Properties properties = new Properties();
        try {
            properties.load(DatabaseConnection.class.getResourceAsStream("/db.properties"));
        } catch (Exception e) {
            throw new RuntimeException("Fallo al cargar la conexión a la base de datos: fin del programa.", e);
        }
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");
        connection = DriverManager.getConnection(url, user, password);
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null) {
            new DatabaseConnection();
        }
        return connection;
    }
}