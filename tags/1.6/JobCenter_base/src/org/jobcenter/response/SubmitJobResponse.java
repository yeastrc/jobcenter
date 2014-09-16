package org.jobcenter.response;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The response to a submit job request
 *
 */
@XmlRootElement(name = "submitJobResponse")

public class SubmitJobResponse extends BaseResponse {

	/**
	 * assigned requestId, or one passed in if provided
	 */
	private int requestId;

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

}
