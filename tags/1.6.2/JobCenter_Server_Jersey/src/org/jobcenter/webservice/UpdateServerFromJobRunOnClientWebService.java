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

@Path( WebServiceURLConstants.UPDATE_SERVER_FROM_JOB_RUN_ON_CLIENT_SERVICE )
//  Jersey specific annotation
@Singleton
public class UpdateServerFromJobRunOnClientWebService {

	private static Logger log = Logger.getLogger(UpdateServerFromJobRunOnClientWebService.class);

	//	Jersey specific annotation, get from Spring context
	@Inject
	private UpdateServerFromJobRunOnClientService updateServerFromJobRunOnClientService;

	/**
	 * @return
	 */
	@POST
	@Consumes("application/xml")
	public UpdateServerFromJobRunOnClientResponse updateJobStatus( UpdateServerFromJobRunOnClientRequest updateJobStatusRequest, @Context HttpServletRequest request ) {



		if ( log.isInfoEnabled() ) {

			log.info( "updateJobStatus: getNodeName(): " + updateJobStatusRequest.getNodeName()  );
		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();

		int errorCode = JobResponse.ERROR_CODE_NO_ERRORS;


		try {
			UpdateServerFromJobRunOnClientResponse updateJobStatusResponse = updateServerFromJobRunOnClientService.updateServerFromJobRunOnClient( updateJobStatusRequest, remoteHost );

			return updateJobStatusResponse;

		} catch (RecordNotUpdatedException e) {

			log.error( "UpdateWebStatus Failed: RecordNotUpdatedException: Exception: JobWebService:: updateJobStatus:   getNodeName(): " + updateJobStatusRequest.getNodeName() + ", Exception: " + e.toString() , e );

			errorCode = JobResponse.ERROR_CODE_DATABASE_NOT_UPDATED;

		} catch (Throwable e) {

			log.error( "UpdateWebStatus Failed: Exception: JobWebService:: updateJobStatus:   getNodeName(): " + updateJobStatusRequest.getNodeName() + ", Exception: " + e.toString() , e );

			errorCode = JobResponse.ERROR_CODE_GENERAL_ERROR;
		}

		UpdateServerFromJobRunOnClientResponse updateJobStatusResponse = new UpdateServerFromJobRunOnClientResponse();

		updateJobStatusResponse.setErrorResponse( true );

		updateJobStatusResponse.setErrorCode( errorCode );

		return updateJobStatusResponse;
	}

}