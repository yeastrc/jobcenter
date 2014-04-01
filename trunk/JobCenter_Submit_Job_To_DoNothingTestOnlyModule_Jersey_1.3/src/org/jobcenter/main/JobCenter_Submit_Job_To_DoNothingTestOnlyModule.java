package org.jobcenter.main;

import java.util.HashMap;
import java.util.Map;

import org.jobcenter.client.main.SubmissionClientConnectionToServer;
import org.jobcenter.coreinterfaces.JobSubmissionInterface;

public class JobCenter_Submit_Job_To_DoNothingTestOnlyModule {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		final String connectionURL = "http://localhost:8080/JobCenter_Server_Jersey/";

		System.out.println( "Connecting to Jobcenter server at URL  " + connectionURL );

		Map<String, String> jobParameters = new HashMap<String, String> ();

		jobParameters.put( "Unused_Param1", "Unused_Param1" );
		jobParameters.put( "Unused_Param2", "Unused_Param2" );

		try {

			JobSubmissionInterface jobSubmissionClient = new SubmissionClientConnectionToServer();

			jobSubmissionClient.init( connectionURL );

			int requestId = jobSubmissionClient.submitJob("DoNothingTestOnlyModule", null /* requestId */, "DoNothingTestOnlyModule", "my submitter", 1, jobParameters );


			System.out.println( "generated requestId = " + requestId );


		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println( "Run complete" );

	}

}
