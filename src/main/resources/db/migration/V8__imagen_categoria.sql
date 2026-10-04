ALTER TABLE `categoria`
  ADD COLUMN `imagen_url` VARCHAR(500) NULL,
  ADD COLUMN `imagen_id_publico` VARCHAR(255) NULL,
  ADD UNIQUE KEY `UK_categoria_imagen_id_publico` (`imagen_id_publico`);