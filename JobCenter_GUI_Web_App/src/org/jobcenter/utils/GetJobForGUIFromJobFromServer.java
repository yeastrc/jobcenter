package org.jobcenter.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.RunDTO;
import org.jobcenter.webservice_response_objects.JobForGUI;
import org.jobcenter.webservice_response_objects.JobParameterForGUI;
import org.jobcenter.webservice_response_objects.RunForGUI;
import org.jobcenter.webservices.ViewJobGetFromServerService;

/**
 * 
 *
 */
public class GetJobForGUIFromJobFromServer {

	private static final Logger log = Logger.getLogger( ViewJobGetFromServerService.class );
	
	public enum PopulateRuns { YES, NO }

	private GetJobForGUIFromJobFromServer() {}
	public static GetJobForGUIFromJobFromServer getInstance() {
		return new GetJobForGUIFromJobFromServer();
	}

	/**
	 * @param jobFromServer
	 * @return
	 */
	public JobForGUI getJobForGUIFromJobFromServer( Job jobFromServer, PopulateRuns populateRuns ) {
		
		JobForGUI jobForGUI = new JobForGUI(); 
		
		jobForGUI.setJobId( jobFromServer.getId() );

		jobForGUI.setJobTypeName( jobFromServer.getJobType().getName() );

		jobForGUI.setRequestId( jobFromServer.getRequestId() );

		jobForGUI.setSubmitDate( jobFromServer.getSubmitDate().toString() );

		jobForGUI.setSubmitter( jobFromServer.getSubmitter() );

		jobForGUI.setPriority( jobFromServer.getPriority() );
		
		jobForGUI.setRequiredExecutionThreads( jobFromServer.getRequiredExecutionThreads() );

		jobForGUI.setStatus( jobFromServer.getStatus().getName() );
		
		jobForGUI.setRequeueable( jobFromServer.isRequeueable() ); 
		jobForGUI.setCancellable(jobFromServer.isCancellable() );

		jobForGUI.setDbRecordVersionNumber( jobFromServer.getDbRecordVersionNumber() );
		
		List<JobParameterForGUI> jobParameterForGUIList = new ArrayList<>();
		jobForGUI.setJobParameterList( jobParameterForGUIList );
		
		for ( Map.Entry<String, String> JobParameterForGUIFromServerEntry : jobFromServer.getJobParameters().entrySet() ) {
			JobParameterForGUI jobParameterForGUI = new JobParameterForGUI();
			jobParameterForGUI.setParamKey( JobParameterForGUIFromServerEntry.getKey() );
			jobParameterForGUI.setParamValue( JobParameterForGUIFromServerEntry.getValue() );
			jobParameterForGUIList.add( jobParameterForGUI );
		}
		
		if ( populateRuns == PopulateRuns.YES ) {

			List<RunDTO> allRunsFromServer = jobFromServer.getAllRuns();

			if ( allRunsFromServer != null && ( ! allRunsFromServer.isEmpty() ) ) {
				List<RunForGUI> runForGUIList = new ArrayList<>( allRunsFromServer.size() );
				jobForGUI.setRunForGUIList( runForGUIList );
				for ( RunDTO runFromServer : allRunsFromServer ) {
					RunForGUI runForGUI = new RunForGUI();
					runForGUIList.add( runForGUI );
					runForGUI.setRunId( runFromServer.getId() );
					runForGUI.setStatus( runFromServer.getStatus().getName() );
					if ( runFromServer.getStartDate() != null ) {
						runForGUI.setStartDate( runFromServer.getStartDate().toString() );
					}
					if ( runFromServer.getEndDate() != null ) {
						runForGUI.setEndDate( runFromServer.getEndDate().toString() );
					}
					if ( runFromServer.getNode() != null ) {
						runForGUI.setNode( runFromServer.getNode().getName() );
					}
					runForGUI.setRunMessageList( runFromServer.getRunMessages() );
					//				runFromServer.getRunOutputParams();
				}
			}
		}
		
		return jobForGUI;
	}
	
}
