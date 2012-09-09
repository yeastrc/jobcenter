package org.jobcenter.response;

import javax.xml.bind.annotation.XmlRootElement;

import org.jobcenter.nondbdto.ClientIdentifierDTO;

/**
 * The response to a client status update
 *
 */
@XmlRootElement(name = "clientStatusUpdateResponse")

public class ClientStatusUpdateResponse extends BaseResponse {


	private Integer waitTimeForNextClientCheckinSpecifiedByServer;


	public Integer getWaitTimeForNextClientCheckinSpecifiedByServer() {
		return waitTimeForNextClientCheckinSpecifiedByServer;
	}

	public void setWaitTimeForNextClientCheckinSpecifiedByServer(
			Integer waitTimeForNextClientCheckinSpecifiedByServer) {
		this.waitTimeForNextClientCheckinSpecifiedByServer = waitTimeForNextClientCheckinSpecifiedByServer;
	}



}
