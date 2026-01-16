-- ----------------------------------------------------
-- SCRIPT MARIADB (Compatible con MySQL)
-- ----------------------------------------------------

USE usuarios_db;

SET FOREIGN_KEY_CHECKS=0;

-- BORRADO SEGURO DE TABLAS
DROP TABLE IF EXISTS articulos;
DROP TABLE IF EXISTS usuarios;

-- Reactivamos la comprobación de claves foráneas
SET FOREIGN_KEY_CHECKS=1;

-- ----------------------------------------------------
-- CREACIÓN DE TABLAS
-- ----------------------------------------------------

-- USUARIOS
CREATE TABLE IF NOT EXISTS usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(120) NOT NULL,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
) 
ENGINE=InnoDB;

-- --------------------------------------------------
-- RELACIÓN 1:N (Usuario -> Articulos)
-- --------------------------------------------------
-- ARTICULOS (Un Usuario "1" tiene "N" Articulos)
CREATE TABLE IF NOT EXISTS articulos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL,
    contenido LONGTEXT NOT NULL,
    fecha_publicacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    autor_id INT NOT NULL,
    
    FOREIGN KEY (autor_id) REFERENCES usuarios(id)
        ON DELETE CASCADE
) 
ENGINE=InnoDB;


-- --------------------------------------------------
-- INSERCIÓN DE DATOS DE EJEMPLO
-- --------------------------------------------------

-- DATOS PARA LA TABLA USUARIOS
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

-- DATOS PARA TABLA ARTICULOS
INSERT INTO articulos (id, titulo, contenido, autor_id) VALUES
(1, 'Introducción a JPA/Hibernate', 'Contenido del artículo sobre JPA...', 1),
(2, 'Principios de Diseño Atómico', 'Contenido sobre diseño atómico...', 3),
(3, 'Guía de Docker para principiantes', 'Contenido sobre Docker...', 4),
(4, 'JPA vs JDBC: Cuál elegir', 'Contenido comparativo...', 1),
(5, 'Limpieza de datos con Pandas', 'Contenido sobre Pandas...', 7),
(6, 'Patrones de diseño en Java', 'Contenido sobre patrones de diseño...', 1),
(7, 'Microservicios con Spring Boot', 'Contenido sobre microservicios...', 1),
(8, 'Estrategias de Link Building en 2025', 'Contenido sobre SEO Off-Page...', 2),
(9, 'SEO On-Page: Guía completa', 'Contenido sobre optimización On-Page...', 2),
(10, 'El auge de las Super-Apps', 'Contenido sobre tendencias UX/UI...', 3),
(11, 'Psicología del color en diseño web', 'Contenido sobre teoría del color...', 3),
(12, 'Infraestructura como Código (IaC) con Terraform', 'Contenido sobre IaC...', 4),
(13, 'Kubernetes: Orquestación de contenedores', 'Contenido sobre K8s...', 4),
(14, 'Novedades en desarrollo nativo iOS 18', 'Contenido sobre Swift 6...', 5),
(15, 'Kotlin Multiplatform Mobile (KMM)', 'Contenido sobre KMM...', 5),
(16, 'Introducción a Selenium para QA', 'Contenido sobre Selenium WebDriver...', 6),
(17, 'Pruebas de rendimiento con JMeter', 'Contenido sobre JMeter...', 6),
(18, 'Redes Neuronales Convolucionales (CNN)', 'Contenido sobre CNNs...', 7),
(19, 'TensorFlow vs PyTorch: Comparativa', 'Contenido sobre frameworks de IA...', 7),
(20, 'Metodología Agile vs Scrum', 'Contenido sobre diferencias...', 8),
(21, 'Gestión de riesgos en proyectos tech', 'Contenido sobre gestión de riesgos...', 8),
(22, 'Optimización de Core Web Vitals', 'Contenido sobre rendimiento web...', 2),
(23, 'Figma: Trucos avanzados de prototipado', 'Contenido sobre Figma...', 3),
(24, 'Análisis de sentimiento con Python', 'Contenido sobre NLP...', 7),
(25, 'Automatización de builds con Jenkins', 'Contenido sobre CI/CD...', 4);

COMMIT;