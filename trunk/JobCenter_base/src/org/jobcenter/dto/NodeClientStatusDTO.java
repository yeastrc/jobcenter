package org.jobcenter.dto;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * DTO for node_client_status table
 *
 */

@XmlRootElement(name = "nodeClientStatusDTO")
public class NodeClientStatusDTO {

	private Integer id;

	private Integer nodeId;

	private Node node;

	private Date lastCheckinTime;

	private Date lateForNextCheckinTime;

	private Integer secondsUntilNextCheckin;
	
	private Boolean notificationSentThatClientLate;
	


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


}
