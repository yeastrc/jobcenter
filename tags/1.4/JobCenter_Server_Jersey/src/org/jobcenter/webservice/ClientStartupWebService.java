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

@Path( WebServiceURLConstants.CLIENT_STARTUP )
//  Jersey specific annotation
@Singleton
public class ClientStartupWebService {

	private static Logger log = Logger.getLogger(ClientStartupWebService.class);

	@Inject
	private ClientStartupService clientStartupService;


	/**
	 * @return
	 */
	@POST
	@Consumes("application/xml")
	public ClientStartupResponse clientStartup( ClientStartupRequest clientStartupRequest, @Context HttpServletRequest request ) {



		if ( log.isInfoEnabled() ) {

			log.info( "clientStartup: getNodeName(): " + clientStartupRequest.getNodeName()  );
		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();

		int errorCode = BaseResponse.ERROR_CODE_NO_ERRORS;


		try {
			ClientStartupResponse response = clientStartupService.clientStartup( clientStartupRequest, remoteHost );

			return response;

		} catch (Throwable e) {

			log.error( "Client Startup Failed: Exception: ClientStartupWebService:: clientStartup:   getNodeName(): " + clientStartupRequest.getNodeName() + ", Exception: " + e.toString() , e );

			errorCode = BaseResponse.ERROR_CODE_GENERAL_ERROR;
		}

		ClientStartupResponse response = new ClientStartupResponse();

		response.setErrorResponse( true );

		response.setErrorCode( errorCode );

		return response;
	}

}