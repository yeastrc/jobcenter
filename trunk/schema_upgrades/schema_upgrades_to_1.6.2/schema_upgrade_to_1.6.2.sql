
use jobcenter;

--  If upgrading from before 1.6, skip statement with "DROP COLUMN required_execution_threads"


ALTER TABLE job_type 
DROP COLUMN required_execution_threads;


ALTER TABLE job_type 
ADD COLUMN max_required_execution_threads INT(10) UNSIGNED NULL DEFAULT NULL AFTER priority;

ALTER TABLE job 
ADD COLUMN required_execution_threads INT(10) UNSIGNED NULL DEFAULT NULL AFTER priority;

