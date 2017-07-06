package org.jobcenter.webservice_response_objects;

import java.util.List;

import org.jobcenter.dto.RunMessageDTO;

/**
 * Sent to JS code on browser
 *
 */
public class RunForGUI {
	
	private int runId;
	private String status;
	private String startDate;
	private String endDate;
	private String node;
	private List<RunMessageDTO> runMessageList;
	
	public int getRunId() {
		return runId;
	}
	public void setRunId(int runId) {
		this.runId = runId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public List<RunMessageDTO> getRunMessageList() {
		return runMessageList;
	}
	public void setRunMessageList(List<RunMessageDTO> runMessageList) {
		this.runMessageList = runMessageList;
	}
	
}
