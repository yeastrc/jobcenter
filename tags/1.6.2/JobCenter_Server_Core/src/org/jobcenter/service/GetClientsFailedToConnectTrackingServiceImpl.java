package org.jobcenter.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.jobcenter.dao.*;
import org.jobcenter.dto.*;
import org.jobcenter.internalservice.ClientNodeNameCheck;
import org.jobcenter.internalservice.ClientsFailedToConnectTrackingService;
import org.jobcenter.jdbc.*;
import org.jobcenter.nondbdto.ClientConnectedDTO;

import org.jobcenter.request.*;
import org.jobcenter.response.*;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    WARNING   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//The only way to get proper database roll backs ( managed by Spring ) is to only use un-checked exceptions.
//
//The best way to make sure there are no checked exceptions is to have no "throws" on any of the methods.


//@Transactional causes Spring to surround calls to methods in this class with a database transaction.
//        Spring will roll back the transaction if a un-checked exception ( extended from RuntimeException ) is thrown.
//                 Otherwise it commits the transaction.


/**
*
*
*/

//  Spring database transaction demarcation.
//  Spring will start a database transaction when any method is called and call commit when it completed
//    or call roll back if an unchecked exception is thrown.
@Transactional ( propagation = Propagation.REQUIRED, readOnly = false )

public class GetClientsFailedToConnectTrackingServiceImpl implements GetClientsFailedToConnectTrackingService {  


	private static Logger log = Logger.getLogger(GetClientsFailedToConnectTrackingServiceImpl.class);


	//  Service

	private ClientNodeNameCheck clientNodeNameCheck;
	
	private ClientsFailedToConnectTrackingService clientsFailedToConnectTrackingService;


	public ClientsFailedToConnectTrackingService getClientsFailedToConnectTrackingService() {
		return clientsFailedToConnectTrackingService;
	}
	public void setClientsFailedToConnectTrackingService(
			ClientsFailedToConnectTrackingService clientsFailedToConnectTrackingService) {
		this.clientsFailedToConnectTrackingService = clientsFailedToConnectTrackingService;
	}
	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
	}

	//  DAO



	//  JDBCDAO



	/* 
	 * Return list of clients where same node name is active on two different clients
	 */
	/* (non-Javadoc)
	 * @see org.jobcenter.service.GetClientsUsingSameNodeNameService#retrieveClientsConnectedList(org.jobcenter.request.GetClientsConnectedListRequest, java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see org.jobcenter.service.GetClientsFailedToConnectTrackingService#retrieveClientsFailedToConnectTrackingList(org.jobcenter.request.GetClientsConnectedListRequest, java.lang.String)
	 */
	@Override
	public GetClientsConnectedListResponse retrieveClientsFailedToConnectTrackingList( GetClientsConnectedListRequest getClientsConnectedListRequest, String remoteHost )
	{
		final String method = "retrieveClientsFailedToConnectTrackingList";


		if ( getClientsConnectedListRequest == null ) {

			log.error( method + "  IllegalArgument:getClientsConnectedListRequest == null");

			throw new IllegalArgumentException( "getClientsConnectedListRequest == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( method + " getClientsConnectedListRequest.getNodeName() = |" + getClientsConnectedListRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}

		GetClientsConnectedListResponse getClientsConnectedListResponse = new GetClientsConnectedListResponse();

		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( getClientsConnectedListResponse, getClientsConnectedListRequest.getNodeName(), remoteHost ) ) {

			return getClientsConnectedListResponse;
		}

		List<ClientConnectedDTO> clientsConnectedList = clientsFailedToConnectTrackingService.retrieveClientsFailedToConnectList();
		
		
		
		getClientsConnectedListResponse.setClientConnectedDTOList( clientsConnectedList );
		
		return getClientsConnectedListResponse;
	}





}
