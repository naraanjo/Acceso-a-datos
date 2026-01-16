package com.agustincrespo.u3.hibernate.avanzado.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class PasswordUtils {
	// Método simple para "hashear" una contraseña (no usar en producción)
	public static String hashPassword(String password) {
		 /* - Convierte el DTO a entidad y completa los campos que la capa de
		 *   persistencia requiere (passwordHash, fechaCreacion si procede).
		 * - Aplica un hash seguro (SHA-256) a la contraseña de texto plano antes
		 *   de almacenarla. En un sistema real se recomienda usar una función de
		 *   derivación de clave específica para contraseñas (bcrypt, scrypt o
		 *   Argon2) con sal por usuario. */
		
		// En un entorno real, usar una librería de hashing segura como BCrypt
		// Hashear la contraseña (simple SHA-256 aquí como ejemplo).
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
			// Convertir a hex
			StringBuilder sb = new StringBuilder();
			for (byte b : hash) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// SHA-256 debería estar disponible; si no, propagar como runtime
			throw new RuntimeException("No se puede hashear la contraseña", e);
		}
	}
}
