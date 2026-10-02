ALTER TABLE `pedido`
  ADD COLUMN `creado_en` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6);

CREATE TABLE `pedido_historial_estado` (
  `id_cambio_estado_pedido` BIGINT NOT NULL AUTO_INCREMENT,
  `estado_anterior` VARCHAR(20) NULL,
  `estado_nuevo` VARCHAR(20) NOT NULL,
  `cambiado_en` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `pedido_id` BIGINT NOT NULL,
  `usuario_id` BIGINT NOT NULL,
  PRIMARY KEY (`id_cambio_estado_pedido`),
  KEY `IDX_pedido_historial_estado_pedido_fecha` (`pedido_id`, `cambiado_en`),
  CONSTRAINT `FK_pedido_historial_estado_pedido`
    FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id_pedido`),
  CONSTRAINT `FK_pedido_historial_estado_usuario`
    FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
