
!!!!!! Dont Forget to Install the JobCenter_Blast_Module. This submitter is useless without it. !!!!!!

Assuming that you followed the directions for installing the server (mysql, apache, tomcat), simply export a project .war file
and drop it in the webapps directory.

To configure the submitter, you need to edit the blast_submitter_config.properties file. If you have one server and are installing many clients,
do this before you export the .war. Change the "connectionURL" to match that of your server. Make sure to escape the : character with a leading \
like this: \:

If you already exported the .war, you may find and modify the blast_submitter_config.properites in the unrolled .war.
Browse to *webapps/blast/WEB-INF/classes and modify the blast_submitter_config.properties file if needed.