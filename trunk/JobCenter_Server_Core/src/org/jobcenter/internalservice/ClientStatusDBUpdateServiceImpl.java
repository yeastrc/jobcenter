package org.jobcenter.internalservice;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jobcenter.constants.ClientStatusUpdateTypeEnum;
import org.jobcenter.constants.ServerConfigKeyValues;
import org.jobcenter.constants.ServerCoreConstants;
import org.jobcenter.dao.NodeClientStatusDAO;
import org.jobcenter.dto.NodeClientStatusDTO;
import org.jobcenter.dtoservernondb.NodeClientStatusDTOPrevCurrent;
import org.jobcenter.request.ClientStartupRequest;
import org.jobcenter.request.ClientStatusUpdateRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    WARNING   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//The only way to get proper database roll backs ( managed by Spring ) is to only use un-checked exceptions.
//
//The best way to make sure there are no checked exceptions is to have no "throws" on any of the methods.


//@Transactional causes Spring to surround calls to methods in this class with a database transaction.
//      Spring will roll back the transaction if a un-checked exception ( extended from RuntimeException ) is thrown.
//               Otherwise it commits the transaction.


/**
* Perform updates to the table node_lient_status
*
*/

//Spring database transaction demarcation.
//Spring will start a database transaction when any method is called and call commit when it completed
//  or call roll back if an unchecked exception is thrown.
@Transactional ( propagation = Propagation.REQUIRED, readOnly = false )

public class ClientStatusDBUpdateServiceImpl implements ClientStatusDBUpdateService {

	private static Logger log = Logger.getLogger(ClientStatusDBUpdateServiceImpl.class);


	//  Service

	private ClientNodeNameCheck clientNodeNameCheck;
	private GetValueFromConfigService getValueFromConfigService;

	//  DAO

	private NodeClientStatusDAO nodeClientStatusDAO;

	public NodeClientStatusDAO getNodeClientStatusDAO() {
		return nodeClientStatusDAO;
	}
	public void setNodeClientStatusDAO(NodeClientStatusDAO nodeClientStatusDAO) {
		this.nodeClientStatusDAO = nodeClientStatusDAO;
	}


	/**
	 * @param client
	 */
	@Override
	public void updateDB( NodeClientStatusDTO client ) {

		nodeClientStatusDAO.saveOrUpdate( client );
	}
	
	

	/**
	 * @param clientStartupRequest
	 * @param timeUntilNextClientStatusUpdate
	 * @param remoteHost
	 */
	@Override
	public NodeClientStatusDTOPrevCurrent updateDBWithStatusFromClientStartup( ClientStartupRequest clientStartupRequest, int timeUntilNextClientStatusUpdate, String remoteHost ) {

		return updateDBWithStatusFromClientInternal( clientStartupRequest.getNodeName(), 
				ClientStatusUpdateTypeEnum.CLIENT_UP, 
				timeUntilNextClientStatusUpdate );
	}
	

	/**
	 * @param clientStatusUpdateRequest
	 * @param remoteHost
	 */
	@Override
	public NodeClientStatusDTOPrevCurrent updateDBWithStatusFromClient( ClientStatusUpdateRequest clientStatusUpdateRequest, String remoteHost ) {

		return updateDBWithStatusFromClientInternal( clientStatusUpdateRequest.getNodeName(), 
				clientStatusUpdateRequest.getUpdateType(), 
				clientStatusUpdateRequest.getTimeUntilNextClientStatusUpdate() );
	}
	
	
	/**
	 * @param nodeName
	 * @param updateType
	 * @param timeUntilNextClientStatusUpdate
	 * @return TODO
	 */
	private NodeClientStatusDTOPrevCurrent updateDBWithStatusFromClientInternal( String nodeName, 
			ClientStatusUpdateTypeEnum updateType,
			int timeUntilNextClientStatusUpdate ) {
		
		
		
		if ( log.isDebugEnabled() ) {


			log.debug( "Updating Client status DB for client node name = " + nodeName );
		}
		
		NodeClientStatusDTOPrevCurrent nodeClientStatusDTOPrevCurrent = new NodeClientStatusDTOPrevCurrent();


		try {

			Integer nodeId = clientNodeNameCheck.getNodeIdForNodeName( nodeName );

			if ( nodeId != null ) {

				List nodeClientStatusDTOList = nodeClientStatusDAO.findByNodeId( nodeId );

				if ( nodeClientStatusDTOList != null && ! nodeClientStatusDTOList.isEmpty() ) {

					//  Update existing entry

					NodeClientStatusDTO nodeClientStatusDTO = (NodeClientStatusDTO) nodeClientStatusDTOList.get( 0 );
					
					NodeClientStatusDTO nodeClientStatusDTOPrev = (NodeClientStatusDTO) nodeClientStatusDTO.clone();
					
					nodeClientStatusDTOPrevCurrent.setPrevNodeClientStatusDTO( nodeClientStatusDTOPrev );

					updateDBRecordWithStatus( nodeClientStatusDTO, updateType, timeUntilNextClientStatusUpdate );

					nodeClientStatusDAO.saveOrUpdate( nodeClientStatusDTO );
					
					nodeClientStatusDTOPrevCurrent.setCurrentNodeClientStatusDTO( nodeClientStatusDTO );

				} else {

					//  create new entry

					NodeClientStatusDTO nodeClientStatusDTO = new NodeClientStatusDTO();

					nodeClientStatusDTO.setNodeId( nodeId );

					updateDBRecordWithStatus( nodeClientStatusDTO, updateType, timeUntilNextClientStatusUpdate );
					
					if ( log.isDebugEnabled() ) {
						
						log.debug( "nodeClientStatusDTO to save: " + nodeClientStatusDTO );
					}

					nodeClientStatusDAO.save( nodeClientStatusDTO );

					nodeClientStatusDTOPrevCurrent.setCurrentNodeClientStatusDTO( nodeClientStatusDTO );
					
					nodeClientStatusDTOPrevCurrent.setPrevNodeClientStatusDTO( null );
				}


			}

		} catch ( Throwable t ) {

			String msg = "Exception updating client status db, retry controlled elsewhere. client node name = " + nodeName;

			log.warn( msg );

			throw new RuntimeException( msg, t );
		}
		
		return nodeClientStatusDTOPrevCurrent;


	}



	/**
	 * @param nodeClientStatusDTO
	 * @param updateType
	 * @param timeUntilNextClientStatusUpdate
	 */
	private void updateDBRecordWithStatus( NodeClientStatusDTO nodeClientStatusDTO, 
			ClientStatusUpdateTypeEnum updateType,
			int timeUntilNextClientStatusUpdate ) {

	
		nodeClientStatusDTO.setNotificationSentThatClientLate( false );

		nodeClientStatusDTO.setLastCheckinTime( new Date() );
		
		if ( updateType == ClientStatusUpdateTypeEnum.CLIENT_ABOUT_TO_EXIT 
			 || updateType == ClientStatusUpdateTypeEnum.CLIENT_SHUTDOWN_REQUESTED ) {

			nodeClientStatusDTO.setClientStarted( false );
			
			nodeClientStatusDTO.setSecondsUntilNextCheckin( null );
			
			nodeClientStatusDTO.setNextCheckinTime( null );
			
			nodeClientStatusDTO.setLateForNextCheckinTime( null );
			
		} else {

			nodeClientStatusDTO.setClientStarted( true );

			nodeClientStatusDTO.setSecondsUntilNextCheckin( timeUntilNextClientStatusUpdate );

			Calendar timeUntilNextClientStatusUpdateCalendar = Calendar.getInstance();

			timeUntilNextClientStatusUpdateCalendar.add( Calendar.SECOND, timeUntilNextClientStatusUpdate );

			Date timeUntilNextClientStatusUpdateDate = timeUntilNextClientStatusUpdateCalendar.getTime();

			nodeClientStatusDTO.setNextCheckinTime( timeUntilNextClientStatusUpdateDate );

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


			int overageUntilLate = (int) ( timeUntilNextClientStatusUpdate * ( ( (float) clientCheckinOverageBeforeLatePercent ) / 100 ) );

			if ( ( overageUntilLate >  clientCheckinOverageBeforeLateMaxValue ) || overageUntilLate <= 0  ) {

				overageUntilLate = clientCheckinOverageBeforeLateMaxValue;
			}


			Calendar timeUntilClientLateForNextCheckinTimeCalendar = (Calendar) timeUntilNextClientStatusUpdateCalendar.clone();

			timeUntilClientLateForNextCheckinTimeCalendar.add( Calendar.SECOND, overageUntilLate );


			Date timeUntilClientLateForNextCheckinTimeDate = timeUntilClientLateForNextCheckinTimeCalendar.getTime();


			nodeClientStatusDTO.setLateForNextCheckinTime( timeUntilClientLateForNextCheckinTimeDate );
			
		}
	}







	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
	}
	public GetValueFromConfigService getGetValueFromConfigService() {
		return getValueFromConfigService;
	}
	public void setGetValueFromConfigService(
			GetValueFromConfigService getValueFromConfigService) {
		this.getValueFromConfigService = getValueFromConfigService;
	}

}
