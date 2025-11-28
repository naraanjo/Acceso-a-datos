-- Desactivamos temporalmente la comprobación de claves foráneas
SET FOREIGN_KEY_CHECKS=0;

USE shop_db;

DROP TABLE IF EXISTS articulo_compra;
DROP TABLE IF EXISTS articulo;
DROP TABLE IF EXISTS compra;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS informacion_fiscal;

-- ----------------------------------------------------
-- CREACIÓN DE TABLAS
-- ----------------------------------------------------

CREATE TABLE informacion_fiscal (
  nif_cif varchar(20) NOT NULL,
  direccion_fiscal varchar(255) NOT NULL,
  telefono varchar(20) NOT NULL,
  PRIMARY KEY (nif_cif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE cliente (
  nif_cif varchar(20) NOT NULL,
  nombre varchar(100) NOT NULL,
  email varchar(100) NOT NULL,
  fecha_registro date NOT NULL,
  PRIMARY KEY (nif_cif),
  CONSTRAINT fk_cliente_informacion_fiscal FOREIGN KEY (nif_cif) 
    REFERENCES informacion_fiscal (nif_cif) 
    ON UPDATE CASCADE 
    ON DELETE CASCADE -- Al eliminar el cliente se elimina su informacion_fiscal
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE compra (
  id integer(10) unsigned NOT NULL AUTO_INCREMENT,
  fecha_realizada datetime NOT NULL,
  estado ENUM('PENDIENTE', 'ENVIADO', 'ENTREGADO', 'ELIMINADO') NOT NULL,
  direccion varchar(255) NOT NULL,
  precio_total decimal(10,2) NOT NULL,
  cliente_nif_cif varchar(20)  NULL,
  PRIMARY KEY (id),
  KEY fk_compra_cliente (cliente_nif_cif),
  CONSTRAINT fk_compra_cliente FOREIGN KEY (cliente_nif_cif) 
    REFERENCES cliente (nif_cif) 
    ON UPDATE CASCADE 
    ON DELETE RESTRICT -- Trigger actua antes
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE articulo (
  id integer(10) unsigned NOT NULL AUTO_INCREMENT,
  nombre varchar(100) NOT NULL,
  descripcion text NOT NULL,
  precio_actual decimal(10,2) NOT NULL,
  stock integer(10) unsigned NOT NULL DEFAULT 0,
  activo boolean NOT NULL DEFAULT TRUE COMMENT 'TRUE=Disponible, FALSE=Eliminado',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE articulo_compra (
  id_articulo integer(10) unsigned NOT NULL,
  id_compra integer(10) unsigned NOT NULL,
  precio_compra decimal(10,2) NOT NULL,
  unidades integer(10) unsigned NOT NULL,
  PRIMARY KEY (id_articulo,id_compra),
  KEY fk_articulo_compra_compra (id_compra),
  CONSTRAINT fk_articulo_compra_articulo FOREIGN KEY (id_articulo) 
    REFERENCES articulo (id) 
    ON UPDATE CASCADE 
    ON DELETE RESTRICT, -- Impide borrar articulos que estan en una compra (se puede actualizar el campo -activo- en la tabla articulo)
  CONSTRAINT fk_articulo_compra_compra FOREIGN KEY (id_compra) 
    REFERENCES compra (id) 
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

-- Reactivamos la comprobación de claves foráneas
SET FOREIGN_KEY_CHECKS=1;

SHOW TABLES;