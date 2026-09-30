-- Posicion de cada imagen dentro de la galeria del producto. 0 es la primera.
ALTER TABLE `esam_db`.`imagen_producto`
  ADD COLUMN `orden` INT NOT NULL DEFAULT 0 AFTER `principal`;

-- El indice es solo de lectura: ordena por (sku, orden).
-- No se declara UNIQUE (sku, orden) a proposito. Al reordenar, Hibernate
-- actualiza fila por fila, asi que durante un intercambio dos imagenes
-- comparten el mismo orden y la restriccion reventa a mitad del swap.
-- El desempate se resuelve con id_imagen_producto en el ORDER BY.
CREATE INDEX `IDX_imagen_producto_sku_orden`
  ON `esam_db`.`imagen_producto` (`sku`, `orden`);

-- Backfill: numera las imagenes que ya existian, por producto y en orden de id.
UPDATE `esam_db`.`imagen_producto` ip
JOIN (
  SELECT `id_imagen_producto`,
         ROW_NUMBER() OVER (PARTITION BY `sku` ORDER BY `id_imagen_producto`) - 1 AS posicion
  FROM `esam_db`.`imagen_producto`
) numeradas ON numeradas.`id_imagen_producto` = ip.`id_imagen_producto`
SET ip.`orden` = numeradas.posicion;