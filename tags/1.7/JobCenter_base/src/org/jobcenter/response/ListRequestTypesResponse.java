package org.jobcenter.response;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.jobcenter.dto.*;

/**
 * The response to a list request types request
 *
 */
@XmlRootElement(name = "listRequestTypesResponse")

public class ListRequestTypesResponse extends BaseResponse {

	private List<RequestTypeDTO> requestTypes;

	public List<RequestTypeDTO> getRequestTypes() {
		return requestTypes;
	}

	public void setRequestTypes(List<RequestTypeDTO> requestTypes) {
		this.requestTypes = requestTypes;
	}


}
