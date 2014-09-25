package org.jobcenter.nondbdto;

import org.jobcenter.constants.ClientStatusUpdateTypeEnum;
import org.jobcenter.nondbdto.ClientIdentifierDTO;

/**
 * Server side tracking of the clients that have connected
 *
 */
public class ClientConnectedDTO {

	private ClientIdentifierDTO clientIdentifierDTO;
	private String nodeName;

	private ClientStatusUpdateTypeEnum clientStatus;

	private String remoteIPAddress;

	private long startTime;

	private long lastStatusUpdatedTime;

	private long nextExpectedStatusUpdatedTime;


	@Override
	public String toString() {
		return "ClientConnectedDTO [clientIdentifierDTO=" + clientIdentifierDTO
				+ ", clientStatus=" + clientStatus + ", lastStatusUpdatedTime="
				+ lastStatusUpdatedTime + ", nextExpectedStatusUpdatedTime="
				+ nextExpectedStatusUpdatedTime + ", nodeName=" + nodeName
				+ ", remoteIPAddress=" + remoteIPAddress + ", startTime="
				+ startTime + "]";
	}


	public ClientIdentifierDTO getClientIdentifierDTO() {
		return clientIdentifierDTO;
	}
	public void setClientIdentifierDTO(ClientIdentifierDTO clientIdentifierDTO) {
		this.clientIdentifierDTO = clientIdentifierDTO;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getRemoteIPAddress() {
		return remoteIPAddress;
	}
	public void setRemoteIPAddress(String remoteIPAddress) {
		this.remoteIPAddress = remoteIPAddress;
	}
	public long getLastStatusUpdatedTime() {
		return lastStatusUpdatedTime;
	}
	public void setLastStatusUpdatedTime(long lastStatusUpdatedTime) {
		this.lastStatusUpdatedTime = lastStatusUpdatedTime;
	}
	public ClientStatusUpdateTypeEnum getClientStatus() {
		return clientStatus;
	}
	public void setClientStatus(ClientStatusUpdateTypeEnum clientStatus) {
		this.clientStatus = clientStatus;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getNextExpectedStatusUpdatedTime() {
		return nextExpectedStatusUpdatedTime;
	}
	public void setNextExpectedStatusUpdatedTime(long nextExpectedStatusUpdatedTime) {
		this.nextExpectedStatusUpdatedTime = nextExpectedStatusUpdatedTime;
	}

}
