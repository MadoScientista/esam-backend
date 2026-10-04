USE `esam_db`;

INSERT INTO `producto` (`sku`, `nombre`, `descripcion`, `precio`, `stock`, `id_marca`)
SELECT 'DEMO-CUAD-001', 'Cuaderno universitario', 'Cuaderno de 100 hojas', 2990, 25, `marca`.`id_marca`
FROM `marca`
WHERE `marca`.`nombre` = 'Staedtler'
  AND NOT EXISTS (SELECT 1 FROM `producto` WHERE `sku` = 'DEMO-CUAD-001');

INSERT INTO `producto` (`sku`, `nombre`, `descripcion`, `precio`, `stock`, `id_marca`)
SELECT 'DEMO-LAP-001', 'Lápices de colores', 'Caja de 12 colores', 1990, 40, `marca`.`id_marca`
FROM `marca`
WHERE `marca`.`nombre` = 'Faber-Castell'
  AND NOT EXISTS (SELECT 1 FROM `producto` WHERE `sku` = 'DEMO-LAP-001');

INSERT INTO `producto` (`sku`, `nombre`, `descripcion`, `precio`, `stock`, `id_marca`)
SELECT 'DEMO-REG-001', 'Regla de 30 cm', 'Regla plástica transparente', 790, 60, `marca`.`id_marca`
FROM `marca`
WHERE `marca`.`nombre` = 'Torre'
  AND NOT EXISTS (SELECT 1 FROM `producto` WHERE `sku` = 'DEMO-REG-001');
