package org.jobcenter.dtoservernondb;

import java.util.List;

import org.jobcenter.dto.Job;

public class JobAndDependenciesHolder {

	private Job job;
	private List<Integer> dependencyJobListPositions;
	
	
	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}
	public List<Integer> getDependencyJobListPositions() {
		return dependencyJobListPositions;
	}
	public void setDependencyJobListPositions(
			List<Integer> dependencyJobListPositions) {
		this.dependencyJobListPositions = dependencyJobListPositions;
	}
}
