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
@Path( WebServiceURLConstants.GUI_LIST_CLIENTS_FAILED_TO_CONNECT )
//  Jersey specific annotation
@Singleton
public class GetClientsFailedToConnectList {

	@Inject
	private GetClientsFailedToConnectTrackingService GetClientsFailedToConnectTrackingService;


	private static Logger log = Logger.getLogger(GetClientsFailedToConnectList.class);

	@POST
	@Consumes("application/xml")
	public GetClientsConnectedListResponse retrieveClientsFailedToConnectTrackingList( GetClientsConnectedListRequest getClientsConnectedListRequest, @Context HttpServletRequest request ) {

		if ( log.isInfoEnabled() ) {

			log.info( "retrieveClientsFailedToConnectTrackingList: getNodeName(): " + getClientsConnectedListRequest.getNodeName()  );



		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();



		try {

			GetClientsConnectedListResponse getClientsConnectedListResponse = GetClientsFailedToConnectTrackingService.retrieveClientsFailedToConnectTrackingList( getClientsConnectedListRequest, remoteHost );

			return getClientsConnectedListResponse;

		} catch (Throwable e) {

			log.error( "retrieveClientsFailedToConnectTrackingList(...) Failed:  Exception: getNodeName(): " + getClientsConnectedListRequest.getNodeName() + ", Exception: " + e.toString() , e );
		}

		GetClientsConnectedListResponse getClientsConnectedListResponse = new GetClientsConnectedListResponse();

		getClientsConnectedListResponse.setErrorResponse( true );

		getClientsConnectedListResponse.setErrorCode( getClientsConnectedListResponse.ERROR_CODE_GENERAL_ERROR );

		return getClientsConnectedListResponse;
	}


}
