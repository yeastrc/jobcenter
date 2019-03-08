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
	
	//  Start and End time of last GetJob call
	private long lastGetJobStartProcessingTime;
	private long lastGetJobEndProcessingTime;
	
	private long tracking_GetJobMaxProcessingTimeSinceLastGUIQuery; // For tracking the next max value (cleared when queried) 
	private long display_GetJobMaxProcessingTimeSinceLastGUIQuery;  // For display of current max value (copied from Tracking when queried)
	
	/**
	 * Set tracking_GetJobMaxProcessingTimeSinceLastGUIQuery to current_GetJobProcessingTime if current_GetJobProcessingTime is larger
	 * @param current_GetJobProcessingTime
	 */
	public synchronized void updateTracking_GetJobMaxProcessingTimeSinceLastGUIQuery( long current_GetJobProcessingTime ) {
		if ( current_GetJobProcessingTime > tracking_GetJobMaxProcessingTimeSinceLastGUIQuery ) {
			tracking_GetJobMaxProcessingTimeSinceLastGUIQuery = current_GetJobProcessingTime;
		}
	}

	/**
	 * Copy tracking_GetJobMaxProcessingTimeSinceLastGUIQuery to display_GetJobMaxProcessingTimeSinceLastGUIQuery
	 * and set tracking_GetJobMaxProcessingTimeSinceLastGUIQuery to zero
	 */
	public synchronized void copyTrackingToDisplayAndClearTracking_GetJobMaxProcessingTimeSinceLastGUIQuery() {
		display_GetJobMaxProcessingTimeSinceLastGUIQuery = tracking_GetJobMaxProcessingTimeSinceLastGUIQuery;
		tracking_GetJobMaxProcessingTimeSinceLastGUIQuery = 0;
	}
	
	@Override
	public String toString() {
		return "ClientConnectedDTO [clientIdentifierDTO=" + clientIdentifierDTO + ", nodeName=" + nodeName
				+ ", clientStatus=" + clientStatus + ", remoteIPAddress=" + remoteIPAddress + ", startTime=" + startTime
				+ ", lastStatusUpdatedTime=" + lastStatusUpdatedTime + ", nextExpectedStatusUpdatedTime="
				+ nextExpectedStatusUpdatedTime + ", lastGetJobStartProcessingTime=" + lastGetJobStartProcessingTime
				+ ", lastGetJobEndProcessingTime=" + lastGetJobEndProcessingTime
				+ ", tracking_GetJobMaxProcessingTimeSinceLastGUIQuery="
				+ tracking_GetJobMaxProcessingTimeSinceLastGUIQuery
				+ ", display_GetJobMaxProcessingTimeSinceLastGUIQuery="
				+ display_GetJobMaxProcessingTimeSinceLastGUIQuery + "]";
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
	public long getLastGetJobStartProcessingTime() {
		return lastGetJobStartProcessingTime;
	}
	public void setLastGetJobStartProcessingTime(long lastGetJobStartProcessingTime) {
		this.lastGetJobStartProcessingTime = lastGetJobStartProcessingTime;
	}
	public long getLastGetJobEndProcessingTime() {
		return lastGetJobEndProcessingTime;
	}
	public void setLastGetJobEndProcessingTime(long lastGetJobEndProcessingTime) {
		this.lastGetJobEndProcessingTime = lastGetJobEndProcessingTime;
	}
	public long getTracking_GetJobMaxProcessingTimeSinceLastGUIQuery() {
		return tracking_GetJobMaxProcessingTimeSinceLastGUIQuery;
	}
	public void setTracking_GetJobMaxProcessingTimeSinceLastGUIQuery(
			long tracking_GetJobMaxProcessingTimeSinceLastGUIQuery) {
		this.tracking_GetJobMaxProcessingTimeSinceLastGUIQuery = tracking_GetJobMaxProcessingTimeSinceLastGUIQuery;
	}
	public long getDisplay_GetJobMaxProcessingTimeSinceLastGUIQuery() {
		return display_GetJobMaxProcessingTimeSinceLastGUIQuery;
	}
	public void setDisplay_GetJobMaxProcessingTimeSinceLastGUIQuery(long display_GetJobMaxProcessingTimeSinceLastGUIQuery) {
		this.display_GetJobMaxProcessingTimeSinceLastGUIQuery = display_GetJobMaxProcessingTimeSinceLastGUIQuery;
	}

}
