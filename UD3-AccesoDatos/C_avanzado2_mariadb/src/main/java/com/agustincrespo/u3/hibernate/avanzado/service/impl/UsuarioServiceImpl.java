package com.agustincrespo.u3.hibernate.avanzado.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.agustincrespo.u3.hibernate.avanzado.dao.UsuarioDAO;
import com.agustincrespo.u3.hibernate.avanzado.dto.UsuarioCambioPasswordDTO;
import com.agustincrespo.u3.hibernate.avanzado.dto.UsuarioDTO;
import com.agustincrespo.u3.hibernate.avanzado.dto.UsuarioRegistroDTO;
import com.agustincrespo.u3.hibernate.avanzado.model.Usuario;
import com.agustincrespo.u3.hibernate.avanzado.service.UsuarioService;

public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioDAO usuarioDao;

    // Inyección de Dependencias: El servicio necesita al DAO para trabajar
    public UsuarioServiceImpl(UsuarioDAO usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    @Override
    public void create(UsuarioRegistroDTO registroDto) {
        if (registroDto == null) {
            throw new IllegalArgumentException("El DTO de registro no puede ser null");
        }

        // TODO: Validación de Negocio (ej. verificar si el email es único usando el DAO)

        // Mapeo Manual (DTO -> Entity):
        // Realizamos esto manualmente y NO con un mapper automático porque:
        //  * Necesitamos hashear la contraseña (lógica sensible).
        //  * Necesitamos establecer valores por defecto del sistema (activo, fechaCreacion).
        Usuario u = new Usuario();
        u.setNombre(registroDto.nombre);
        u.setEmail(registroDto.email);
        u.setActivo(true); // Por defecto activo al registrarse
        u.setFechaCreacion(java.time.LocalDateTime.now());

        // Seguridad: Hasheamos la password antes de tocar la entidad
        String passwordHasheada = PasswordUtils.hashPassword(registroDto.password);
        u.setPasswordHash(passwordHasheada);

        usuarioDao.save(u);
    }

    @Override
    public void changePassword(UsuarioCambioPasswordDTO dto) {
        if (dto == null)
            throw new IllegalArgumentException("DTO no puede ser null");

        Usuario u = usuarioDao.findById(dto.id);
        if (u == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Regla de Negocio: Verificar la contraseña antigua antes de permitir el cambio
        String oldHash = PasswordUtils.hashPassword(dto.oldPassword);
        if (!oldHash.equals(u.getPasswordHash()))
            throw new IllegalArgumentException("La contraseña antigua no coincide");

        // Si coincide, procedemos a hashear y guardar la nueva
        String newHash = PasswordUtils.hashPassword(dto.newPassword);
        u.setPasswordHash(newHash);
        
        usuarioDao.update(u);
    }

    @Override
    public UsuarioDTO findById(int id) {
        Usuario u = usuarioDao.findById(id);
        return UsuarioDTO.fromEntity(u); // Conversión Entity -> DTO
    }

    @Override
    public List<UsuarioDTO> findAll() {
        return usuarioDao.findAll().stream()
                .map(UsuarioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioDTO update(UsuarioDTO dto) {
        // Documentación interna del proceso de update:
        // 
        //  * Recupera la entidad actual desde la base de datos usando el ID del DTO.
        //    Si no existe, lanzamos excepción para evitar null pointers.
        // 
        //  * Mezcla (Merge) selectivo: Solo pasamos los campos permitidos (nombre, email, activo).
        //    IMPORTANTE: No tocamos 'passwordHash' ni 'fechaCreacion' para proteger la integridad.
        //
        //  * Delega la persistencia al DAO y devuelve el DTO actualizado.

        if (dto == null) {
            throw new IllegalArgumentException("dto no puede ser null");
        }

        // Recuperar entidad actual (Paso 1)
        Usuario existing = usuarioDao.findById(dto.id);
        if (existing == null) {
            throw new IllegalArgumentException("Usuario con id " + dto.id + " no encontrado");
        }

        // Actualizar solo campos permitidos (Paso 2)
        if (dto.nombre != null && !dto.nombre.equals(existing.getNombre())) {
            existing.setNombre(dto.nombre);
        }

        if (dto.email != null && !dto.email.equals(existing.getEmail())) {
            existing.setEmail(dto.email);
            // Nota: aquí se podría validar unicidad de email antes de asignar
        }

        // El estado activo se sobrescribe directamente
        existing.setActivo(dto.activo);

        // No se toca existing.passwordHash ni existing.fechaCreacion
        
        Usuario updated = usuarioDao.update(existing);
        
        return UsuarioDTO.fromEntity(updated);
    }

    @Override
    public void delete(int id) {
        Usuario u = usuarioDao.findById(id);
        if (u != null) {
            usuarioDao.delete(u);
        }
    }
}