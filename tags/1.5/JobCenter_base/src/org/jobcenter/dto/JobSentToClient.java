package org.jobcenter.dto;


import javax.xml.bind.annotation.XmlRootElement;


/**
 * DTO for job_sent_to_client table
 *
 */

@XmlRootElement(name = "jobSentToClient")
public class JobSentToClient {



	private int id;
	private int jobId;
	private int runId;
	private int jobParameterCountWhenJobSubmitted = -1;
	private int jobParameterCountWhenJobRetrieved = -1;
	private String xmlMarshalledJob;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public int getRunId() {
		return runId;
	}

	public void setRunId(int runId) {
		this.runId = runId;
	}

	public String getXmlMarshalledJob() {
		return xmlMarshalledJob;
	}

	public void setXmlMarshalledJob(String xmlMarshalledJob) {
		this.xmlMarshalledJob = xmlMarshalledJob;
	}

	public int getJobParameterCountWhenJobSubmitted() {
		return jobParameterCountWhenJobSubmitted;
	}

	public void setJobParameterCountWhenJobSubmitted(
			int jobParameterCountWhenJobSubmitted) {
		this.jobParameterCountWhenJobSubmitted = jobParameterCountWhenJobSubmitted;
	}

	public int getJobParameterCountWhenJobRetrieved() {
		return jobParameterCountWhenJobRetrieved;
	}

	public void setJobParameterCountWhenJobRetrieved(
			int jobParameterCountWhenJobRetrieved) {
		this.jobParameterCountWhenJobRetrieved = jobParameterCountWhenJobRetrieved;
	}
}
