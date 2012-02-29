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

@Path( WebServiceURLConstants.GET_RUN_ID_LIST_SERVICE )
//  Jersey specific annotation
@Singleton
public class GetRunIdListWebService {

	private static Logger log = Logger.getLogger(GetRunIdListWebService.class);

	//	Jersey specific annotation, get from Spring context
	@Inject
	private GetRunIdListService getRunIdListService;

	/**
	 * @return
	 */
	@POST
	@Consumes("application/xml")
	public GetRunIdListResponse getRunIdList( GetRunIdListRequest getRunIdListRequest, @Context HttpServletRequest request ) {



		if ( log.isInfoEnabled() ) {

			log.info( "getRunIdList: getNodeName(): " + getRunIdListRequest.getNodeName()  );
		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();

		int errorCode = GetRunIdListResponse.ERROR_CODE_NO_ERRORS;


		try {
			GetRunIdListResponse getRunIdListResponse = getRunIdListService.getRunIdListFromJob( getRunIdListRequest, remoteHost );

			return getRunIdListResponse;


		} catch (Throwable e) {

			log.error( "Get Run Failed: Exception: GetRunIdListWebService:: getRunIdList:   getNodeName(): " + getRunIdListRequest.getNodeName() + ", Exception: " + e.toString() , e );

			errorCode = GetRunIdListResponse.ERROR_CODE_GENERAL_ERROR;
		}

		GetRunIdListResponse getRunIdListResponse = new GetRunIdListResponse();

		getRunIdListResponse.setErrorResponse( true );

		getRunIdListResponse.setErrorCode( errorCode );

		return getRunIdListResponse;
	}

}