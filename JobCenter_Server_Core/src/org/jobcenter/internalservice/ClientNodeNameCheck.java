package org.jobcenter.internalservice;

import org.jobcenter.response.BaseResponse;


public interface ClientNodeNameCheck {


	/**
	 * If the nodeName and remoteHost are not found, the baseResponse is updated accordingly and false is returned
	 * @param baseResponse - response object to update if return false
	 * @param nodeName
	 * @param remoteHost - network address calling from
	 * @return true if record found for nodeName and remoteHost, false otherwise
	 */
	public boolean validateNodeNameAndNetworkAddress( BaseResponse baseResponse, String nodeName, String remoteHost );


	/**
	 * If the nodeName is found, return the node id, otherwise return null
	 * @param nodeName
	 * @return return the node id, otherwise return null
	 */
	public Integer getNodeIdForNodeName( String nodeName );


}
