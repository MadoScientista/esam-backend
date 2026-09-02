-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema esam_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `esam_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `esam_db` ;

-- -----------------------------------------------------
-- Table `esam_db`.`region`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `esam_db`.`region` (
  `id_region` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_region`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `esam_db`.`comuna`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `esam_db`.`comuna` (
  `id_comuna` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NULL DEFAULT NULL,
  `id_region` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`id_comuna`),
  INDEX `IDX_comuna_id_region` (`id_region` ASC) VISIBLE,
  CONSTRAINT `FK_comuna_region`
    FOREIGN KEY (`id_region`)
    REFERENCES `esam_db`.`region` (`id_region`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table `esam_db`.`marca`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `esam_db`.`marca` (
  `id_marca` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_marca`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `esam_db`.`producto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `esam_db`.`producto` (
  `sku` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NULL DEFAULT NULL,
  `descripcion` LONGTEXT NULL DEFAULT NULL,
  `precio` BIGINT NULL DEFAULT NULL,
  `stock` BIGINT NULL DEFAULT NULL,
  `img` VARCHAR(255) NULL DEFAULT NULL,
  `id_marca` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`sku`),
  INDEX `IDX_producto_id_marca` (`id_marca` ASC) VISIBLE,
  CONSTRAINT `FK_producto_marca`
    FOREIGN KEY (`id_marca`)
    REFERENCES `esam_db`.`marca` (`id_marca`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `esam_db`.`rol_usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `esam_db`.`rol_usuario` (
  `id_rol_usuario` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_rol_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `esam_db`.`usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `esam_db`.`usuario` (
  `id_usuario` BIGINT NOT NULL AUTO_INCREMENT,
  `a_materno` VARCHAR(255) NULL DEFAULT NULL,
  `a_paterno` VARCHAR(255) NULL DEFAULT NULL,
  `nombre_usuario` VARCHAR(255) NULL DEFAULT NULL,
  `p_nombre` VARCHAR(255) NULL DEFAULT NULL,
  `password` VARCHAR(255) NULL DEFAULT NULL,
  `s_nombre` VARCHAR(255) NULL DEFAULT NULL,
  `id_rol_usuario` BIGINT NULL DEFAULT NULL,
  `id_comuna` BIGINT NULL DEFAULT NULL,
  `id_region` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  INDEX `IDX_usuario_id_rol_usuario` (`id_rol_usuario` ASC) VISIBLE,
  INDEX `IDX_usuario_id_comuna` (`id_comuna` ASC) VISIBLE,
  INDEX `IDX_usuario_id_region` (`id_region` ASC) VISIBLE,
  CONSTRAINT `FK_usuario_rol_usuario`
    FOREIGN KEY (`id_rol_usuario`)
    REFERENCES `esam_db`.`rol_usuario` (`id_rol_usuario`),
  CONSTRAINT `FK_usuario_comuna`
    FOREIGN KEY (`id_comuna`)
    REFERENCES `esam_db`.`comuna` (`id_comuna`),
  CONSTRAINT `FK_usuario_region`
    FOREIGN KEY (`id_region`)
    REFERENCES `esam_db`.`region` (`id_region`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
