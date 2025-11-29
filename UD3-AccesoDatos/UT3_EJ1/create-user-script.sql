
-- ----------------------------------------------------
-- CREACIÓN Y ASIGNACIÓN DE PERMISOS PARA UN USUARIO 
-- ----------------------------------------------------

-- 1. Crear el usuario 'adminalvaro' y establecer su contraseña.
--    El '%' permite la conexión desde cualquier host (incluyendo localhost).
CREATE USER 'admin_alvaro'@'%' IDENTIFIED BY 'Abcd1234$';

-- 2. Conceder permisos
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON shop_db.* TO 'admin_alvaro'@'%';
FLUSH PRIVILEGES;