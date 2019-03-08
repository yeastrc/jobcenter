package org.jobcenter.internalservice;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jobcenter.constants.ClientStatusUpdateTypeEnum;
import org.jobcenter.nondbdto.ClientConnectedDTO;
import org.jobcenter.nondbdto.ClientIdentifierDTO;

/**
 * This Singleton holds all the clients that have connected.  The clients are purged off after a time
 *
 */

public class ClientsConnectedTrackingServiceImpl implements ClientsConnectedTrackingService {

	private static Logger log = Logger.getLogger(ClientsConnectedTrackingServiceImpl.class);


	private List<ClientConnectedDTO> clientsConnectedList = new LinkedList<ClientConnectedDTO>();

	
	/**
	 * @return
	 */
	@Override
	public synchronized List<ClientConnectedDTO> retrieveClientsConnectedList() {
		
		//  First call copyTrackingToDisplayAndClearTracking_GetJobMaxProcessingTimeSinceLastGUIQuery() on each element
		
		if ( ! clientsConnectedList.isEmpty() ) {
			for ( ClientConnectedDTO item : clientsConnectedList ) {
				item.copyTrackingToDisplayAndClearTracking_GetJobMaxProcessingTimeSinceLastGUIQuery();
			}
		}
		
		return clientsConnectedList;
	}
	

	/**
	 * @param clientConnectedDTO
	 */
	@Override
	public synchronized void addClient( ClientConnectedDTO clientConnectedDTO ) {

		//  purge any old entries greater than x days

		long purgeBeforeTime = System.currentTimeMillis() - ( 5 /* days */ * 24 /* hours */ * 60 /*minutes */ * 60 /* seconds */ * 1000 /* milliseconds */ );


		Iterator<ClientConnectedDTO> listIter = clientsConnectedList.iterator();

		while ( listIter.hasNext() ) {

			ClientConnectedDTO clientConnectedDTOInList = listIter.next();

			if ( clientConnectedDTOInList.getLastStatusUpdatedTime() < purgeBeforeTime ) {

				listIter.remove();
			}
		}
		
		
		//  restrict list to 300 entries and arbitrarily remove first entry since that is fastest in a linked list.
		if ( clientsConnectedList.size() > 300 ) {
			
			clientsConnectedList.remove( 0 );
		}
		

		clientsConnectedList.add( clientConnectedDTO );

	}

	/**
	 * @param clientIdentifierDTO
	 * @param updateType
	 */
	@Override
	public synchronized void updateClientStatusAndInfo( ClientIdentifierDTO clientIdentifierDTO, ClientStatusUpdateTypeEnum updateType ) {

		final String method = "updateClientStatusAndInfo";


		if ( clientIdentifierDTO == null ) {

			throw new IllegalArgumentException( "Parameter clientIdentifierDTO cannot == null" );
		}

		log.debug( method + " called, clientConnectedList current contents: " );

		for ( ClientConnectedDTO clientConnectedDTO: clientsConnectedList ) {

				log.debug( method + " called, clientConnectedDTO = " + clientConnectedDTO );

		}


		log.info( method + " called, clientConnectedList Item to update" );



		for ( ClientConnectedDTO clientConnectedDTO: clientsConnectedList ) {

			if ( clientIdentifierDTO.equals( clientConnectedDTO.getClientIdentifierDTO() ) ) {

				clientConnectedDTO.setClientStatus( updateType );

				clientConnectedDTO.setLastStatusUpdatedTime( System.currentTimeMillis() );

				log.info( method + " called, clientConnectedDTO updated = " + clientConnectedDTO );

				return;
			}
		}
	}

	/**
	 * @param clientIdentifierDTO
	 */
	@Override
	public synchronized void updateClientGetJobStartTimestamp( ClientIdentifierDTO clientIdentifierDTO ) {

		final String method = "updateClientGetJobStartTimestamp";
		
		if ( clientIdentifierDTO == null ) {
			throw new IllegalArgumentException( method + "Parameter clientIdentifierDTO cannot == null" );
		}

		if ( log.isDebugEnabled() ) {
			log.debug( method + " called, clientConnectedList current contents: " );
			for ( ClientConnectedDTO clientConnectedDTO: clientsConnectedList ) {
					log.debug( method + " called, clientConnectedDTO = " + clientConnectedDTO );
			}
		}
		if ( log.isInfoEnabled() ) {
			log.info( method + " called, clientConnectedList Item to update" );
		}

		//  Find entry in list to update and update it
		for ( ClientConnectedDTO clientConnectedDTO: clientsConnectedList ) {

			if ( clientIdentifierDTO.equals( clientConnectedDTO.getClientIdentifierDTO() ) ) {

				clientConnectedDTO.setLastGetJobStartProcessingTime( System.currentTimeMillis() );

				if ( log.isInfoEnabled() ) {
					log.info( method + " called, clientConnectedDTO updated = " + clientConnectedDTO );
				}
				return;
			}
		}
	}

	/**
	 * @param clientIdentifierDTO
	 */
	@Override
	public synchronized void updateClientGetJobEndTimestamp( ClientIdentifierDTO clientIdentifierDTO ) {

		final String method = "updateClientGetJobEndTimestamp";

		if ( clientIdentifierDTO == null ) {
			throw new IllegalArgumentException( method + "Parameter clientIdentifierDTO cannot == null" );
		}

		if ( log.isDebugEnabled() ) {
			log.debug( method + " called, clientConnectedList current contents: " );
			for ( ClientConnectedDTO clientConnectedDTO: clientsConnectedList ) {
					log.debug( method + " called, clientConnectedDTO = " + clientConnectedDTO );
			}
		}
		if ( log.isInfoEnabled() ) {
			log.info( method + " called, clientConnectedList Item to update" );
		}

		//  Find entry in list to update and update it
		for ( ClientConnectedDTO clientConnectedDTO: clientsConnectedList ) {

			if ( clientIdentifierDTO.equals( clientConnectedDTO.getClientIdentifierDTO() ) ) {

				clientConnectedDTO.setLastGetJobEndProcessingTime( System.currentTimeMillis() );

				if ( log.isInfoEnabled() ) {
					log.info( method + " called, clientConnectedDTO updated = " + clientConnectedDTO );
				}
				return;
			}
		}
	}
	

	/**
	 * @param clientIdentifierDTO
	 */
	@Override
	public synchronized void updateClient_Tracking_GetJobMaxProcessingTimeSinceLastGUIQuery( long current_GetJobProcessingTime, ClientIdentifierDTO clientIdentifierDTO ) {

		final String method = "updateClient_Tracking_GetJobMaxProcessingTimeSinceLastGUIQuery";

		if ( clientIdentifierDTO == null ) {
			throw new IllegalArgumentException( method + "Parameter clientIdentifierDTO cannot == null" );
		}

		if ( log.isDebugEnabled() ) {
			log.debug( method + " called, clientConnectedList current contents: " );
			for ( ClientConnectedDTO clientConnectedDTO: clientsConnectedList ) {
					log.debug( method + " called, clientConnectedDTO = " + clientConnectedDTO );
			}
		}
		if ( log.isInfoEnabled() ) {
			log.info( method + " called, clientConnectedList Item to update" );
		}

		//  Find entry in list to update and update it
		for ( ClientConnectedDTO clientConnectedDTO: clientsConnectedList ) {

			if ( clientIdentifierDTO.equals( clientConnectedDTO.getClientIdentifierDTO() ) ) {

				clientConnectedDTO.updateTracking_GetJobMaxProcessingTimeSinceLastGUIQuery( current_GetJobProcessingTime );

				if ( log.isInfoEnabled() ) {
					log.info( method + " called, clientConnectedDTO updated = " + clientConnectedDTO );
				}
				return;
			}
		}
	}
	
	
}
