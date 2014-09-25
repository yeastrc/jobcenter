package org.jobcenter.nondbdto;

import java.util.List;
import java.util.Map;

/**
 * a submitted job and it's dependencies
 *
 */
public class SubmittedJobAndDependencies {

	private String jobTypeName;
	private Integer priority;
	private Map<String, String> jobParameters;
	
	private List<Integer> dependencyJobListPositions;
	
	
	public String getJobTypeName() {
		return jobTypeName;
	}
	public void setJobTypeName(String jobTypeName) {
		this.jobTypeName = jobTypeName;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public Map<String, String> getJobParameters() {
		return jobParameters;
	}
	public void setJobParameters(Map<String, String> jobParameters) {
		this.jobParameters = jobParameters;
	}
	public List<Integer> getDependencyJobListPositions() {
		return dependencyJobListPositions;
	}
	public void setDependencyJobListPositions(
			List<Integer> dependencyJobListPositions) {
		this.dependencyJobListPositions = dependencyJobListPositions;
	}

}
