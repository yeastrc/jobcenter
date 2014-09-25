package org.jobcenter.do_nothing_module.main;

import java.util.Map;

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

/**
 *
 *
 */
public class DoNothingTestModuleMain implements ModuleInterfaceClientMainInterface {

	private static Logger log = Logger.getLogger(DoNothingTestModuleMain.class);

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

		log.info( "Called DoNothingTestModuleMain.init()" );
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.job_module_interface.ModuleInterfaceClientMainInterface#processRequest()
	 */
	@Override
	public void processRequest( ModuleInterfaceRequest moduleRequest, ModuleInterfaceResponse moduleResponse, ModuleInterfaceJobProgress moduleJobProgress, ModuleInterfaceClientServices moduleInterfaceClientServices ) throws Throwable {

//		log.error( "Logged to Error:  Called DoNothingTestModuleMain.processRequest()" );

		log.info( "Called DoNothingTestModuleMain.processRequest()" );

		int numberOfThreadsForRunningJob = moduleRequest.getNumberOfThreadsForRunningJob();

		log.info("numberOfThreadsForRunningJob = " + numberOfThreadsForRunningJob );


		log.info("Calling moduleRequest.getJobParameters()" );


		Map<String, String> parameters = moduleRequest.getJobParameters();



		if ( parameters == null ) {

			log.info( "DoNothingTestModuleMain.processRequest(), parameters == null " );

		} else {

			for ( Map.Entry<String, String> entry : parameters.entrySet() ) {

				log.info("parameter: " + entry.getKey() + ", value = |" + entry.getValue() + "|." );
			}
		}
		
		log.info( "JobRequiredExecutionThreads:" + moduleRequest.getJobRequiredExecutionThreads() );
		
		log.info( "JobcenterClientNodeName:" + moduleRequest.getJobcenterClientNodeName() );

		int jobStatus = JobStatusValuesConstants.JOB_STATUS_RUNNING;

		log.info( "DoNothingTestModuleMain.processRequest(), next sleeping 5 seconds to simulate time to do work." );

		try {

			synchronized (this) {
				wait( 5 * 1000 );
			}


		} catch (InterruptedException e) {

			log.info( "DoNothingTestModuleMain.processRequest(), after sleeping5 seconds to simulate time to do work. Sleep Interrupted" );

		}

		log.info( "DoNothingTestModuleMain.processRequest(), after sleeping 5 seconds then 65 seconds, then 48 seconds to simulate time to do work." );


		jobStatus = JobStatusValuesConstants.JOB_STATUS_FINISHED;

		moduleResponse.setStatusCode( jobStatus );

		String resultMessage = "Successful Completion DoNothingTestModuleMain";
		
		
		try {
			moduleResponse.addRunMessageAutoCleanString( RunMessageTypesConstants.RUN_MESSAGE_TYPE_MSG, resultMessage, " " /* replacementStringForInvalidXMLChars */ );
			
		} catch ( JobcenterModuleInterfaceInvalidCharactersInStringException ex ) {

			String msg = "failed to save response message due to invalid XML characters";
			
			log.error( msg + ". original message: " + resultMessage );
			
			moduleResponse.addRunMessage( RunMessageTypesConstants.RUN_MESSAGE_TYPE_MSG, msg );
		}


		log.info( "Exiting: DoNothingTestModuleMain.processRequest()" );

	}

	/* (non-Javadoc)
	 * @see org.jobcenter.job_module_interface.ModuleInterfaceClientMainInterface#reset()
	 */
	@Override
	public void reset()  throws Throwable {


	}

}
