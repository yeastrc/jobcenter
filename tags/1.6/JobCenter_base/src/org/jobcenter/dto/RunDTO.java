package org.jobcenter.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

/**
 * A record in the run table
 *
 */
public class RunDTO {

	private Integer id;

//	private int jobId;
//	private int nodeId;
//	private int statusId;

	private Integer jobId = new Integer(0);
	private Integer nodeId = new Integer(0);
	private Integer statusId = new Integer(0);

	private Date startDate;
	private Date endDate;


	//  Not loaded by Hibernate since it doesn't map directly to the table
	private Map<String, String> runOutputParams;



	private List<RunMessageDTO> runMessages;

	private Set<RunMessageDTO> runMessagesSet;


	@XmlTransient  //
	public Set<RunMessageDTO> getRunMessagesSet() {
		return runMessagesSet;
	}

	public void setRunMessagesSet(Set<RunMessageDTO> runMessagesSet) {
		this.runMessagesSet = runMessagesSet;

		if ( runMessagesSet != null ) {

			runMessages = new ArrayList<RunMessageDTO>();

			for ( RunMessageDTO msg : runMessagesSet ) {

				runMessages.add( msg );
			}
		}
	}


	public static class ReverseSortByStartDateComparator implements Comparator<RunDTO> {

		@Override
		public int compare(RunDTO o1, RunDTO o2) {

			if ( o1 == null || o2 == null ) {
				return 0;
			}

			// "-" in front since reverse
			return  -   o1.getStartDate().compareTo( o2.getStartDate() );
		}




	}




	private Node node;


	private StatusDTO status;


	private Job job;


	@XmlTransient  //  circular reference so mark transient for XML binding
	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}


	public StatusDTO getStatus() {
		return status;
	}
	public void setStatus(StatusDTO status) {
		this.status = status;

		if ( status != null ) {

			statusId = status.getId();
		}
	}




	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getJobId() {
		return jobId;
	}
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}
	public Integer getNodeId() {
		return nodeId;
	}
	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}
	public Integer getStatusId() {
		return statusId;
	}
	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}

	public List<RunMessageDTO> getRunMessages() {
		return runMessages;
	}
	public void setRunMessages(List<RunMessageDTO> runMessages) {
		this.runMessages = runMessages;
	}

	public Map<String, String> getRunOutputParams() {
		return runOutputParams;
	}

	public void setRunOutputParams(Map<String, String> runOutputParams) {
		this.runOutputParams = runOutputParams;
	}
}
