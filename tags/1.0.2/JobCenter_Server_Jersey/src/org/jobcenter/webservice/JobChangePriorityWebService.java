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

@Path( WebServiceURLConstants.JOB_CHANGE_PRIORITY )
//  Jersey specific annotation
@Singleton
public class JobChangePriorityWebService {

	private static Logger log = Logger.getLogger(JobChangePriorityWebService.class);

	@Inject
	private JobChangePriorityService jobChangePriorityService;

	/**
	 * @return
	 */
	@POST
	@Consumes("application/xml")
	public JobChangePriorityResponse jobChangePriority( JobChangePriorityRequest jobChangePriorityRequest, @Context HttpServletRequest request ) {



		if ( log.isInfoEnabled() ) {

			log.info( "jobChangePriority: getNodeName(): " + jobChangePriorityRequest.getNodeName()  );

//			log.info( "JobId: " + updateJobStatusRequest.getJobId() + ", JobStatus " + updateJobStatusRequest.getJobStatus() );
		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();

		int errorCode = JobResponse.ERROR_CODE_NO_ERRORS;


		try {
			JobChangePriorityResponse jobChangePriorityResponse = jobChangePriorityService.jobChangePriority( jobChangePriorityRequest, remoteHost );

			return jobChangePriorityResponse;

		} catch (RecordNotUpdatedException e) {

			log.error( "JobChangePriority Failed: RecordNotUpdatedException: Exception: JobWebService:: jobChangePriority:   getNodeName(): " + jobChangePriorityRequest.getNodeName() + ", Exception: " + e.toString() , e );

			errorCode = JobResponse.ERROR_CODE_DATABASE_NOT_UPDATED;

		} catch (Throwable e) {

			log.error( "JobChangePriority Failed: Exception:   getNodeName(): " + jobChangePriorityRequest.getNodeName() + ", Exception: " + e.toString() , e );

			errorCode = JobResponse.ERROR_CODE_GENERAL_ERROR;
		}

		JobChangePriorityResponse jobChangePriorityResponse = new JobChangePriorityResponse();

		jobChangePriorityResponse.setErrorResponse( true );

		jobChangePriorityResponse.setErrorCode( errorCode );

		return jobChangePriorityResponse;
	}

}