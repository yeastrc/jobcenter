package org.jobcenter.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.jobcenter.dao.*;
import org.jobcenter.dto.*;
import org.jobcenter.internalservice.ClientNodeNameCheck;
import org.jobcenter.internalservice.ClientsConnectedTrackingService;
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

public class GetClientsUsingSameNodeNameServiceImpl implements GetClientsUsingSameNodeNameService { 


	private static Logger log = Logger.getLogger(GetClientsUsingSameNodeNameServiceImpl.class);


	//  Service

	private ClientNodeNameCheck clientNodeNameCheck;
	
	private ClientsConnectedTrackingService clientsConnectedTrackingService;


	public ClientsConnectedTrackingService getClientsConnectedTrackingService() {
		return clientsConnectedTrackingService;
	}
	public void setClientsConnectedTrackingService(
			ClientsConnectedTrackingService clientsConnectedTrackingService) {
		this.clientsConnectedTrackingService = clientsConnectedTrackingService;
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
	@Override
	public GetClientsConnectedListResponse retrieveClientsUsingSameNodeNameList( GetClientsConnectedListRequest getClientsConnectedListRequest, String remoteHost )
	{
		final String method = "retrieveClientsUsingSameNodeNameList";


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

		List<ClientConnectedDTO> clientsConnectedList = clientsConnectedTrackingService.retrieveClientsConnectedList();
		
		List<ClientConnectedDTO> clientsConnectedDuplicateNodeList = new ArrayList<ClientConnectedDTO>();
		
		for ( ClientConnectedDTO entry : clientsConnectedList ) {
			
			for ( ClientConnectedDTO compareEntry : clientsConnectedList ) {
			
				if ( entry != compareEntry ) {  // do not compare to itself
					
					if ( entry.getNodeName() != null && entry.getNodeName().equals( compareEntry.getNodeName() ) ) {
						
						//  if entry start time within compare entry start and last update time
						if ( entry.getStartTime() > compareEntry.getStartTime()
								&& entry.getStartTime() < compareEntry.getLastStatusUpdatedTime() ) {
						
							if ( ! clientsConnectedDuplicateNodeList.contains( entry ) ) {
								
								clientsConnectedDuplicateNodeList.add( entry );
							}
							
							if ( ! clientsConnectedDuplicateNodeList.contains( compareEntry ) ) {
								
								clientsConnectedDuplicateNodeList.add( compareEntry );
							}

						}
					}
					
				}
			
			}
		}
		
		
		
		getClientsConnectedListResponse.setClientConnectedDTOList( clientsConnectedDuplicateNodeList );
		
		return getClientsConnectedListResponse;
	}





}
