DROP SCHEMA IF EXISTS `jobcenter` ;
CREATE SCHEMA IF NOT EXISTS `jobcenter` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `jobcenter` ;

-- -----------------------------------------------------
-- Table `node`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `node` ;

CREATE TABLE IF NOT EXISTS `node` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(2000) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `name_UNIQUE` ON `node` (`name` ASC);


-- -----------------------------------------------------
-- Table `node_access_rule`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `node_access_rule` ;

CREATE TABLE IF NOT EXISTS `node_access_rule` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `node_id` INT NOT NULL,
  `network_address` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `node_id_fk`
    FOREIGN KEY (`node_id`)
    REFERENCES `node` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `node_id_index` ON `node_access_rule` (`node_id` ASC);


-- -----------------------------------------------------
-- Table `job_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_type` ;

CREATE TABLE IF NOT EXISTS `job_type` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `priority` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(2000) NULL,
  `enabled` BIT(1) NOT NULL,
  `module_name` VARCHAR(200) NOT NULL,
  `minimum_module_version` INT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `status`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `status` ;

CREATE TABLE IF NOT EXISTS `status` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(2000) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `id_UNIQUE` ON `status` (`id` ASC);


-- -----------------------------------------------------
-- Table `request_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `request_type` ;

CREATE TABLE IF NOT EXISTS `request_type` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `name_UNIQUE` ON `request_type` (`name` ASC);


-- -----------------------------------------------------
-- Table `request`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `request` ;

CREATE TABLE IF NOT EXISTS `request` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `type` INT UNSIGNED NOT NULL,
  `db_record_version_number` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  CONSTRAINT `type_fk`
    FOREIGN KEY (`type`)
    REFERENCES `request_type` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `type_index` ON `request` (`type` ASC);


-- -----------------------------------------------------
-- Table `job`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job` ;

CREATE TABLE IF NOT EXISTS `job` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `request_id` INT NOT NULL,
  `job_type_id` INT NOT NULL,
  `submit_date` DATETIME NOT NULL,
  `submitter` VARCHAR(200) NULL,
  `priority` INT NOT NULL,
  `status_id` INT NOT NULL,
  `insert_complete` ENUM('T','F') NOT NULL DEFAULT 'F',
  `job_parameter_count` INT NULL DEFAULT -1,
  `delay_job_until` DATETIME NULL,
  `param_error_retry_count` INT NOT NULL DEFAULT 0,
  `soft_error_retry_count` INT NOT NULL DEFAULT 0,
  `db_record_version_number` INT NOT NULL DEFAULT 1,
  `job_label` VARCHAR(255) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `job_type_id_fk`
    FOREIGN KEY (`job_type_id`)
    REFERENCES `job_type` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `status_id_fk`
    FOREIGN KEY (`status_id`)
    REFERENCES `status` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `request_id_fk`
    FOREIGN KEY (`request_id`)
    REFERENCES `request` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `job_type_id_index` ON `job` (`job_type_id` ASC);

CREATE INDEX `status_id_index` ON `job` (`status_id` ASC);

CREATE INDEX `request_id_index` ON `job` (`request_id` ASC);


-- -----------------------------------------------------
-- Table `job_parameter`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_parameter` ;

CREATE TABLE IF NOT EXISTS `job_parameter` (
  `job_id` INT NOT NULL,
  `key` VARCHAR(200) NOT NULL,
  `value` LONGTEXT NULL,
  PRIMARY KEY (`job_id`, `key`),
  CONSTRAINT `job_id_fk`
    FOREIGN KEY (`job_id`)
    REFERENCES `job` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `job_id_index` ON `job_parameter` (`job_id` ASC);


-- -----------------------------------------------------
-- Table `run`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `run` ;

CREATE TABLE IF NOT EXISTS `run` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `node_id` INT NOT NULL,
  `job_id` INT NOT NULL,
  `status_id` INT NOT NULL,
  `start_date` DATETIME NOT NULL,
  `end_date` DATETIME NULL,
  `db_record_version_number` INT UNSIGNED NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  CONSTRAINT `node_id_fk_run`
    FOREIGN KEY (`node_id`)
    REFERENCES `node` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `job_id_fk_run`
    FOREIGN KEY (`job_id`)
    REFERENCES `job` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `status_id_fk_run`
    FOREIGN KEY (`status_id`)
    REFERENCES `status` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `node_id_fk_run_index` ON `run` (`node_id` ASC);

CREATE INDEX `job_id_fk_run_index` ON `run` (`job_id` ASC);

CREATE INDEX `status_id_fk_run_index` ON `run` (`status_id` ASC);


-- -----------------------------------------------------
-- Table `run_msg_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `run_msg_type` ;

CREATE TABLE IF NOT EXISTS `run_msg_type` (
  `id` SMALLINT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(2000) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `name_UNIQUE` ON `run_msg_type` (`name` ASC);


-- -----------------------------------------------------
-- Table `run_msg`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `run_msg` ;

CREATE TABLE IF NOT EXISTS `run_msg` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `run_id` INT NULL,
  `type` SMALLINT NULL,
  `message` LONGTEXT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `run_id_fk`
    FOREIGN KEY (`run_id`)
    REFERENCES `run` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `run_msg_type_fk`
    FOREIGN KEY (`type`)
    REFERENCES `run_msg_type` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `run_id_index` ON `run_msg` (`run_id` ASC);

CREATE INDEX `run_msg_type_index` ON `run_msg` (`type` ASC);


-- -----------------------------------------------------
-- Table `run_output_params`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `run_output_params` ;

CREATE TABLE IF NOT EXISTS `run_output_params` (
  `run_id` INT NOT NULL,
  `key` VARCHAR(200) NOT NULL,
  `value` LONGTEXT NULL,
  PRIMARY KEY (`run_id`, `key`),
  CONSTRAINT `run_id_fk_r_o_p`
    FOREIGN KEY (`run_id`)
    REFERENCES `run` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `run_id_fk_index` ON `run_output_params` (`run_id` ASC);


-- -----------------------------------------------------
-- Table `config_system`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `config_system` ;

CREATE TABLE IF NOT EXISTS `config_system` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `config_key` VARCHAR(45) NOT NULL,
  `config_value` VARCHAR(4000) NULL,
  `version` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `config_key_UNIQUE` ON `config_system` (`config_key` ASC);


-- -----------------------------------------------------
-- Table `node_client_status`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `node_client_status` ;

CREATE TABLE IF NOT EXISTS `node_client_status` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `node_id` INT NOT NULL,
  `client_started` TINYINT(1) NOT NULL DEFAULT true,
  `last_checkin_time` DATETIME NOT NULL,
  `next_checkin_time` DATETIME NULL,
  `seconds_until_next_checkin` INT NULL,
  `late_for_next_checkin_time` DATETIME NULL,
  `db_record_version_number` INT UNSIGNED NOT NULL DEFAULT 1,
  `notification_sent_that_client_late` TINYINT(1) NOT NULL DEFAULT false,
  PRIMARY KEY (`id`),
  CONSTRAINT `node_id_fk_n_c_s`
    FOREIGN KEY (`node_id`)
    REFERENCES `node` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `node_id_index` ON `node_client_status` (`node_id` ASC);

CREATE UNIQUE INDEX `node_id_UNIQUE` ON `node_client_status` (`node_id` ASC);


-- -----------------------------------------------------
-- Table `job_sent_to_client`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_sent_to_client` ;

CREATE TABLE IF NOT EXISTS `job_sent_to_client` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `job_id` INT UNSIGNED NULL,
  `run_id` INT UNSIGNED NULL,
  `job_parameter_count` INT UNSIGNED NULL,
  `xml_marshalled_job` LONGTEXT NULL,
  `job_sent_to_clientcol` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `job_dependencies`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `job_dependencies` ;

CREATE TABLE IF NOT EXISTS `job_dependencies` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `dependent_job` INT NOT NULL,
  `dependee_job` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `dependent_job_fk`
    FOREIGN KEY (`dependent_job`)
    REFERENCES `job` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `dependee_job_fk`
    FOREIGN KEY (`dependee_job`)
    REFERENCES `job` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `dependent_job_fk_idx` ON `job_dependencies` (`dependent_job` ASC);

CREATE INDEX `dependee_job_fk_idx` ON `job_dependencies` (`dependee_job` ASC);
