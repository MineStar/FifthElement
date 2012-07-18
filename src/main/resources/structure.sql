-- -----------------------------------------------------
-- Table `home`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `home` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `player` VARCHAR(32) NOT NULL ,
  `world` VARCHAR(32) NOT NULL ,
  `x` DOUBLE NOT NULL ,
  `y` DOUBLE NOT NULL ,
  `z` DOUBLE NOT NULL ,
  `yaw` FLOAT NOT NULL ,
  `pitch` FLOAT NOT NULL ,
  PRIMARY KEY (`id`) );

-- -----------------------------------------------------
-- Table `bank`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `bank` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `player` VARCHAR(32) NOT NULL ,
  `world` VARCHAR(32) NOT NULL ,
  `x` DOUBLE NOT NULL ,
  `y` DOUBLE NOT NULL ,
  `z` DOUBLE NOT NULL ,
  `yaw` FLOAT NOT NULL ,
  `pitch` FLOAT NOT NULL ,
  PRIMARY KEY (`id`) );

-- -----------------------------------------------------
-- Table `warp`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `warp` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(32) NOT NULL ,
  `owner` VARCHAR(32) NOT NULL ,
  `world` VARCHAR(32) NOT NULL ,
  `x` DOUBLE NOT NULL ,
  `y` DOUBLE NOT NULL ,
  `z` DOUBLE NOT NULL ,
  `yaw` FLOAT NOT NULL ,
  `pitch` FLOAT NOT NULL ,
  `isPublic` TINYINT(1) NOT NULL ,
  `guests` TEXT NULL ,
  `useMode` SMALLINT NOT NULL ,
  `creationDate` DATETIME NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;
