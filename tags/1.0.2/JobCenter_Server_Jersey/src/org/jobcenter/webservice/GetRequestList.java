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
@Path( WebServiceURLConstants.GUI_LIST_REQUESTS )
//  Jersey specific annotation
@Singleton
public class GetRequestList {

	@Inject
	private GetRequestListService GetRequestListService;


	private static Logger log = Logger.getLogger(GetRequestList.class);

	@POST
	@Consumes("application/xml")
	public ListRequestsResponse retrieveRequestsList( ListRequestsRequest listRequestsRequest, @Context HttpServletRequest request ) {

		if ( log.isInfoEnabled() ) {

			log.info( "retrieveRequestsList: getNodeName(): " + listRequestsRequest.getNodeName()  );



		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();



		try {

			ListRequestsResponse listRequestsResponse = GetRequestListService.retrieveRequestsList( listRequestsRequest, remoteHost );

			return listRequestsResponse;

		} catch (Throwable e) {

			log.error( "retrieveRequestsList(...) Failed:  Exception: getNodeName(): " + listRequestsRequest.getNodeName() + ", Exception: " + e.toString() , e );
		}

		ListRequestsResponse listRequestsResponse = new ListRequestsResponse();

		listRequestsResponse.setErrorResponse( true );

		listRequestsResponse.setErrorCode( listRequestsResponse.ERROR_CODE_GENERAL_ERROR );

		return listRequestsResponse;
	}


}
