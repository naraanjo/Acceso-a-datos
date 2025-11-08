package Veterinario.vet;

import java.util.List;
import clinica_model.Certificacion;
import clinica_model.Veterinario;
import clinica_persistence.CertificacionPersistence;
import clinica_persistence.VeterinarioPersistence;

/**
 * Clase que contiene todas las funcionalidades de menú y la lógica de interacción
 * con el usuario para la gestión de Veterinarios y Certificaciones.
 * Actúa como la capa de presentación que llama a los métodos de persistencia (CRUD).
 */
public class FuncionalidadMenu {

	/**
	 * Muestra el menú principal de la aplicación.
	 *
	 * @return La opción numérica seleccionada por el usuario.
	 */
	public static int mostrarMenuPrincipal() {
		System.out.println("\n=== MENÚ PRINCIPAL ===");
		System.out.println("1. Gestión de Veterinarios");
		System.out.println("2. Gestión de Certificaciones");
		System.out.println("3. Mostrar todos los datos del sistema");
		System.out.println("4. Limpiar base de datos");
		System.out.println("0. Salir");
		return Libreria.leerEnteroEnRango("Seleccione una opción: ", 0, 4);
	}

	/**
	 * Muestra el menú de opciones para la gestión de Veterinarios.
	 *
	 * @return La opción numérica seleccionada por el usuario.
	 */
	public static int mostrarMenuVeterinarios() {
		System.out.println("\n=== MENÚ VETERINARIOS ===");
		System.out.println("1. Crear veterinario");
		System.out.println("2. Buscar veterinario por licencia");
		System.out.println("3. Mostrar todos los veterinarios");
		System.out.println("4. Actualizar veterinario");
		System.out.println("5. Eliminar veterinario");
		System.out.println("0. Volver");
		return Libreria.leerEnteroEnRango("Seleccione una opción: ", 0, 5);
	}

	/**
	 * Muestra el menú de opciones para la gestión de Certificaciones.
	 *
	 * @return La opción numérica seleccionada por el usuario.
	 */
	public static int mostrarMenuCertificaciones() {
		System.out.println("\n=== MENÚ CERTIFICACIONES ===");
		System.out.println("1. Crear certificación");
		System.out.println("2. Buscar certificación por ID");
		System.out.println("3. Mostrar todas las certificaciones");
		System.out.println("4. Buscar certificaciones por veterinario");
		System.out.println("5. Actualizar certificación");
		System.out.println("6. Eliminar certificación");
		System.out.println("0. Volver");
		return Libreria.leerEnteroEnRango("Seleccione una opción: ", 0, 6);
	}

	/**
	 * Solicita los datos de un nuevo veterinario (licencia, nombre, apellido, fecha, salario, horario)
	 * y los persiste en la base de datos, incluyendo la opción de crear una certificación inicial.
	 */
	public static void crearVeterinario() {
		System.out.println("\n=== CREAR VETERINARIO ===");
		
		List<Veterinario> existentes = VeterinarioPersistence.readAll();
		if (!existentes.isEmpty()) {
			System.out.println("Veterinarios existentes:");
			for (Veterinario v : existentes) {
				System.out.println("	Licencia " + v.getNum_licencia() + ": " + 
								v.getNombre() + " " + v.getApellido());
			}
			System.out.println();
		}
		
		Veterinario vet = new Veterinario();

		int licencia;
		do {
			licencia = Libreria.leerEnteroPositivo("Número de licencia: ");
			if (VeterinarioPersistence.readById(licencia) != null) {
				System.out.println("Error: Ya existe un veterinario con la licencia " + licencia);
				System.out.println("	Use una licencia diferente.");
			} else {
				break;
			}
		} while (true);
		vet.setNum_licencia(licencia);

		vet.setNombre(Libreria.leerStringNoVacio("Nombre: "));
		vet.setApellido(Libreria.leerStringNoVacio("Apellido: "));

		vet.setFecha_contratacion(Libreria.leerFechaValida("Fecha [yyyy-MM-dd]: "));

		vet.setSalarioBase(Libreria.leerDoubleNoNegativo("Salario base: "));
		vet.setHorarioSemanal(Libreria.leerDoubleEnRango("Horas semanales (1-60): ", 1, 60));

		if (VeterinarioPersistence.create(vet)) {
			System.out.println("Veterinario creado exitosamente.");

			boolean crearCert = Libreria.leerSiNo("¿Desea crear una certificación para este veterinario? (s/n): ");
			if (crearCert) {
				System.out.println("\n--- CREAR CERTIFICACIÓN ---");
				Certificacion cert = new Certificacion();
				cert.setVeterinario_licencia(vet.getNum_licencia());
				cert.setInstitucion_emisora(Libreria.leerStringNoVacio("Institución emisora: "));
				cert.setNombre_especialidad(Libreria.leerStringNoVacio("Nombre de especialidad: "));

				if (CertificacionPersistence.create(cert)) {
					System.out.println("Certificación creada correctamente para el veterinario " + vet.getNombre() + ".");
				} else {
					System.out.println("Error al crear la certificación asociada.");
				}
			}
		} else {
			System.out.println("Error al crear veterinario.");
		}
	}

	/**
	 * Solicita un número de licencia y muestra los detalles del veterinario correspondiente.
	 * Si no existe, notifica al usuario.
	 */
	public static void buscarVeterinarioPorLicencia() {
		int licencia = Libreria.leerEntero("\nIngrese número de licencia: ");
		Veterinario vet = VeterinarioPersistence.readById(licencia);

		if (vet != null) {
			System.out.println("\n--- DETALLES VETERINARIO ---");
			VeterinarioPersistence.mostrarDetallesVeterinario(vet);
		} else {
			System.out.println("Veterinario no encontrado.");
		}
	}

	/**
	 * Recupera y muestra los detalles de todos los veterinarios registrados en la base de datos.
	 */
	public static void mostrarTodosVeterinarios() {
		List<Veterinario> veterinarios = VeterinarioPersistence.readAll();
		if (veterinarios.isEmpty()) {
			System.out.println("No hay veterinarios registrados.");
		} else {
			System.out.println("\n--- LISTADO DE VETERINARIOS ---");
			veterinarios.forEach(VeterinarioPersistence::mostrarDetallesVeterinario);
		}
	}

	/**
	 * Solicita un número de licencia, permite al usuario actualizar los campos del veterinario
	 * y de su contrato, y ofrece la opción de gestionar sus certificaciones asociadas.
	 */
	public static void actualizarVeterinario() {
		int licencia = Libreria.leerEntero("\nIngrese número de licencia del veterinario a actualizar: ");
		Veterinario vet = VeterinarioPersistence.readById(licencia);

		if (vet == null) {
			System.out.println("Veterinario no encontrado.");
			return;
		}

		System.out.println("\n=== ACTUALIZAR VETERINARIO ===");
		System.out.println("Veterinario actual:");
		VeterinarioPersistence.mostrarDetallesVeterinario(vet);
		
		System.out.println("\n--- DATOS PERSONALES ---");
		String nuevoNombre = Libreria.leerStringOpcional("Nuevo nombre (" + vet.getNombre() + "): ");
		if (!nuevoNombre.isBlank()) vet.setNombre(nuevoNombre);

		String nuevoApellido = Libreria.leerStringOpcional("Nuevo apellido (" + vet.getApellido() + "): ");
		if (!nuevoApellido.isBlank()) vet.setApellido(nuevoApellido);

		String fecha=Libreria.leerFechaValida("Fecha [yyyy-MM-dd]: ");
		if(!fecha.isBlank()) vet.setFecha_contratacion(fecha);

		

		System.out.println("\n--- DATOS LABORALES ---");
		Double nuevoSalario = Libreria.leerDoubleOpcional("Nuevo salario base (" + vet.getSalarioBase() + "): ");
		if (nuevoSalario != null) vet.setSalarioBase(nuevoSalario);

		Double nuevoHorario = Libreria.leerDoubleOpcional("Nuevo horario semanal (" + vet.getHorarioSemanal() + "): ");
		if (nuevoHorario != null) vet.setHorarioSemanal(nuevoHorario);

		System.out.println("\n--- GESTIÓN DE CERTIFICACIONES ---");
		gestionarCertificacionesVeterinario(vet);

		if (VeterinarioPersistence.update(vet)) {
			System.out.println("Veterinario actualizado correctamente.");
		} else {
			System.out.println("Error al actualizar veterinario.");
		}
	}

	/**
	 * Menú de gestión anidado para añadir o eliminar certificaciones de un veterinario específico.
	 *
	 * @param vet El veterinario cuyas certificaciones se van a gestionar.
	 */
	private static void gestionarCertificacionesVeterinario(Veterinario vet) {
		
		int opcionCert;
		do {
			// Recarga la lista de certificaciones al inicio de cada ciclo para reflejar cambios.
			List<Certificacion> certificacionesActuales = CertificacionPersistence.readByVeterinarioLicencia(vet.getNum_licencia());
			
			if (!certificacionesActuales.isEmpty()) {
				System.out.println("\nCertificaciones actuales:");
				for (int i = 0; i < certificacionesActuales.size(); i++) {
					Certificacion c = certificacionesActuales.get(i);
					System.out.println("	" + (i + 1) + ". " + c.getNombre_especialidad() + " - " + c.getInstitucion_emisora() + " (ID: " + c.getId() + ")"); 
				}
			} else {
				System.out.println("No hay certificaciones registradas.");
			}

			System.out.println("\n--- OPCIONES CERTIFICACIONES ---");
			System.out.println("1. Añadir nueva certificación");
			System.out.println("2. Eliminar certificación existente");
			System.out.println("0. Volver a actualización del veterinario");
			
			opcionCert = Libreria.leerEnteroEnRango("Seleccione una opción: ", 0, 2);
			
			switch (opcionCert) {
				case 1 -> añadirCertificacionVeterinario(vet);
				case 2 -> eliminarCertificacionVeterinario(certificacionesActuales);
				case 0 -> System.out.println("Continuando con la actualización...");
			}
		} while (opcionCert != 0);
	}

	/**
	 * Solicita los datos para una nueva certificación y la asocia al veterinario dado,
	 * persistiendo el nuevo registro.
	 *
	 * @param vet El veterinario al que se asociará la nueva certificación.
	 */
	private static void añadirCertificacionVeterinario(Veterinario vet) {
		System.out.println("\n--- AÑADIR NUEVA CERTIFICACIÓN ---");
		Certificacion cert = new Certificacion();
		cert.setVeterinario_licencia(vet.getNum_licencia());
		cert.setInstitucion_emisora(Libreria.leerStringNoVacio("Institución emisora: "));
		cert.setNombre_especialidad(Libreria.leerStringNoVacio("Nombre de especialidad: "));

		if (CertificacionPersistence.create(cert)) {
			System.out.println("Certificación añadida correctamente.");
			vet.getCertificacionesMap().put(cert.getId(), cert); //Sincronizar el objeto en memoria
		} else {
			System.out.println("Error al añadir certificación.");
		}
	}

	/**
	 * Muestra una lista de certificaciones y permite al usuario seleccionar una para su eliminación
	 * de la base de datos, previa confirmación.
	 *
	 * @param certificaciones La lista actual de certificaciones del veterinario.
	 */
	private static void eliminarCertificacionVeterinario(List<Certificacion> certificaciones) {
		if (certificaciones.isEmpty()) {
			System.out.println("No hay certificaciones para eliminar.");
			return;
		}

		System.out.println("\n--- ELIMINAR CERTIFICACIÓN ---");
		for (int i = 0; i < certificaciones.size(); i++) {
			Certificacion c = certificaciones.get(i);
			System.out.println((i + 1) + ". " + c.getNombre_especialidad() + " - " + c.getInstitucion_emisora() + " (ID: " + c.getId() + ")");
		}

		int seleccion = Libreria.leerEnteroEnRango("Seleccione el número de certificación a eliminar (0 para cancelar): ", 0, certificaciones.size());
		
		if (seleccion == 0) {
			System.out.println("Operación cancelada.");
			return;
		}

		Certificacion certAEliminar = certificaciones.get(seleccion - 1);
		boolean confirmar = Libreria.leerSiNo("¿Está seguro de eliminar la certificación '" + 
			certAEliminar.getNombre_especialidad() + "'? (s/n): ");
		
		if (confirmar) {
			if (CertificacionPersistence.delete(certAEliminar.getId())) {
				System.out.println("Certificación eliminada correctamente.");
				certificaciones.remove(seleccion - 1);
			} else {
				System.out.println("Error al eliminar certificación.");
			}
		} else {
			System.out.println("Eliminación cancelada.");
		}
	}

	/**
	 * Solicita un número de licencia y elimina el veterinario correspondiente de la base de datos,
	 * incluyendo su contrato y todas sus certificaciones, previa confirmación.
	 */
	public static void eliminarVeterinario() {
		int licencia = Libreria.leerEntero("\nIngrese número de licencia del veterinario a eliminar: ");
		
		Veterinario vet = VeterinarioPersistence.readById(licencia);
		if (vet == null) {
			System.out.println("No existe un veterinario con la licencia especificada.");
			return;
		}

		System.out.println("\nVeterinario a eliminar:");
		System.out.println("Licencia: " + vet.getNum_licencia());
		System.out.println("Nombre: " + vet.getNombre() + " " + vet.getApellido());
		
		boolean confirmar = Libreria.leerSiNo("¿Está seguro de que desea eliminar este veterinario? (s/n): ");
		
		if (!confirmar) {
			System.out.println("Eliminación cancelada.");
			return;
		}

		if (VeterinarioPersistence.delete(licencia)) {
			System.out.println("Veterinario eliminado exitosamente junto con sus certificaciones y contrato.");
		} else {
			System.out.println("Error al eliminar veterinario.");
		}
	}

	/**
	 * Solicita los datos de una nueva certificación (institución, especialidad, licencia de veterinario)
	 * y la persiste en la base de datos. Requiere que el veterinario asociado ya exista.
	 */
	public static void crearCertificacion() {
		System.out.println("\n=== CREAR CERTIFICACIÓN ===");
		Certificacion cert = new Certificacion();

		cert.setInstitucion_emisora(Libreria.leerStringNoVacio("Institución emisora: "));
		cert.setNombre_especialidad(Libreria.leerStringNoVacio("Nombre de especialidad: "));
		int licencia = Libreria.leerEnteroPositivo("Licencia del veterinario: ");
		cert.setVeterinario_licencia(licencia);

		Veterinario vet = VeterinarioPersistence.readById(licencia);
		if (vet == null) {
			System.out.println("Error: No existe un veterinario con licencia " + licencia);
			return;
		}

		if (CertificacionPersistence.create(cert))
			System.out.println("Certificación creada con ID: " + cert.getId());
		else
			System.out.println("Error al crear certificación.");
	}

	/**
	 * Solicita un ID de certificación y muestra sus detalles si se encuentra.
	 */
	public static void buscarCertificacionPorId() {
		int id = Libreria.leerEntero("\nIngrese ID de certificación: ");
		Certificacion cert = CertificacionPersistence.readById(id);

		if (cert != null)
			CertificacionPersistence.mostrarDetallesCertificacion(cert);
		else
			System.out.println("Certificación no encontrada.");
	}

	/**
	 * Recupera y muestra los detalles de todas las certificaciones registradas.
	 */
	public static void mostrarTodasCertificaciones() {
		List<Certificacion> lista = CertificacionPersistence.readAll();
		if (lista.isEmpty()) {
			System.out.println("No hay certificaciones registradas.");
		} else {
			System.out.println("\n--- TODAS LAS CERTIFICACIONES ---");
			lista.forEach(CertificacionPersistence::mostrarDetallesCertificacion);
		}
	}

	/**
	 * Solicita un número de licencia de veterinario y lista todas las certificaciones
	 * asociadas a ese veterinario.
	 */
	public static void buscarCertificacionesPorVeterinario() {
		int licencia = Libreria.leerEntero("\nIngrese número de licencia del veterinario: ");
		List<Certificacion> lista = CertificacionPersistence.readByVeterinarioLicencia(licencia);

		if (lista.isEmpty())
			System.out.println("No se encontraron certificaciones para este veterinario.");
		else
			lista.forEach(CertificacionPersistence::mostrarDetallesCertificacion);
	}

	/**
	 * Solicita un ID de certificación y permite al usuario actualizar la institución emisora
	 * y el nombre de la especialidad.
	 */
	public static void actualizarCertificacion() {
		int id = Libreria.leerEntero("\nIngrese ID de certificación a actualizar: ");
		Certificacion cert = CertificacionPersistence.readById(id);

		if (cert == null) {
			System.out.println("Certificación no encontrada.");
			return;
		}

		String nuevaInst = Libreria.leerStringOpcional("Nueva institución (" + cert.getInstitucion_emisora() + "): ");
		if (!nuevaInst.isBlank()) cert.setInstitucion_emisora(nuevaInst);

		String nuevaEsp = Libreria.leerStringOpcional("Nueva especialidad (" + cert.getNombre_especialidad() + "): ");
		if (!nuevaEsp.isBlank()) cert.setNombre_especialidad(nuevaEsp);

		if (CertificacionPersistence.update(cert))
			System.out.println("Certificación actualizada correctamente.");
		else
			System.out.println("Error al actualizar certificación.");
	}

	/**
	 * Solicita un ID de certificación y procede a su eliminación.
	 */
	public static void eliminarCertificacion() {
		int id = Libreria.leerEntero("\nIngrese ID de certificación a eliminar: ");
		if (CertificacionPersistence.delete(id))
			System.out.println("Certificación eliminada exitosamente.");
		else
			System.out.println("Error al eliminar certificación.");
	}

	/**
	 * Llama a los métodos para mostrar todos los veterinarios y todas las certificaciones
	 * registradas en el sistema.
	 */
	public static void mostrarTodosLosDatos() {
		System.out.println("\n=== DATOS DEL SISTEMA ===");
		mostrarTodosVeterinarios();
		mostrarTodasCertificaciones();
	}
	
	/**
	 * Intenta eliminar todos los registros de la base de datos (Veterinarios y sus datos asociados,
	 * incluyendo certificaciones) para "limpiar" el sistema.
	 */
	public static void limpiarBD() {		
		List<Veterinario> veterinarios = VeterinarioPersistence.readAll();
		List<Certificacion> certificaciones = CertificacionPersistence.readAll();
 
		try {
			int veterinariosEliminados = 0;
			int certificacionesEliminadas = 0;
			
			for (Veterinario vet : veterinarios) {
				if (VeterinarioPersistence.delete(vet.getNum_licencia())) {
					veterinariosEliminados++;
					certificacionesEliminadas += CertificacionPersistence.readByVeterinarioLicencia(vet.getNum_licencia()).size();
				}
			}

			List<Veterinario> veterinariosRestantes = VeterinarioPersistence.readAll();
			List<Certificacion> certificacionesRestantes = CertificacionPersistence.readAll();
			
			if (veterinariosRestantes.isEmpty() && certificacionesRestantes.isEmpty()) {
				System.out.println("Base de datos completamente vacía.");
			}
			
		} catch (Exception e) {
			System.out.println("Error durante la limpieza: " + e.getMessage());
		}
	}
}