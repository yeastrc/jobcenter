
-- -----------------------------------------------------
-- Use jobcenter database
-- -----------------------------------------------------
USE `jobcenter` ;

-- -----------------------------------------------------
-- Add the request type
-- -----------------------------------------------------
INSERT INTO `request_type` (`name`) VALUES ('blast');

-- -----------------------------------------------------
-- Add the job type
-- -----------------------------------------------------
INSERT INTO `job_type` (`priority`, `name`, `description`, `enabled`, `module_name`) VALUES (4, 'blast', 'blast module', 1, 'blastModule');

-- -----------------------------------------------------
-- Add unique blast name and description
-- -----------------------------------------------------
INSERT INTO `node` (`name`, `description`) VALUES ('blast_submitter', 'blast submitter for blast module');

INSERT INTO `node_access_rule` (`node_id`, `network_address`) VALUES ((SELECT `id` FROM `node` WHERE `name` = "blast_submitter"), '127.0.0.1');

-- -----------------------------------------------------
-- Add unique client name and desctiption
-- -----------------------------------------------------




-- !!!!!!!!!!!!!! IMPORTANT !!!!!!!!!!!!!!!!!!



-- CHANGE ALL INSTANCES OF "your_unique_name" to something you like.

-- CHANGE ALL INSTANCES OF "127.0.0.1" to the correct IP address for your setup.

INSERT INTO `node` (`name`, `description`) VALUES ('your_unique_name', 'blast client on your computer');

INSERT INTO `node_access_rule` (`node_id`, `network_address`) VALUES ((SELECT `id` FROM `node` WHERE `name` = "your_unique_name"), '127.0.0.1');
