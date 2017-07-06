package org.jobcenter.webservices;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jobcenter.config_property_files.JobcenterGUIConfigPropertyFileContents;
import org.jobcenter.constants.WebServiceErrorMessageConstants;
import org.jobcenter.dto.JobType;
import org.jobcenter.gui_connection_to_server_client_factory.GUIConnectionToServerClientFactory;
import org.jobcenter.guiclient.GUIConnectionToServerClient;

/**
 * 
 *
 */
@Path("/getFromJCServer")
public class JobTypesGetFromServerService {

	private static final Logger log = Logger.getLogger( JobTypesGetFromServerService.class );
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/jobTypesListAll") 
	public JobTypesListAllResponse jobTypesListAll( @Context HttpServletRequest request ) {
		try {

			GUIConnectionToServerClient connToServer = 
					GUIConnectionToServerClientFactory.getInstance().getGUIConnectionToServerClient();

			List<JobType> jobTypes = connToServer.listJobTypes();
			
			//  From config file
			Set<String> jobTypeNamesAllowed = JobcenterGUIConfigPropertyFileContents.getInstance().getJobTypeNamesAllowed();
					
			if ( jobTypeNamesAllowed != null && ( ! jobTypeNamesAllowed.isEmpty() ) ) {
				// restrict to job types allowed
				List<JobType> jobTypesFiltered = new ArrayList<>( jobTypes.size() );
				for ( JobType jobType : jobTypes ) {
					if ( jobTypeNamesAllowed.contains( jobType.getName() ) ) {
						jobTypesFiltered.add( jobType );
					}
				}
				if ( jobTypesFiltered.isEmpty() ) {
					log.error( "No Job types from server are in the allowed job types in config file" );
				}
				jobTypes = jobTypesFiltered;
			}

			JobTypesListAllResponse webserviceResponse = new JobTypesListAllResponse();
			webserviceResponse.statusSuccess = true;
			webserviceResponse.jobTypes = jobTypes;
			
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
	public static class JobTypesListAllResponse {
		
		private boolean statusSuccess = false;
		private List<JobType> jobTypes;
		
		public boolean isStatusSuccess() {
			return statusSuccess;
		}
		public void setStatusSuccess(boolean statusSuccess) {
			this.statusSuccess = statusSuccess;
		}
		public List<JobType> getJobTypes() {
			return jobTypes;
		}
		public void setJobTypes(List<JobType> jobTypes) {
			this.jobTypes = jobTypes;
		}
	}
}
