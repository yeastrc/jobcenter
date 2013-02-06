package org.jobcenter.dto;

public class RunMessageDTO {

	private Integer id;

	private int runId;

	private int type;

	private String message;

	private RunMsgTypeDTO runMsgType;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getRunId() {
		return runId;
	}

	public void setRunId(int runId) {
		this.runId = runId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public RunMsgTypeDTO getRunMsgType() {
		return runMsgType;
	}

	public void setRunMsgType(RunMsgTypeDTO runMsgType) {
		this.runMsgType = runMsgType;

		if ( runMsgType != null ) {

			type = runMsgType.getId();
		}
	}
}
