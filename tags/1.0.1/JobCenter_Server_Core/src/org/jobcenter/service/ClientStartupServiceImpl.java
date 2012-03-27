package org.jobcenter.service;

import java.util.List;

import org.apache.commons.lang.mutable.MutableLong;
import org.apache.log4j.Logger;

import org.jobcenter.constants.ClientStatusUpdateTypeEnum;
import org.jobcenter.constants.ServerConfigKeyValues;
import org.jobcenter.constants.ServerCoreConstants;
import org.jobcenter.dao.*;
import org.jobcenter.dto.*;
import org.jobcenter.internalservice.ClientNodeNameCheck;
import org.jobcenter.internalservice.ClientsConnectedTrackingService;
import org.jobcenter.internalservice.GetValueFromConfigService;
import org.jobcenter.jdbc.*;
import org.jobcenter.nondbdto.ClientConnectedDTO;
import org.jobcenter.nondbdto.ClientIdentifierDTO;

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

public class ClientStartupServiceImpl implements ClientStartupService {


	private static Logger log = Logger.getLogger(ClientStartupServiceImpl.class);

	//  one copy for all instances
	private static MutableLong prevTime = new MutableLong();




	//  Service

	private ClientNodeNameCheck clientNodeNameCheck;
	private GetValueFromConfigService getValueFromConfigService;
	
	private ClientsConnectedTrackingService clientsConnectedTrackingService;

	public ClientsConnectedTrackingService getClientsConnectedTrackingService() {
		return clientsConnectedTrackingService;
	}
	public void setClientsConnectedTrackingService(
			ClientsConnectedTrackingService clientsConnectedTrackingService) {
		this.clientsConnectedTrackingService = clientsConnectedTrackingService;
	}
	public GetValueFromConfigService getGetValueFromConfigService() {
		return getValueFromConfigService;
	}
	public void setGetValueFromConfigService(
			GetValueFromConfigService getValueFromConfigService) {
		this.getValueFromConfigService = getValueFromConfigService;
	}
	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
	}

	//  DAO

//	private JobDAO jobDAO;
//	private StatusDAO statusDAO;
//
//	public StatusDAO getStatusDAO() {
//		return statusDAO;
//	}
//	public void setStatusDAO(StatusDAO statusDAO) {
//		this.statusDAO = statusDAO;
//	}
//	public JobDAO getJobDAO() {
//		return jobDAO;
//	}
//	public void setJobDAO(JobDAO jobDAO) {
//		this.jobDAO = jobDAO;
//	}


	//  JDBCDAO



	/**
	 * @param jobRequest
	 * @param remoteHost
	 * @return
	 */
	@Override
	public ClientStartupResponse clientStartup( ClientStartupRequest clientStartupRequest, String remoteHost )
	{
		final String method = "clientStartup";


		if ( clientStartupRequest == null ) {

			log.error( method + "  IllegalArgument:clientStartupRequest == null");

			throw new IllegalArgumentException( "clientStartupRequest == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( method + " clientStartupRequest.getNodeName() = |" + clientStartupRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}

		ClientStartupResponse clientStartupResponse = new ClientStartupResponse();

		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( clientStartupResponse, clientStartupRequest.getNodeName(), remoteHost ) ) {

			return clientStartupResponse;
		}

		long currentTime = System.currentTimeMillis();

		clientStartupResponse.setCurrentServerTime( currentTime );


		synchronized (prevTime) {

			//  prevent assigning the same value to two different clients

			if ( prevTime.longValue() == currentTime ) {

				currentTime++;
			}

			prevTime.setValue( currentTime );
		}







		ClientIdentifierDTO clientIdentifierDTO = new ClientIdentifierDTO();

		//  assign client identifier to current time ( milliseconds )

		clientIdentifierDTO.setClientIdentifier( currentTime );

		clientStartupResponse.setClientIdentifierDTO( clientIdentifierDTO );


		//  TODO  Also need to store the info for the client with this id


		ClientConnectedDTO clientConnectedDTO = new ClientConnectedDTO();

		clientConnectedDTO.setNodeName( clientStartupRequest.getNodeName() );
		clientConnectedDTO.setRemoteIPAddress( remoteHost );

		clientConnectedDTO.setClientIdentifierDTO( clientIdentifierDTO );

		long now = System.currentTimeMillis();

		clientConnectedDTO.setStartTime( now );
		clientConnectedDTO.setLastStatusUpdatedTime( now );


		int waitTimeForNextClientCheckin = ServerCoreConstants.DEFAULT_CLIENT_CHECKIN_TIME_IN_SECONDS;

		Integer waitTimeForNextClientCheckinFromConfig = getValueFromConfigService.getConfigValueAsInteger( ServerConfigKeyValues.CLIENT_CHECKIN_WAIT_TIME );

		if ( waitTimeForNextClientCheckinFromConfig != null ) {

			waitTimeForNextClientCheckin = waitTimeForNextClientCheckinFromConfig;
		}

		long nextExpectedStatusUpdatedTime = now + ( waitTimeForNextClientCheckin * 1000 );

		clientConnectedDTO.setNextExpectedStatusUpdatedTime(nextExpectedStatusUpdatedTime);

		clientConnectedDTO.setClientStatus( ClientStatusUpdateTypeEnum.CLIENT_UP );

		clientsConnectedTrackingService.addClient( clientConnectedDTO );


		log.info( method + " called, clientConnectedDTO added = " + clientConnectedDTO );


		clientStartupResponse.setWaitTimeForNextClientCheckin( waitTimeForNextClientCheckin );


		return clientStartupResponse;
	}





}
