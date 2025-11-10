package com.agustincrespo.u3.hibernate.intermedio.persistence.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Este Converter enseña a JPA/Hibernate cómo persistir el tipo moderno
 * 'LocalDateTime' de Java en una columna de BBDD que almacena 'TEXT' (VARCHAR),
 * como hace SQLite con DATETIME.
 *
 * NOTA: SQLite no almacena milisegundos por defecto, así que usamos un
 * formateador que los excluye.
 */
@Converter(autoApply = false) // No lo aplicamos automáticamente, lo especificamos con @Convert
public class LocalDateTimeToStringConverter implements AttributeConverter<LocalDateTime, String> {

	// Formato estándar de SQLite: YYYY-MM-DD HH:MM:SS
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/**
	 * Convierte el objeto LocalDateTime de Java a un String para la BBDD. (Ej:
	 * LocalDateTime.now() -> "2025-10-22 10:30:00")
	 */
	@Override
	public String convertToDatabaseColumn(LocalDateTime attribute) {
		if (attribute == null) {
			return null;
		}
		try {
			return attribute.format(FORMATTER);
		} catch (Exception e) {
			return attribute.toString(); // Fallback
		}
	}

	/**
	 * Convierte el String de la BBDD (TEXT) a un objeto LocalDateTime para Java.
	 * (Ej: "2025-10-22 10:30:00" -> LocalDateTime.of(2025, 10, 22, 10, 30, 0))
	 */
	@Override
	public LocalDateTime convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isBlank()) {
			return null;
		}
		try {
			return LocalDateTime.parse(dbData, FORMATTER);
		} catch (Exception e) {
			// Fallback si el formato es ligeramente diferente (ej. con 'T' o milisegundos)
			try {
				return LocalDateTime.parse(dbData);
			} catch (Exception e2) {
				System.err.println("No se pudo parsear la fecha de la BBDD: " + dbData);
				return null;
			}
		}
	}
}
