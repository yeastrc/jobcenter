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
@Path( WebServiceURLConstants.GUI_LIST_CLIENTS_STATUS )
//  Jersey specific annotation
@Singleton
public class GetClientsStatusList {

	@Inject
	private GetClientsStatusListService GetClientsStatusListService;


	private static Logger log = Logger.getLogger(GetClientsStatusList.class);

	@POST
	@Consumes("application/xml")
	public ListClientsStatusResponse retrieveClientsStatusList( ListClientsStatusRequest listClientsStatusRequest, @Context HttpServletRequest request ) {

		if ( log.isInfoEnabled() ) {

			log.info( "retrieveClientsStatusList: getNodeName(): " + listClientsStatusRequest.getNodeName()  );



		}

		String remoteHost = request.getRemoteHost();


//		int remotePort = request.getRemotePort();



		try {

			ListClientsStatusResponse ListClientsStatusResponse = GetClientsStatusListService.retrieveClientsStatusList( listClientsStatusRequest, remoteHost );

			return ListClientsStatusResponse;

		} catch (Throwable e) {

			log.error( "retrieveClientsStatusList(...) Failed:  Exception: getNodeName(): " + listClientsStatusRequest.getNodeName() + ", Exception: " + e.toString() , e );
		}

		ListClientsStatusResponse listClientsStatusResponse = new ListClientsStatusResponse();

		listClientsStatusResponse.setErrorResponse( true );

		listClientsStatusResponse.setErrorCode( listClientsStatusResponse.ERROR_CODE_GENERAL_ERROR );

		return listClientsStatusResponse;
	}



}
