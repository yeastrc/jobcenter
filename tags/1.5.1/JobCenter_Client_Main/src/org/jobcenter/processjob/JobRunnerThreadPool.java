package org.jobcenter.processjob;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;


/**
 * pools of JobRunner objects
 *
 */
public class JobRunnerThreadPool {



	private static Logger log = Logger.getLogger(JobRunnerThreadPool.class);


	private static int jobRunnerThreadCounter = 0;

//	private static Queue<JobRunnerThread> activeJobRunnerThreads = new ConcurrentLinkedQueue<JobRunnerThread>();

	private static Queue<JobRunnerThread> inactiveJobRunnerThreads = new ConcurrentLinkedQueue<JobRunnerThread>();



//	/**
//	 * @return singleton, thread safe queue
//	 */
//	public static Queue<JobRunnerThread> getActiveJobRunnerThreadsInstance() {
//
//		return activeJobRunnerThreads;
//	}



	/**
	 * @return singleton, thread safe queue
	 */
	public static Queue<JobRunnerThread> getInactiveJobRunnerThreadsInstance() {

		return inactiveJobRunnerThreads;
	}


	/**
	 * increments and returns jobRunnerThreadCounter
	 *
	 * @return
	 */
	public synchronized static int getNextJobRunnerThreadCounter() {

		jobRunnerThreadCounter++;

		return jobRunnerThreadCounter;
	}

}
