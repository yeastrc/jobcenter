
-------------------------------

----   Other database population


-- -----------------------------------------------------
-- Data for table `run_msg_type`
-- -----------------------------------------------------
SET AUTOCOMMIT=0;
USE `jobcenter`;
INSERT INTO run_msg_type (`id`, `name`, `description`) VALUES ('1', 'info', 'info');
INSERT INTO run_msg_type (`id`, `name`, `description`) VALUES ('2', 'warning', 'warning');
INSERT INTO run_msg_type (`id`, `name`, `description`) VALUES ('3', 'error', 'error');

COMMIT;


USE `jobcenter`;

-- -----------------------------------------------------
-- Data for table `status`

-- -----------------------------------------------------
SET AUTOCOMMIT=0;
INSERT INTO status (`id`, `name`, `description`) VALUES ('1', 'Submitted', '');
INSERT INTO status (`id`, `name`, `description`) VALUES ('2', 'Running', '');
INSERT INTO status (`id`, `name`, `description`) VALUES ('3', 'Stalled', '');
INSERT INTO status (`id`, `name`, `description`) VALUES ('4', 'Complete', '');
INSERT INTO status (`id`, `name`, `description`) VALUES ('5', 'Complete with warnings', '');
INSERT INTO status (`id`, `name`, `description`) VALUES ('6', 'Hard Error', '');
INSERT INTO status (`id`, `name`, `description`) VALUES ('7', 'Soft Error', '');
INSERT INTO status (`id`, `name`, `description`) VALUES ('8', 'Canceled', '');
INSERT INTO status (`id`, `name`, `description`) VALUES ('9', 'Requeued', '');


COMMIT;













-- -----------------------------------------------------
-- Data for table node - set up defaults for quick start
-- -----------------------------------------------------
SET AUTOCOMMIT=0;
USE `jobcenter`;

INSERT INTO node (name, description) VALUES ('localNode', 'Client on same machine as server');
INSERT INTO node (name, description) VALUES ('submissionClientDefaultNodeName', 'Node used to submit jobs if submitter uses the default');
INSERT INTO node (name, description) VALUES ('guiClientDefaultNodeName', 'Node used by the provided GUI');
COMMIT;




-- -----------------------------------------------------
-- Data for table node_access_rule - set up defaults for quick start

-- -----------------------------------------------------
SET AUTOCOMMIT=0;

USE `jobcenter`;

INSERT INTO node_access_rule( node_id, network_address) VALUES (  ( SELECT id FROM node WHERE name = 'localNode'), '127.0.0.1');
INSERT INTO node_access_rule( node_id, network_address) VALUES (  ( SELECT id FROM node WHERE name = 'localNode'), '0:0:0:0:0:0:0:1');
INSERT INTO node_access_rule( node_id, network_address) VALUES (  ( SELECT id FROM node WHERE name = 'submissionClientDefaultNodeName'), '127.0.0.1');
INSERT INTO node_access_rule( node_id, network_address) VALUES (  ( SELECT id FROM node WHERE name = 'submissionClientDefaultNodeName'), '0:0:0:0:0:0:0:1');
INSERT INTO node_access_rule( node_id, network_address) VALUES (  ( SELECT id FROM node WHERE name = 'guiClientDefaultNodeName'), '127.0.0.1');
INSERT INTO node_access_rule( node_id, network_address) VALUES (  ( SELECT id FROM node WHERE name = 'guiClientDefaultNodeName'), '0:0:0:0:0:0:0:1');
COMMIT;





-- -----------------------------------------------------
-- Data for table request_type and job_type - set up defaults for quick start
-- -----------------------------------------------------
SET AUTOCOMMIT=0;
USE `jobcenter`;
INSERT INTO request_type (name) VALUES ('DoNothingTestOnlyModule');
INSERT INTO job_type (priority, name, description, enabled, module_name, minimum_module_version) VALUES ('10', 'DoNothingTestOnlyModule', 'A module that is only for testing a Jobcenter installation.  The module does no actual work', 1, 'DoNothingTestOnlyModule', '1');
COMMIT;



