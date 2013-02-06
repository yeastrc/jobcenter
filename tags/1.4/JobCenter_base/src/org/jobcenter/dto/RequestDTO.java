package org.jobcenter.dto;

import java.util.List;


/**
 * DTO for request table
 *
 */
public class RequestDTO {

	private Integer id;
	private Integer type;
	
	/**
	 *  not in database, used for GUI
	 */
	private List<Job> jobList;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 *  not in database, used for GUI
	 */
	public List<Job> getJobList() {
		return jobList;
	}

	/**
	 *  not in database, used for GUI
	 */
	public void setJobList(List<Job> jobList) {
		this.jobList = jobList;
	}

}
