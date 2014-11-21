package org.jobcenter.sample.main;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.constants.RunMessageTypesConstants;
import org.jobcenter.exceptions.JobcenterModuleInterfaceInvalidCharactersInStringException;
import org.jobcenter.exceptions.JobcenterSystemErrorException;
import org.jobcenter.job_client_module_interface.ModuleInterfaceClientMainInterface;
import org.jobcenter.job_client_module_interface.ModuleInterfaceClientServices;
import org.jobcenter.job_client_module_interface.ModuleInterfaceJobProgress;
import org.jobcenter.job_client_module_interface.ModuleInterfaceRequest;
import org.jobcenter.job_client_module_interface.ModuleInterfaceResponse;

public class SampleMain implements ModuleInterfaceClientMainInterface {

	private static Logger log = Logger.getLogger(SampleMain.class);
	
	private static final int  SLEEP_SECONDS = 6;

	//  must be volatile since shutdown() is called on a different thread
	private volatile boolean keepRunning = true;

	private volatile boolean stopRunningAfterProcessingJob = false;

	/* (non-Javadoc)
	 * @see org.jobcenter.job_module_interface.ModuleInterfaceClientMainInterface#destroy()
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

		keepRunning = false;

		log.info( "shutdown called. set keepRunning = false. Calling 'notify()'" );

		synchronized (this) {

			notify();
		}


	}


	@Override
	public void stopRunningAfterProcessingJob() {

		log.info( "stopRunningAfterProcessingJob() called. set stopRunningAfterProcessingJob = true." );

		stopRunningAfterProcessingJob = true;

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

		System.out.println("\nCalled SampleMain.init()");


		System.out.println("\n System.getProperty( \"java.class.path\" ) |" + System.getProperty( "java.class.path" ) + "|" );



		ClassLoader thisClassLoader = this.getClass().getClassLoader();


		//  don't need since log4j will load the log4j.xml file when the module is loaded since it it loaded in a new class loader.

//		InputStream input =  thisClassLoader.getResourceAsStream("log4j.xml");
//
//		//  Configure log4j
//		new DOMConfigurator().doConfigure(input, LogManager.getLoggerRepository());



		log.info( "Called SampleMain.init()" );



		log.error( "Called SampleMain.init()" );




		InputStream props = thisClassLoader.getResourceAsStream("config.properties");

		if ( props == null ) {

			log.info( "props == null, config.properties not found " );

		} else {

			Properties configProps = new Properties();

			configProps.load(props);

			log.info( "config.location = " + configProps.getProperty("config.location") );

		}




		InputStream propsMain = thisClassLoader.getResourceAsStream("config_client_main.properties");

		if ( propsMain == null ) {

			log.info( "propsMain == null, config_client_main.properties not found " );

		} else {

			Properties configPropsMain = new Properties();

			configPropsMain.load(propsMain);

			log.info( "config_client_main.properties:  config.location = " + configPropsMain.getProperty("config.location") );

		}

	}

	/* (non-Javadoc)
	 * @see org.jobcenter.job_module_interface.ModuleInterfaceClientMainInterface#processRequest()
	 */
	@Override
	public void processRequest( ModuleInterfaceRequest moduleRequest, ModuleInterfaceResponse moduleResponse, ModuleInterfaceJobProgress moduleJobProgress, ModuleInterfaceClientServices moduleInterfaceClientServices ) throws Throwable {

//		log.error( "Logged to Error:  Called SampleMain.processRequest()" );

		log.info( "Called SampleMain.processRequest()" );

		System.out.println("\nCalled SampleMain.processRequest()");


		int numberOfThreadsForRunningJob = moduleRequest.getNumberOfThreadsForRunningJob();

		log.info("numberOfThreadsForRunningJob = " + numberOfThreadsForRunningJob );


		log.info("Calling moduleRequest.getJobParameters()" );


		Map<String, String> parameters = moduleRequest.getJobParameters();



		if ( parameters == null ) {

			log.info( "SampleMain.processRequest(), parameters == null " );

		} else {

			for ( Map.Entry<String, String> entry : parameters.entrySet() ) {

				log.info("parameter: " + entry.getKey() + ", value = |" + entry.getValue() + "|." );
			}
		}

//
//
//		Map<String, String> firstRunOutputParams =  moduleInterfaceClientServices.getFirstRunOutputParams();
//
//
//		if ( firstRunOutputParams == null ) {
//
//			log.info( "SampleMain.processRequest(), firstRunOutputParams == null " );
//
//		} else if ( firstRunOutputParams.isEmpty() ) {
//
//				log.info( "SampleMain.processRequest(), firstRunOutputParams is emtpy " );
//
//		} else {
//
//			for ( Map.Entry<String, String> entry : firstRunOutputParams.entrySet() ) {
//
//				log.info("firstRunOutputParams: " + entry.getKey() + ", value = |" + entry.getValue() + "|." );
//			}
//		}
//
//		log.info("Calling moduleInterfaceClientServices.getRunCount()" );
//
//
//		int prevRunOutputParamsCount = moduleInterfaceClientServices.getRunCount();
//
//		log.info("prevRunOutputParamsCount: " + prevRunOutputParamsCount );
//
//
//		log.info("Calling moduleInterfaceClientServices.getRunOutputParamsUsingIndex( 1 );" );
//
//
//		Map<String, String> secondRunOutputParams =  moduleInterfaceClientServices.getRunOutputParamsUsingIndex( 1 );
//
//
//		if ( secondRunOutputParams == null ) {
//
//			log.info( "SampleMain.processRequest(), secondRunOutputParams == null " );
//
//		} else if ( secondRunOutputParams.isEmpty() ) {
//
//				log.info( "SampleMain.processRequest(), secondRunOutputParams is emtpy " );
//
//		} else {
//
//			for ( Map.Entry<String, String> entry : secondRunOutputParams.entrySet() ) {
//
//				log.info("secondRunOutputParams: " + entry.getKey() + ", value = |" + entry.getValue() + "|." );
//			}
//		}
//
//		log.info("Calling moduleInterfaceClientServices.getRunOutputParamsUsingIndex( 2 );" );
//
//
//		Map<String, String> thirdRunOutputParams =  moduleInterfaceClientServices.getRunOutputParamsUsingIndex( 2 );
//
//
//		if ( thirdRunOutputParams == null ) {
//
//			log.info( "SampleMain.processRequest(), thirdRunOutputParams == null " );
//
//		} else if ( thirdRunOutputParams.isEmpty() ) {
//
//				log.info( "SampleMain.processRequest(), thirdRunOutputParams is emtpy " );
//
//		} else {
//
//			for ( Map.Entry<String, String> entry : thirdRunOutputParams.entrySet() ) {
//
//				log.info("thirdRunOutputParams: " + entry.getKey() + ", value = |" + entry.getValue() + "|." );
//			}
//		}
//
//
//
//		log.info("Calling moduleInterfaceClientServices.getPreviousRunOutputParams();" );
//
//		Map<String, String> prevRunOutputParams =  moduleInterfaceClientServices.getPreviousRunOutputParams();
//
//
//		if ( prevRunOutputParams == null ) {
//
//			log.info( "SampleMain.processRequest(), prevRunOutputParams == null " );
//
//		} else if ( prevRunOutputParams.isEmpty() ) {
//
//				log.info( "SampleMain.processRequest(), prevRunOutputParams is empty " );
//
//
//		} else {
//
//			for ( Map.Entry<String, String> entry : prevRunOutputParams.entrySet() ) {
//
//				log.info("prevRunOutputParam: " + entry.getKey() + ", value = |" + entry.getValue() + "|." );
//			}
//		}

		int jobStatus = JobStatusValuesConstants.JOB_STATUS_RUNNING;

		log.info( "SampleMain.processRequest(), next sleeping " + SLEEP_SECONDS + " seconds to simulate time to do work." );

		System.out.println( "SampleMain.processRequest(), next sleeping  " + SLEEP_SECONDS + "  seconds to simulate time to do work." );

		try {

//			log.info( "Submitting a job" );
//
//			boolean successfullySubmittedJob = false;
//
//			while ( ! successfullySubmittedJob ) {
//
//				try {
//					moduleInterfaceClientServices.submitJob( "Testing" , null, "sampleModule", "submitter.test", 5, null);
//
////					moduleInterfaceClientServices( requestTypeName, requestId, jobTypeName, submitter, priority, jobParameters)
//
//					successfullySubmittedJob = true;
//
//				} catch ( Throwable t ) {
//
//					if ( ! keepRunning || stopRunningAfterProcessingJob ) {
//
//						throw t;
//					}
//				}
//			}

			synchronized (this) {
				wait(  SLEEP_SECONDS * 1000 );
			}

//			if ( keepRunning ) {
//
//				log.info( "SampleMain.processRequest(), next sleeping 65 seconds to simulate time to do work." );
//
//				System.out.println( "SampleMain.processRequest(), next sleeping 65 seconds to simulate time to do work." );
//
//				moduleJobProgress.progressPing();
//
//				moduleJobProgress.supportsPercentComplete();
//
//				moduleJobProgress.updatePercentComplete( 5 );
//
//				synchronized (this) {
//					wait( 65 * 1000);
//				}
//			}

			if ( keepRunning ) {

				log.info( "SampleMain.processRequest(), next sleeping 48 seconds to simulate time to do work." );

				System.out.println( "SampleMain.processRequest(), next sleeping 48 seconds to simulate time to do work." );


				moduleJobProgress.progressPing();

				moduleJobProgress.updatePercentComplete( 30 );

				synchronized (this) {
					wait( 48 * 1000 );
				}
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();

			log.info( "SampleMain.processRequest(), after sleeping5 seconds to simulate time to do work. Sleep Interrupted" );

		}

		log.info( "SampleMain.processRequest(), after sleeping 5 seconds then 65 seconds, then 48 seconds to simulate time to do work." );

		try {
			EmailSendUtil.sendTestMail( "Called from SampleMain.processRequest()"  );
		} catch (Exception e) {

			log.error( "Exception in call to EmailSendUtil.sendTestMail: " + e.toString(), e );
		}


		jobStatus = JobStatusValuesConstants.JOB_STATUS_FINISHED;

//		jobStatus = JobStatusValuesConstants.JOB_STATUS_HARD_ERROR;


		//  Non-valid status value
//		jobStatus = 99;


		moduleResponse.setStatusCode( jobStatus );


		
		String resultMessage = "Successful Completion Sample Main";
		


		try {

			//  Allow cleaning of invalid for XML Chars and to ASCII if needed
			moduleResponse.addRunMessageAutoCleanStringToASCII( RunMessageTypesConstants.RUN_MESSAGE_TYPE_MSG, resultMessage, "?" );
			
		} catch ( JobcenterModuleInterfaceInvalidCharactersInStringException ex ) {
			
			//  The passed in string was still invalid after cleaning
			
			String msg = "ERROR:  Failed to add message to jobcenter job completion response due to invalid characters.";

			log.error( msg + "  status message is: " + resultMessage, ex );

			moduleResponse.addRunMessageAutoCleanStringToASCII( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR,
					msg + "  Status message is written to log on machine where the job was run.", "?" );
					
		} catch ( JobcenterSystemErrorException ex ) {
			
			//  Jobcenter internals encountered an error while validating the data
			
			String msg = "ERROR:  Failed to add message to jobcenter job completion response due to JobcenterSystemErrorException.";

			log.error( msg + "  status message is: " + resultMessage, ex );

			moduleResponse.addRunMessageAutoCleanStringToASCII( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR,
					msg + "  Status message is written to log on machine where the job was run.", "?" );
		}
				
		

		String resultWarning = "Second Message, warning";
		
		


		try {

			//  Allow cleaning of invalid for XML Chars and to ASCII if needed
			moduleResponse.addRunMessageAutoCleanStringToASCII( RunMessageTypesConstants.RUN_MESSAGE_TYPE_WARNING, resultWarning, "?" );
			
		} catch ( JobcenterModuleInterfaceInvalidCharactersInStringException ex ) {
			
			//  The passed in string was still invalid after cleaning
			
			String msg = "ERROR:  Failed to add message to jobcenter job completion response due to invalid characters.";

			log.error( msg + "  status message is: " + resultWarning, ex );

			moduleResponse.addRunMessageAutoCleanStringToASCII( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR,
					msg + "  Status message is written to log on machine where the job was run.", "?" );
					
		} catch ( JobcenterSystemErrorException ex ) {
			
			//  Jobcenter internals encountered an error while validating the data
			
			String msg = "ERROR:  Failed to add message to jobcenter job completion response due to JobcenterSystemErrorException.";

			log.error( msg + "  status message is: " + resultWarning, ex );

			moduleResponse.addRunMessageAutoCleanStringToASCII( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR,
					msg + "  Status message is written to log on machine where the job was run.", "?" );
		}
		
		
		

		String outputParamKey = "SampleModKeyV2";
		
		String outputParamValue = "SampleModValueV2";
		


		try {

			//  Should not use allow cleaning of invalid for XML Chars and to ASCII if saving data for a retry 
			//    that needs the data un-modified.
			//  In that case, consider using base-64 encoding for the data to ensure it is not a problem,
			//  Otherwise, fail the job since unable to pass data to a retry of the job,
			//     and put a plain text string in RunOutputParam that such problem was encountered so that 
			//     retry knows there is data missing from RunOutputParam
			
			moduleResponse.addRunOutputParam( outputParamKey, outputParamValue );
			
//			moduleResponse.addRunOutputParamAutoCleanStringToASCII( outputParamKey, outputParamValue, "?" );
			
		} catch ( JobcenterModuleInterfaceInvalidCharactersInStringException ex ) {
			
			//  The passed in string was still invalid after cleaning
			
			String msg = "ERROR:  Failed to add outputParamValue to jobcenter job completion response due to invalid characters.";

			log.error( msg + "  outputParamValue is: " + outputParamValue, ex );

			moduleResponse.addRunMessageAutoCleanStringToASCII( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR,
					msg + "  Status message is written to log on machine where the job was run.", "?" );
			
			throw ex;
					
		} catch ( JobcenterSystemErrorException ex ) {
			
			//  Jobcenter internals encountered an error while validating the data
			
			String msg = "ERROR:  Failed to add message to jobcenter job completion response due to JobcenterSystemErrorException.";

			log.error( msg + "  outputParamValue is: " + outputParamValue, ex );

			moduleResponse.addRunMessageAutoCleanStringToASCII( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR,
					msg + "  outputParamValue is written to log on machine where the job was run.", "?" );
			
			throw ex;
		}
		
		
//		try {
//			
//
//			moduleResponse.addRunOutputParam( outputParamKey, outputParamValue );
//			
//		} catch ( JobcenterModuleInterfaceInvalidCharactersInStringException ex ) {
//			
//			outputParamValue = moduleInterfaceClientServices.replaceInvalidCharactersInXMLUTF8InString( outputParamValue, " " );
//			
//			try {
//				
//				moduleResponse.addRunOutputParam( outputParamKey, outputParamValue );
//					
//			} catch ( JobcenterModuleInterfaceInvalidCharactersInStringException ex2 ) {
//				
//				moduleResponse.addRunOutputParam( outputParamKey, "OutputValueFailedToEncodetoXML" );
//				
//			} catch ( JobcenterSystemErrorException ex2 ) {
//
//				throw ex;
//			}
//		
//			
//		} catch ( JobcenterSystemErrorException ex ) {
//			
//			throw ex;
//		}



		log.info( "Exiting: SampleMain.processRequest()" );

	}

	/* (non-Javadoc)
	 * @see org.jobcenter.job_module_interface.ModuleInterfaceClientMainInterface#reset()
	 */
	@Override
	public void reset()  throws Throwable {


	}

}
