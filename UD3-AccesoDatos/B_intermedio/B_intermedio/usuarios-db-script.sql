-- ----------------------------------------------------
-- SCRIPT SQLITE CON RELACIONES DE TIPO  1:1, 1:N, N:M
-- ----------------------------------------------------

-- ACTIVAR CLAVES FORÁNEAS (Importante para SQLite)
PRAGMA foreign_keys = ON;

-- BORRADO SEGURO DE TABLAS (en orden INVERSO de dependencia)
DROP TABLE IF EXISTS articulo_categoria;
DROP TABLE IF EXISTS categorias;
DROP TABLE IF EXISTS articulos;
DROP TABLE IF EXISTS perfiles;
DROP TABLE IF EXISTS usuarios;


-- USUARIOS
CREATE TABLE IF NOT EXISTS usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    esta_activo INTEGER DEFAULT 1
);

-- PERFILES (Relación 1:1 opcional con Usuarios)
CREATE TABLE IF NOT EXISTS perfiles (
    usuario_id INTEGER PRIMARY KEY,
    biografia TEXT,
    sitio_web TEXT,
    ubicacion TEXT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);



-- --------------------------------------------------
-- AMPLIACIÓN: RELACIÓN 1:N (Usuario -> Articulos)
-- --------------------------------------------------

-- ARTICULOS (Un Usuario "1" tiene "N" Articulos)
CREATE TABLE IF NOT EXISTS articulos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo TEXT NOT NULL,
    contenido TEXT,
    fecha_publicacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  
    autor_id INTEGER NOT NULL,
    
    FOREIGN KEY (autor_id) REFERENCES usuarios(id)
        ON DELETE CASCADE
);  -- Si se borra un usuario, se borran sus artículos



-- --------------------------------------------------
-- RELACIÓN N:M (Articulos <-> Categorias)
-- --------------------------------------------------

-- CATEGORIAS (Entidad simple)
CREATE TABLE IF NOT EXISTS categorias (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE
);

-- ARTICULO_CATEGORIA (Tabla intermedia o "pivote")
CREATE TABLE IF NOT EXISTS articulo_categoria (
    articulo_id INTEGER NOT NULL,
    categoria_id INTEGER NOT NULL,
    
    PRIMARY KEY (articulo_id, categoria_id),
    
    FOREIGN KEY (articulo_id) REFERENCES articulos(id)
        ON DELETE CASCADE,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
        ON DELETE CASCADE
);


-- --------------------------------------------------
-- INSERCIÓN DE DATOS DE EJEMPLO
-- --------------------------------------------------

-- DATOS PARA LA TABLA USUARIOS
INSERT INTO usuarios (nombre, email, password_hash) VALUES
('ana_dev', 'ana@ejemplo.com', 'hash123'),         -- id 1
('benito_seo', 'benito@ejemplo.com', 'hash456'),   -- id 2
('carla_design', 'carla@ejemplo.com', 'hash789'),  -- id 3
('david_admin', 'david@ejemplo.com', 'hash101'),   -- id 4
('elena_movil', 'elena@ejemplo.com', 'hash112'),   -- id 5
('felipe_qa', 'felipe@ejemplo.com', 'hash131'),    -- id 6
('gloria_data', 'gloria@ejemplo.com', 'hash415'),  -- id 7
('hugo_pm', 'hugo@ejemplo.com', 'hash161'),        -- id 8
('inma_copy', 'inma@ejemplo.com', 'hash718'),      -- id 9
('jaime_beca', 'jaime@ejemplo.com', 'hash192');    -- id 10

-- DATOS PARA PERFILES (Usuarios 1-8 tienen perfil, 9-10 no)
INSERT INTO perfiles (usuario_id, biografia, sitio_web, ubicacion) VALUES
(1, 'Desarrolladora Backend en Madrid.', 'https://ana.dev', 'Madrid, España'),
(2, 'Especialista en SEO y Marketing Digital.', NULL, 'Valencia, España'),
(3, 'Diseñadora UX/UI. Amante del minimalismo.', 'https://carla.design', 'Barcelona, España'),
(4, 'SysAdmin y DevOps.', 'https://david.io', 'Online'),
(5, 'Desarrolladora de apps para iOS y Android.', NULL, NULL),
(6, 'QA Tester. Buscando bugs desde 2010.', NULL, 'Sevilla, España'),
(7, 'Científica de Datos. Apasionada del Machine Learning.', 'https://gloria.ai', 'Bilbao, España'),
(8, 'Project Manager certificado. Organizando el caos.', NULL, 'Madrid, España');

-- DATOS PARA TABLA CATEGORIAS
INSERT INTO categorias (id, nombre) VALUES
(1, 'Tecnología'),
(2, 'Tutoriales'),
(3, 'Diseño UX/UI'),
(4, 'DevOps'),
(5, 'Ciencia de Datos'),
(6, 'Marketing Digital'),
(7, 'Gestión de Proyectos'),
(8, 'Testing y QA');

-- DATOS PARA TABLA ARTICULOS
INSERT INTO articulos (id, titulo, contenido, autor_id) VALUES
(1, 'Introducción a JPA/Hibernate', 'Contenido del artículo sobre JPA...', 1), 			 -- (Autor: ana_dev)
(2, 'Principios de Diseño Atómico', 'Contenido sobre diseño atómico...', 3),   			 -- (Autor: carla_design)
(3, 'Guía de Docker para principiantes', 'Contenido sobre Docker...', 4),      			 -- (Autor: david_admin)
(4, 'JPA vs JDBC: Cuál elegir', 'Contenido comparativo...', 1),                   		 -- (Autor: ana_dev)
(5, 'Limpieza de datos con Pandas', 'Contenido sobre Pandas...', 7),           	  		 -- (Autor: gloria_data)
(6, 'Patrones de diseño en Java', 'Contenido sobre patrones de diseño...', 1),           -- (Autor: ana_dev)
(7, 'Microservicios con Spring Boot', 'Contenido sobre microservicios...', 1),        	 -- (Autor: ana_dev)
(8, 'Estrategias de Link Building en 2025', 'Contenido sobre SEO Off-Page...', 2),       -- (Autor: benito_seo)
(9, 'SEO On-Page: Guía completa', 'Contenido sobre optimización On-Page...', 2),    	 -- (Autor: benito_seo)
(10, 'El auge de las Super-Apps', 'Contenido sobre tendencias UX/UI...', 3),             -- (Autor: carla_design)
(11, 'Psicología del color en diseño web', 'Contenido sobre teoría del color...', 3), 	 -- (Autor: carla_design)
(12, 'Infraestructura como Código (IaC) con Terraform', 'Contenido sobre IaC...', 4),    -- (Autor: david_admin)
(13, 'Kubernetes: Orquestación de contenedores', 'Contenido sobre K8s...', 4),           -- (Autor: david_admin)
(14, 'Novedades en desarrollo nativo iOS 18', 'Contenido sobre Swift 6...', 5),          -- (Autor: elena_movil)
(15, 'Kotlin Multiplatform Mobile (KMM)', 'Contenido sobre KMM...', 5),                	 -- (Autor: elena_movil)
(16, 'Introducción a Selenium para QA', 'Contenido sobre Selenium WebDriver...', 6),     -- (Autor: felipe_qa)
(17, 'Pruebas de rendimiento con JMeter', 'Contenido sobre JMeter...', 6),               -- (Autor: felipe_qa)
(18, 'Redes Neuronales Convolucionales (CNN)', 'Contenido sobre CNNs...', 7),            -- (Autor: gloria_data)
(19, 'TensorFlow vs PyTorch: Comparativa', 'Contenido sobre frameworks de IA...', 7),    -- (Autor: gloria_data)
(20, 'Metodología Agile vs Scrum', 'Contenido sobre diferencias...', 8),                 -- (Autor: hugo_pm)
(21, 'Gestión de riesgos en proyectos tech', 'Contenido sobre gestión de riesgos...', 8),-- (Autor: hugo_pm)
(22, 'Optimización de Core Web Vitals', 'Contenido sobre rendimiento web...', 2),        -- (Autor: benito_seo)
(23, 'Figma: Trucos avanzados de prototipado', 'Contenido sobre Figma...', 3),           -- (Autor: carla_design)
(24, 'Análisis de sentimiento con Python', 'Contenido sobre NLP...', 7),                 -- (Autor: gloria_data)
(25, 'Automatización de builds con Jenkins', 'Contenido sobre CI/CD...', 4);             -- (Autor: david_admin)


-- DATOS PARA ARTICULO_CATEGORIA (Relación N:M)
INSERT INTO articulo_categoria (articulo_id, categoria_id) VALUES
(1, 1), -- Art 1 (JPA) -> Cat 1 (Tecnología)
(1, 2), -- Art 1 (JPA) -> Cat 2 (Tutoriales)
(2, 3), -- Art 2 (Diseño) -> Cat 3 (Diseño UX/UI)
(3, 1), -- Art 3 (Docker) -> Cat 1 (Tecnología)
(3, 4), -- Art 3 (Docker) -> Cat 4 (DevOps)
(4, 1), -- Art 4 (JPA vs JDBC) -> Cat 1 (Tecnología)
(5, 5), -- Art 5 (Pandas) -> Cat 5 (Ciencia de Datos)
(6, 1), -- Art 6 (Patrones Java) -> Cat 1 (Tecnología)
(6, 2), -- Art 6 (Patrones Java) -> Cat 2 (Tutoriales)
(7, 1), -- Art 7 (Microservicios) -> Cat 1 (Tecnología)
(8, 6), -- Art 8 (Link Building) -> Cat 6 (Marketing Digital)
(9, 6), -- Art 9 (SEO On-Page) -> Cat 6 (Marketing Digital)
(9, 2), -- Art 9 (SEO On-Page) -> Cat 2 (Tutoriales)
(10, 3), -- Art 10 (Super-Apps) -> Cat 3 (Diseño UX/UI)
(11, 3), -- Art 11 (Psic. Color) -> Cat 3 (Diseño UX/UI)
(12, 4), -- Art 12 (Terraform) -> Cat 4 (DevOps)
(12, 1), -- Art 12 (Terraform) -> Cat 1 (Tecnología)
(13, 4), -- Art 13 (Kubernetes) -> Cat 4 (DevOps)
(14, 1), -- Art 14 (iOS) -> Cat 1 (Tecnología)
(15, 1), -- Art 15 (KMM) -> Cat 1 (Tecnología)
(16, 8), -- Art 16 (Selenium) -> Cat 8 (Testing y QA)
(16, 2), -- Art 16 (Selenium) -> Cat 2 (Tutoriales)
(17, 8), -- Art 17 (JMeter) -> Cat 8 (Testing y QA)
(18, 5), -- Art 18 (CNN) -> Cat 5 (Ciencia de Datos)
(19, 5), -- Art 19 (TF vs PyTorch) -> Cat 5 (Ciencia de Datos)
(20, 7), -- Art 20 (Agile) -> Cat 7 (Gestión de Proyectos)
(21, 7), -- Art 21 (Gestión Riesgos) -> Cat 7 (Gestión de Proyectos)
(22, 6), -- Art 22 (Web Vitals) -> Cat 6 (Marketing Digital)
(23, 3), -- Art 23 (Figma) -> Cat 3 (Diseño UX/UI)
(23, 2), -- Art 23 (Figma) -> Cat 2 (Tutoriales)
(24, 5), -- Art 24 (Sentimiento) -> Cat 5 (Ciencia de Datos)
(25, 4); -- Art 25 (Jenkins) -> Cat 4 (DevOps)