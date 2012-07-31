package org.jobcenter.blast.main;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.constants.RunMessageTypesConstants;

import org.jobcenter.job_client_module_interface.ModuleInterfaceClientMainInterface;
import org.jobcenter.job_client_module_interface.ModuleInterfaceClientServices;
import org.jobcenter.job_client_module_interface.ModuleInterfaceJobProgress;
import org.jobcenter.job_client_module_interface.ModuleInterfaceRequest;
import org.jobcenter.job_client_module_interface.ModuleInterfaceResponse;



public class BlastMain implements ModuleInterfaceClientMainInterface {

	// Get logger so that we can write to the log files.
	private static Logger log = Logger.getLogger(BlastMain.class);
	// Set the name of the .properties file
	private static final String PROPERTIES = "blast_module_config.properties";
	// Set the names of the parameters that we are getting from the server.
	private static final String JOB_PARAM_QUERY = "query";
	private static final String JOB_PARAM_TASK = "task";
	private static final String JOB_PARAM_DATABASE = "database";
	private static final String JOB_PARAM_OUTFMT = "outfmt";
	private static final String JOB_PARAM_ALIGNMENTS = "alignments";
	private static final String JOB_PARAM_DESCRIPTIONS = "descriptions";
	private static final String JOB_PARAM_EMAIL = "email";
	private static final String JOB_PARAM_FILENAME= "filename";
	// Get an instance of the configProperties object to store the .properties parameters.
	private ConfigProperties configProperties = null;
	// Get an instance of BlastUtil class so we can call blastUtil() method and pass it the job_params.
	private BlastUtil blastUtil = new BlastUtil();
	
	/* (non-Javadoc)
	 * @see org.jobcenter.job_module_interface.JobManagerInterface#destroy()
	 */
	@Override
	public void destroy() {

	}


	@Override
	/**
	 * Called on a separate thread when a shutdown request comes from the operating system.
	 * If this is not heeded, the process may be killed by the operating system after some time has passed ( controlled by the operating system )
	 */
	public void shutdown() {

	}


	@Override
	/**
	 * Not used in this module.
	 */
	public void stopRunningAfterProcessingJob() {
		// TODO Auto-generated method stub

	}


	/**
	 * Called when the object is instantiated.
	 * The object will not be used to process requests if this method throws an exception
	 *
	 * @param moduleInstanceNumber - The instance number of this module in this client,
	 *         incremented for each time the module instantiated while the client is running,
	 *         reset to 1 when the client is restarted.
	 *         This can be useful for separating logging or other file resources between
	 *         instances of the same module in the same client.
	 * @throws Throwable
	 */
	@Override
	public void init(int moduleInstanceNumber) throws Throwable {

		/* 
		 * 
		 * Read the properties file.
		 * This whole thing could go in a separate method, but its the only thing we are doing in init.
		 * Just consider that for neatness in your module and disregard my lack of it here please :)
		 *  
		 */
		// Get instance of ClassLoader for BlastMain
		ClassLoader thisClassLoader = BlastMain.class.getClassLoader();
		// Get instance of InputStream and set to null
		InputStream prop = null;
		// Try to load the .properties
    	try {
    		prop = thisClassLoader.getResourceAsStream(PROPERTIES);
    		Properties configProps = new Properties();
			configProps.load(prop);

			/*
			 * If any of the .properties parameters are null, we need to STOP
			 * 
			 * Any missing properties will cause the module to fail, and we need
			 * it to FAIL RIGHT HERE. 
			 * 
			 * If it does not fail here, the module will be
			 * made available to the client, which will in turn be set as available 
			 * to the server. 
			 * 
			 * Should someone try to submit a job, it would then fail
			 * without the user knowing that it failed. 
			 */
			String blast_dir = configProps.getProperty("blast_dir");
			String tmp_dir = configProps.getProperty("tmp_dir");
			String db_dir = configProps.getProperty("db_dir");
			String smtp_host = configProps.getProperty("smtp_host");
			String ip_host = configProps.getProperty("ip_host");
			boolean isValid = true;
			String invalidProps = "";
			
			/* 
			 * Make sure that nothing is empty or null. If it is, 
			 * set isValid to false and note which one it was.
			 */
			if (blast_dir == null || blast_dir.isEmpty()) {
				isValid = false;
				invalidProps += " " + blast_dir + " ";
			}
			
			if (tmp_dir == null || tmp_dir.isEmpty()) {
				isValid = false;
				invalidProps += " " + tmp_dir + " ";
			}
			
			if (db_dir == null || db_dir.isEmpty()) {
				isValid = false;
				invalidProps += " " + db_dir + " ";
			}
			
			if (smtp_host == null || smtp_host.isEmpty()) {
				isValid = false;
				invalidProps += " " + smtp_host + " ";
			}
			
			if (ip_host == null || ip_host.isEmpty()) {
				isValid = false;
				invalidProps += " " + ip_host + " ";
			}
				
			
			/*
			 *  If all the properties checked out, create the object
			 */
			if (isValid) {
				
				configProperties = new ConfigProperties(blast_dir, tmp_dir, db_dir, smtp_host, ip_host);
				/* If everything was ok while reading the properties, and the are all valid,
				 * then log them.
				 */
	            log.info("Read in " + PROPERTIES + ", tmp_dir: " + configProperties.tmp_dir + 
	            		", db_dir: " + configProperties.db_dir + ", blast_dir: " + configProperties.blast_dir + 
	            		", smtp host: " + configProperties.smtp_host + ", ip: " + configProperties.ip_host);
				
			}
			else {
				
				// Log the error and DO NOT CONTINUE
				log.error("There were some invalid properties detected: " + invalidProps);
				throw new Exception("Invalid parameters were found: " + invalidProps);
			
			}

        } catch (Throwable t) {
        	// Again, 
        	log.error("Error reading properties file: " + PROPERTIES, t);
        	throw t;
        	
        } finally {
        	try {
        		if (prop != null) {
        			prop.close();
        		}
        	} catch (Throwable t){
        		log.error("Error closing properties file " + PROPERTIES, t);
        	}
        }
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.job_module_interface.JobManagerInterface#processRequest()
	 */
	@Override
	public void processRequest( ModuleInterfaceRequest jobManagerModuleRequest, ModuleInterfaceResponse jobManagerModuleResponse, ModuleInterfaceJobProgress jobManagerModuleJobProgress, ModuleInterfaceClientServices jobManagerClientServices ) throws Throwable {

		int jobStatus = JobStatusValuesConstants.JOB_STATUS_RUNNING;
		log.info( "Called BlastMain.processRequest()" );
		
		Map<String, String> parameters = jobManagerModuleRequest.getJobParameters();
				
		// set the job parameters
		String query = parameters.get(JOB_PARAM_QUERY);
		String task = parameters.get(JOB_PARAM_TASK);
		String database = parameters.get(JOB_PARAM_DATABASE);
		String outfmt = parameters.get(JOB_PARAM_OUTFMT);
		String alignments = parameters.get(JOB_PARAM_ALIGNMENTS);
		String descriptions = parameters.get(JOB_PARAM_DESCRIPTIONS);
		String email = parameters.get(JOB_PARAM_EMAIL);
		String filename = parameters.get( JOB_PARAM_FILENAME );
		boolean nullParamFound = false;
				
		// Make sure they are not empty or null
		if (query == null || query == ""){
			jobStatus = JobStatusValuesConstants.JOB_STATUS_HARD_ERROR;
			jobManagerModuleResponse.addRunMessage(RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR, "Query string was not provided.");
			nullParamFound = true;
		}
		
		if (task == null || task == ""){
			jobStatus = JobStatusValuesConstants.JOB_STATUS_HARD_ERROR;
			jobManagerModuleResponse.addRunMessage(RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR, "Blast task was not provided.");
			nullParamFound = true;
		}
		
		if (database == null || database == ""){
			jobStatus = JobStatusValuesConstants.JOB_STATUS_HARD_ERROR;
			jobManagerModuleResponse.addRunMessage(RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR, "Database was not provided.");
			nullParamFound = true;
		}
		
		if (outfmt == null || outfmt == ""){
			jobStatus = JobStatusValuesConstants.JOB_STATUS_HARD_ERROR;
			jobManagerModuleResponse.addRunMessage(RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR, "outfmt was not provided.");
			nullParamFound = true;
		}
		
		if (alignments == null || alignments == ""){
			jobStatus = JobStatusValuesConstants.JOB_STATUS_HARD_ERROR;
			jobManagerModuleResponse.addRunMessage(RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR, "alignments number was not provided.");
			nullParamFound = true;
		}
		
		if (descriptions == null || descriptions == ""){
			jobStatus = JobStatusValuesConstants.JOB_STATUS_HARD_ERROR;
			jobManagerModuleResponse.addRunMessage(RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR, "descriptions number was not provided.");
			nullParamFound = true;
		}
		
		if (email == null || email == ""){
			jobStatus = JobStatusValuesConstants.JOB_STATUS_HARD_ERROR;
			jobManagerModuleResponse.addRunMessage(RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR, "email address was not provided.");
			nullParamFound = true;
		}
		
		if ((filename == null || filename == "") && (email == null || email == "")){
			jobStatus = JobStatusValuesConstants.JOB_STATUS_HARD_ERROR;
			jobManagerModuleResponse.addRunMessage(RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR, "emial or filename was not provided.");
			nullParamFound = true;
		}
		
		// If an empty or null was found
		if (nullParamFound){
			log.debug("Null parameter found.");
			return;
		}
		
		// Log the status of this job. It is now running.
		jobStatus = JobStatusValuesConstants.JOB_STATUS_RUNNING;
		log.info("Job Status: " + jobStatus);

		try {
			
			// Call submitBlast() and pass it all the validated parameters.
			blastUtil.submitBlast(query, task, database, outfmt, alignments, descriptions, email, filename, configProperties);
			jobStatus = JobStatusValuesConstants.JOB_STATUS_FINISHED;
			jobManagerModuleResponse.addRunMessage( RunMessageTypesConstants.RUN_MESSAGE_TYPE_MSG, "Successful Completion Blast Main" );
			
		} catch (Throwable t) {
			
			jobStatus = JobStatusValuesConstants.JOB_STATUS_HARD_ERROR;
			log.error( "Exception in call to blastUtil.submitBlast: ", t );
			log.error("Print all the things...   query: " + query);
			log.error("| task: " + task + " | database: " + database + " | outfmt: " + outfmt + " | alignments: " + alignments + " | descriptions: " + descriptions + " | email: " + email + " | filename: " + filename);
			log.error("smtp_host: " + configProperties.smtp_host + " ||| ip_host: " + configProperties.ip_host);
			jobManagerModuleResponse.setStatusCode( JobStatusValuesConstants.JOB_STATUS_HARD_ERROR );

			//  TODO   Improve exception reporting

			ByteArrayOutputStream baos = new ByteArrayOutputStream( 1000 );

			PrintStream printStream = new PrintStream( baos );

			t.printStackTrace( printStream );

			printStream.close();

			String exStackTrace = baos.toString();

			String message = "Failed Completion. Exception type = " + t.getClass().getName() + ", Exception string = " + t.toString()
					+ "\n" + exStackTrace;

			jobManagerModuleResponse.addRunMessage( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR, message );
			
		}

		log.info("Job Status: " + jobStatus);
		
		jobManagerModuleResponse.setStatusCode( jobStatus );

	}

	/* (non-Javadoc)
	 * @see org.jobcenter.job_module_interface.JobManagerInterface#reset()
	 */
	@Override
	public void reset()  throws Throwable {

	}

	
}
