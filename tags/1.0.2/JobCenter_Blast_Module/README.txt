
!!!!!! Dont Forget to Install the JobManager_Blast_Submitter. This Module is useless without it. !!!!!!

This readme assumes that you already installed and setup the sql server. If you did not, then follow the instructions in "create_database.txt" before you begin.

Configure the Server for the Blast Module

!!! IMPORTANT !!!
	If you don't want to run the mysql commands, just MODIFY and THEN use the accompanying .sql file. (instructions are in the .sql file)

1. Get the module name and module version number. These are found in the jobcenter_module_config_per_module.properties file.

	For the Blast_Module, the module name = blastModule and the version = 1.

2. Find the Request_Type_Name and Job_Type_Name from the Job Submitter Application. If you are unsure about these names, ask the person who wrote the submitter.

	For the Blast_Module, these have both been set to: blast

	a. Add the Request_Type_Name to the 'request_type' table, but do not add an ID as it is auto-generated. Upon successful insertion, write down the ID that was generated.

		!! Example !!

		INSERT INTO `jobcenter`.`request_type` (`name`) VALUES ('blast');

	b. Create an entry in the table 'job_type'. Populate the fields as follows

		field::value --notes

		ID::auto-generated 				--don't specify a value
		priority::4 					--The lower number you use, the higher the priority. If this job type should be high priority, use 1 ro 2.
		name::blast 					--this is the Job_Type_Name name you got from the job submitter
		description::Blast Module 		--Briefly describe the module.
		enabled::1 						--1=enables, 0=disabled
		module_name::blastModule 		--get this from the jobcenter_module_config_per_module.properties file
		minimum_module::1 				--get this from the jobcenter_module_config_per_module.properties file

		!! Example !!

		INSERT INTO `jobcenter`.`job_type` (`priority`, `name`, `description`, `enabled`, `module_name`) VALUES (4, 'blast', 'blast module', 1, 'blastModule');


3. Create node names for the submitter and one for each client. IMPORTANT!!! These names CANNOT be the same.

	a. Update the 'node' table, adding new node name for each.

		1. One for the submitter
			I recommend: "blast_submitter"

			!! Example !!

			INSERT INTO `jobcenter`.`node` (`name`, `description`) VALUES ('blast_submitter', 'blast submitter for blast module');


		2. One for the client (NOT THE SAME AS SUBMITTER)
			This one should be unique to your client. I chose "michaelmacpro" because its running on my MacPro computer.

			!! Example !!

			INSERT INTO `jobcenter`.`node` (`name`, `description`) VALUES ('!!!! NAME !!!!', '!!!! DESCRIPTION !!!!');

	b. Update the  'node_access_rule'  table, adding the node's ID from the 'node' table and IP address of the machine where this submission process will be installed.
		Note: the IP address MUST be the address visible to the server. if the server has a WAN ip and you are behind a firewall, your up should be the your WAN ip.
		If you are on the same network segment as the server, use your ifconfig/ipconfig listed address.

		1. Create a node_access_rule for each entry in the node table.
		IMPORTANT!!!! Each entry in the 'node' table MUST have an entry in the 'node_access_rule' table.

			!! Example !!

			INSERT INTO `jobcenter`.`node_access_rule` (`node_id`, `network_address`) VALUES ((SELECT `id` FROM `node` WHERE `name` = "!!!!! GET NAME FROM NODE TABLE !!!!!"), '127.0.0.1');




Building the Module

1. Checkout the code.

	# Non-members may check out a read-only working copy anonymously over HTTP.
	svn checkout http://job-center.googlecode.com/svn/trunk/ job-center-read-only

2.	Compile the Client and Module by executing the "build_all_JobManager.xml" ant script.

3.	If you don't know how to do this, but can export the project to a jar file, do that. But keep in mind that you will need a specific directory structure,
	and some other files from the project. If you export the project jar, copy and pase/ create appropriate directories as seen below.
	 The following example is what you should end up with no matter which method you use.

	Directory Structure

	1. jobcenter_modules/blast_module/config
	2. jobcenter_modules/blast_module/config_sample_files
	3. jobcenter_modules/blast_module/lib
	4. jobcenter_modules/blast_module/main_jar

   Required Files Per Directory
	config:
		jobcenter_module_config_per_client.properties
		blast_module_config.properties

	config_sample_files:
		jobcenter_module_config_per_client.properties
		blast_module_config.properties
		config_sample_files_directory_explanation.txt

	lib:
		apache-log4j-extras-1.0.jar
		log4j-1.2.15.jar
		mail.jar

   main_jar:
   		jobcenter_blast_module.jar



Installing the Module

1.	First, you must build the module. If you checked out the trunk via SVN, run the "build_all_JobManager.xml" ant script, then navigate to the
	installables directory and copy the directory according to step 2. If not, check out the trunk first. The Blast_Module depends on some other projects.

2.	For the Blast_Module, simply place the blast_module directory inside the path "jobcenter_client_installation/jobcenter_modules".
	Then copy the two .properties files into the config directory. Make sure and modify your .properties files appropriately.
	Each file contains its own instructions with verbose comments.

