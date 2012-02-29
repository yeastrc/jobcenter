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

@Path( WebServiceURLConstants.GUI_LIST_REQUEST_TYPES )

//  Jersey specific annotation
@Singleton
public class GetRequestTypeList {

	private static Logger log = Logger.getLogger(GetRequestTypeList.class);

	@Inject
	private GetRequestTypesService GetRequestTypesService;

	@POST
	@Consumes("application/xml")
	public ListRequestTypesResponse retrieveRequestTypesList( ListRequestTypesRequest listRequestTypesRequest, @Context HttpServletRequest request ) {

		if ( log.isInfoEnabled() ) {

			log.info( "retrieveRequestTypesList: getNodeName(): " + listRequestTypesRequest.getNodeName()  );



		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();



		try {

			ListRequestTypesResponse listRequestTypesResponse = GetRequestTypesService.retrieveRequestTypes( listRequestTypesRequest, remoteHost );

			return listRequestTypesResponse;

		} catch (Throwable e) {

			log.error( "retrieveRequestTypesList(...) Failed:  Exception: getNodeName(): " + listRequestTypesRequest.getNodeName() + ", Exception: " + e.toString() , e );
		}

		ListRequestTypesResponse listRequestTypesResponse = new ListRequestTypesResponse();

		listRequestTypesResponse.setErrorResponse( true );

		listRequestTypesResponse.setErrorCode( listRequestTypesResponse.ERROR_CODE_GENERAL_ERROR );

		return listRequestTypesResponse;
	}


}
