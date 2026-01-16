package com.agustincrespo.u3.hibernate.avanzado.dto;

/**
 * DTO específico para operaciones de creación (Registro).
 * Separa los datos de entrada (que incluyen password en texto plano)
 * de la vista de salida (UsuarioDTO).
 */
public class UsuarioRegistroDTO {
    
    public String nombre;
    public String email;
    public String password; // Texto plano, aquí está decodificado

    public UsuarioRegistroDTO() {
    }

    public UsuarioRegistroDTO(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }
}
