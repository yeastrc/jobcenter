


-- -----------------------------------------------------
-- Data for table jobcenter.config_system
-- -----------------------------------------------------
SET AUTOCOMMIT=0;

USE jobcenter;

INSERT INTO config_system (config_key, config_value) VALUES ('client.checkin.notification.from.email', '***');

--  to.email is a comma delimited list
INSERT INTO config_system (config_key, config_value) VALUES ('client.checkin.notification.to.email', '***');
INSERT INTO config_system (config_key, config_value) VALUES ('client.checkin.notification.smtp.email.host', 'localhost');

INSERT INTO config_system (config_key, config_value) VALUES ('client.normal.startup.notification', 'false');
INSERT INTO config_system (config_key, config_value) VALUES ('client.startup.not.prev.shutdown.notification', 'false');
INSERT INTO config_system (config_key, config_value) VALUES ('client.shutdown.notification', 'false');


--  client status email config.  Send an email with the current client status at 6 am each day

INSERT INTO config_system (config_key, config_value) VALUES ('client.status.notification', 'false');

INSERT INTO config_system (config_key, config_value) VALUES ('client.status.notification.from.email', '***');

--  to.email is a comma delimited list
INSERT INTO config_system (config_key, config_value) VALUES ('client.status.notification.to.email', '***');

--  The "base" URL of the Web GUI (for links to running jobs in the email)  example:  'http://localhost/jobcenter'
--  If this is empty, then there will not be links to the running jobs.
INSERT INTO config_system (config_key, config_value) VALUES ('client.status.notification.server.address', '');



--  soft error retry count max - a hard error is generated once this is reached, defaults to 5 if this is empty or cannot be parsed
INSERT INTO config_system (config_key, config_value) VALUES ('soft.error.retry.count.max', '5');

--  save each job sent to a client to the table 'job_sent_to_client'
INSERT INTO config_system (config_key, config_value) VALUES ('save.job.sent.to.client.for.debugging', 'false');

COMMIT;
