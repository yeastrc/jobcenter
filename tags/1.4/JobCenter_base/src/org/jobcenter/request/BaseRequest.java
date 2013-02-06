package org.jobcenter.request;

import org.jobcenter.nondbdto.ClientIdentifierDTO;

public abstract class BaseRequest {

	private String nodeName;

	/**
	 * The identifier assigned to the client by the server when the client starts up.
	 */
	private ClientIdentifierDTO clientIdentifierDTO;

	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	/**
	 * The identifier assigned to the client by the server when the client starts up.
	 * @return
	 */
	public ClientIdentifierDTO getClientIdentifierDTO() {
		return clientIdentifierDTO;
	}
	/**
	 * The identifier assigned to the client by the server when the client starts up.
	 * @param clientIdentifierDTO
	 */
	public void setClientIdentifierDTO(ClientIdentifierDTO clientIdentifierDTO) {
		this.clientIdentifierDTO = clientIdentifierDTO;
	}

}
