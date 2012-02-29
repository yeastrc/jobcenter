package org.jobcenter.internalservice;

import java.util.Set;

import org.apache.log4j.Logger;
import org.jobcenter.dao.NodeDAO;
import org.jobcenter.dto.Node;
import org.jobcenter.dto.NodeAccessRule;
import org.jobcenter.response.BaseResponse;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    WARNING   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//The only way to get proper roll backs ( managed by Spring ) is to only use un-checked exceptions.
//
//The best way to make sure there are no checked exceptions is to have no "throws" on any of the methods.


//@Transactional causes Spring to surround calls to methods in this class with a database transaction.
//        Spring will roll back the transaction if a un-checked exception ( extended from RuntimeException ) is thrown.
//                 Otherwise it commits the transaction.


/**
*
*
*/
@Transactional ( propagation = Propagation.REQUIRED, readOnly = false )

public class ClientNodeNameCheckImpl implements ClientNodeNameCheck {

	private static Logger log = Logger.getLogger(ClientNodeNameCheckImpl.class);

	private ClientsFailedToConnectTrackingService clientsFailedToConnectTrackingService;


	public ClientsFailedToConnectTrackingService getClientsFailedToConnectTrackingService() {
		return clientsFailedToConnectTrackingService;
	}
	public void setClientsFailedToConnectTrackingService(
			ClientsFailedToConnectTrackingService clientsFailedToConnectTrackingService) {
		this.clientsFailedToConnectTrackingService = clientsFailedToConnectTrackingService;
	}


	private NodeDAO nodeDAO;


	public NodeDAO getNodeDAO() {
		return nodeDAO;
	}
	public void setNodeDAO(NodeDAO nodeDAO) {
		this.nodeDAO = nodeDAO;
	}


	/**
	 * If the nodeName and remoteHost are not found, the baseResponse is updated accordingly and false is returned
	 * @param baseResponse - response object to update if return false
	 * @param nodeName
	 * @param remoteHost - network address calling from
	 * @return true if record found for nodeName and remoteHost, false otherwise
	 * @throws Exception
	 */
	@Override
	public boolean validateNodeNameAndNetworkAddress( BaseResponse baseResponse, String nodeName, String remoteHost ){

		final String method = "validateNodeNameAndNetworkAddress";

		if ( log.isDebugEnabled() ) {

			log.debug( method + ":  nodeName = |" + nodeName + "|, remoteHost = |" + remoteHost + "|."  );
		}

		//  always pass the IP address to the client that the server passed to it

		baseResponse.setClientIPAddressAtServer( remoteHost );


		//  always pass the node name to the client that the server passed to it

		baseResponse.setClientNodeName( nodeName );

//		Integer nodeId = getNodeIdForNodeNameAndNetworkAddress( nodeName, remoteHost );


		boolean nodeValidForIPAddress = false;

		Node node = nodeDAO.findByName( nodeName );

		if ( node != null ) {

			Set<NodeAccessRule> nodeAccessRuleSet = node.getNodeAccessRuleSet();

			for ( NodeAccessRule nodeAccessRule : nodeAccessRuleSet ) {

				String networkAddress = nodeAccessRule.getNetworkAddress().trim();

				if ( networkAddress.endsWith( "*" ) ) {

					String networkAddressWithoutAsterisk = networkAddress.substring( 0, networkAddress.length() - 1 );
					if ( remoteHost.startsWith( networkAddressWithoutAsterisk ) ) {

						if ( log.isDebugEnabled() ) {

							log.debug( "Validating node IP address, node ACCEPTED.  Node name = '" + nodeName
									+ "', Node IP address = '" + remoteHost
									+ "', Node access rule ip address = '" + networkAddress + "'." );
						}

						nodeValidForIPAddress = true;

						break;
					}
				} else {

					if ( remoteHost.equals( networkAddress ) ) {

						if ( log.isDebugEnabled() ) {

							log.debug( "Validating node IP address, node ACCEPTED.  Node name = '" + nodeName
									+ "', Node IP address = '" + remoteHost
									+ "', Node access rule ip address = '" + networkAddress + "'." );
						}

						nodeValidForIPAddress = true;

						break;
					}
				}

			}
		}

		if ( ! nodeValidForIPAddress ) {

			if ( log.isInfoEnabled() ) {

				log.info( method + ":  Node Name not found or valid for networkaddress:  nodeName = |" + nodeName + "|, remoteHost = |" + remoteHost + "|."  );
			}

			clientsFailedToConnectTrackingService.addFailedNodeName( nodeName, remoteHost );


			baseResponse.setErrorResponse( true );

			baseResponse.setErrorCode( BaseResponse.ERROR_CODE_NODE_NAME_INVALID_FOR_NETWORK_ADDRESS );

			return false;
		}

		return true;
	}



	/**
	 * If the nodeName and remoteHost are not found, the baseResponse is updated accordingly and false is returned
	 * @param baseResponse - response object to update if return false
	 * @param nodeName
	 * @param remoteHost - network address calling from
	 * @return true if record found for nodeName and remoteHost, false otherwise
	 * @throws Exception
	 */
	@Override
	public Integer getNodeIdForNodeName( String nodeName ){

		final String method = "getNodeIdForNodeName";

		if ( log.isDebugEnabled() ) {

			log.debug( method + ":  nodeName = |" + nodeName + "|."  );
		}


		Node node = nodeDAO.findByName( nodeName );

		if ( node == null ) {

			return null;
		}

		return node.getId();
	}
}
