package org.jobcenter.webservice;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.jobcenter.constants.WebServiceURLConstants;
import org.jobcenter.exception.RecordNotUpdatedException;
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

@Path( WebServiceURLConstants.GET_RUN_SERVICE )
//  Jersey specific annotation
@Singleton
public class GetRunWebService {

	private static Logger log = Logger.getLogger(GetRunWebService.class);

	//	Jersey specific annotation, get from Spring context
	@Inject
	private GetRunService getRunService;

	/**
	 * @return
	 */
	@POST
	@Consumes("application/xml")
	public GetRunResponse getRun( GetRunRequest getRunRequest, @Context HttpServletRequest request ) {



		if ( log.isInfoEnabled() ) {

			log.info( "getRun: getNodeName(): " + getRunRequest.getNodeName()  );

//			log.info( "JobId: " + getRunRequest.getJobId() + ", JobStatus " + getRunRequest.getJobStatus() );
		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();

		int errorCode = GetRunResponse.ERROR_CODE_NO_ERRORS;


		try {
			GetRunResponse getRunResponse = getRunService.getRunFromJob( getRunRequest, remoteHost );

			return getRunResponse;


		} catch (Throwable e) {

			log.error( "Get Run Failed: Exception: JobWebService:: getRun:   getNodeName(): " + getRunRequest.getNodeName() + ", Exception: " + e.toString() , e );

			errorCode = GetRunResponse.ERROR_CODE_GENERAL_ERROR;
		}

		GetRunResponse getRunResponse = new GetRunResponse();

		getRunResponse.setErrorResponse( true );

		getRunResponse.setErrorCode( errorCode );

		return getRunResponse;
	}

}