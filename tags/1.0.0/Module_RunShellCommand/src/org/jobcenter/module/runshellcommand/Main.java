package org.jobcenter.module.runshellcommand;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.extras.DOMConfigurator;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.constants.RunMessageTypesConstants;
import org.jobcenter.job_client_module_interface.ModuleInterfaceClientMainInterface;
import org.jobcenter.job_client_module_interface.ModuleInterfaceClientServices;
import org.jobcenter.job_client_module_interface.ModuleInterfaceJobProgress;
import org.jobcenter.job_client_module_interface.ModuleInterfaceRequest;
import org.jobcenter.job_client_module_interface.ModuleInterfaceResponse;

/**
 * main class executed by  Client
 *
 */
public class Main implements ModuleInterfaceClientMainInterface {

	private static Logger log = Logger.getLogger(ModuleInterfaceClientMainInterface.class);


	private static final String LOG4J_CONFIG_FILENAME = "log4j_run_shell_command.xml";

	private static final String CONFIG_PROPERTIES_FILENAME = "run_shell_command.properties";

//	private static final String CONFIG_PROPERTY_OUTPUT_ROOT_DIRECTORY = "outputRootDirectory";
//
//	private static final String CONFIG_PROPERTY_SERVER_DIRECTORY = "serverDirectory";
//
//	private static final String CONFIG_PROPERTY_IMPORT_SERVER_IP = "importServerIP";
//
//	private static final String CONFIG_PROPERTY_IMPORT_SERVER_USERNAME = "importServerUsername";

	private static final String JOB_PARAM_COMMAND_TO_RUN = "commandToRun";

	/////////////////////////////////

	//  has the module been initialized

	private boolean initialized = false;

	/////////////////////////////////

	//   Configuration from properties file

//
//	private File outputRootDirectory;
//
//	private String serverDirectory;
//
//
//	//  In Configuration properties file  Used for logging on to Imagerepo for the file transfer
//
//	private String serverIPAddress;
//
//	private String serverUsername;


	//  has a shutdown request been received.  Volatile since the shutdown request will be on a different thread

	private volatile boolean shutdownRequested = false;







	//   Constructor:    must be public so job manager client can instantiate it
	public Main() {
	}

	/**
	 * get an instance of Main
	 * @return an instance of Main
	 */
	public static Main getInstance() {
		return new Main();
	}


	//////////////////////////////////////////////////////////////////////////


//	Job Manager lifecycle methods


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

//		System.out.println("\nCalled Main.init()");
//
//		System.out.println("\n System.getProperty( \"java.class.path\" ) |" + System.getProperty( "java.class.path" ) + "|" );


		ClassLoader thisClassLoader = this.getClass().getClassLoader();

		InputStream input =  thisClassLoader.getResourceAsStream( LOG4J_CONFIG_FILENAME );

		//  Configure log4j
		new DOMConfigurator().doConfigure(input, LogManager.getLoggerRepository());

		log.info( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Starting Main .....  Called Main.init()" );


		URL log4jFile = thisClassLoader.getResource( LOG4J_CONFIG_FILENAME );

		log.info( "log4j file '" + LOG4J_CONFIG_FILENAME + "' load path = " + log4jFile.getFile() );


		//  these files are used by the job manager program

//		InputStream propsPerModule = thisClassLoader.getResourceAsStream( ModuleConfigPropertyFileNames.MODULE_CONFIG_PER_MODULE );
//		Properties configPropsPerModule = new Properties();
//
//		if ( propsPerModule == null ) {
//
//			log.info( "propsPerModule == null, " + ModuleConfigPropertyFileNames.MODULE_CONFIG_PER_MODULE + " not found " );
//
//		} else {
//
//			configPropsPerModule.load(propsPerModule);
//		}
//
//
//		InputStream propsPerClient = thisClassLoader.getResourceAsStream( ModuleConfigPropertyFileNames.MODULE_CONFIG_PER_CLIENT );
//		Properties configPropsPerClient = new Properties();
//
//		if ( propsPerClient == null ) {
//
//			log.info( "propsPerClient == null, " + ModuleConfigPropertyFileNames.MODULE_CONFIG_PER_CLIENT + " not found " );
//
//		} else {
//
//			configPropsPerClient.load(propsPerClient);
//		}


		URL configPropFile = thisClassLoader.getResource( CONFIG_PROPERTIES_FILENAME );

		if ( configPropFile == null ) {

			log.error( "Properties file '" + CONFIG_PROPERTIES_FILENAME + "' not found " );

		} else {

			log.info( "Properties file '" + CONFIG_PROPERTIES_FILENAME + "' load path = " + configPropFile.getFile() );
		}


		InputStream props = thisClassLoader.getResourceAsStream( CONFIG_PROPERTIES_FILENAME );
		Properties configProps = new Properties();

		if ( props != null ) {
			configProps.load(props);
		}


//
//		String outputRootDirectoryString = configProps.getProperty( CONFIG_PROPERTY_OUTPUT_ROOT_DIRECTORY );
//
//		serverDirectory = configProps.getProperty( CONFIG_PROPERTY_SERVER_DIRECTORY );
//		serverIPAddress = configProps.getProperty( CONFIG_PROPERTY_IMPORT_SERVER_IP );
//		serverUsername = configProps.getProperty( CONFIG_PROPERTY_IMPORT_SERVER_USERNAME );
//
//		if ( StringUtils.isEmpty( serverDirectory )
//				&& ( ! StringUtils.isEmpty( serverIPAddress ) || ! StringUtils.isEmpty( serverUsername ) ) ) {
//
//			String msg = "In Properties file '" + CONFIG_PROPERTIES_FILENAME + "', '" + CONFIG_PROPERTY_SERVER_DIRECTORY + "' property does NOT have a value but '"
//							+ CONFIG_PROPERTY_IMPORT_SERVER_IP + "' property or '" + CONFIG_PROPERTY_IMPORT_SERVER_USERNAME
//							+ "' property has a value and they must not have values if '" + CONFIG_PROPERTY_SERVER_DIRECTORY
//							+ "' does not have a value";
//
//			log.error( msg );
//
//			throw new Exception( msg );
//
//
//		} else if ( ! StringUtils.isEmpty( serverDirectory )
//				&& ( StringUtils.isEmpty( serverIPAddress ) || StringUtils.isEmpty( serverUsername ) ) ) {
//
//
//		}
//
//		outputRootDirectory = new File( outputRootDirectoryString );
//		if (!outputRootDirectory.exists()) {
//
//			String msg = "Output Directory '" + outputRootDirectory.getAbsolutePath() + "' does not exist.";
//
//			log.error( msg );
//
//			throw new Exception( msg );
//		}
//
//		if (!outputRootDirectory.isDirectory()) {
//			String msg = "Output Directory '" + outputRootDirectory.getAbsolutePath() + "' is not a directory.";
//
//			log.error( msg );
//
//			throw new Exception( msg );
//		}

		initialized = true;
	}



	/**
	 * Called when the object will no longer be used
	 */
	@Override
	public void destroy() {

		log.info( "destroy() called " );

		log.info( "destroy() exitting " );


	}



	@Override
	/**
	 * Called on a separate thread when a shutdown request comes from the operating system.
	 * If this is not heeded, the process may be killed by the operating system after some time has passed ( controlled by the operating system )
	 */
	public void shutdown() {


		log.info( "shutdown() called " );


//		if ( ! StringUtils.isEmpty( outputDirectory ) ) {
//
//			try {
//				UpdateStatusFile.updateStatusFile( Constants.EXTRACTOR_STATUS_FILE_NAME, outputDirectory, Constants.STATUS_FILE_SHUTDOWN_INITIATED );
//
//			} catch (Exception e) {
//
//				log.info( "In shutdown(), failed to update status file, exception: " + e.toString(), e );
//			}
//		}

		shutdownRequested = true;


		log.info( "shutdown() exitting " );


	}

	@Override
	public void stopRunningAfterProcessingJob() {
		// TODO Auto-generated method stub

	}

	/**
	 * called before and possibly after each request is processed to clear input parameters and stored errors
	 */
	@Override
	public void reset() throws Throwable {

		if ( ! initialized ) {

			throw new Exception( "Module has not been initialized" );
		}

	}


	/**
	 * Process the request
	 */
	@Override
	public void processRequest( ModuleInterfaceRequest jobManagerModuleRequest, ModuleInterfaceResponse jobManagerModuleResponse, ModuleInterfaceJobProgress jobManagerModuleJobProgress, ModuleInterfaceClientServices jobManagerClientServices ) throws Throwable {


		final String methodName = "processRequest()";

		log.info( methodName + " called " );


		Map<String, String> jobParameters = jobManagerModuleRequest.getJobParameters();


		if ( ! initialized ) {

			throw new Exception( "Module has not been initialized" );
		}

//		ClassLoader thisClassLoader = this.getClass().getClassLoader();


		BufferedReader reader = null;
		InputStream inStreamErr = null;
		OutputStream outStrToProcess = null;



		try {

			String commandToRun = jobParameters.get( JOB_PARAM_COMMAND_TO_RUN );


			log.info( "execute: running shell command:  " + commandToRun );

			Runtime run = Runtime.getRuntime() ;

			Process process = run.exec( commandToRun ) ;

			inStreamErr = process.getErrorStream();
			outStrToProcess = process.getOutputStream();
			reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) ) ;

			process.waitFor() ;

			int exitValue = process.exitValue();


			StringBuilder responseSB = new StringBuilder();


			String line ;
			while ( (  line = reader.readLine() ) != null )
			{
				responseSB.append( line );
				responseSB.append("\n");
			}

			String response = responseSB.toString();

			String message = "shell command: execute command.  return code = " + exitValue + ", command = " + commandToRun + "\nresponse = \n" + response;


			int jobStatus = JobStatusValuesConstants.JOB_STATUS_FINISHED;

			int msgType = RunMessageTypesConstants.RUN_MESSAGE_TYPE_MSG;

			if ( exitValue == 0 ) {

				log.info(message);

//				jobStatus = JobStatusValuesConstants.JOB_STATUS_FINISHED;
//
//				msgType = RunMessageTypesConstants.RUN_MESSAGE_TYPE_MSG;

			} else {

				log.error(message);

				jobStatus = JobStatusValuesConstants.JOB_STATUS_HARD_ERROR;

				msgType = RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR;

			}

			jobManagerModuleResponse.setStatusCode( jobStatus );

			jobManagerModuleResponse.addRunMessage( msgType, message );

		} catch ( Throwable ex ) {

			log.error("General Exception while running the shell command.  This error may have already been logged and emailed.  Exception = " + ex.toString(), ex );


			jobManagerModuleResponse.setStatusCode( JobStatusValuesConstants.JOB_STATUS_HARD_ERROR );


			ByteArrayOutputStream baos = new ByteArrayOutputStream( 1000 );

			PrintStream printStream = new PrintStream( baos );

			ex.printStackTrace( printStream );

			printStream.close();

			String exStackTrace = baos.toString();



			String message = "Failed Completion. Exception type = " + ex.getClass().getName() + ", Exception string = " + ex.toString()
					+ "\n" + exStackTrace;

			jobManagerModuleResponse.addRunMessage( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR, message );


		} finally {


			if ( reader != null ) {

				try {
					reader.close();
				} catch ( Throwable t ) {
				}
				reader = null;
			}

			if ( inStreamErr != null ) {
				try {
					inStreamErr.close();
				} catch ( Throwable t ) {
				}
				inStreamErr = null;
			}


			if ( outStrToProcess != null ) {
				try {
					outStrToProcess.close();
				} catch ( Throwable t ) {
				}
				outStrToProcess = null;
			}
		}


		log.info( methodName + " exitting processRequest()" );
	}


}
