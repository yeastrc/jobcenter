
use jobcenter;


ALTER TABLE job_type ADD COLUMN required_execution_threads INT(10) UNSIGNED NULL DEFAULT NULL  AFTER priority ;

