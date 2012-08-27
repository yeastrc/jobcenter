


-- -----------------------------------------------------
-- Data for table `jobcenter`.`config_system`
-- -----------------------------------------------------
SET AUTOCOMMIT=0;

USE `jobcenter`;

INSERT INTO `config_system` (`config_key`, `config_value`) VALUES ('client.checkin.notification.from.email', '***');
INSERT INTO `config_system` (`config_key`, `config_value`) VALUES ('client.checkin.notification.to.email', '***');
INSERT INTO `config_system` (`config_key`, `config_value`) VALUES ('client.checkin.notification.smtp.email.host', 'localhost');




INSERT INTO `job_processor`.`config_system` (`config_key`, `config_value`) VALUES ('client.normal.startup.notification', 'false');

INSERT INTO `job_processor`.`config_system` (`config_key`, `config_value`) VALUES ('client.startup.not.prev.shutdown.notification', 'false');

INSERT INTO `job_processor`.`config_system` (`config_key`, `config_value`) VALUES ('client.shutdown.notification', 'false');


COMMIT;
