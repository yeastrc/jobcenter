package org.jobcenter.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jobcenter.constants.JobStatusValuesConstants;
import org.jobcenter.constants.RunMessageTypesConstants;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.RunDTO;
import org.jobcenter.dto.RunMessageDTO;

public class MarkJobsInProgressAsFailed {



//	private static final String className = MarkJobsInProgressAsFailed.class.getSimpleName();

	private static Logger log = Logger.getLogger(MarkJobsInProgressAsFailed.class);

	
	private static final String ERROR_MSG = "This job was being processed when the client was previously running.  " 
											+ "The client has been restarted so the job is in an unknown state and needs to be researched.";

	private volatile boolean keepRunning = true;


	private SendJobStatusToServer sendJobStatusToServer = new SendJobStatusToServer();

	



	/**
	 * Called on a different thread.
	 * The ManagerThread instance has detected that the user has requested that the Jobmanager client stop retrieving jobs.
	 */
	public void stopRunningAfterProcessingJob() {

		synchronized (this) {

			this.keepRunning = false;

		}

		//  awaken this thread if it is in 'wait' state ( not currently processing a job )


//		awaken();

		//  This object/thread will then run until the current job is complete and then will die.
	}


	/**
	 * shutdown was received from the operating system.  This is called on a different thread.
	 */
	public void shutdown() {


		log.info("shutdown() called: " );


		synchronized (this) {

			this.keepRunning = false;

		}

		//  awaken this thread if it is in 'wait' state ( not currently processing a job )

//		this.awaken();
	}




	/**
	 * mark all jobs in the "Jobs In progress as failed and send that to the server.
	 * 
	 * This method must log and not rethrow all exceptions
	 */
	public boolean markJobsInProgressAsFailed()  {
		
		boolean completedTask = false;
		
		try {
			
			List<Job> jobsInDirectory = null;
			
			try {
			
				jobsInDirectory = JobToFile.listJobsInJobsInProgressDirectory();
				
				int z = 0;

			} catch ( Throwable t ) {

				log.error( "Exception from call to listJobsInJobsInProgressDirectory(): ", t );
				
				throw t;
			}
			
			List<RunMessageDTO> runMessages = new ArrayList<RunMessageDTO>();
			
			RunMessageDTO runMessageDTO = new RunMessageDTO();
			
			runMessageDTO.setType( RunMessageTypesConstants.RUN_MESSAGE_TYPE_ERROR );
			
			runMessageDTO.setMessage( ERROR_MSG );
			
			runMessages.add( runMessageDTO );

			
			for ( Job job : jobsInDirectory ) {
				

				job.setStatusId( JobStatusValuesConstants.JOB_STATUS_HARD_ERROR );

				RunDTO run = job.getCurrentRun();

				run.setEndDate( new Date() );

				run.setStatusId(  JobStatusValuesConstants.JOB_STATUS_HARD_ERROR );

				run.setRunMessages( runMessages );

//				run.setRunOutputParams( runOutputParams );


				sendJobStatusToServer.sendJobStatusToServer( job );
				
				if ( ! keepRunning ) {
					
					break;
				}
			}

			if ( keepRunning ) {

				//  clean out directory
				
				JobToFile.cleanJobsInProgressDirectoryOfAllJobsFiles();
				
				completedTask = true;
			}

			
		} catch ( Throwable t ) {
			
			
			log.error( "ERROR sending to the server all the jobs that were in progress when the client was previously running.", t );
		}
		
		return completedTask;
		
	}
	

}
