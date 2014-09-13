package org.jobcenter.main;

import java.util.HashMap;
import java.util.Map;

import org.jobcenter.client.main.SubmissionClientConnectionToServer;
import org.jobcenter.coreinterfaces.JobSubmissionInterface;

public class SampleJobSubmitter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		final String connectionURL = "http://localhost:8080/JobCenter_Server_Jersey/";

		Map<String, String> jobParameters = new HashMap<String, String> ();

		jobParameters.put( "SampleKey20101124+4:45", "SampleValue20101124+4:45" );
		jobParameters.put( "SampleKeyEE", "SampleValueEE" );

		try {

			JobSubmissionInterface jobSubmissionClient = new SubmissionClientConnectionToServer();

			jobSubmissionClient.init( connectionURL );

			int requestId = jobSubmissionClient.submitJob("sampleModuleRT", null /* requestId */, "sampleModule", "Sample Module submitter", 1, jobParameters );


			System.out.println( "requestId = " + requestId );


		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println( "Run complete" );

	}

}
