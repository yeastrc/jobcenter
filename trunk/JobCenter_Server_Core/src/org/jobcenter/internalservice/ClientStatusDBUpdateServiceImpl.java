package org.jobcenter.internalservice;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jobcenter.constants.ServerConfigKeyValues;
import org.jobcenter.constants.ServerCoreConstants;
import org.jobcenter.dao.NodeClientStatusDAO;
import org.jobcenter.dto.NodeClientStatusDTO;
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
	 * @param clientStatusUpdateRequest
	 * @param remoteHost
	 */
	@Override
	public void updateDBWithStatusFromClient( ClientStatusUpdateRequest clientStatusUpdateRequest, String remoteHost ) {


		if ( log.isDebugEnabled() ) {


			log.debug( "Updating Client status DB for client node name = " + clientStatusUpdateRequest.getNodeName() );
		}


		try {

			Integer nodeId = clientNodeNameCheck.getNodeIdForNodeName( clientStatusUpdateRequest.getNodeName() );

			if ( nodeId != null ) {

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


			}

		} catch ( Throwable t ) {


			log.warn( "Exception updating client status db, retry controlled elsewhere. client node name = " + clientStatusUpdateRequest.getNodeName() );

			throw new RuntimeException( t );
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
