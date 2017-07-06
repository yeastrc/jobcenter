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
import org.jobcenter.dto.RequestTypeDTO;
import org.jobcenter.gui_connection_to_server_client_factory.GUIConnectionToServerClientFactory;
import org.jobcenter.guiclient.GUIConnectionToServerClient;

/**
 * 
 *
 */
@Path("/getFromJCServer")
public class RequestTypesGetFromServerService {

	private static final Logger log = Logger.getLogger( RequestTypesGetFromServerService.class );
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/requestTypesListAll") 
	public RequestTypesListAllResponse requestTypesListAll( @Context HttpServletRequest request ) {
		try {

			GUIConnectionToServerClient connToServer = 
					GUIConnectionToServerClientFactory.getInstance().getGUIConnectionToServerClient();

			List<RequestTypeDTO> requestTypes = connToServer.listRequestTypes();

			//  From config file
			Set<String> requestTypeNamesAllowed = JobcenterGUIConfigPropertyFileContents.getInstance().getRequestTypeNamesAllowed();
					
			if ( requestTypeNamesAllowed != null && ( ! requestTypeNamesAllowed.isEmpty() ) ) {
				// restrict to request types allowed
				List<RequestTypeDTO> requestTypesFiltered = new ArrayList<>( requestTypes.size() );
				for ( RequestTypeDTO requestType : requestTypes ) {
					if ( requestTypeNamesAllowed.contains( requestType.getName() ) ) {
						requestTypesFiltered.add( requestType );
					}
				}
				if ( requestTypesFiltered.isEmpty() ) {
					log.error( "No Request types from server are in the allowed request types in config file" );
				}
				requestTypes = requestTypesFiltered;
			}
			
			RequestTypesListAllResponse webserviceResponse = new RequestTypesListAllResponse();
			webserviceResponse.statusSuccess = true;
			webserviceResponse.requestTypes = requestTypes;
			
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
	public static class RequestTypesListAllResponse {
		
		private boolean statusSuccess = false;
		private List<RequestTypeDTO> requestTypes;
		
		public List<RequestTypeDTO> getRequestTypes() {
			return requestTypes;
		}
		public void setRequestTypes(List<RequestTypeDTO> requestTypes) {
			this.requestTypes = requestTypes;
		}
		public boolean isStatusSuccess() {
			return statusSuccess;
		}
		public void setStatusSuccess(boolean statusSuccess) {
			this.statusSuccess = statusSuccess;
		}
	}
}
