

All the jar files in this project in the lib are not copied to the actual web project
so they also need to be in the web project, currently "JobCenter_Server_Jersey".


Database connection set up in Tomcat context.xml is "jdbc/jobcenter".


jdbc:mysql://localhost:3306/jobcenter?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=UTF-8&amp;characterSetResults=UTF-8"/>