
The way the JobCenter project was created was to create
Java Eclipse projects for all the JobCenter_... directories.

After importing all those projects, it is then optional to create
a generic Eclipse project in the "JobCenter" root directory just
to allow running of the ant scripts in that directory via Eclipse.

Create Variable GIT_Clone_HOME under Link src then variables.
	It should point to the root dir of the clone of the jobcenter GIT clone.
	