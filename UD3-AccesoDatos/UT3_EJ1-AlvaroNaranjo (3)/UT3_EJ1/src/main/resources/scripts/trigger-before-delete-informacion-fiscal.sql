DROP TRIGGER IF EXISTS before_delete_cliente;

DELIMITER 

CREATE TRIGGER before_delete_cliente
BEFORE DELETE ON cliente
FOR EACH ROW
BEGIN
    -- Actualizamos todas las compras del cliente a borrar
    UPDATE compra
    SET 
        estado = 'ELIMINADO',
        direccion = '',
        precio_total = 0.00, 
        fecha_realizada = NOW(), 
        -- Desvinculamos poniendo NULL. 
        -- Esto evita que salte el 'ON DELETE RESTRICT' de la tabla compra.
        cliente_nif_cif = NULL
        
    WHERE cliente_nif_cif = OLD.nif_cif;
    
END 

DELIMITER ;


--	Actualmente, en el modelo de datos se pueden eliminar algunas entidades bajo condiciones específicas.
--	El cliente puede ser eliminado en cascada, eliminando su informacion_fiscal
-- modifico los campos de las compras de un  Cliente mediante el trigger, al ser este eliminado 
-- para poder eliminar al maximo toda su informacion. 
	
--7	Este trigger me permite modificar los campos a ("") en caso de string, (0) en caso de numeros,
--	(fecha actual NOW()) en caso de  fecha 
