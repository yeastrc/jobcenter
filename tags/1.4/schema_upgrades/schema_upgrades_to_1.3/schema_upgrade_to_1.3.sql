
--  use appropriate use statement based on the database name used ( job_processor was the earlier name )

--  use job_processor;

 use jobcenter;


--  client status email config.  Send an email with the current client status at 6 am each day

INSERT INTO config_system (config_key, config_value) VALUES ('client.status.notification', 'false');

INSERT INTO config_system (config_key, config_value) VALUES ('client.status.notification.from.email', '***');

--  to.email is a comma delimited list
INSERT INTO config_system (config_key, config_value) VALUES ('client.status.notification.to.email', '***');

--  The "base" URL of the Web GUI (for links to running jobs in the email)  example:  'http://localhost/jobcenter'
--  If this is empty, then there will not be links to the running jobs.
INSERT INTO config_system (config_key, config_value) VALUES ('client.status.notification.server.address', '');
