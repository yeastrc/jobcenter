

--  use appropriate use statement based on the database name used ( job_processor was the earlier name )

--  use job_processor;

 use jobcenter;




ALTER TABLE node_client_status
  ADD COLUMN client_started TINYINT(1) NOT NULL DEFAULT true  AFTER node_id ,
  ADD COLUMN next_checkin_time DATETIME NULL DEFAULT NULL  AFTER last_checkin_time ,
  CHANGE COLUMN seconds_until_next_checkin seconds_until_next_checkin INT(11) NULL DEFAULT NULL  ,
  CHANGE COLUMN late_for_next_checkin_time late_for_next_checkin_time DATETIME NULL DEFAULT NULL  ,
  CHANGE COLUMN notification_sent_that_client_late notification_sent_that_client_late TINYINT(1) NOT NULL DEFAULT false ;


UPDATE node_client_status SET next_checkin_time = TIMESTAMPADD(SECOND, seconds_until_next_checkin, last_checkin_time )
  WHERE last_checkin_time IS NOT NULL AND seconds_until_next_checkin IS NOT NULL;


INSERT INTO config_system (config_key, config_value) VALUES ('client.normal.startup.notification', 'false');

INSERT INTO config_system (config_key, config_value) VALUES ('client.startup.not.prev.shutdown.notification', 'false');

INSERT INTO config_system (config_key, config_value) VALUES ('client.shutdown.notification', 'false');