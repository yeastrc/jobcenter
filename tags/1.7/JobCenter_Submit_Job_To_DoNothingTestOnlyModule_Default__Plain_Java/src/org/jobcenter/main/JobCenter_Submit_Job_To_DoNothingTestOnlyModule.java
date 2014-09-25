package org.jobcenter.main;

import java.util.HashMap;
import java.util.Map;

import org.jobcenter.client.main.SubmissionClientConnectionToServer;
import org.jobcenter.client_exceptions.JobcenterSubmissionHTTPErrorException;
import org.jobcenter.client_exceptions.JobcenterSubmissionIOErrorException;
import org.jobcenter.coreinterfaces.JobSubmissionInterface;

public class JobCenter_Submit_Job_To_DoNothingTestOnlyModule {

	/**
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {

		final String connectionURL = "http://localhost:8080/JobCenter_Server_Jersey/";

		System.out.println( "Connecting to Jobcenter server at URL  " + connectionURL );

		Map<String, String> jobParameters = new HashMap<String, String> ();

		jobParameters.put( "Unused_Param1", "Unused_Param1" );
		jobParameters.put( "Unused_Param2", "Unused_Param2" );

		JobSubmissionInterface jobSubmissionClient = null;

		try {

			jobSubmissionClient = new SubmissionClientConnectionToServer();

			jobSubmissionClient.init( connectionURL );

			//  Should succeed

			int requestId = jobSubmissionClient.submitJob("DoNothingTestOnlyModule", null /* requestId */, "DoNothingTestOnlyModule", "my submitter", 1, jobParameters );

			System.out.println( "generated requestId = " + requestId );


			//  Should fail

//			int requestId = jobSubmissionClient.submitJob("DoNothingTestOnlyModule____", null /* requestId */, "DoNothingTestOnlyModule", "my submitter", 1, jobParameters );

//			System.out.println( "generated requestId = " + requestId );



			//  Should succeed

//			for ( int i = 0; i < 50000; i++ ) {
//
//				//  Should succeed
//
//				int requestId = jobSubmissionClient.submitJob("DoNothingTestOnlyModule", null /* requestId */, "DoNothingTestOnlyModule", "my submitter", 1, jobParameters );
//
//				System.out.println( "generated requestId = " + requestId );
//			}


		} catch ( JobcenterSubmissionHTTPErrorException e ) {

			System.err.println( "JobcenterSubmissionHTTPErrorException thrown:" );
			System.err.println( "Http Error Code: " + e.getHttpErrorCode() );
			System.err.println( "Full URL: " + e.getFullConnectionURL() );

			try {
				if ( e.getErrorStreamContents() == null ) {

					System.err.println( "e.getErrorStreamContents() == null" );
				} else {
					String errorStreamContents = new String( e.getErrorStreamContents() ); // Use default character set
					System.err.println( "Error Stream Contents: " + errorStreamContents );
				}
			} catch (Exception ex ) {

				System.err.println( "Error printing 'Error Stream Contents': " + ex.toString() );
			}
			e.printStackTrace();

			throw e;


		} catch ( JobcenterSubmissionIOErrorException e ) {

			System.err.println( "JobcenterSubmissionIOErrorException thrown:" );

			System.err.println( "Full URL: " + e.getFullConnectionURL() );
			try {
				if ( e.getErrorStreamContents() == null ) {

					System.err.println( "e.getErrorStreamContents() == null" );
				} else {
					String errorStreamContents = new String( e.getErrorStreamContents() ); // Use default character set
					System.err.println( "Error Stream Contents: " + errorStreamContents );
				}
			} catch (Exception ex ) {

				System.err.println( "Error printing 'Error Stream Contents': " + ex.toString() );
			}
			e.printStackTrace();

			throw e;

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			throw e;
		} finally {

			if ( jobSubmissionClient != null ) {

				jobSubmissionClient.destroy();
			}
		}

		System.out.println( "Run complete" );

	}

}
