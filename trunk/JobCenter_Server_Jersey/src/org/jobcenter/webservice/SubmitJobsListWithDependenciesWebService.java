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

@Path( WebServiceURLConstants.SUBMIT_JOBS_LIST_WITH_DEPENDENCIES )

//  Jersey specific annotation
@Singleton
public class SubmitJobsListWithDependenciesWebService {

	private static Logger log = Logger.getLogger(SubmitJobsListWithDependenciesWebService.class);


	@Inject
	private SubmitJobsListWithDependenciesService submitJobsListWithDependenciesService;


	/**
	 * @return
	 */
	@POST
	@Consumes("application/xml")
	public SubmitJobsListWithDependenciesResponse submitJobsListWithDependencies( SubmitJobsListWithDependenciesRequest submitJobsListWithDependenciesRequest, @Context HttpServletRequest request ) {



		if ( log.isInfoEnabled() ) {

			log.info( "submitJob: getNodeName(): " + submitJobsListWithDependenciesRequest.getNodeName()  );
		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();

		int errorCode = JobResponse.ERROR_CODE_NO_ERRORS;


		try {
			SubmitJobsListWithDependenciesResponse submitJobsListWithDependenciesResponse = submitJobsListWithDependenciesService.submitJobsListWithDependencies( submitJobsListWithDependenciesRequest, remoteHost );

			return submitJobsListWithDependenciesResponse;

		} catch (RecordNotUpdatedException e) {

			log.error( "submitJob Failed: RecordNotUpdatedException: Exception: JobWebService:: updateJobStatus:   getNodeName(): " + submitJobsListWithDependenciesRequest.getNodeName() + ", Exception: " + e.toString() , e );

			errorCode = JobResponse.ERROR_CODE_DATABASE_NOT_UPDATED;

		} catch (Throwable e) {

			log.error( "submitJob Failed: Exception: JobWebService:: updateJobStatus:   getNodeName(): " + submitJobsListWithDependenciesRequest.getNodeName() + ", Exception: " + e.toString() , e );

			errorCode = JobResponse.ERROR_CODE_GENERAL_ERROR;
		}

		SubmitJobsListWithDependenciesResponse submitJobsListWithDependenciesResponse = new SubmitJobsListWithDependenciesResponse();

		submitJobsListWithDependenciesResponse.setErrorResponse( true );

		submitJobsListWithDependenciesResponse.setErrorCode( errorCode );

		return submitJobsListWithDependenciesResponse;
	}

}