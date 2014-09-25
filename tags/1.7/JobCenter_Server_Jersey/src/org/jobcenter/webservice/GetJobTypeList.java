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

@Path( WebServiceURLConstants.GUI_LIST_JOB_TYPES )

//  Jersey specific annotation
@Singleton
public class GetJobTypeList {

	private static Logger log = Logger.getLogger(GetJobTypeList.class);

	@Inject
	private GetJobTypesService GetJobTypesService;

	@POST
	@Consumes("application/xml")
	public ListJobTypesResponse retrieveJobTypesList( ListJobTypesRequest listJobTypesRequest, @Context HttpServletRequest request ) {

		if ( log.isInfoEnabled() ) {

			log.info( "retrieveJobTypesList: getNodeName(): " + listJobTypesRequest.getNodeName()  );



		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();



		try {

			ListJobTypesResponse listJobTypesResponse = GetJobTypesService.retrieveJobTypes( listJobTypesRequest, remoteHost );

			return listJobTypesResponse;

		} catch (Throwable e) {

			log.error( "retrieveJobTypesList(...) Failed:  Exception: getNodeName(): " + listJobTypesRequest.getNodeName() + ", Exception: " + e.toString() , e );
		}

		ListJobTypesResponse listJobTypesResponse = new ListJobTypesResponse();

		listJobTypesResponse.setErrorResponse( true );

		listJobTypesResponse.setErrorCode( listJobTypesResponse.ERROR_CODE_GENERAL_ERROR );

		return listJobTypesResponse;
	}


}
