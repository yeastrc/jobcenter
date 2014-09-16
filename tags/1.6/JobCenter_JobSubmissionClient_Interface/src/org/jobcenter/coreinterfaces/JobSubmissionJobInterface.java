package org.jobcenter.coreinterfaces;

import java.util.List;
import java.util.Map;

/**
 * Interface for a specific job to be submitted
 *
 */
public interface JobSubmissionJobInterface {
	
	public abstract void addToDependencyJobsListPositions( int dependencyJobListPosition );
	public abstract void clearDependencyJobsListPositions(  );

	public abstract String getJobTypeName();

	public abstract void setJobTypeName(String jobTypeName);

	public abstract Integer getPriority();

	public abstract void setPriority(Integer priority);

	public abstract Map<String, String> getJobParameters();

	public abstract void setJobParameters(Map<String, String> jobParameters);

	public abstract List<Integer> getDependencyJobsListPositions();

	public abstract void setDependencyJobsListPositions( List<Integer> dependencyJobsListPositions );

}