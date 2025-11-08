package Veterinario.vet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Clase de utilidad estática para la lectura segura de datos (strings, enteros, dobles, fechas)
 * desde la consola (System.in), con validación básica de formato y rango.
 */
public class Libreria {
	private static Scanner scanner = new Scanner(System.in);
	private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	
	/**
	 * Solicita al usuario una fecha y valida que esté en el formato "yyyy-MM-dd".
	 * Permite la entrada de una cadena vacía, en cuyo caso devuelve una cadena vacía.
	 *
	 * @param mensaje El mensaje a mostrar al usuario.
	 * @return La fecha válida como {@code String} en formato "yyyy-MM-dd", o una cadena vacía si se deja vacío.
	 */
	public static String leerFechaValida(String mensaje) {
		String input = "";
		boolean valido = false;

		do {
			System.out.print(mensaje);
			input = scanner.nextLine().trim();

			if (input.isEmpty()) {
				// Si se deja vacío, se permite devolver cadena vacía
				return "";
			}

			try {
				LocalDate fecha = LocalDate.parse(input, ISO_DATE_FORMATTER);
				// Si se pudo parsear correctamente, la fecha es válida
				valido = true;
				// Devuelve la fecha en formato normalizado (ej: 2025-03-07)
				return fecha.format(ISO_DATE_FORMATTER);

			} catch (DateTimeParseException e) {
				System.out.println("Error: Formato de fecha inválido. Use el formato yyyy-MM-dd (ej: 2024-05-23).");
			}

		} while (!valido);

		return input;
	}

	
	/**
	 * Solicita y lee una cadena de texto desde la consola.
	 *
	 * @param mensaje El mensaje a mostrar al usuario.
	 * @return La cadena de texto introducida por el usuario, sin espacios iniciales/finales.
	 */
	public static String leerString(String mensaje) {
		System.out.print(mensaje);
		return scanner.nextLine().trim();
	}
	
	/**
	 * Solicita y lee una cadena de texto que puede ser opcional (vacía).
	 * Utilizado comúnmente en funciones de actualización donde un campo puede no ser modificado.
	 *
	 * @param mensaje El mensaje a mostrar al usuario.
	 * @return La cadena de texto introducida por el usuario, sin espacios iniciales/finales.
	 */
	public static String leerStringOpcional(String mensaje) {
		System.out.print(mensaje);
		return scanner.nextLine().trim();
	}
	
	/**
	 * Solicita y lee una cadena de texto, repitiendo la solicitud si la entrada está vacía.
	 *
	 * @param mensaje El mensaje a mostrar al usuario.
	 * @return La cadena de texto introducida por el usuario (garantizada no vacía), sin espacios iniciales/finales.
	 */
	public static String leerStringNoVacio(String mensaje) {
		String input;
		do {
			System.out.print(mensaje);
			input = scanner.nextLine().trim();
			if (input.isEmpty()) {
				System.out.println("Error: Este campo no puede estar vacío.");
			}
		} while (input.isEmpty());
		return input;
	}
	
	/**
	 * Solicita al usuario un número entero y valida que la entrada sea un entero válido.
	 *
	 * @param mensaje El mensaje a mostrar al usuario.
	 * @return El número entero introducido.
	 */
	public static int leerEntero(String mensaje) {
		int numero = 0;
		boolean valido;
		do {
			valido = true;
			System.out.print(mensaje);
			String input = scanner.nextLine().trim();
			try {
				numero = Integer.parseInt(input);
			} catch (NumberFormatException e) {
				System.out.println("Error: Por favor ingrese un número entero válido.");
				valido = false;
			}
		} while (!valido);
		return numero;
	}

	/**
	 * Solicita al usuario un número entero y valida que sea positivo (mayor que 0).
	 *
	 * @param mensaje El mensaje a mostrar al usuario.
	 * @return El número entero positivo introducido.
	 */
	public static int leerEnteroPositivo(String mensaje) {
		int numero = 0;
		boolean valido;
		do {
			valido = true;
			System.out.print(mensaje);
			String input = scanner.nextLine().trim();
			try {
				numero = Integer.parseInt(input);
				if (numero <= 0) {
					System.out.println("Error: El número debe ser mayor que cero.");
					valido = false;
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: Por favor ingrese un número entero válido.");
				valido = false;
			}
		} while (!valido);
		return numero;
	}

	/**
	 * Solicita al usuario un número entero y valida que esté dentro del rango [min, max] (inclusivo).
	 *
	 * @param mensaje El mensaje a mostrar al usuario.
	 * @param min El valor mínimo permitido.
	 * @param max El valor máximo permitido.
	 * @return El número entero dentro del rango.
	 */
	public static int leerEnteroEnRango(String mensaje, int min, int max) {
		int numero = 0;
		boolean valido;
		do {
			valido = true;
			System.out.print(mensaje);
			String input = scanner.nextLine().trim();
			try {
				numero = Integer.parseInt(input);
				if (numero < min || numero > max) {
					System.out.println("Error: El número debe estar entre " + min + " y " + max + ".");
					valido = false;
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: Por favor ingrese un número entero válido.");
				valido = false;
			}
		} while (!valido);
		return numero;
	}

	/**
	 * Solicita al usuario un número decimal (double) y valida la entrada.
	 *
	 * @param mensaje El mensaje a mostrar al usuario.
	 * @return El número decimal introducido.
	 */
	public static double leerDouble(String mensaje) {
		double numero = 0.0;
		boolean valido;
		do {
			valido = true;
			System.out.print(mensaje);
			String input = scanner.nextLine().trim();
			try {
				numero = Double.parseDouble(input);
			} catch (NumberFormatException e) {
				System.out.println("Error: Por favor ingrese un número decimal válido.");
				valido = false;
			}
		} while (!valido);
		return numero;
	}

	/**
	 * Solicita al usuario un número decimal (double) y valida que no sea negativo.
	 *
	 * @param mensaje El mensaje a mostrar al usuario.
	 * @return El número decimal no negativo introducido.
	 */
	public static double leerDoubleNoNegativo(String mensaje) {
		double numero = 0.0;
		boolean valido;
		do {
			valido = true;
			System.out.print(mensaje);
			String input = scanner.nextLine().trim();
			try {
				numero = Double.parseDouble(input);
				if (numero < 0) {
					System.out.println("Error: El número no puede ser negativo.");
					valido = false;
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: Por favor ingrese un número decimal válido.");
				valido = false;
			}
		} while (!valido);
		return numero;
	}

	/**
	 * Solicita al usuario un número decimal (double) y valida que esté dentro del rango [min, max] (inclusivo).
	 *
	 * @param mensaje El mensaje a mostrar al usuario.
	 * @param min El valor mínimo permitido.
	 * @param max El valor máximo permitido.
	 * @return El número decimal dentro del rango.
	 */
	public static double leerDoubleEnRango(String mensaje, double min, double max) {
		double numero = 0.0;
		boolean valido;
		do {
			valido = true;
			System.out.print(mensaje);
			String input = scanner.nextLine().trim();
			try {
				numero = Double.parseDouble(input);
				if (numero < min || numero > max) {
					System.out.println("Error: El número debe estar entre " + min + " y " + max + ".");
					valido = false;
				}
			} catch (NumberFormatException e) {
				System.out.println("Error: Por favor ingrese un número decimal válido.");
				valido = false;
			}
		} while (!valido);
		return numero;
	}

	/**
	 * Solicita al usuario un número decimal (double) que puede ser opcional.
	 * Si la entrada está vacía, devuelve {@code null}. Si se ingresa texto, intenta parsear.
	 *
	 * @param mensaje El mensaje a mostrar al usuario.
	 * @return El número decimal introducido, o {@code null} si se dejó vacío.
	 */
	public static Double leerDoubleOpcional(String mensaje) {
		Double numero = null;
		boolean valido;
		do {
			valido = true;
			System.out.print(mensaje);
			String input = scanner.nextLine().trim();
			
			if (input.isEmpty()) {
				return null;
			}
			
			try {
				numero = Double.parseDouble(input);
			} catch (NumberFormatException e) {
				System.out.println("Error: Por favor ingrese un número decimal válido o deje vacío para no cambiar.");
				valido = false;
			}
		} while (!valido);
		return numero;
	}

	/**
	 * Intenta convertir una cadena de texto a un número entero. Captura {@code NumberFormatException}
	 * en caso de error y devuelve 0.
	 *
	 * @param texto La cadena de texto a parsear.
	 * @return El entero resultante, o 0 si falla el parseo.
	 */
	public static int parsearEntero(String texto) {
		try {
			return Integer.parseInt(texto.trim());
		} catch (NumberFormatException e) {
			System.out.println("Error: No se pudo convertir '" + texto + "' a número entero.");
			return 0;
		}
	}

	/**
	 * Intenta convertir una cadena de texto a un número decimal (double). Captura {@code NumberFormatException}
	 * en caso de error y devuelve 0.0.
	 *
	 * @param texto La cadena de texto a parsear.
	 * @return El double resultante, o 0.0 si falla el parseo.
	 */
	public static double parsearDouble(String texto) {
		try {
			return Double.parseDouble(texto.trim());
		} catch (NumberFormatException e) {
			System.out.println("Error: No se pudo convertir '" + texto + "' a número decimal.");
			return 0.0;
		}
	}

	/**
	 * Muestra un mensaje y espera una respuesta de sí o no ('s', 'si', 'sí' / 'n', 'no').
	 *
	 * @param mensaje El mensaje de la pregunta (ej: "¿Desea continuar? (s/n): ").
	 * @return {@code true} si la respuesta es afirmativa, {@code false} si es negativa.
	 */
	public static boolean leerSiNo(String mensaje) {
		String respuesta;
		boolean valido;
		do {
			valido = true;
			respuesta = leerString(mensaje).toLowerCase();
			if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí") || respuesta.equals("y") || respuesta.equals("yes")) {
				return true;
			} else if (respuesta.equals("n") || respuesta.equals("no")) {
				return false;
			} else {
				System.out.println("Error: Por favor ingrese 's' para sí o 'n' para no.");
				valido = false;
			}
		} while (!valido);
		return false; // nunca se llega, pero necesario por sintaxis
	}


}