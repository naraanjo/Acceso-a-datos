-- Uso: Debido a que no podemos eliminar un articulo, si este se encuentra en alguna compra
-- he decidido vaciar todos sus campos, y añadir una columna
-- que indica si ese articulo esta o no activo, ya que en un entorno real
-- considero que podria ser una practica mucho mas segura
-- que cambiar la pk a la misma pero en negativa


CREATE PROCEDURE eliminar_articulo(IN p_id_articulo INT)
BEGIN
    

    -- 2. "Eliminamos" el artículo transformándolo
    UPDATE articulo
    SET 
        nombre = "", -- Nombre marca
        descripcion = '',       -- Vacío
        precio_actual = 0.00,   -- Precio 0
        stock = 0, -- Stock 0
        activo = false
    WHERE id = p_id_articulo;
    
END //

DELIMITER ;
SHOW PROCEDURE STATUS WHERE Db = 'shop_db';
drop procedure eliminar_articulo;