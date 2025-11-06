-- --------------------------------------------------
-- SCRIPT COMPLETO PARA SQLITE: USUARIOS Y PERFILES (1:1 Opcional)
-- --------------------------------------------------

-- Es buena práctica en SQLite asegurarse de que las claves foráneas
-- estén activadas (en algunas configuraciones vienen desactivadas por defecto).
PRAGMA foreign_keys = ON;

-- --------------------------------------------------
-- TABLA 1: USUARIOS (Tabla principal)
-- esta_activo por defecto es 1 (true), útil para 'borrado lógico' (0 = inactivo, 1 = activo).
-- --------------------------------------------------
CREATE TABLE usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    esta_activo INTEGER DEFAULT 1
);

-- --------------------------------------------------
-- TABLA 2: PERFILES (Tabla opcional 1:1)
-- 'usuario_id INTEGER PRIMARY KEY': Esta es la clave del diseño.
--     * Es la CLAVE PRIMARIA de esta tabla (no se puede repetir el id).
--     * NO es AUTOINCREMENT. Debemos insertar el ID del usuario manualmente.
-- --------------------------------------------------
CREATE TABLE perfiles (
    usuario_id INTEGER PRIMARY KEY,
    biografia TEXT,
    sitio_web TEXT,
    ubicacion TEXT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- --------------------------------------------------
-- DATOS DE EJEMPLO: 10 USUARIOS
-- id 9 y 10 sin perfil
-- --------------------------------------------------
INSERT INTO usuarios (nombre, email, password_hash) VALUES
('ana_dev', 'ana@ejemplo.com', 'hash123'),
('benito_seo', 'benito@ejemplo.com', 'hash456'),
('carla_design', 'carla@ejemplo.com', 'hash789'),
('david_admin', 'david@ejemplo.com', 'hash101'),
('elena_movil', 'elena@ejemplo.com', 'hash112'),
('felipe_qa', 'felipe@ejemplo.com', 'hash131'),
('gloria_data', 'gloria@ejemplo.com', 'hash415'),
('hugo_pm', 'hugo@ejemplo.com', 'hash161'),
('inma_copy', 'inma@ejemplo.com', 'hash718'),
('jaime_beca', 'jaime@ejemplo.com', 'hash192');

-- --------------------------------------------------
-- DATOS DE EJEMPLO: 8 PERFILES (para los usuarios 1 al 8)
-- --------------------------------------------------
-- Insertamos perfiles solo para los primeros 8 usuarios.
-- Los usuarios con id 9 (Inma) y 10 (Jaime) NO tendrán perfil,
-- demostrando la naturaleza "opcional" de la tabla.

INSERT INTO perfiles (usuario_id, biografia, sitio_web, ubicacion) VALUES
(1, 'Desarrolladora Backend en Madrid.', 'https://ana.dev', 'Madrid, España'),
(2, 'Especialista en SEO y Marketing Digital.', NULL, 'Valencia, España'),
(3, 'Diseñadora UX/UI. Amante del minimalismo.', 'https://carla.design', 'Barcelona, España'),
(4, 'SysAdmin y DevOps.', 'https://david.io', 'Online'),
(5, 'Desarrolladora de apps para iOS y Android.', NULL, NULL),
(6, 'QA Tester. Buscando bugs desde 2010.', NULL, 'Sevilla, España'),
(7, 'Científica de Datos. Apasionada del Machine Learning.', 'https://gloria.ai', 'Bilbao, España'),
(8, 'Project Manager certificado. Organizando el caos.', NULL, 'Madrid, España');


-- --------------------------------------------------
-- PRUEBA DE CONSULTA (OPCIONAL)
-- --------------------------------------------------
-- Con este LEFT JOIN, puedes ver a TODOS los usuarios
-- y sus perfiles (o NULL si no tienen).
/*
SELECT
    u.id,
    u.nombre,
    p.biografia,
    p.ubicacion
FROM
    usuarios u
LEFT JOIN
    perfiles p ON u.id = p.usuario_id;
*/