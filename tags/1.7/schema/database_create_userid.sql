
--  --------------------

--  Create user id for jobcenter

--   Update the password 'jobcenter_root_pass' to something else
   
   create user 'jobcenter_root'@'localhost' identified by 'jobcenter_root_pass';
   grant all privileges on jobcenter.* to 'jobcenter_root'@'localhost';

