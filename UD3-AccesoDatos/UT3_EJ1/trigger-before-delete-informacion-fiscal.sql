
CREATE TRIGGER before_delete_informacion_fiscal
BEFORE DELETE ON informacion_fiscal
FOR EACH ROW
BEGIN
    -- Actualizamos todas las compras del cliente a borrar
    UPDATE compra
    SET 
        estado = 'ELIMINADO',
        direccion = '',
        precio_total = 0.00, 
        fecha_realizada = NOW(), 
       
        
    WHERE cliente_nif_cif = OLD.nif_cif;
    
END 

DELIMITER ;

SHOW TRIGGERS FROM shop_db;
DROP TRIGGER IF EXISTS before_delete_informacion_fiscal;


--	Actualmente, en el modelo de datos se pueden eliminar algunas entidades bajo condiciones específicas.
--	El cliente puede ser eliminado en cascada, eliminando su informacion_fiscal, ( trigger
--	que salta antes de hacer DELETE sobre informacion_fiscal ), modificando los campos de las compras de un  Cliente 
--	para poder eliminar al maximo toda su informacion. 
	
--7	Este trigger me permite modificar los campos a ("") en caso de string, (0) en caso de numeros,
--	(fecha actual NOW()) en caso de  fecha 
