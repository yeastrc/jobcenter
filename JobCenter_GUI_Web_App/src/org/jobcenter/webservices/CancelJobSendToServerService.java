package org.jobcenter.webservices;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jobcenter.constants.WebServiceErrorMessageConstants;
import org.jobcenter.gui_connection_to_server_client_factory.GUIConnectionToServerClientFactory;
import org.jobcenter.guiclient.GUICallStatus;
import org.jobcenter.guiclient.GUIConnectionToServerClient;

/**
 * 
 *
 */
@Path("/postToJCServer")
public class CancelJobSendToServerService {

	private static final Logger log = Logger.getLogger( CancelJobSendToServerService.class );

	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/cancelJob") 
	public CancelJobResponse requeueJob(   
			@FormParam( "job_id" ) Integer jobId,
			@FormParam( "job_record_version_id" ) Integer jobRecordVersionId,
			@Context HttpServletRequest request )
	throws Exception {
		try {
			if ( jobId == null ) {
				log.warn( "job_id empty" );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);  //  Early Exit with Data Exception
			}
			if ( jobRecordVersionId == null ) {
				log.warn( "job_record_version_id empty" );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);  //  Early Exit with Data Exception
			}

			GUIConnectionToServerClient connToServer = 
					GUIConnectionToServerClientFactory.getInstance().getGUIConnectionToServerClient();

			GUICallStatus guiCallStatus =
					connToServer.cancelJob( jobId, jobRecordVersionId );

			CancelJobResponse webserviceResponse = new CancelJobResponse();
			
			if ( guiCallStatus ==  GUICallStatus.SUCCESS ) {
				webserviceResponse.statusSuccess = true;

			} else if ( guiCallStatus == GUICallStatus.FAILED_JOB_NO_LONGER_REQUEUEABLE ) {
				webserviceResponse.jobNoLongerRequeable = true;

			} else if ( guiCallStatus == GUICallStatus.FAILED_JOB_NOT_FOUND ) {
				webserviceResponse.jobNotFound = true;

			} else if ( guiCallStatus == GUICallStatus.FAILED_VERSION_NOT_MATCH_DB ) {
				webserviceResponse.failedVersionNotMatchDB = true;

			} else {
				log.error( "Unknown Status returned from 'connToServer.cancelJob', guiCallStatus = " + guiCallStatus );
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
	public static class CancelJobResponse {
		
		private boolean statusSuccess = false;
		
		private boolean failedVersionNotMatchDB = false;
		
		private boolean jobNotFound = false;
		private boolean jobNoLongerRequeable = false;
		private boolean jobNoLongerCancelable = false;
		
		public boolean isStatusSuccess() {
			return statusSuccess;
		}
		public void setStatusSuccess(boolean statusSuccess) {
			this.statusSuccess = statusSuccess;
		}
		public boolean isFailedVersionNotMatchDB() {
			return failedVersionNotMatchDB;
		}
		public void setFailedVersionNotMatchDB(boolean failedVersionNotMatchDB) {
			this.failedVersionNotMatchDB = failedVersionNotMatchDB;
		}
		public boolean isJobNotFound() {
			return jobNotFound;
		}
		public void setJobNotFound(boolean jobNotFound) {
			this.jobNotFound = jobNotFound;
		}
		public boolean isJobNoLongerRequeable() {
			return jobNoLongerRequeable;
		}
		public void setJobNoLongerRequeable(boolean jobNoLongerRequeable) {
			this.jobNoLongerRequeable = jobNoLongerRequeable;
		}
		public boolean isJobNoLongerCancelable() {
			return jobNoLongerCancelable;
		}
		public void setJobNoLongerCancelable(boolean jobNoLongerCancelable) {
			this.jobNoLongerCancelable = jobNoLongerCancelable;
		}
	}
}
