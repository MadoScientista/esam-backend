CREATE SCHEMA IF NOT EXISTS `esam_db`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE `esam_db`;

CREATE TABLE `region` (
  `id_region` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id_region`),
  UNIQUE KEY `UK_region_nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `comuna` (
  `id_comuna` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NOT NULL,
  `region_id` BIGINT NOT NULL,
  PRIMARY KEY (`id_comuna`),
  UNIQUE KEY `UK_comuna_nombre` (`nombre`),
  KEY `IDX_comuna_region_id` (`region_id`),
  CONSTRAINT `FK_comuna_region`
    FOREIGN KEY (`region_id`) REFERENCES `region` (`id_region`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `marca` (
  `id_marca` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NOT NULL,
  `activo` BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (`id_marca`),
  UNIQUE KEY `UK_marca_nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `rol_usuario` (
  `id_rol_usuario` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id_rol_usuario`),
  UNIQUE KEY `UK_rol_usuario_nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `producto` (
  `id_producto` BIGINT NOT NULL AUTO_INCREMENT,
  `sku` VARCHAR(255) NOT NULL,
  `nombre` VARCHAR(255) NOT NULL,
  `descripcion` TEXT NULL,
  `precio` BIGINT NOT NULL,
  `stock` INT NOT NULL,
  `activo` BOOLEAN NOT NULL DEFAULT TRUE,
  `version` BIGINT NOT NULL DEFAULT 0,
  `id_marca` BIGINT NOT NULL,
  PRIMARY KEY (`id_producto`),
  UNIQUE KEY `UK_producto_sku` (`sku`),
  KEY `IDX_producto_id_marca` (`id_marca`),
  CONSTRAINT `FK_producto_marca`
    FOREIGN KEY (`id_marca`) REFERENCES `marca` (`id_marca`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `usuario` (
  `id_usuario` BIGINT NOT NULL AUTO_INCREMENT,
  `nombres` VARCHAR(25) NULL,
  `a_paterno` VARCHAR(25) NULL,
  `a_materno` VARCHAR(25) NULL,
  `rut` BIGINT NULL,
  `dv` VARCHAR(1) NULL,
  `fecha_nacimiento` DATE NULL,
  `correo` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `telefono` VARCHAR(255) NULL,
  `activo` BOOLEAN NOT NULL DEFAULT TRUE,
  `id_rol_usuario` BIGINT NOT NULL,
  `id_comuna` BIGINT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `UK_usuario_correo` (`correo`),
  KEY `IDX_usuario_id_rol_usuario` (`id_rol_usuario`),
  KEY `IDX_usuario_id_comuna` (`id_comuna`),
  CONSTRAINT `FK_usuario_rol_usuario`
    FOREIGN KEY (`id_rol_usuario`) REFERENCES `rol_usuario` (`id_rol_usuario`),
  CONSTRAINT `FK_usuario_comuna`
    FOREIGN KEY (`id_comuna`) REFERENCES `comuna` (`id_comuna`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `imagen_producto` (
  `id_imagen_producto` BIGINT NOT NULL AUTO_INCREMENT,
  `id_publico` VARCHAR(255) NOT NULL,
  `url` VARCHAR(500) NOT NULL,
  `texto_alternativo` VARCHAR(255) NULL,
  `orden` INT NOT NULL,
  `principal` BOOLEAN NOT NULL DEFAULT FALSE,
  `producto_id` BIGINT NOT NULL,
  PRIMARY KEY (`id_imagen_producto`),
  UNIQUE KEY `UK_imagen_producto_id_publico` (`id_publico`),
  KEY `IDX_imagen_producto_producto_orden` (`producto_id`, `orden`),
  CONSTRAINT `FK_imagen_producto_producto`
    FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `categoria` (
  `id_categoria` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NOT NULL,
  `slug` VARCHAR(255) NOT NULL,
  `activo` BOOLEAN NOT NULL DEFAULT TRUE,
  `padre_id` BIGINT NULL,
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `UK_categoria_slug` (`slug`),
  KEY `IDX_categoria_padre_id` (`padre_id`),
  CONSTRAINT `FK_categoria_padre`
    FOREIGN KEY (`padre_id`) REFERENCES `categoria` (`id_categoria`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `producto_categoria` (
  `id_producto` BIGINT NOT NULL,
  `id_categoria` BIGINT NOT NULL,
  PRIMARY KEY (`id_producto`, `id_categoria`),
  KEY `IDX_producto_categoria_id_categoria` (`id_categoria`),
  CONSTRAINT `FK_producto_categoria_producto`
    FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`),
  CONSTRAINT `FK_producto_categoria_categoria`
    FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id_categoria`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `direccion` (
  `id_direccion` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre_receptor` VARCHAR(255) NOT NULL,
  `telefono_receptor` VARCHAR(255) NOT NULL,
  `calle` VARCHAR(255) NOT NULL,
  `numero` VARCHAR(255) NOT NULL,
  `complemento` VARCHAR(255) NULL,
  `predeterminada` BOOLEAN NOT NULL DEFAULT FALSE,
  `activo` BOOLEAN NOT NULL DEFAULT TRUE,
  `usuario_id` BIGINT NOT NULL,
  `comuna_id` BIGINT NOT NULL,
  PRIMARY KEY (`id_direccion`),
  KEY `IDX_direccion_usuario_id` (`usuario_id`),
  KEY `IDX_direccion_comuna_id` (`comuna_id`),
  CONSTRAINT `FK_direccion_usuario`
    FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `FK_direccion_comuna`
    FOREIGN KEY (`comuna_id`) REFERENCES `comuna` (`id_comuna`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `carrito` (
  `id_carrito` BIGINT NOT NULL AUTO_INCREMENT,
  `usuario_id` BIGINT NOT NULL,
  PRIMARY KEY (`id_carrito`),
  UNIQUE KEY `UK_carrito_usuario_id` (`usuario_id`),
  CONSTRAINT `FK_carrito_usuario`
    FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `item_carrito` (
  `id_item_carrito` BIGINT NOT NULL AUTO_INCREMENT,
  `cantidad` INT NOT NULL,
  `id_carrito` BIGINT NOT NULL,
  `id_producto` BIGINT NOT NULL,
  PRIMARY KEY (`id_item_carrito`),
  UNIQUE KEY `UK_item_carrito_carrito_producto` (`id_carrito`, `id_producto`),
  KEY `IDX_item_carrito_id_producto` (`id_producto`),
  CONSTRAINT `FK_item_carrito_carrito`
    FOREIGN KEY (`id_carrito`) REFERENCES `carrito` (`id_carrito`),
  CONSTRAINT `FK_item_carrito_producto`
    FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pedido` (
  `id_pedido` BIGINT NOT NULL AUTO_INCREMENT,
  `numero_pedido` VARCHAR(255) NOT NULL,
  `estado` VARCHAR(20) NOT NULL,
  `total` BIGINT NOT NULL,
  `nombre_receptor` VARCHAR(255) NOT NULL,
  `telefono_receptor` VARCHAR(255) NOT NULL,
  `calle` VARCHAR(255) NOT NULL,
  `numero` VARCHAR(255) NOT NULL,
  `complemento` VARCHAR(255) NULL,
  `comuna_nombre` VARCHAR(255) NOT NULL,
  `region_nombre` VARCHAR(255) NOT NULL,
  `usuario_id` BIGINT NOT NULL,
  PRIMARY KEY (`id_pedido`),
  UNIQUE KEY `UK_pedido_numero_pedido` (`numero_pedido`),
  KEY `IDX_pedido_usuario_id` (`usuario_id`),
  CONSTRAINT `FK_pedido_usuario`
    FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `detalle_pedido` (
  `id_detalle_pedido` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre_producto` VARCHAR(255) NOT NULL,
  `sku_producto` VARCHAR(255) NOT NULL,
  `precio_unitario` BIGINT NOT NULL,
  `cantidad` INT NOT NULL,
  `subtotal` BIGINT NOT NULL,
  `pedido_id` BIGINT NOT NULL,
  `producto_id` BIGINT NOT NULL,
  PRIMARY KEY (`id_detalle_pedido`),
  KEY `IDX_detalle_pedido_pedido_id` (`pedido_id`),
  KEY `IDX_detalle_pedido_producto_id` (`producto_id`),
  CONSTRAINT `FK_detalle_pedido_pedido`
    FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id_pedido`),
  CONSTRAINT `FK_detalle_pedido_producto`
    FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
