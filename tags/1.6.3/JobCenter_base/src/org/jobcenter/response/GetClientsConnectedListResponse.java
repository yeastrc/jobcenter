package org.jobcenter.response;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.jobcenter.dto.*;
import org.jobcenter.nondbdto.ClientConnectedDTO;

/**
 * The response to a list request types request
 *
 */
@XmlRootElement(name = "getClientsConnectedListResponse")

public class GetClientsConnectedListResponse extends BaseResponse {


	private List<ClientConnectedDTO> clientConnectedDTOList;

	public List<ClientConnectedDTO> getClientConnectedDTOList() {
		return clientConnectedDTOList;
	}

	public void setClientConnectedDTOList(
			List<ClientConnectedDTO> clientConnectedDTOList) {
		this.clientConnectedDTOList = clientConnectedDTOList;
	}


}
