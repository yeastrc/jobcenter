package org.jobcenter.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import org.jobcenter.constants.ServerConfigKeyValues;
import org.jobcenter.constants.ServerCoreConstants;
import org.jobcenter.dao.NodeClientStatusDAO;
import org.jobcenter.dto.NodeClientStatusDTO;
import org.jobcenter.internalservice.ClientNodeNameCheck;
import org.jobcenter.internalservice.ClientsConnectedTrackingService;
import org.jobcenter.internalservice.GetValueFromConfigService;
import org.jobcenter.nondbdto.RunInProgressDTO;

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

public class ClientStatusUpdateServiceImpl implements ClientStatusUpdateService {

	private static Logger log = Logger.getLogger(ClientStatusUpdateServiceImpl.class);


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

	private NodeClientStatusDAO nodeClientStatusDAO;

	public NodeClientStatusDAO getNodeClientStatusDAO() {
		return nodeClientStatusDAO;
	}
	public void setNodeClientStatusDAO(NodeClientStatusDAO nodeClientStatusDAO) {
		this.nodeClientStatusDAO = nodeClientStatusDAO;
	}


	/**
	 * @param jobRequest
	 * @param remoteHost
	 * @return
	 */
	@Override
	public ClientStatusUpdateResponse clientStatusUpdate( ClientStatusUpdateRequest clientStatusUpdateRequest, String remoteHost )
	{
		final String method = "clientStatusUpdate";


		if ( clientStatusUpdateRequest == null ) {

			log.error( method + "  IllegalArgument:clientStatusUpdateRequest == null");

			throw new IllegalArgumentException( "clientStatusUpdateRequest == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( method + " clientStatusUpdateRequest.getNodeName() = |" + clientStatusUpdateRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}

		ClientStatusUpdateResponse clientStatusUpdateResponse = new ClientStatusUpdateResponse();

		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( clientStatusUpdateResponse, clientStatusUpdateRequest.getNodeName(), remoteHost ) ) {

			return clientStatusUpdateResponse;
		}


		if ( log.isDebugEnabled() ) {


			log.debug( method + ": clientStatusUpdateRequest = " + clientStatusUpdateRequest );
		}

		clientStatusUpdateRequest.getTimeUntilNextClientStatusUpdate();



		//  TODO    Need to actually do something with the provided info


//		long currentTime = System.currentTimeMillis();
//
//		long timeDiffOfClient = currentTime - clientStatusUpdateRequest.getClientCurrentTime();
//
//		log.info( method + " called. NodeName = " + clientStatusUpdateRequest.getNodeName()
//				+ ", ClientIdentifierDTO = "  + clientStatusUpdateRequest.getClientIdentifierDTO()
//				+ ", updateType = " + clientStatusUpdateRequest.getUpdateType()
//				+ ", timeDiffOfClient = " + timeDiffOfClient );



		clientsConnectedTrackingService.updateClientStatusAndInfo( clientStatusUpdateRequest.getClientIdentifierDTO(), clientStatusUpdateRequest.getUpdateType() );

		updateDBWithStatus( clientStatusUpdateRequest, remoteHost );

		List<RunInProgressDTO> runsInProgress = clientStatusUpdateRequest.getRunsInProgress();

		if ( runsInProgress != null ) {

			for ( RunInProgressDTO runInProgress : runsInProgress ) {

				log.info( method + " called. runInProgress:  JobId = " + runInProgress.getJobId()
						+ ", RunId = "  + runInProgress.getRunId()
						+ ", LastStatusTimeStampFromModule = "  + runInProgress.getLastStatusTimeStampFromModule()
						+ ", PercentageComplete = " + runInProgress.getPercentageComplete() );


				//  This tracking is available for a future enhancement.

				//   The "LastStatusTimeStampFromModule" may not be accurate.  The "PercentageComplete" is not being set by many modules

//				if ( runInProgress.getLastStatusTimeStampFromModule() != null ) {
//
//					long lastStatusTimeStampFromModuleAdjustedToServerTime = runInProgress.getLastStatusTimeStampFromModule() - timeDiffOfClient;
//
//
//					long timeSinceLastStatusTimeStampFromModule = currentTime - lastStatusTimeStampFromModuleAdjustedToServerTime;
//
//					long secondsSinceLastStatusTimeStampFromModule = timeSinceLastStatusTimeStampFromModule / 1000;
//
//					log.info( method + " called. lastStatusTimeStampFromModuleAdjustedToServerTime = " + lastStatusTimeStampFromModuleAdjustedToServerTime
//							+ ", timeSinceLastStatusTimeStampFromModule = " + timeSinceLastStatusTimeStampFromModule
//							+ ", secondsSinceLastStatusTimeStampFromModule = " + secondsSinceLastStatusTimeStampFromModule );
//
//				}
			}

		}

		return clientStatusUpdateResponse;
	}


	/**
	 * @param clientStatusUpdateRequest
	 * @param remoteHost
	 */
	private void updateDBWithStatus( ClientStatusUpdateRequest clientStatusUpdateRequest, String remoteHost ) {

		try {

			Integer nodeId =clientNodeNameCheck.getNodeIdForNodeName( clientStatusUpdateRequest.getNodeName() );

			if ( nodeId != null ) {


				boolean updatedEntry = false;

				int retryCount = 0;

				while ( ! updatedEntry ) {

					try {

						List nodeClientStatusDTOList = nodeClientStatusDAO.findByNodeId( nodeId );

						if ( nodeClientStatusDTOList != null && ! nodeClientStatusDTOList.isEmpty() ) {

							//  Update existing entry

							NodeClientStatusDTO nodeClientStatusDTO = (NodeClientStatusDTO) nodeClientStatusDTOList.get( 0 );

							updateDBRecordWithStatus( nodeClientStatusDTO, clientStatusUpdateRequest );

							nodeClientStatusDAO.saveOrUpdate( nodeClientStatusDTO );

						} else {

							//  create new entry

							NodeClientStatusDTO nodeClientStatusDTO = new NodeClientStatusDTO();

							nodeClientStatusDTO.setNodeId( nodeId );

							updateDBRecordWithStatus( nodeClientStatusDTO, clientStatusUpdateRequest );

							nodeClientStatusDAO.save( nodeClientStatusDTO );
						}

						updatedEntry = true;

					} catch ( Throwable t ) {


						retryCount++;

						if ( retryCount > 5 ) {

							break;
						}

						log.error( "" );
					}
				}

			}

		} catch ( Throwable t ) {


			log.error( "" );
		}


	}


	/**
	 * @param nodeClientStatusDTO
	 * @param clientStatusUpdateRequest
	 */
	private void updateDBRecordWithStatus( NodeClientStatusDTO nodeClientStatusDTO, ClientStatusUpdateRequest clientStatusUpdateRequest ) {

		nodeClientStatusDTO.setNotificationSentThatClientLate( false );

		nodeClientStatusDTO.setLastCheckinTime( new Date() );

		nodeClientStatusDTO.setSecondsUntilNextCheckin( clientStatusUpdateRequest.getTimeUntilNextClientStatusUpdate() );

		Calendar timeUntilNextClientStatusUpdateCalendar = Calendar.getInstance();

		timeUntilNextClientStatusUpdateCalendar.add( Calendar.SECOND, clientStatusUpdateRequest.getTimeUntilNextClientStatusUpdate() );

		//  Add additional time so not late the split second after it was supposed to check in.


		int clientCheckinOverageBeforeLatePercent = ServerCoreConstants.DEFAULT_CLIENT_CHECKIN_OVERAGE_BEFORE_LATE_PERCENT;
		int clientCheckinOverageBeforeLateMaxValue = ServerCoreConstants.DEFAULT_CLIENT_CHECKIN_OVERAGE_BEFORE_LATE_MAX_VALUE;

		Integer clientCheckinOverageBeforeLatePercentFromConfig = getValueFromConfigService.getConfigValueAsInteger( ServerConfigKeyValues.CLIENT_CHECKIN_OVERAGE_BEFORE_LATE_PERCENT );
		if ( clientCheckinOverageBeforeLatePercentFromConfig != null ) {
			clientCheckinOverageBeforeLatePercent = clientCheckinOverageBeforeLatePercentFromConfig;
		}

		Integer clientCheckinOverageBeforeLateMaxValueFromConfig = getValueFromConfigService.getConfigValueAsInteger( ServerConfigKeyValues.CLIENT_CHECKIN_OVERAGE_BEFORE_LATE_MAX_VALUE );
		if ( clientCheckinOverageBeforeLateMaxValueFromConfig != null ) {
			clientCheckinOverageBeforeLateMaxValue = clientCheckinOverageBeforeLateMaxValueFromConfig;
		}


		int overageUntilLate = clientStatusUpdateRequest.getTimeUntilNextClientStatusUpdate() * ( clientCheckinOverageBeforeLatePercent / 100 );

		if ( overageUntilLate >  clientCheckinOverageBeforeLateMaxValue ) {

			overageUntilLate = clientCheckinOverageBeforeLateMaxValue;
		}

		timeUntilNextClientStatusUpdateCalendar.add( Calendar.SECOND, overageUntilLate );


		Date timeUntilNextClientStatusUpdateDate = timeUntilNextClientStatusUpdateCalendar.getTime();

		nodeClientStatusDTO.setLateForNextCheckinTime( timeUntilNextClientStatusUpdateDate );


	}



}
