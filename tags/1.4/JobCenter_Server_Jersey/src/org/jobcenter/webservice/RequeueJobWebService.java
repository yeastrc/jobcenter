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

@Path( WebServiceURLConstants.REQUEUE_JOB )
//  Jersey specific annotation
@Singleton
public class RequeueJobWebService {

	private static Logger log = Logger.getLogger(RequeueJobWebService.class);

	@Inject
	private RequeueJobService requeueJobService;

	/**
	 * @return
	 */
	@POST
	@Consumes("application/xml")
	public RequeueJobResponse requeueJob( RequeueJobRequest requeueJobRequest, @Context HttpServletRequest request ) {



		if ( log.isInfoEnabled() ) {

			log.info( "requeueJob: getNodeName(): " + requeueJobRequest.getNodeName()  );

//			log.info( "JobId: " + updateJobStatusRequest.getJobId() + ", JobStatus " + updateJobStatusRequest.getJobStatus() );
		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();

		int errorCode = JobResponse.ERROR_CODE_NO_ERRORS;


		try {
			RequeueJobResponse requeueJobResponse = requeueJobService.requeueJob( requeueJobRequest, remoteHost );

			return requeueJobResponse;

		} catch (RecordNotUpdatedException e) {

			log.error( "RequeueJob Failed: RecordNotUpdatedException: Exception: JobWebService:: requeueJob:   getNodeName(): " + requeueJobRequest.getNodeName() + ", Exception: " + e.toString() , e );

			errorCode = JobResponse.ERROR_CODE_DATABASE_NOT_UPDATED;

		} catch (Throwable e) {

			log.error( "RequeueJob Failed: Exception:   getNodeName(): " + requeueJobRequest.getNodeName() + ", Exception: " + e.toString() , e );

			errorCode = JobResponse.ERROR_CODE_GENERAL_ERROR;
		}

		RequeueJobResponse requeueJobResponse = new RequeueJobResponse();

		requeueJobResponse.setErrorResponse( true );

		requeueJobResponse.setErrorCode( errorCode );

		return requeueJobResponse;
	}

}