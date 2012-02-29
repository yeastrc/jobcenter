package org.jobcenter.webservice;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.jobcenter.constants.WebServiceURLConstants;
import org.jobcenter.request.JobRequest;
import org.jobcenter.request.JobRequestModuleInfo;
import org.jobcenter.response.JobResponse;
import org.jobcenter.service.GetNextJobForClientService;

import com.sun.jersey.spi.inject.Inject;
import com.sun.jersey.spi.resource.Singleton;



/**
 *
 *
 */
@Produces("application/xml")

@Path( WebServiceURLConstants.GET_NEXT_JOB_FOR_CLIENT_TO_PROCESS )

//  Jersey specific annotation
@Singleton
public class GetNextJobForClientToProcess {

	private static Logger log = Logger.getLogger(GetNextJobForClientToProcess.class);


	@Inject
	private GetNextJobForClientService getNextJobForClientService;


	/**
	 * @return
	 */
	@POST
	@Consumes("application/xml")
	public JobResponse retrieveJob( JobRequest jobRequest, @Context HttpServletRequest request ) {

		if ( log.isInfoEnabled() ) {

			log.info( "retrieveJob: getNodeName(): " + jobRequest.getNodeName()  );

			log.info( "retrieveJob: jobRequest.getJobTypeNames(): "  );

			if ( jobRequest.getClientModules() == null ) {

				log.info( "jobRequest.getClientModules() == null  " );

			} else {

				for ( JobRequestModuleInfo jobRequestModuleInfo : jobRequest.getClientModules() ) {

					log.info( "name: " + jobRequestModuleInfo.getModuleName() + ", version " + jobRequestModuleInfo.getModuleVersion() );
				}
			}

		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();



		try {

			JobResponse jobResponse = getNextJobForClientService.getNextJobForClientService( jobRequest, remoteHost );

			return jobResponse;

		} catch (Throwable e) {

			log.error( "GetJobForClientWebService::retrieveJob(...) Failed:  Exception: JobWebService:: retrieveJob:   getNodeName(): " + jobRequest.getNodeName() + ", Exception: " + e.toString() , e );
		}

		JobResponse jobResponse = new JobResponse();

		jobResponse.setErrorResponse( true );

		jobResponse.setErrorCode( JobResponse.ERROR_CODE_GENERAL_ERROR );

		return jobResponse;
	}


}