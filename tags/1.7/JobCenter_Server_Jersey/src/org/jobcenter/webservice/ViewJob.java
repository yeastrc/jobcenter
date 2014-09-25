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
@Path( WebServiceURLConstants.GUI_VIEW_JOB )
//  Jersey specific annotation
@Singleton
public class ViewJob {

	private static Logger log = Logger.getLogger(ViewJob.class);

	@Inject
	private GetJobForGUIService getJobForGUIService;

	@POST
	@Consumes("application/xml")
	public ViewJobResponse retrieveJob( ViewJobRequest viewJobRequest, @Context HttpServletRequest request ) {

		if ( log.isInfoEnabled() ) {

			log.info( "retrieveJob: getNodeName(): " + viewJobRequest.getNodeName()  );



		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();



		try {

			ViewJobResponse viewJobResponse = getJobForGUIService.retrieveJob( viewJobRequest, remoteHost );

			return viewJobResponse;

		} catch (Throwable e) {

			log.error( "retrieveJob(...) Failed:  Exception: getNodeName(): " + viewJobRequest.getNodeName() + ", Exception: " + e.toString() , e );
		}

		ViewJobResponse viewJobResponse = new ViewJobResponse();

		viewJobResponse.setErrorResponse( true );

		viewJobResponse.setErrorCode( viewJobResponse.ERROR_CODE_GENERAL_ERROR );

		return viewJobResponse;
	}


}
