package org.jobcenter.dto;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * DTO for node_client_status table
 *
 */

@XmlRootElement(name = "nodeClientStatusDTO")
public class NodeClientStatusDTO implements Cloneable {


	private Integer id;

	private Integer nodeId;

	private Node node;

	private Boolean clientStarted;

	private Date lastCheckinTime;

	private Date nextCheckinTime;

	private Date lateForNextCheckinTime;

	private Integer secondsUntilNextCheckin;

	private Boolean notificationSentThatClientLate;

	
	
	public Object clone() {
		
		NodeClientStatusDTO clone = new NodeClientStatusDTO();
		
		clone.id = this.id;
		clone.nodeId = this.nodeId;
		clone.node = this.node;
		clone.clientStarted = this.clientStarted;
		clone.lastCheckinTime = this.lastCheckinTime;
		clone.nextCheckinTime = this.nextCheckinTime;
		clone.lateForNextCheckinTime = this.lateForNextCheckinTime;
		clone.secondsUntilNextCheckin = this.secondsUntilNextCheckin;
		clone.notificationSentThatClientLate = this.notificationSentThatClientLate;
		clone.dbRecordVersionNumber = this.dbRecordVersionNumber;
		clone.checkinIsLate = this.checkinIsLate;
		
		return clone;
	}
	


	/**
	 * Current database record version number, for optimistic locking version tracking
	 */
	private Integer dbRecordVersionNumber;


	//  for web interface

	private boolean checkinIsLate;

	private List<Job> runningJobs;


	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getNodeId() {
		return nodeId;
	}
	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}
	public Date getLastCheckinTime() {
		return lastCheckinTime;
	}
	public void setLastCheckinTime(Date lastCheckinTime) {
		this.lastCheckinTime = lastCheckinTime;
	}
	public Date getLateForNextCheckinTime() {
		return lateForNextCheckinTime;
	}
	public void setLateForNextCheckinTime(Date lateForNextCheckinTime) {
		this.lateForNextCheckinTime = lateForNextCheckinTime;
	}
	public Integer getSecondsUntilNextCheckin() {
		return secondsUntilNextCheckin;
	}
	public void setSecondsUntilNextCheckin(Integer secondsUntilNextCheckin) {
		this.secondsUntilNextCheckin = secondsUntilNextCheckin;
	}
	public Integer getDbRecordVersionNumber() {
		return dbRecordVersionNumber;
	}
	public void setDbRecordVersionNumber(Integer dbRecordVersionNumber) {
		this.dbRecordVersionNumber = dbRecordVersionNumber;
	}
	public boolean isCheckinIsLate() {
		return checkinIsLate;
	}
	public void setCheckinIsLate(boolean checkinIsLate) {
		this.checkinIsLate = checkinIsLate;
	}
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	public List<Job> getRunningJobs() {
		return runningJobs;
	}
	public void setRunningJobs(List<Job> runningJobs) {
		this.runningJobs = runningJobs;
	}
	public Boolean getNotificationSentThatClientLate() {
		return notificationSentThatClientLate;
	}
	public void setNotificationSentThatClientLate(
			Boolean notificationSentThatClientLate) {
		this.notificationSentThatClientLate = notificationSentThatClientLate;
	}
	public Date getNextCheckinTime() {
		return nextCheckinTime;
	}
	public void setNextCheckinTime(Date nextCheckinTime) {
		this.nextCheckinTime = nextCheckinTime;
	}
	public Boolean getClientStarted() {
		return clientStarted;
	}
	public void setClientStarted(Boolean clientStarted) {
		this.clientStarted = clientStarted;
	}


}
