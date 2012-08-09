package org.jobcenter.request;


import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jobcenter.constants.ClientStatusUpdateTypeEnum;
import org.jobcenter.nondbdto.RunInProgressDTO;





@XmlRootElement(name = "clientStatusUpdateRequest")

public class ClientStatusUpdateRequest extends BaseRequest {

	//  nodeName is in BaseRequest


	private ClientStatusUpdateTypeEnum updateType;

	/**
	 * number of seconds until next client status update
	 */
	private int timeUntilNextClientStatusUpdate;

	private List<RunInProgressDTO> runsInProgress;


	private boolean clientAcceptsServerValueForTimeUntilNextClientStatusUpdate = false;

	/**
	 * Not currently used
	 */
	private long clientCurrentTime;




	@Override
	public String toString() {
		return "ClientStatusUpdateRequest [clientAcceptsServerValueForTimeUntilNextClientStatusUpdate="
				+ clientAcceptsServerValueForTimeUntilNextClientStatusUpdate
				+ ", clientCurrentTime="
				+ clientCurrentTime
				+ ", runsInProgress="
				+ runsInProgress
				+ ", timeUntilNextClientStatusUpdate="
				+ timeUntilNextClientStatusUpdate
				+ ", updateType="
				+ updateType + "]";
	}




	public long getClientCurrentTime() {
		return clientCurrentTime;
	}
	public void setClientCurrentTime(long clientCurrentTime) {
		this.clientCurrentTime = clientCurrentTime;
	}
	public List<RunInProgressDTO> getRunsInProgress() {
		return runsInProgress;
	}
	public void setRunsInProgress(List<RunInProgressDTO> runsInProgress) {
		this.runsInProgress = runsInProgress;
	}
	public ClientStatusUpdateTypeEnum getUpdateType() {
		return updateType;
	}
	public void setUpdateType(ClientStatusUpdateTypeEnum updateType) {
		this.updateType = updateType;
	}
	public int getTimeUntilNextClientStatusUpdate() {
		return timeUntilNextClientStatusUpdate;
	}
	public void setTimeUntilNextClientStatusUpdate(
			int timeUntilNextClientStatusUpdate) {
		this.timeUntilNextClientStatusUpdate = timeUntilNextClientStatusUpdate;
	}






	public boolean isClientAcceptsServerValueForTimeUntilNextClientStatusUpdate() {
		return clientAcceptsServerValueForTimeUntilNextClientStatusUpdate;
	}






	public void setClientAcceptsServerValueForTimeUntilNextClientStatusUpdate(
			boolean clientAcceptsServerValueForTimeUntilNextClientStatusUpdate) {
		this.clientAcceptsServerValueForTimeUntilNextClientStatusUpdate = clientAcceptsServerValueForTimeUntilNextClientStatusUpdate;
	}

}
