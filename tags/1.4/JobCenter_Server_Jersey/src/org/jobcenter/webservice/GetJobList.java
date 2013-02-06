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
@Path( WebServiceURLConstants.GUI_LIST_JOBS )
//  Jersey specific annotation
@Singleton
public class GetJobList {

	@Inject
	private GetJobListService GetJobListService;


	private static Logger log = Logger.getLogger(GetJobList.class);

	@POST
	@Consumes("application/xml")
	public ListJobsResponse retrieveJobsList( ListJobsRequest listJobsRequest, @Context HttpServletRequest request ) {

		if ( log.isInfoEnabled() ) {

			log.info( "retrieveJobsList: getNodeName(): " + listJobsRequest.getNodeName()  );



		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();



		try {

			ListJobsResponse listJobsResponse = GetJobListService.retrieveJobsList( listJobsRequest, remoteHost );

			return listJobsResponse;

		} catch (Throwable e) {

			log.error( "retrieveJobsList(...) Failed:  Exception: getNodeName(): " + listJobsRequest.getNodeName() + ", Exception: " + e.toString() , e );
		}

		ListJobsResponse listJobsResponse = new ListJobsResponse();

		listJobsResponse.setErrorResponse( true );

		listJobsResponse.setErrorCode( listJobsResponse.ERROR_CODE_GENERAL_ERROR );

		return listJobsResponse;
	}


}
