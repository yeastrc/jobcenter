package org.jobcenter.sample.main;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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

public class SampleMain implements ModuleInterfaceClientMainInterface {

	private static Logger log = Logger.getLogger(SampleMain.class);

	//  must be volatile since shutdown() is called on a different thread
	private volatile boolean keepRunning = true;

	private volatile boolean stopRunningAfterProcessingJob = false;

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
	 * @see org.jobcenter.job_module_interface.JobManagerInterface#processRequest()
	 */
	@Override
	public void processRequest( ModuleInterfaceRequest jobManagerModuleRequest, ModuleInterfaceResponse jobManagerModuleResponse, ModuleInterfaceJobProgress jobManagerModuleJobProgress, ModuleInterfaceClientServices jobManagerClientServices ) throws Throwable {

//		log.error( "Logged to Error:  Called SampleMain.processRequest()" );

		log.info( "Called SampleMain.processRequest()" );

		System.out.println("\nCalled SampleMain.processRequest()");



		Map<String, String> firstRunOutputParams =  jobManagerClientServices.getFirstRunOutputParams();


		if ( firstRunOutputParams == null ) {

			log.info( "SampleMain.processRequest(), firstRunOutputParams == null " );

		} else if ( firstRunOutputParams.isEmpty() ) {

				log.info( "SampleMain.processRequest(), firstRunOutputParams is emtpy " );

		} else {

			for ( Map.Entry<String, String> entry : firstRunOutputParams.entrySet() ) {

				log.info("firstRunOutputParams: " + entry.getKey() + ", value = |" + entry.getValue() + "|." );
			}
		}

		log.info("Calling jobManagerClientServices.getRunCount()" );


		int prevRunOutputParamsCount = jobManagerClientServices.getRunCount();

		log.info("prevRunOutputParamsCount: " + prevRunOutputParamsCount );


		log.info("Calling jobManagerClientServices.getRunOutputParamsUsingIndex( 1 );" );


		Map<String, String> secondRunOutputParams =  jobManagerClientServices.getRunOutputParamsUsingIndex( 1 );


		if ( secondRunOutputParams == null ) {

			log.info( "SampleMain.processRequest(), secondRunOutputParams == null " );

		} else if ( secondRunOutputParams.isEmpty() ) {

				log.info( "SampleMain.processRequest(), secondRunOutputParams is emtpy " );

		} else {

			for ( Map.Entry<String, String> entry : secondRunOutputParams.entrySet() ) {

				log.info("secondRunOutputParams: " + entry.getKey() + ", value = |" + entry.getValue() + "|." );
			}
		}

		log.info("Calling jobManagerClientServices.getRunOutputParamsUsingIndex( 2 );" );


		Map<String, String> thirdRunOutputParams =  jobManagerClientServices.getRunOutputParamsUsingIndex( 2 );


		if ( thirdRunOutputParams == null ) {

			log.info( "SampleMain.processRequest(), thirdRunOutputParams == null " );

		} else if ( thirdRunOutputParams.isEmpty() ) {

				log.info( "SampleMain.processRequest(), thirdRunOutputParams is emtpy " );

		} else {

			for ( Map.Entry<String, String> entry : thirdRunOutputParams.entrySet() ) {

				log.info("thirdRunOutputParams: " + entry.getKey() + ", value = |" + entry.getValue() + "|." );
			}
		}



		log.info("Calling jobManagerClientServices.getPreviousRunOutputParams();" );

		Map<String, String> prevRunOutputParams =  jobManagerClientServices.getPreviousRunOutputParams();


		if ( prevRunOutputParams == null ) {

			log.info( "SampleMain.processRequest(), prevRunOutputParams == null " );

		} else if ( prevRunOutputParams.isEmpty() ) {

				log.info( "SampleMain.processRequest(), prevRunOutputParams is empty " );


		} else {

			for ( Map.Entry<String, String> entry : prevRunOutputParams.entrySet() ) {

				log.info("prevRunOutputParam: " + entry.getKey() + ", value = |" + entry.getValue() + "|." );
			}
		}

		log.info("Calling jobManagerModuleRequest.getJobParameters()" );


		Map<String, String> parameters = jobManagerModuleRequest.getJobParameters();



		if ( parameters == null ) {

			log.info( "SampleMain.processRequest(), parameters == null " );

		} else {

			for ( Map.Entry<String, String> entry : parameters.entrySet() ) {

				log.info("parameter: " + entry.getKey() + ", value = |" + entry.getValue() + "|." );
			}
		}

		int jobStatus = JobStatusValuesConstants.JOB_STATUS_RUNNING;

		log.info( "SampleMain.processRequest(), next sleeping 5 seconds to simulate time to do work." );

		System.out.println( "SampleMain.processRequest(), next sleeping 5 seconds to simulate time to do work." );

		try {

//			log.info( "Submitting a job" );
//
//			boolean successfullySubmittedJob = false;
//
//			while ( ! successfullySubmittedJob ) {
//
//				try {
//					jobManagerClientServices.submitJob( "Testing" , null, "sampleModule", "submitter.test", 5, null);
//
////					jobManagerClientServices.submitJob( requestTypeName, requestId, jobTypeName, submitter, priority, jobParameters)
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
				wait( 5 * 1000 );
			}

			if ( keepRunning ) {

				log.info( "SampleMain.processRequest(), next sleeping 65 seconds to simulate time to do work." );

				System.out.println( "SampleMain.processRequest(), next sleeping 65 seconds to simulate time to do work." );

				jobManagerModuleJobProgress.progressPing();

				jobManagerModuleJobProgress.supportsPercentComplete();

				jobManagerModuleJobProgress.updatePercentComplete( 5 );

				synchronized (this) {
					wait( 65 * 1000);
				}
			}

			if ( keepRunning ) {

				log.info( "SampleMain.processRequest(), next sleeping 48 seconds to simulate time to do work." );

				System.out.println( "SampleMain.processRequest(), next sleeping 48 seconds to simulate time to do work." );


				jobManagerModuleJobProgress.progressPing();

				jobManagerModuleJobProgress.updatePercentComplete( 30 );

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

		jobManagerModuleResponse.setStatusCode( jobStatus );

		jobManagerModuleResponse.addRunMessage( RunMessageTypesConstants.RUN_MESSAGE_TYPE_MSG, "Successful Completion Sample Main" );

		jobManagerModuleResponse.addRunMessage( RunMessageTypesConstants.RUN_MESSAGE_TYPE_WARNING, "Second Message, warning" );


		jobManagerModuleResponse.addRunOutputParam( "SampleModKeyV2", "SampleModValueV2" );


	}

	/* (non-Javadoc)
	 * @see org.jobcenter.job_module_interface.JobManagerInterface#reset()
	 */
	@Override
	public void reset()  throws Throwable {


	}

}
