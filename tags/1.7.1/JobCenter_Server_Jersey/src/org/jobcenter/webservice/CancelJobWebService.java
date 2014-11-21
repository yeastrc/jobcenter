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

@Path( WebServiceURLConstants.CANCEL_JOB )
//  Jersey specific annotation
@Singleton
public class CancelJobWebService {

	private static Logger log = Logger.getLogger(CancelJobWebService.class);

	@Inject
	private CancelJobService cancelJobService;


	/**
	 * @return
	 */
	@POST
	@Consumes("application/xml")
	public CancelJobResponse cancelJob( CancelJobRequest cancelJobRequest, @Context HttpServletRequest request ) {



		if ( log.isInfoEnabled() ) {

			log.info( "cancelJob: getNodeName(): " + cancelJobRequest.getNodeName()  );

//			log.info( "JobId: " + updateJobStatusRequest.getJobId() + ", JobStatus " + updateJobStatusRequest.getJobStatus() );
		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();

		int errorCode = JobResponse.ERROR_CODE_NO_ERRORS;


		try {
			CancelJobResponse cancelJobResponse = cancelJobService.cancelJob( cancelJobRequest, remoteHost );

			return cancelJobResponse;

		} catch (RecordNotUpdatedException e) {

			log.error( "CancelJob Failed: RecordNotUpdatedException: Exception: JobWebService:: requeueJob:   getNodeName(): " + cancelJobRequest.getNodeName() + ", Exception: " + e.toString() , e );

			errorCode = JobResponse.ERROR_CODE_DATABASE_NOT_UPDATED;

		} catch (Throwable e) {

			log.error( "Cancel Job Failed: Exception: JobWebService:: cancelJob:   getNodeName(): " + cancelJobRequest.getNodeName() + ", Exception: " + e.toString() , e );

			errorCode = JobResponse.ERROR_CODE_GENERAL_ERROR;
		}

		CancelJobResponse cancelJobResponse = new CancelJobResponse();

		cancelJobResponse.setErrorResponse( true );

		cancelJobResponse.setErrorCode( errorCode );

		return cancelJobResponse;
	}

}