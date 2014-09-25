package org.jobcenter.response;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.jobcenter.dto.RequestDTO;

/**
 * The response to a list requests request
 *
 */
@XmlRootElement(name = "listRequestsResponse")

public class ListRequestsResponse extends BaseResponse {

	private List<RequestDTO> requests;

	public List<RequestDTO> getRequests() {
		return requests;
	}
	public void setRequests(List<RequestDTO> requests) {
		this.requests = requests;
	}


}
