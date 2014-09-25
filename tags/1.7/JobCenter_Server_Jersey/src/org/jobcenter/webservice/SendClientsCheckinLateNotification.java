package org.jobcenter.webservice;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
 *  Optional URL to trigger sending a notification that clients are late to check in.
 *
 *  Need to uncomment the code below.  Commented out since no remote IP checking.
 */
@Produces("application/xml")
@Path( WebServiceURLConstants.SEND_CLIENTS_CHECKIN_LATE_NOTIFICATION )
//  Jersey specific annotation
@Singleton
public class SendClientsCheckinLateNotification {

	@Inject
	private SendClientsCheckinLateNotificationServiceForWebInitiation SendClientsCheckinLateNotificationServiceForWebInitiation;


	private static Logger log = Logger.getLogger(SendClientsCheckinLateNotification.class);



	@GET
	public SendClientsCheckinLateNotificationResponse retrieveClientsStatusListGET( @Context HttpServletRequest request ) {

		String remoteHost = request.getRemoteHost();

		SendClientsCheckinLateNotificationResponse sendClientsCheckinLateNotificationResponse = new SendClientsCheckinLateNotificationResponse();

		//  comment out for now


//		try {
//
//			SendClientsCheckinLateNotificationServiceForWebInitiation.sendClientsCheckinLateNotification( remoteHost );
//
//			sendClientsCheckinLateNotificationResponse.setErrorResponse( false );
//
//			sendClientsCheckinLateNotificationResponse.setErrorCode( ListClientsStatusResponse.ERROR_CODE_NO_ERRORS );
//
//			return sendClientsCheckinLateNotificationResponse;
//
//		} catch (Throwable e) {
//
//			log.error( "sendClientsCheckinLateNotification(...) Failed:  Exception: " + e.toString() , e );
//		}

		sendClientsCheckinLateNotificationResponse.setErrorResponse( true );

		sendClientsCheckinLateNotificationResponse.setErrorCode( ListClientsStatusResponse.ERROR_CODE_GENERAL_ERROR );

		return sendClientsCheckinLateNotificationResponse;
	}


}
