package org.jobcenter.webservices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jobcenter.config_property_files.JobcenterGUIConfigPropertyFileContents;
import org.jobcenter.constants.WebServiceErrorMessageConstants;
import org.jobcenter.dto.Job;
import org.jobcenter.gui_connection_to_server_client_factory.GUIConnectionToServerClientFactory;
import org.jobcenter.guiclient.GUIConnectionToServerClient;
import org.jobcenter.guiclient.response.GUIListJobsResponse;
import org.jobcenter.utils.GetJobForGUIFromJobFromServer;
import org.jobcenter.webservice_response_objects.JobForGUI;

/**
 * 
 *
 */
@Path("/getFromJCServer")
public class ListJobsGetFromServerService {

	private static final Logger log = Logger.getLogger( ListJobsGetFromServerService.class );
	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getJobForGUIForJobList") 
	public GetJobForGUIForJobList getJobForGUIForJobList( @QueryParam( "job_id" ) Integer jobId,
			@Context HttpServletRequest request ) {
		try {
			if ( jobId == null ) {
				log.warn( "job_id empty" );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);  //  Early Exit with Data Exception
			}
			GetJobForGUIForJobList webserviceResponse = new GetJobForGUIForJobList();

			GUIConnectionToServerClient connToServer = 
					GUIConnectionToServerClientFactory.getInstance().getGUIConnectionToServerClient();

			Job jobFromServer 
				= connToServer.viewJob( jobId );

			if ( jobFromServer != null ) {
				JobForGUI jobForGUI = 
						GetJobForGUIFromJobFromServer.getInstance()
						.getJobForGUIFromJobFromServer( jobFromServer, GetJobForGUIFromJobFromServer.PopulateRuns.NO );
				webserviceResponse.statusSuccess = true;
				webserviceResponse.job = jobForGUI;
			}
			
			return webserviceResponse;

		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}

			
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/listJobs") 
	public ListJobsResponse listJobs(
			@QueryParam( "statusId" ) List<Integer> statusIdList,
			@QueryParam( "jobTypeName" ) List<String> jobTypeNameList,
			@QueryParam( "requestTypeName" ) List<String> requestTypeNameList,
			@QueryParam( "requestId" ) Integer requestId,
			@QueryParam( "submitter" ) String submitter,
			@QueryParam( "indexStart" ) Integer indexStart,
			@QueryParam( "jobsReturnCountMax" ) Integer jobsReturnCountMax,
			@Context HttpServletRequest request ) {
		try {
			ListJobsResponse webserviceResponse = new ListJobsResponse();
			
			GUIConnectionToServerClient connToServer = 
					GUIConnectionToServerClientFactory.getInstance().getGUIConnectionToServerClient();

			Set<Integer> statusIds = new HashSet<>( statusIdList );
			Set<String> requestTypeNames = new HashSet<>( requestTypeNameList );
			Set<String> jobTypeNames = new HashSet<>( jobTypeNameList );
			
			//  Restrict to Job Type and Request Type names allowed in config

			//  From config file
			Set<String> requestTypeNamesAllowed = JobcenterGUIConfigPropertyFileContents.getInstance().getRequestTypeNamesAllowed();

			if ( requestTypeNamesAllowed != null && ( ! requestTypeNamesAllowed.isEmpty() ) ) {
				// restrict to request types allowed
				if ( requestTypeNames != null && ( ! requestTypeNames.isEmpty() ) ) {
					//  Filter requested request types to allowed
					Set<String> requestTypeNamesFiltered = new HashSet<>();
					for ( String requestTypeName : requestTypeNames ) {
						if ( requestTypeNamesAllowed.contains( requestTypeName ) ) {
							requestTypeNamesFiltered.add( requestTypeName );
						}
					}
					requestTypeNames = requestTypeNamesFiltered;
				} else {
					//  No requested request type names so send to server request type names allowed
					requestTypeNames = requestTypeNamesAllowed;
				}
			}
			
			Set<String> jobTypeNamesAllowed = JobcenterGUIConfigPropertyFileContents.getInstance().getJobTypeNamesAllowed();

			if ( jobTypeNamesAllowed != null && ( ! jobTypeNamesAllowed.isEmpty() ) ) {
				// restrict to job types allowed
				if ( jobTypeNames != null && ( ! jobTypeNames.isEmpty() ) ) {
					//  Filter jobed job types to allowed
					Set<String> jobTypeNamesFiltered = new HashSet<>();
					for ( String jobTypeName : jobTypeNames ) {
						if ( jobTypeNamesAllowed.contains( jobTypeName ) ) {
							jobTypeNamesFiltered.add( jobTypeName );
						}
					}
					jobTypeNames = jobTypeNamesFiltered;
				} else {
					//  No jobed job type names so send to server job type names allowed
					jobTypeNames = jobTypeNamesAllowed;
				}
			}
			
			///
	
			GUIListJobsResponse guiListJobsResponse 
				= connToServer.listJobs( statusIds, requestTypeNames, requestId, jobTypeNames, submitter, indexStart, jobsReturnCountMax );

			List<Job> jobListFromServer = guiListJobsResponse.getJobs();
			
			int jobListFromServerSize = 0;
			
			if ( jobListFromServer != null ) {
				jobListFromServerSize = jobListFromServer.size();
			}
			
			List<JobForGUI> jobList = new ArrayList<>( jobListFromServerSize );

			if ( jobListFromServer != null && ( ! jobListFromServer.isEmpty() ) ) {
				for ( Job jobFromServer : jobListFromServer ) {
					JobForGUI jobForGUI = 
							GetJobForGUIFromJobFromServer.getInstance()
							.getJobForGUIFromJobFromServer( jobFromServer, GetJobForGUIFromJobFromServer.PopulateRuns.NO );
					jobList.add( jobForGUI );
				}
			}
			
			webserviceResponse.statusSuccess = true;
			webserviceResponse.jobList = jobList;
			webserviceResponse.jobCountTotal = guiListJobsResponse.getJobCount();
			
			return webserviceResponse;

		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}


	
	/**
	 * 
	 *
	 */
	public static class GetJobForGUIForJobList {

		private boolean statusSuccess = false;
		
		private JobForGUI job;

		public boolean isStatusSuccess() {
			return statusSuccess;
		}

		public void setStatusSuccess(boolean statusSuccess) {
			this.statusSuccess = statusSuccess;
		}

		public JobForGUI getJob() {
			return job;
		}

		public void setJob(JobForGUI job) {
			this.job = job;
		}
		
	}
	
	/**
	 * 
	 *
	 */
	public static class ListJobsResponse {
		
		private boolean statusSuccess = false;
		
		private List<JobForGUI> jobList;
		
		/**
		 * Total job count for query params
		 */
		private int jobCountTotal;
		
		public List<JobForGUI> getJobList() {
			return jobList;
		}
		public void setJobList(List<JobForGUI> jobList) {
			this.jobList = jobList;
		}
		public boolean isStatusSuccess() {
			return statusSuccess;
		}
		public void setStatusSuccess(boolean statusSuccess) {
			this.statusSuccess = statusSuccess;
		}
		public int getJobCountTotal() {
			return jobCountTotal;
		}
		public void setJobCountTotal(int jobCountTotal) {
			this.jobCountTotal = jobCountTotal;
		}
	}
	
}
