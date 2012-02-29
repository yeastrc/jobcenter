package org.jobcenter.internalservice;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jobcenter.nondbdto.ClientConnectedDTO;

/**
 * This Singleton holds all the clients that have not connected.  The clients are purged off after a time
 *
 */

public class ClientsFailedToConnectTrackingServiceImpl implements ClientsFailedToConnectTrackingService {

	private static Logger log = Logger.getLogger(ClientsFailedToConnectTrackingServiceImpl.class);


	private List<ClientConnectedDTO> clientsFailedToConnectList = new LinkedList<ClientConnectedDTO>();

	
	/* (non-Javadoc)
	 * @see org.jobcenter.service.ClientsFailedToConnectTrackingService#retrieveClientsFailedToConnectList()
	 */
	@Override
	public synchronized List<ClientConnectedDTO> retrieveClientsFailedToConnectList() {
		
		return new ArrayList<ClientConnectedDTO>( clientsFailedToConnectList );
	}
	

	/* (non-Javadoc)
	 * @see org.jobcenter.service.ClientsFailedToConnectTrackingService#addFailedNodeName(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized void addFailedNodeName( String nodeName, String remoteIPAddress ) {

		long now = System.currentTimeMillis();
		
		//  purge any old entries greater than x days

		long purgeBeforeTime = now - ( 1 /* days */ * 24 /* hours */ * 60 /*minutes */ * 60 /* seconds */ * 1000 /* milliseconds */ );


		boolean found = false;
		

		Iterator<ClientConnectedDTO> listIter = clientsFailedToConnectList.iterator();

		while ( listIter.hasNext() ) {

			ClientConnectedDTO clientConnectedDTO = listIter.next();

			if ( clientConnectedDTO.getNodeName().equals( nodeName ) && clientConnectedDTO.getRemoteIPAddress().equals( remoteIPAddress ) ) {
				
				found = true;
				
				clientConnectedDTO.setLastStatusUpdatedTime( now );
				
			} else {

				if ( clientConnectedDTO.getLastStatusUpdatedTime() < purgeBeforeTime ) {

					listIter.remove();
				}
			}
		}
		
		
		if ( ! found ) {
			
			//  restrict list to 50 entries and arbitrarily remove first entry since that is fastest in a linked list.
			if ( clientsFailedToConnectList.size() > 50 ) {
				
				clientsFailedToConnectList.remove( 0 );
			}
			

			ClientConnectedDTO clientConnectedDTO = new ClientConnectedDTO();
			
			clientConnectedDTO.setNodeName( nodeName );
			
			clientConnectedDTO.setRemoteIPAddress( remoteIPAddress );
			clientConnectedDTO.setLastStatusUpdatedTime( now );

			clientsFailedToConnectList.add( clientConnectedDTO );
			
		}

	}
}
