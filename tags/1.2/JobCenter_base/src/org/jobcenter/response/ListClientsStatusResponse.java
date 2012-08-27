package org.jobcenter.response;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.jobcenter.dto.NodeClientStatusDTO;

/**
 * The response to a list clients request
 *
 */
@XmlRootElement(name = "listClientsStatusResponse")

public class ListClientsStatusResponse extends BaseResponse {

	private List<NodeClientStatusDTO> clients;

	public List<NodeClientStatusDTO> getClients() {
		return clients;
	}

	public void setClients(List<NodeClientStatusDTO> clients) {
		this.clients = clients;
	}

}
