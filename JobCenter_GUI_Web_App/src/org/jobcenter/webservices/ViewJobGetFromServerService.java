package org.jobcenter.webservices;

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
import org.jobcenter.constants.WebServiceErrorMessageConstants;
import org.jobcenter.dto.Job;
import org.jobcenter.gui_connection_to_server_client_factory.GUIConnectionToServerClientFactory;
import org.jobcenter.guiclient.GUIConnectionToServerClient;
import org.jobcenter.utils.GetJobForGUIFromJobFromServer;
import org.jobcenter.webservice_response_objects.JobForGUI;

/**
 * 
 *
 */
@Path("/getFromJCServer")
public class ViewJobGetFromServerService {

	private static final Logger log = Logger.getLogger( ViewJobGetFromServerService.class );
	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getJob") 
	public GetJobResponse getJob( @QueryParam( "job_id" ) Integer jobId,
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
			GetJobResponse webserviceResponse = new GetJobResponse();

			GUIConnectionToServerClient connToServer = 
					GUIConnectionToServerClientFactory.getInstance().getGUIConnectionToServerClient();

			Job jobFromServer 
				= connToServer.viewJob( jobId );

			if ( jobFromServer != null ) {
				JobForGUI jobForGUI = 
						GetJobForGUIFromJobFromServer.getInstance()
						.getJobForGUIFromJobFromServer( jobFromServer, GetJobForGUIFromJobFromServer.PopulateRuns.YES );
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

	/**
	 * 
	 *
	 */
	public static class GetJobResponse {

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
	
}
