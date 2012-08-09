package org.jobcenter.webservice;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.jobcenter.constants.WebServiceURLConstants;
import org.jobcenter.request.*;
import org.jobcenter.response.*;
import org.jobcenter.service.*;

import com.sun.jersey.spi.inject.Inject;
import com.sun.jersey.spi.resource.Singleton;



/**
 *
 *
 */
@Produces("application/xml")

@Path( WebServiceURLConstants.CLIENT_UPDATE_STATUS )
//  Jersey specific annotation
@Singleton
public class ClientStatusUpdateWebService {

	private static Logger log = Logger.getLogger(ClientStatusUpdateWebService.class);

	@Inject
	private ClientStatusUpdateService clientStatusUpdateService;


	/**
	 * @return
	 */
	@POST
	@Consumes("application/xml")
	public ClientStatusUpdateResponse clientStartup( ClientStatusUpdateRequest clientStatusUpdateRequest, @Context HttpServletRequest request ) {



		if ( log.isInfoEnabled() ) {

			log.info( "ClientStatusUpdateWebService: getNodeName(): " + clientStatusUpdateRequest.getNodeName()  );
		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();

		int errorCode = BaseResponse.ERROR_CODE_NO_ERRORS;


		try {
			ClientStatusUpdateResponse response = clientStatusUpdateService.clientStatusUpdateFromClient( clientStatusUpdateRequest, remoteHost );

			return response;

		} catch (Throwable e) {

			log.error( "Client Startup Failed: Exception: ClientStatusUpdateWebService:: clientStartup:   getNodeName(): " + clientStatusUpdateRequest.getNodeName() + ", Exception: " + e.toString() , e );

			errorCode = BaseResponse.ERROR_CODE_GENERAL_ERROR;
		}

		ClientStatusUpdateResponse response = new ClientStatusUpdateResponse();

		response.setErrorResponse( true );

		response.setErrorCode( errorCode );

		return response;
	}

}