

   Welcome to the Jobcenter documentation.


   Jobcenter is a framework to run units of work on various machines.

   Beyond Jobcenter, you will need code that
   	1) submits these units of work.
   	2) processes these units of work (Jobcenter modules that are put in the Jobcenter client)

   	These are assumed to be written in Java as they need to interface with the Jobcenter code.

	Jobcenter is shipped with two modules along with code to submit the jobs for them.

	1) A simple do nothing module that is pre-installed in the client installation

	2) A module that runs Blast on a provided sequence.
	     If you choose to install and use this module, you will need to install and configure Blast
	     and configure the module with the location of Blast


    See the document on character encoding for limitations on what values can be passed through 
    the Jobcenter framework.
    
    

Options for installation

1)  Jobcenter is normally installed on more than one computer with the server installed on one
    computer and the client installed on various computers.
    To do this type of installation, follow the instructions in the folder 'installation'
    starting with the file '0_overall_installation.txt'

2)  Quick sample installation on Windows:
		Jobcenter can be installed where everything is run on a single computer.
		This will let you see all the components operating.
    	To do this type of installation,
    	follow the instructions in the folder 'quick_installation_on_windows'
    	starting with the file '0_overall_quick_installation.txt'

