package clinica_persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import clinica_model.Certificacion;
import clinica_model.Veterinario;
import errores.Errores;

/**
 * Clase de persistencia para la entidad {@link Certificacion}.
 * 
 * <p>Gestiona las operaciones CRUD sobre la base de datos y 
 * proporciona utilidades para mostrar y consultar datos relacionados 
 * con las certificaciones y veterinarios.</p>
 */
public class CertificacionPersistence {

    // MÉTODOS CRUD 

    /**
     * Inserta una nueva certificación en la base de datos.
     *
     * @param certificacion objeto {@link Certificacion} a insertar
     * @return {@code true} si la operación fue exitosa, {@code false} en caso contrario
     */
    public static boolean create(Certificacion certificacion) {
        String sql = "INSERT INTO Certificacion (institucion_emisora, nombre_especialidad, veterinario_licencia) VALUES (?, ?, ?)";

        Connection connection = null;
        boolean insertado = false;

        if (certificacion == null || certificacion.getVeterinario_licencia() <= 0) {
            return false;
        }

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, certificacion.getInstitucion_emisora());
            stmt.setString(2, certificacion.getNombre_especialidad());
            stmt.setInt(3, certificacion.getVeterinario_licencia());

            insertado = stmt.executeUpdate() > 0;

            if (insertado) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        certificacion.setId(generatedKeys.getInt(1));
                    }
                }
                connection.commit();
            } else {
                connection.rollback();
            }

            stmt.close();

        } catch (SQLException e) {
            Errores.notificarError("crear certificacion", e);
            insertado = false;
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                Errores.notificarError("hacer rollback al crear certificacion", e1);
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    Errores.notificarError("cerrar conexión al crear certificacion", e);
                }
            }
        }

        return insertado;
    }

    /**
     * Recupera todas las certificaciones de la base de datos.
     *
     * @return lista de objetos {@link Certificacion}; lista vacía si no hay registros
     */
    public static List<Certificacion> readAll() {
        List<Certificacion> certificaciones = new ArrayList<>();
        String sql = "SELECT id, institucion_emisora, nombre_especialidad, veterinario_licencia FROM Certificacion";

        Connection connection = null;

        try {
            connection = DatabaseConnection.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Certificacion certificacion = new Certificacion(
                        rs.getInt("id"),
                        rs.getString("institucion_emisora"),
                        rs.getString("nombre_especialidad"),
                        rs.getInt("veterinario_licencia"));
                certificaciones.add(certificacion);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            Errores.notificarError("leer todas las certificaciones", e);
            certificaciones = new ArrayList<>();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                Errores.notificarError("restaurar autoCommit", e);
            }
        }

        return certificaciones;
    }

    /**
     * Recupera una certificación específica por su identificador.
     *
     * @param id identificador único de la certificación
     * @return objeto {@link Certificacion} si existe, o {@code null} si no se encuentra
     */
    public static Certificacion readById(int id) {
        String sql = "SELECT id, institucion_emisora, nombre_especialidad, veterinario_licencia FROM Certificacion WHERE id = ?";
        Certificacion certificacion = null;

        Connection connection = null;

        try {
            connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                certificacion = new Certificacion(
                        rs.getInt("id"),
                        rs.getString("institucion_emisora"),
                        rs.getString("nombre_especialidad"),
                        rs.getInt("veterinario_licencia"));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            Errores.notificarError("leer certificacion por ID", e);
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                Errores.notificarError("restaurar autoCommit", e);
            }
        }

        return certificacion;
    }

    /**
     * Recupera todas las certificaciones asociadas a un veterinario por su número de licencia.
     *
     * @param veterinarioLicencia número de licencia del veterinario
     * @return lista de objetos {@link Certificacion} pertenecientes al veterinario indicado
     */
    public static List<Certificacion> readByVeterinarioLicencia(int veterinarioLicencia) {
        List<Certificacion> certificaciones = new ArrayList<>();
        String sql = "SELECT id, institucion_emisora, nombre_especialidad, veterinario_licencia FROM Certificacion WHERE veterinario_licencia = ?";

        Connection connection = null;

        try {
            connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, veterinarioLicencia);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Certificacion certificacion = new Certificacion(
                        rs.getInt("id"),
                        rs.getString("institucion_emisora"),
                        rs.getString("nombre_especialidad"),
                        rs.getInt("veterinario_licencia"));
                certificaciones.add(certificacion);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            Errores.notificarError("leer certificaciones por licencia de veterinario", e);
            certificaciones = new ArrayList<>();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                Errores.notificarError("restaurar autoCommit", e);
            }
        }

        return certificaciones;
    }

    /**
     * Actualiza los datos de una certificación existente en la base de datos.
     *
     * @param certificacion objeto {@link Certificacion} con los datos actualizados
     * @return {@code true} si la operación fue exitosa, {@code false} si no se actualizó
     */
    public static boolean update(Certificacion certificacion) {
        if (certificacion == null || certificacion.getId() <= 0) {
            return false;
        }

        String sql = "UPDATE Certificacion SET institucion_emisora = ?, nombre_especialidad = ?, veterinario_licencia = ? WHERE id = ?";

        Connection connection = null;
        boolean actualizado = false;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, certificacion.getInstitucion_emisora());
            stmt.setString(2, certificacion.getNombre_especialidad());
            stmt.setInt(3, certificacion.getVeterinario_licencia());
            stmt.setInt(4, certificacion.getId());

            actualizado = stmt.executeUpdate() > 0;
            stmt.close();

            if (actualizado)
                connection.commit();
            else
                connection.rollback();

        } catch (SQLException e) {
            Errores.notificarError("actualizar certificacion", e);
            actualizado = false;
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                Errores.notificarError("hacer rollback al actualizar certificacion", e1);
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    Errores.notificarError("cerrar conexión al actualizar certificacion", e);
                }
            }
        }

        return actualizado;
    }

    /**
     * Elimina una certificación de la base de datos por su identificador.
     *
     * @param id identificador único de la certificación
     * @return {@code true} si fue eliminada correctamente, {@code false} si no se eliminó
     */
    public static boolean delete(int id) {
        if (id <= 0) {
            return false;
        }

        String sql = "DELETE FROM Certificacion WHERE id = ?";

        Connection connection = null;
        boolean eliminado = false;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            eliminado = stmt.executeUpdate() > 0;
            stmt.close();

            if (eliminado)
                connection.commit();
            else
                connection.rollback();

        } catch (SQLException e) {
            Errores.notificarError("eliminar certificacion", e);
            eliminado = false;
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                Errores.notificarError("hacer rollback al eliminar certificacion", e1);
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    Errores.notificarError("cerrar conexión al eliminar certificacion", e);
                }
            }
        }

        return eliminado;
    }

    /**
     * Elimina una certificación usando el objeto {@link Certificacion} como parámetro.
     *
     * @param certificacion objeto a eliminar
     * @return {@code true} si fue eliminada correctamente, {@code false} si el objeto es nulo o inválido
     */
    public static boolean delete(Certificacion certificacion) {
        if (certificacion != null) {
            return delete(certificacion.getId());
        } else {
            return false;
        }
    }

    // MÉTODOS AUXILIARES 

    /**
     * Muestra en consola los detalles de una certificación específica.
     *
     * @param cert objeto {@link Certificacion} cuyos datos se mostrarán
     */
    public static void mostrarDetallesCertificacion(Certificacion cert) {
        if (cert == null) {
            System.out.println("Certificación nula o no encontrada.");
            return;
        }

        System.out.println("\n--- DETALLES CERTIFICACIÓN ---");
        System.out.println("ID: " + cert.getId());
        System.out.println("Especialidad: " + cert.getNombre_especialidad());
        System.out.println("Institución: " + cert.getInstitucion_emisora());
        System.out.println("Licencia Veterinario: " + cert.getVeterinario_licencia());

        try {
            Veterinario vet = VeterinarioPersistence.readById(cert.getVeterinario_licencia());
            if (vet != null) {
                System.out.println("Veterinario: " + vet.getNombre() + " " + vet.getApellido());
            }
        } catch (Exception e) {
            System.out.println("Veterinario: información no disponible.");
        }
    }

    /**
     * Muestra en consola todas las certificaciones registradas en el sistema.
     */
    public static void mostrarTodasCertificaciones() {
        List<Certificacion> certificaciones = readAll();
        if (certificaciones == null || certificaciones.isEmpty()) {
            System.out.println("No hay certificaciones registradas.");
            return;
        }

        System.out.println("\n--- TODAS LAS CERTIFICACIONES ---");
        for (Certificacion cert : certificaciones) {
            mostrarDetallesCertificacion(cert);
        }
    }

    /**
     * Muestra en consola todas las certificaciones pertenecientes a un veterinario específico.
     *
     * @param licenciaVet número de licencia del veterinario
     */
    public static void mostrarCertificacionesPorVeterinario(int licenciaVet) {
        List<Certificacion> certificaciones = readByVeterinarioLicencia(licenciaVet);
        if (certificaciones == null || certificaciones.isEmpty()) {
            System.out.println("No se encontraron certificaciones para el veterinario con licencia " + licenciaVet);
            return;
        }

        System.out.println("\n--- CERTIFICACIONES DEL VETERINARIO " + licenciaVet + " ---");
        for (Certificacion cert : certificaciones) {
            mostrarDetallesCertificacion(cert);
        }
    }

    /**
     * Recupera un objeto {@link Veterinario} según su número de licencia.
     * <p>Este método evita dependencias circulares entre las clases de modelo y persistencia.</p>
     *
     * @param licencia número de licencia del veterinario
     * @return objeto {@link Veterinario} correspondiente o {@code null} si no se encuentra
     */
    public static Veterinario readVeterinarioByLicencia(int licencia) {
        return VeterinarioPersistence.readById(licencia);
    }

}
