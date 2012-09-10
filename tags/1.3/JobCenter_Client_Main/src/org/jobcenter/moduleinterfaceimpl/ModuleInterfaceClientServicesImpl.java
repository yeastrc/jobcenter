package org.jobcenter.moduleinterfaceimpl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jobcenter.config.ClientConfigDTO;
import org.jobcenter.dto.Job;
import org.jobcenter.dto.RunDTO;
import org.jobcenter.job_client_module_interface.ModuleInterfaceClientServices;
import org.jobcenter.request.GetRunIdListRequest;
import org.jobcenter.request.GetRunRequest;
import org.jobcenter.request.SubmitJobRequest;
import org.jobcenter.response.GetRunIdListResponse;
import org.jobcenter.response.GetRunResponse;
import org.jobcenter.response.SubmitJobResponse;
import org.jobcenter.serverinterface.ServerConnection;

/**
 *   !!!!!!!!!!!!!!!!   Important:           Wrap every call from the module with saving the contextClassLoader and restoring it on exit  !!!!!!!!!!!!!!
 *
 */
public class ModuleInterfaceClientServicesImpl implements ModuleInterfaceClientServices {

	private static Logger log = Logger.getLogger(ModuleInterfaceClientServicesImpl.class);


	private static final int RUN_ID_POSITION_NOT_SET = -1;


	private Job currentJob;
	private RunDTO currentRun;

	private volatile boolean keepRunning = true;



	/**
	 *
	 */
	public void stopRunningAfterProcessingJob() {

		keepRunning = false;

	}


	public void init() throws Throwable {



	}

	public void destroy() {


	}



	/**
	 * Returns the Run Output Params from the previous run for the current job.
	 * Returns null if no previous run for the current job.
	 * @return
	 * @throws Throwable
	 */
	@Override
	public Map<String, String> getPreviousRunOutputParams() throws Throwable {

		ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();

		try {
			//  Set context class loader to class loader for module

			log.info( "Setting current thread context class loader to class loader for current class" );

	        Thread.currentThread().setContextClassLoader( this.getClass().getClassLoader() );


	        GetRunRequest getRunRequest = new GetRunRequest();

	        getRunRequest.setNodeName( ClientConfigDTO.getSingletonInstance().getClientNodeName() );

	        getRunRequest.setJobId( currentJob.getId() );
	        getRunRequest.setCurrentRunId( currentRun.getId() );

	        getRunRequest.setGetRunRequestType( GetRunRequest.GetRunRequestType.GET_PREVIOUS_RUN );

	        GetRunResponse getRunResponse = ServerConnection.getInstance().getRunRequest( getRunRequest );


	        if ( getRunResponse.isErrorResponse() ) {

	        	String msg =  "Retrieval of previous run output params failed.";

	        	if ( currentJob != null ) {

	        		msg += "  Job id = " + currentJob.getId();

	        		if ( currentJob.getCurrentRun() != null ) {

	        			msg += ".  run id = " + currentJob.getCurrentRun().getId();
	        		} else {

	        			msg += ".  run is null ";
	        		}
	        	} else {

	        		msg += "  job reference is null.";
	        	}

	        	log.error( msg );


	        	throw new Exception( msg );


	        }


	        RunDTO run = getRunResponse.getRun();

	        if ( run == null ) {

	        	log.info( "Request for getPreviousRunOutputParams() returned null 'run' so returning null." );

	        	return null;
	        }

	        return run.getRunOutputParams();

		} finally {

			log.info( "Setting current thread context class loader to saved class loader. savedClassLoader  = " + savedClassLoader );

	        Thread.currentThread().setContextClassLoader( savedClassLoader );


		}

	}

	/**
	 * Returns the Run Output Params from the first run ( not including the current run ) for the current job.
	 * Returns null if no first run ( not including the current run ) run for the current job.
	 * @return
	 * @throws Throwable
	 */
	@Override
	public Map<String, String> getFirstRunOutputParams() throws Throwable {

		ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();

		try {
			//  Set context class loader to class loader for module

			log.info( "Setting current thread context class loader to class loader for current class" );

	        Thread.currentThread().setContextClassLoader( this.getClass().getClassLoader() );


	        GetRunRequest getRunRequest = new GetRunRequest();

	        getRunRequest.setNodeName( ClientConfigDTO.getSingletonInstance().getClientNodeName() );

	        getRunRequest.setJobId( currentJob.getId() );
	        getRunRequest.setCurrentRunId( currentRun.getId() );

	        getRunRequest.setGetRunRequestType( GetRunRequest.GetRunRequestType.GET_FIRST_RUN );

	        GetRunResponse getRunResponse = ServerConnection.getInstance().getRunRequest( getRunRequest );


	        if ( getRunResponse.isErrorResponse() ) {

	        	String msg =  "Retrieval of first run output params failed.";

	        	if ( currentJob != null ) {

	        		msg += "  Job id = " + currentJob.getId();

	        		if ( currentJob.getCurrentRun() != null ) {

	        			msg += ".  run id = " + currentJob.getCurrentRun().getId();
	        		} else {

	        			msg += ".  run is null ";
	        		}
	        	} else {

	        		msg += "  job reference is null.";
	        	}

	        	log.error( msg );


	        	throw new Exception( msg );


	        }


	        RunDTO run = getRunResponse.getRun();

	        if ( run == null ) {

	        	log.info( "Request for getFirstRunOutputParams() returned null 'run' so returning null." );

	        	return null;
	        }

	        return run.getRunOutputParams();

		} finally {

			log.info( "Setting current thread context class loader to saved class loader. savedClassLoader  = " + savedClassLoader );

	        Thread.currentThread().setContextClassLoader( savedClassLoader );


		}

	}


	/**
	 * Provides a count of the runs for the job being processed.  Only the runs before the current run are included.
	 * @return
	 * @throws Throwable
	 */
	@Override
	public int getRunCount() throws Throwable {

		ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();

		try {
			//  Set context class loader to class loader for module

			log.info( "Setting current thread context class loader to class loader for current class" );

	        Thread.currentThread().setContextClassLoader( this.getClass().getClassLoader() );

	        GetRunIdListRequest getRunIdListRequest = new GetRunIdListRequest();

	        getRunIdListRequest.setNodeName( ClientConfigDTO.getSingletonInstance().getClientNodeName() );

	        getRunIdListRequest.setJobId( currentJob.getId() );

	        GetRunIdListResponse getRunIdListResponse = ServerConnection.getInstance().getRunIdListRequest( getRunIdListRequest );


	        if ( getRunIdListResponse.isErrorResponse() ) {

	        	String msg =  "Retrieval of run count failed.";

	        	if ( currentJob != null ) {

	        		msg += "  Job id = " + currentJob.getId();

	        	} else {

	        		msg += "  job reference is null.";
	        	}

	        	log.error( msg );


	        	throw new Exception( msg );
	        }

	        List<Integer> runIdList = getRunIdListResponse.getRunIdList();

	        if ( runIdList == null ) {

	        	log.info( "Request for getRunCount() returned null 'runIdList' so returning zero." );

	        	return 0;
	        }

	        if ( runIdList.isEmpty() ) {

	        	log.info( "Request for getRunCount() returned empty 'runIdList' so returning zero." );

	        	return 0;
	        }

	        int currentRunIdPosition = RUN_ID_POSITION_NOT_SET;


	        for ( int count = runIdList.size() - 1; count >= 0; count-- ) {

	        	if ( runIdList.get(count).intValue() == currentRun.getId().intValue() ) {

	        		currentRunIdPosition = count;
	        		break;
	        	}
	        }

	        if ( currentRunIdPosition == RUN_ID_POSITION_NOT_SET ) {

	        	String msg = "System error.  Current run not found in server database.  Job id = " + currentJob.getId()
	        	+ ", run id = " + currentRun.getId();

	        	log.error( msg );

	        	throw new Exception( msg );
	        }

	        //  simply return currentRunIdPosition since zero based list
	        return currentRunIdPosition;

		} finally {

			log.info( "Setting current thread context class loader to saved class loader. savedClassLoader  = " + savedClassLoader );

	        Thread.currentThread().setContextClassLoader( savedClassLoader );


		}

	}

	/**
	 * Returns the Run Output Params from the run based on the index ( not including the current run ) for the current job.
	 * Zero based index.
	 * Returns null if no run based on the index exists ( not including the current run ) for the current job.
	 * @return
	 * @throws Throwable
	 */
	@Override
	public Map<String, String> getRunOutputParamsUsingIndex( int index ) throws Throwable {

		ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();

		try {
			//  Set context class loader to class loader for module

			log.info( "Setting current thread context class loader to class loader for current class" );

	        Thread.currentThread().setContextClassLoader( this.getClass().getClassLoader() );

	        GetRunRequest getRunRequest = new GetRunRequest();

	        getRunRequest.setNodeName( ClientConfigDTO.getSingletonInstance().getClientNodeName() );

	        getRunRequest.setJobId( currentJob.getId() );
	        getRunRequest.setCurrentRunId( currentRun.getId() );

	        getRunRequest.setGetRunRequestType( GetRunRequest.GetRunRequestType.GET_RUN_FROM_INDEX );

	        getRunRequest.setRunIndex( index );

	        GetRunResponse getRunResponse = ServerConnection.getInstance().getRunRequest( getRunRequest );


	        if ( getRunResponse.isErrorResponse() ) {

	        	String msg =  "Retrieval of previous run output params failed.";

	        	if ( currentJob != null ) {

	        		msg += "  Job id = " + currentJob.getId();

	        		if ( currentJob.getCurrentRun() != null ) {

	        			msg += ".  run id = " + currentJob.getCurrentRun().getId();
	        		} else {

	        			msg += ".  run is null ";
	        		}
	        	} else {

	        		msg += "  job reference is null.";
	        	}

	        	log.error( msg );


	        	throw new Exception( msg );


	        }


	        RunDTO run = getRunResponse.getRun();

	        if ( run == null ) {

	        	log.info( "Request for getRunOutputParamsUsingIndex( int index ) returned null 'run' so returning null." );

	        	return null;
	        }

	        return run.getRunOutputParams();


		} finally {

			log.info( "Setting current thread context class loader to saved class loader. savedClassLoader  = " + savedClassLoader );

	        Thread.currentThread().setContextClassLoader( savedClassLoader );
		}

	}




	/**
	 * @param requestTypeName - the name of the request type
	 * @param requestId - Pass in to relate the submitted job to an existing requestId.  Pass in null otherwise
	 * @param jobTypeName - the name of the job type
	 * @param submitter
	 * @param priority
	 * @param priority - Pass null if want to use value from jobtype table
	 * @param jobParameters
	 * @return requestId - the next assigned id related to the particular requestTypeName.  Will return the passed in requestId if one is provided ( not null )
	 * @throws Throwable - throws an error if any errors related to submitting the job
	 */
	@Override
	public int submitJob( String requestTypeName, Integer requestId, String jobTypeName, String submitter, Integer priority, Map<String, String> jobParameters ) throws Throwable {

		ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();

		try {
			//  Set context class loader to class loader for module

			log.info( "Setting current thread context class loader to class loader for current class" );

	        Thread.currentThread().setContextClassLoader( this.getClass().getClassLoader() );


			SubmitJobRequest submitJobRequest = new SubmitJobRequest();

			submitJobRequest.setRequestTypeName( requestTypeName );
			submitJobRequest.setRequestId( requestId );

			submitJobRequest.setJobTypeName( jobTypeName );

			submitJobRequest.setPriority (priority );
			submitJobRequest.setSubmitter( submitter );
			submitJobRequest.setJobParameters( jobParameters );


			SubmitJobResponse submitJobResponse = ServerConnection.getInstance().submitJob( submitJobRequest );


	        if ( submitJobResponse.isErrorResponse() ) {

	        	String msg =  "Submission of job failed.  ";

	        	if ( currentJob != null ) {

	        		msg += "  Job id = " + currentJob.getId();

	        		if ( currentJob.getCurrentRun() != null ) {

	        			msg += ".  run id = " + currentJob.getCurrentRun().getId();
	        		} else {

	        			msg += ".  run is null ";
	        		}
	        	} else {

	        		msg += "  job reference is null.";
	        	}

	        	log.error( msg );


				String msgThrown = "Submission of job failed.  Error code = " + submitJobResponse.getErrorCode() + ", error code desc = " + submitJobResponse.getErrorCodeDescription()
					+ ", the client's IP address as seen by the server = " + submitJobResponse.getClientIPAddressAtServer()
					+ "\n  requestTypeName = |" + requestTypeName + "|, requestId = " + requestId + ", jobTypeName = |" + jobTypeName + "|.";

				log.error( msgThrown );

				throw new Exception( msgThrown );


	        }


	        return submitJobResponse.getRequestId();


		} finally {

			log.info( "Setting current thread context class loader to saved class loader. savedClassLoader  = " + savedClassLoader );

	        Thread.currentThread().setContextClassLoader( savedClassLoader );
		}

	}




	public Job getCurrentJob() {
		return currentJob;
	}

	public void setCurrentJob(Job currentJob) {
		this.currentJob = currentJob;
	}

	public RunDTO getCurrentRun() {
		return currentRun;
	}

	public void setCurrentRun(RunDTO currentRun) {
		this.currentRun = currentRun;
	}

}
