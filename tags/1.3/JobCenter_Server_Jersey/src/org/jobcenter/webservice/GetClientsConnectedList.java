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
@Path( WebServiceURLConstants.GUI_LIST_CLIENTS_CONNECTED )
//  Jersey specific annotation
@Singleton
public class GetClientsConnectedList {

	@Inject
	private GetClientsConnectedListService GetClientsConnectedListService;


	private static Logger log = Logger.getLogger(GetClientsConnectedList.class);

	@POST
	@Consumes("application/xml")
	public GetClientsConnectedListResponse retrieveClientsConnectedList( GetClientsConnectedListRequest getClientsConnectedListRequest, @Context HttpServletRequest request ) {

		if ( log.isInfoEnabled() ) {

			log.info( "retrieveClientsConnectedList: getNodeName(): " + getClientsConnectedListRequest.getNodeName()  );



		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();



		try {

			GetClientsConnectedListResponse getClientsConnectedListResponse = GetClientsConnectedListService.retrieveClientsConnectedList( getClientsConnectedListRequest, remoteHost );

			return getClientsConnectedListResponse;

		} catch (Throwable e) {

			log.error( "retrieveClientsStatusList(...) Failed:  Exception: getNodeName(): " + getClientsConnectedListRequest.getNodeName() + ", Exception: " + e.toString() , e );
		}

		GetClientsConnectedListResponse getClientsConnectedListResponse = new GetClientsConnectedListResponse();

		getClientsConnectedListResponse.setErrorResponse( true );

		getClientsConnectedListResponse.setErrorCode( getClientsConnectedListResponse.ERROR_CODE_GENERAL_ERROR );

		return getClientsConnectedListResponse;
	}



}
