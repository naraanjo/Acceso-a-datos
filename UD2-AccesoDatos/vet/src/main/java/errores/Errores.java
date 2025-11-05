package errores;

import java.sql.SQLException;

public class Errores {

	public static void notificarError(String accion, SQLException e) {
		int errorCode = e.getErrorCode();
		String mensaje = e.getMessage();

		System.out.print("Error al " + accion + ": ");

		if (errorCode == 19) {

			// Verificación del mensaje para distinguir el tipo de restricción SQLITE

			if (mensaje.contains("UNIQUE constraint failed")) {
				System.out.println("Violación de restricción ÚNICA (Code: 19).");
			} else if (mensaje.contains("FOREIGN KEY constraint failed")) {
				System.out.println("Violación de Clave Foránea (Code: 19).");
			} else if (mensaje.contains("PRIMARY KEY")) {
				System.out.println("Violación de Clave Primaria (Code: 19).");
			} else {
				System.out.println("Violación de Restricción de Integridad (Code: 19).");
			}

		} else {
			System.out.println(mensaje + " (Code: " + errorCode + ")");
		}
	}
}