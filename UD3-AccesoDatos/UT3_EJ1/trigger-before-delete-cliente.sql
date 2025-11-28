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
        fecha_realizada = '1000-01-01 00:00:00', -- Fecha inexistente
        
        -- Desvinculamos poniendo NULL. 
        -- Esto evita que salte el 'ON DELETE RESTRICT' de la tabla compra.
        cliente_nif_cif = NULL
        
    WHERE cliente_nif_cif = OLD.nif_cif;
    
END 

DELIMITER ;

SHOW TRIGGERS FROM shop_db


--	Actualmente, en el modelo de datos se pueden eliminar algunas entidades bajo condiciones específicas.
--	La información fiscal puede ser eliminada, al eliminar el cliente, a su vez he creado un trigger
--	que salte antes de hacer DELETE sobre Cliente, modificando los campos en todas sus compras
--	para poder eliminar al maximo toda su informacion. (trigger-before-delete-cliente)
	
--7	Este trigger me permite modificar los campos a ("") en caso de string, (0) en caso de numeros
--	o una fecha antigua si el campo es de tipo fecha (ej:1000-01-01 00:00:00)
