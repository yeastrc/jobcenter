package org.jobcenter.service;

import org.jobcenter.internalservice.ProcessClientsStatusForLateCheckinsService;


import org.apache.log4j.Logger;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    WARNING   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//The only way to get proper roll backs ( managed by Spring ) is to only use un-checked exceptions.
//
//The best way to make sure there are no checked exceptions is to have no "throws" on any of the methods.


//@Transactional causes Spring to surround calls to methods in this class with a database transaction.
//        Spring will roll back the transaction if a un-checked exception ( extended from RuntimeException ) is thrown.
//                 Otherwise it commits the transaction.

/**
 *
 *
 */
@Transactional ( propagation = Propagation.REQUIRED, readOnly = false )



public class SendClientsCheckinLateNotificationServiceForWebInitiationImpl implements SendClientsCheckinLateNotificationServiceForWebInitiation {

	private static Logger log = Logger.getLogger(SendClientsCheckinLateNotificationServiceForWebInitiationImpl.class);

	//  Service

	private ProcessClientsStatusForLateCheckinsService processClientsStatusForLateCheckinsService;



	/* (non-Javadoc)
	 * @see org.jobcenter.service.SendClientsCheckinLateNotificationServiceForWebInitiation#sendClientsCheckinLateNotification(java.lang.String)
	 */
	@Override
	public  void sendClientsCheckinLateNotification( String remoteHost )
	{
//		ListClientsStatusResponse listClientsStatusResponse = new ListClientsStatusResponse();
//
//		if ( listClientsStatusRequest == null ) {
//
//			log.error("retrieveClientsStatusList(...) IllegalArgument: listClientsStatusRequest == null");
//
//			throw new IllegalArgumentException( "listClientsStatusRequest == null" );
//		}
//
//		if ( log.isDebugEnabled() ) {
//
//			log.debug( "retrieveClientsStatusList  listClientsStatusRequest.getNodeName() = |" + listClientsStatusRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
//		}



//		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( listClientsStatusResponse, listClientsStatusRequest.getNodeName(), remoteHost ) ) {
//
//			return listClientsStatusResponse;
//		}
//
//		if ( log.isDebugEnabled() ) {
//
//			log.debug( "retrieveClientsStatusList  listClientsStatusRequest = " + listClientsStatusRequest );
//		}


		processClientsStatusForLateCheckinsService.processClientsStatusForLateCheckins();
	}



	public ProcessClientsStatusForLateCheckinsService getProcessClientsStatusForLateCheckinsService() {
		return processClientsStatusForLateCheckinsService;
	}



	public void setProcessClientsStatusForLateCheckinsService(
			ProcessClientsStatusForLateCheckinsService processClientsStatusForLateCheckinsService) {
		this.processClientsStatusForLateCheckinsService = processClientsStatusForLateCheckinsService;
	}





}
