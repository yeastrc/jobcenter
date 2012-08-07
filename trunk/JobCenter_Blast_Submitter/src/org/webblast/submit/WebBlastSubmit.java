package org.webblast.submit;

// Needed for key value pairs
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

// Needed for logging
import org.apache.log4j.Logger;

// Needed for any job submitter
import org.jobcenter.client.main.SubmissionClientConnectionToServer;
import org.jobcenter.coreinterfaces.JobSubmissionInterface;
import org.webblast.listeners.ServletContextAppListener;

public class WebBlastSubmit {

	//log4j logger
	private static Logger log = Logger.getLogger(WebBlastSubmit.class);

	//private static final String connectionURL = "http://localhost:8080/JobCenter_Server_Jersey/";
	private static String connectionURL;

	// This is the job_type_name and request_type_name that will be referenced
	// in the configuration of the JobCenter Server and will be stored in the database.
	private static final String BLAST_REQUEST_TYPE_NAME = "blast";
	private static final String BLAST_JOB_TYPE_NAME = "blast";
	private static final int BLAST_JOB_PRIORITY = 4;
	private static final String BLAST_NODE_NAME = "blast_submitter";
	private static final String PROPERTIES = "blast_submitter_config.properties";

	// These parameters are specific to the blast module
	// You will place your own parameters here.
	private static final String JOB_PARAM_QUERY = "query";
	private static final String JOB_PARAM_TASK = "task";
	private static final String JOB_PARAM_DATABASE = "database";
	private static final String JOB_PARAM_OUTFMT = "outfmt";
	private static final String JOB_PARAM_ALIGNMENTS = "alignments";
	private static final String JOB_PARAM_DESCRIPTIONS = "descriptions";
	private static final String JOB_PARAM_EMAIL = "email";
	private static final String JOB_PARAM_FILENAME = "filename";


	/**
	 * @param args
	 */
	public void main(String query, String task, String database, String outfmt, String alignments, String descriptions, String email, String filename) throws Throwable {


		Map<String, String> jobParameters = new HashMap<String, String> ();

		// This is how you will build the key:value map
		// which is sent to the JobCenter Server and then passed to your module.
		jobParameters.put( JOB_PARAM_QUERY, query );
		jobParameters.put( JOB_PARAM_TASK, task );
		jobParameters.put( JOB_PARAM_DATABASE, database );
		jobParameters.put( JOB_PARAM_OUTFMT, outfmt );
		jobParameters.put( JOB_PARAM_ALIGNMENTS, alignments );
		jobParameters.put( JOB_PARAM_DESCRIPTIONS, descriptions);
		jobParameters.put( JOB_PARAM_EMAIL, email );
		jobParameters.put( JOB_PARAM_FILENAME, filename );

		readPropertiesFile();

		try {

			// Create new jobSubmissionClient
			JobSubmissionInterface jobSubmissionClient = new SubmissionClientConnectionToServer();

			// This is the node name you will use when are configuring the server for this submitter
			jobSubmissionClient.setNodeName( BLAST_NODE_NAME );

			// Initialize the connection object to the JobCenterServer
			jobSubmissionClient.init( connectionURL );

			// Send the job to the JobCenterServer and include all of your required parameters.
			// Use the following format:
			// BLAST_REQUEST_TYPE_NAME, null, BLAST_JOB_TYPE_NAME, email, BLAST_JOB_PRIORITY, jobParameters
			// Note: If you are submitting the second part of a two part job, you must include the request ID
			// when submitting part two if you want them to be tracked together as the same request.
			int requestId = jobSubmissionClient.submitJob(BLAST_REQUEST_TYPE_NAME, null /* requestId */, BLAST_JOB_TYPE_NAME, email, BLAST_JOB_PRIORITY, jobParameters );

			// Log the requestId
			if (log.isInfoEnabled()) {

				log.info( "requestId = " + requestId );

			}

		} catch (Throwable t) {

			// Throw an error and be verbose.
			log.error("Error submitting JobCenter job from. ConnectionURL:" + connectionURL
					+ " job_type_name: " + BLAST_JOB_TYPE_NAME + " request_type_name " + BLAST_REQUEST_TYPE_NAME, t);
			throw t;

		}

	}

public void readPropertiesFile() throws Throwable {

		//read the file
		ClassLoader thisClassLoader = ServletContextAppListener.class.getClassLoader();
		InputStream prop = null;

    	try {
            //load and read properties file
    		prop = thisClassLoader.getResourceAsStream(PROPERTIES);
    		Properties configProps = new Properties();
			configProps.load(prop);

			if (configProps.getProperty("connectionURL") != null && !configProps.getProperty("connectionURL").isEmpty()) {
				connectionURL = configProps.getProperty("connectionURL");

			}
			else {

				throw new Exception("Reading the properties file for connectionURL failed.");

			}

            log.info("Read in " + PROPERTIES + ", connectionURL: " + connectionURL);

        } catch (Throwable t) {

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

}