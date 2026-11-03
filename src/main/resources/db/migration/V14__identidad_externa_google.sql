ALTER TABLE `usuario`
  MODIFY COLUMN `password` VARCHAR(255) NULL;

CREATE TABLE `identidad_externa` (
  `id_identidad_externa` BIGINT NOT NULL AUTO_INCREMENT,
  `proveedor` VARCHAR(32) NOT NULL,
  `subject` VARCHAR(255) NOT NULL,
  `id_usuario` BIGINT NOT NULL,
  PRIMARY KEY (`id_identidad_externa`),
  UNIQUE KEY `UK_identidad_externa_proveedor_subject` (`proveedor`, `subject`),
  KEY `IDX_identidad_externa_id_usuario` (`id_usuario`),
  CONSTRAINT `FK_identidad_externa_usuario`
    FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
