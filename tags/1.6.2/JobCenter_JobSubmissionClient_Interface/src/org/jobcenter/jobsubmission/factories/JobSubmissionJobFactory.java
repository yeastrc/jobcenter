package org.jobcenter.jobsubmission.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jobcenter.coreinterfaces.JobSubmissionJobInterface;

/**
 * create an instance of JobSubmissionJobInterface
 *
 */
public class JobSubmissionJobFactory {

	/**
	 * @return an instance of JobSubmissionJobInterface
	 */
	public static JobSubmissionJobInterface buildJobSubmissionJobInterface() {
		
		return new JobSubmissionJobInterfaceImpl();
	}
	
	
	private static class JobSubmissionJobInterfaceImpl implements JobSubmissionJobInterface {

		private String jobTypeName; 
		private Integer priority;
		private Map<String, String> jobParameters;
		
		private List<Integer> dependencyJobsListPositions;


		@Override
		public void addToDependencyJobsListPositions(int dependencyJobListPosition) {

			if ( dependencyJobsListPositions == null ) {
				
				dependencyJobsListPositions = new ArrayList<Integer>();
			}
			dependencyJobsListPositions.add( dependencyJobListPosition );
		}

		@Override
		public void clearDependencyJobsListPositions() {
			
			dependencyJobsListPositions = null;
		}
		
		
		/* (non-Javadoc)
		 * @see org.jobcenter.coreinterfaces.JobSubmissionJobInterface#getJobTypeName()
		 */
		@Override
		public String getJobTypeName() {
			return jobTypeName;
		}

		/* (non-Javadoc)
		 * @see org.jobcenter.coreinterfaces.JobSubmissionJobInterface#setJobTypeName(java.lang.String)
		 */
		@Override
		public void setJobTypeName(String jobTypeName) {
			this.jobTypeName = jobTypeName;
		}


		/* (non-Javadoc)
		 * @see org.jobcenter.coreinterfaces.JobSubmissionJobInterface#getPriority()
		 */
		@Override
		public Integer getPriority() {
			return priority;
		}

		/* (non-Javadoc)
		 * @see org.jobcenter.coreinterfaces.JobSubmissionJobInterface#setPriority(java.lang.Integer)
		 */
		@Override
		public void setPriority(Integer priority) {
			this.priority = priority;
		}

		/* (non-Javadoc)
		 * @see org.jobcenter.coreinterfaces.JobSubmissionJobInterface#getJobParameters()
		 */
		@Override
		public Map<String, String> getJobParameters() {
			return jobParameters;
		}

		/* (non-Javadoc)
		 * @see org.jobcenter.coreinterfaces.JobSubmissionJobInterface#setJobParameters(java.util.Map)
		 */
		@Override
		public void setJobParameters(Map<String, String> jobParameters) {
			this.jobParameters = jobParameters;
		}

		@Override
		public List<Integer> getDependencyJobsListPositions() {
			return dependencyJobsListPositions;
		}

		@Override
		public void setDependencyJobsListPositions(
				List<Integer> dependencyJobsListPositions) {
			this.dependencyJobsListPositions = dependencyJobsListPositions;
		}

		
	}
}
