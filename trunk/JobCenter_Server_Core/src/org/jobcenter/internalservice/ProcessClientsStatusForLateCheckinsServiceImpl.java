package org.jobcenter.internalservice;

import java.util.List;

import org.jobcenter.dao.NodeClientStatusDAO;
import org.jobcenter.dto.*;


import org.apache.log4j.Logger;


/**
 * Database Transaction managed at lower level
 *
 */
public class ProcessClientsStatusForLateCheckinsServiceImpl implements ProcessClientsStatusForLateCheckinsService {




	private static Logger log = Logger.getLogger(ProcessClientsStatusForLateCheckinsServiceImpl.class);



	//  Service

	private ClientStatusDBUpdateService clientStatusDBUpdateService;
	private ClientsLateCheckinRetrieveAndSendEmailService clientsLateCheckinRetrieveAndSendEmailService;


	//  DAO

	private NodeClientStatusDAO nodeClientStatusDAO;





	/* (non-Javadoc)
	 * @see org.jobcenter.internalservice.ProcessClientsStatusForLateCheckinsService#processClientsStatusForLateCheckins()
	 */
	public  void processClientsStatusForLateCheckins(  )
	{

		List<NodeClientStatusDTO> clientsThatAreLate = clientsLateCheckinRetrieveAndSendEmailService.sendClientsCheckinLateNotification();

		if ( clientsThatAreLate != null && ! clientsThatAreLate.isEmpty() ) {

			for ( NodeClientStatusDTO client : clientsThatAreLate ) {


				updateDBWithStatus( client );
			}
		}
	}



	/**
	 * @param clientStatusUpdateRequest
	 * @param remoteHost
	 */
	private void updateDBWithStatus( NodeClientStatusDTO client ) {

		if ( log.isDebugEnabled() ) {

			log.debug( "Updating Client status DB for client node id = " + client.getNodeId() );
		}

		boolean updatedEntry = false;

		int retryCount = 0;

		while ( ! updatedEntry ) {

			try {
				updateClientValues( client );

				clientStatusDBUpdateService.updateDB( client );

				updatedEntry = true;

				if ( log.isDebugEnabled() ) {

					log.debug( "Update of Client status DB Successful for client node id = " + client.getNodeId() );
				}

			} catch ( Throwable t ) {

				retryCount++;

				if ( retryCount > 5 ) {

					String msg = "Update of Client status DB FAILED for client node id = " + client.getNodeId()
					+ ".  Retry count exceeded.  Exception: " + t.toString();

					log.error( msg, t );

					throw new RuntimeException( msg, t );
				}

				try {

					List nodeClientStatusDTOList = nodeClientStatusDAO.findByNodeId( client.getId() );

					if ( nodeClientStatusDTOList != null && ! nodeClientStatusDTOList.isEmpty() ) {

						//  Update existing entry

						client = (NodeClientStatusDTO) nodeClientStatusDTOList.get( 0 );

						updateClientValues( client );
					} else {

						String msg =  "Trying to retry update but unable to retrieve NodeClientStatusDTO to update( record not found), id = "
							+ client.getId() + ", original exception = " + t.toString();

						log.error( msg, t );
						throw new RuntimeException( msg, t );
					}


				} catch ( Throwable tt ) {

					String msg =  "Trying to retry update but unable to retrieve NodeClientStatusDTO to update (exception retrieving record), id = "
						+ client.getId() + ", original exception = " + t.toString();

					log.error( msg, t );
					throw new RuntimeException( msg, t );

				}
			}
		}
	}


	/**
	 * @param client
	 */
	private void updateClientValues ( NodeClientStatusDTO client ) {

		client.setNotificationSentThatClientLate( true );
	}


	public ClientStatusDBUpdateService getClientStatusDBUpdateService() {
		return clientStatusDBUpdateService;
	}
	public void setClientStatusDBUpdateService(
			ClientStatusDBUpdateService clientStatusDBUpdateService) {
		this.clientStatusDBUpdateService = clientStatusDBUpdateService;
	}
	public ClientsLateCheckinRetrieveAndSendEmailService getClientsLateCheckinRetrieveAndSendEmailService() {
		return clientsLateCheckinRetrieveAndSendEmailService;
	}
	public void setClientsLateCheckinRetrieveAndSendEmailService(
			ClientsLateCheckinRetrieveAndSendEmailService clientsLateCheckinRetrieveAndSendEmailService) {
		this.clientsLateCheckinRetrieveAndSendEmailService = clientsLateCheckinRetrieveAndSendEmailService;
	}
	public NodeClientStatusDAO getNodeClientStatusDAO() {
		return nodeClientStatusDAO;
	}
	public void setNodeClientStatusDAO(NodeClientStatusDAO nodeClientStatusDAO) {
		this.nodeClientStatusDAO = nodeClientStatusDAO;
	}



}
