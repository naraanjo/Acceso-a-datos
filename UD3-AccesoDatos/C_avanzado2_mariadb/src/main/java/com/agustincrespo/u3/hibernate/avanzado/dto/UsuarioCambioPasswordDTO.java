package com.agustincrespo.u3.hibernate.avanzado.dto;

public class UsuarioCambioPasswordDTO {
    public int id; // ID del usuario que cambia la password
    public String oldPassword; // Necesaria para verificar identidad
    public String newPassword; // La nueva clave a hashear
}