package org.jobcenter.util;

import java.util.ArrayList;
import java.util.List;

import org.jobcenter.config.ConfigFromServer;
import org.jobcenter.constants.ClientStatusUpdateTypeEnum;
import org.jobcenter.dto.Job;
import org.jobcenter.moduleinterfaceimpl.ModuleInterfaceModuleJobProgressImpl;
import org.jobcenter.nondbdto.RunInProgressDTO;
import org.jobcenter.processjob.JobRunnerThread;
import org.jobcenter.processjob.ThreadsHolderSingleton;
import org.jobcenter.request.ClientStatusUpdateRequest;
import org.jobcenter.serverinterface.ServerConnection;

public class SendClientStatusUpdateToServer {
	
	
	public static enum PassJobsToServer {
		
		PASS_JOBS_TO_SERVER_YES,
		PASS_JOBS_TO_SERVER_NO
	}

	
	/**
	 *
	 */
	public static void sendClientStatusUpdateToServer( ClientStatusUpdateTypeEnum clientStatus, PassJobsToServer passJobsToServer ) throws Throwable {

		ServerConnection serverConnection = ServerConnection.getInstance();

		ClientStatusUpdateRequest clientStatusUpdateRequest = new ClientStatusUpdateRequest();

		clientStatusUpdateRequest.setUpdateType( clientStatus );
		
		
		
		int timeUntilNextClientStatusUpdate = 0;
		
		Integer timeUntilNextClientStatusUpdateInteger = ConfigFromServer.getInstance().getWaitTimeForNextClientCheckin();
		
		if ( timeUntilNextClientStatusUpdateInteger != null ) {
			
			timeUntilNextClientStatusUpdate = timeUntilNextClientStatusUpdateInteger;
		}
		
		clientStatusUpdateRequest.setTimeUntilNextClientStatusUpdate( timeUntilNextClientStatusUpdate );
		
		

		if ( passJobsToServer == PassJobsToServer.PASS_JOBS_TO_SERVER_YES ) {

			List<JobRunnerThread> jobRunnerThreads = ThreadsHolderSingleton.getInstance().getJobRunnerThreads();

			List<RunInProgressDTO> runsInProgress = new ArrayList<RunInProgressDTO>( jobRunnerThreads.size() );

			clientStatusUpdateRequest.setRunsInProgress( runsInProgress );

			for ( JobRunnerThread jobRunnerThreadInList: jobRunnerThreads) {

				Job jobInProgress = jobRunnerThreadInList.getJob();
				ModuleInterfaceModuleJobProgressImpl jobProgress = jobRunnerThreadInList.getJobManagerModuleJobProgressImpl();

				if ( jobInProgress != null && jobInProgress.getCurrentRun() != null && jobProgress != null ) {

					RunInProgressDTO runInProgressDTO = new RunInProgressDTO();

					runInProgressDTO.setJobId( jobInProgress.getId() );
					runInProgressDTO.setRunId( jobInProgress.getCurrentRun().getId() );

					runInProgressDTO.setLastStatusTimeStampFromModule( jobProgress.getLastProgressPingTimestamp() );

					if ( jobProgress.isSupportsPercentComplete() ) {
						runInProgressDTO.setPercentageComplete( jobProgress.getPercentComplete() );
					}

					runsInProgress.add( runInProgressDTO );
				}
			}

		}


		clientStatusUpdateRequest.setClientCurrentTime( System.currentTimeMillis() );


		serverConnection.clientStatusUpdate( clientStatusUpdateRequest );


	}

}
