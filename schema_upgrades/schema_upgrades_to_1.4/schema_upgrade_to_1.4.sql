
use jobcenter;

ALTER TABLE job ADD COLUMN job_parameter_count INT(11) NULL DEFAULT -1  AFTER status_id  ;
ALTER TABLE job ADD COLUMN delay_job_until DATETIME NULL DEFAULT NULL  AFTER status_id ;


ALTER TABLE job ADD COLUMN param_error_retry_count INT(11) NOT NULL DEFAULT 0  AFTER delay_job_until , 
	ADD COLUMN soft_error_retry_count INT(11) NOT NULL DEFAULT 0  AFTER param_error_retry_count ;

CREATE  TABLE IF NOT EXISTS job_sent_to_client (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
  job_id INT(10) UNSIGNED NULL DEFAULT NULL ,
  run_id INT(10) UNSIGNED NULL DEFAULT NULL ,
  job_parameter_count_when_job_submitted INT(10)  NULL DEFAULT NULL ,
  job_parameter_count_when_job_retrieved INT(10)  NULL DEFAULT NULL ,
  xml_marshalled_job LONGTEXT NULL DEFAULT NULL ,
  PRIMARY KEY (id) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


--  soft error retry count max - a hard error is generated once this is reached, defaults to 5 if this is empty or cannot be parsed
INSERT INTO config_system (config_key, config_value) VALUES ('soft.error.retry.count.max', '5');

--  save each job sent to a client to the table 'job_sent_to_client'
INSERT INTO config_system (config_key, config_value) VALUES ('save.job.sent.to.client.for.debugging', 'false');



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

INSERT INTO node_access_rule( node_id, network_address) VALUES (  ( SELECT id FROM node WHERE name = 'localNode' ), '127.0.0.1' );
INSERT INTO node_access_rule( node_id, network_address) VALUES (  ( SELECT id FROM node WHERE name = 'localNode' ), '0:0:0:0:0:0:0:1' );
INSERT INTO node_access_rule( node_id, network_address) VALUES (  ( SELECT id FROM node WHERE name = 'submissionClientDefaultNodeName' ), '127.0.0.1' );
INSERT INTO node_access_rule( node_id, network_address) VALUES (  ( SELECT id FROM node WHERE name = 'submissionClientDefaultNodeName' ), '0:0:0:0:0:0:0:1' );
INSERT INTO node_access_rule( node_id, network_address) VALUES (  ( SELECT id FROM node WHERE name = 'guiClientDefaultNodeName' ), '127.0.0.1' );
INSERT INTO node_access_rule( node_id, network_address) VALUES (  ( SELECT id FROM node WHERE name = 'guiClientDefaultNodeName' ), '0:0:0:0:0:0:0:1' );
COMMIT;


-- -----------------------------------------------------
-- Data for table request_type and job_type - set up defaults for quick start
-- -----------------------------------------------------
SET AUTOCOMMIT=0;
USE `jobcenter`;
INSERT INTO request_type (name) VALUES ('DoNothingTestOnlyModule');
INSERT INTO job_type (priority, name, description, enabled, module_name, minimum_module_version) VALUES ('10', 'DoNothingTestOnlyModule', 'A module that is only for testing a Jobcenter installation.  The module does no actual work', 1, 'DoNothingTestOnlyModule', '1');
COMMIT;

