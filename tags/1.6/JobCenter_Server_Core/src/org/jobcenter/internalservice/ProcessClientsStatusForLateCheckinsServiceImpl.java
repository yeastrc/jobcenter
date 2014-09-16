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
					+ ", client version = " + client.getDbRecordVersionNumber()
					+ ".  Retry count exceeded.  Exception: " + t.toString();

					log.error( msg, t );

					throw new RuntimeException( msg, t );
				}

				try {

					try {

					Thread.sleep( 100 );

					} catch ( Throwable tt ) {


					}

					String msgUpdateException = "Update of Client status DB failed for client node id = " + client.getNodeId()
					+ ", client version = " + client.getDbRecordVersionNumber()
					+ ".  Update will be RETRIED.  retryCount = " + retryCount + ".   Exception: " + t.toString();

					log.warn( msgUpdateException, t );

					NodeClientStatusDTO nodeClientStatusDTOFromDB = nodeClientStatusDAO.findById( client.getId() );

					if ( nodeClientStatusDTOFromDB != null ) {

						//  Update client variable with new entry from database

						client = nodeClientStatusDTOFromDB;

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
