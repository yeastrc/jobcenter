package org.jobcenter.response;

import javax.xml.bind.annotation.XmlRootElement;

import org.jobcenter.nondbdto.ClientIdentifierDTO;

/**
 * The response to a client startup
 *
 */
@XmlRootElement(name = "clientStartupResponse")

public class ClientStartupResponse extends BaseResponse {

	private ClientIdentifierDTO clientIdentifierDTO;

	private Integer waitTimeForNextClientCheckin;

	private long currentServerTime;

	public ClientIdentifierDTO getClientIdentifierDTO() {
		return clientIdentifierDTO;
	}

	public void setClientIdentifierDTO(ClientIdentifierDTO clientIdentifierDTO) {
		this.clientIdentifierDTO = clientIdentifierDTO;
	}


	public long getCurrentServerTime() {
		return currentServerTime;
	}

	public void setCurrentServerTime(long currentServerTime) {
		this.currentServerTime = currentServerTime;
	}

	public Integer getWaitTimeForNextClientCheckin() {
		return waitTimeForNextClientCheckin;
	}

	public void setWaitTimeForNextClientCheckin(Integer waitTimeForNextClientCheckin) {
		this.waitTimeForNextClientCheckin = waitTimeForNextClientCheckin;
	}


}
