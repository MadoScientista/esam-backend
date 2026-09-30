ALTER TABLE `esam_db`.`imagen_producto`
  DROP INDEX `UK_imagen_producto_principal`,
  DROP COLUMN `clave_principal`,
  DROP COLUMN `principal`;