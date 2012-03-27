package org.jobcenter.processjob;

import java.util.ArrayList;
import java.util.List;


public class ThreadsHolderSingleton {


	private GetJobThread getJobThread;

	private List<JobRunnerThread> jobRunnerThreads = new ArrayList<JobRunnerThread>();


	private static ThreadsHolderSingleton instance = new ThreadsHolderSingleton();

	private ThreadsHolderSingleton() {


	}

	public static ThreadsHolderSingleton getInstance() {

		return instance;
	}




	public GetJobThread getGetJobThread() {
		return getJobThread;
	}

	public void setGetJobThread(GetJobThread getJobThread) {
		this.getJobThread = getJobThread;
	}

	public List<JobRunnerThread> getJobRunnerThreads() {
		return jobRunnerThreads;
	}

}
