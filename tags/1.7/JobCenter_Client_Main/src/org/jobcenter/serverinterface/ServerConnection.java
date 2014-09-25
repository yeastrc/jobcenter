package org.jobcenter.serverinterface;


import org.apache.log4j.Logger;
import org.jobcenter.config.ClientConfigDTO;
import org.jobcenter.config.ConfigFromServer;
import org.jobcenter.constants.ClientConstants;
import org.jobcenter.coreinterfaces.ClientConnectionToServerIF;
import org.jobcenter.request.*;
import org.jobcenter.response.*;

import org.jobcenter.util.BuildURLClassLoader;

/**
 * Singleton
 *
 * This handles calls to the server
 *
 */
public class ServerConnection {

	private static Logger log = Logger.getLogger(ServerConnection.class);

	private static ServerConnection instance;




	private ClientConnectionToServerIF clientConnectionToServer = null;


	/**
	 * @return
	 */
	public static synchronized ServerConnection getInstance() throws Throwable {

		if ( instance == null ) {

			instance = new ServerConnection();

			instance.init( ClientConstants.CLIENT_SERVER_INTERFACE_MODULE_FILE_PATH, ClientConstants.CLIENT_SERVER_INTERFACE_MODULE_CLASS_NAME );
		}

		return instance;
	}

	/**
	 * constructor
	 */
	private ServerConnection() {

	}


	/**
	 * @param serverInterfaceClassLoader
	 * @param classToLaunch
	 * @throws Throwable
	 */
	@SuppressWarnings("unchecked")
	public void init( String client_server_interface_module_file_path, String classToLaunch ) throws Throwable {

		log.info( "init(...) called, client_server_interface_module_file_path = '" + client_server_interface_module_file_path
				+ "', classToLaunch = '" + classToLaunch + "'." );

		BuildURLClassLoader buildURLClassLoader = new BuildURLClassLoader();

		ClassLoader serverInterfaceClassLoader = buildURLClassLoader.getClassLoaderForBaseDirectoryThisClassLoaderAsParent( client_server_interface_module_file_path );

		Class<ClientConnectionToServerIF> classToRun  = (Class<ClientConnectionToServerIF>) serverInterfaceClassLoader.loadClass( classToLaunch );

		clientConnectionToServer = classToRun.newInstance();

		log.info( "init(...) called, calling clientConnectionToServer.init()." );

		clientConnectionToServer.init();

		log.info( "exitting init(...)" );

	}



	/**
	 * call clientConnectionToServer.destroy();
	 *
	 * @throws Throwable
	 */
	public void destroy() {

		log.info( "destroy() called" );

		if ( clientConnectionToServer != null ) {

			log.info( "destroy(): calling clientConnectionToServer.destroy()" );

			clientConnectionToServer.destroy();

			clientConnectionToServer = null;
		}


		log.info( "exitting destroy()" );
	}



	/**
	 * Connect to server to identify client and get node identifier
	 *
	 * @param clientStartupRequest
	 * @return
	 * @throws Throwable
	 */
	public ClientStartupResponse clientStartup( ClientStartupRequest clientStartupRequest ) throws Throwable
	{
		if ( clientConnectionToServer == null ) {

			throw new IllegalStateException( "clientConnectionToServer is null" );
		}

		clientStartupRequest.setNodeName( ClientConfigDTO.getSingletonInstance().getClientNodeName() );

		return clientConnectionToServer.clientStartup( clientStartupRequest );
	}



	/**
	 * Connect to server to send heart beat
	 *
	 * @param clientStartupRequest
	 * @return
	 * @throws Throwable
	 */
	public ClientStatusUpdateResponse clientStatusUpdate( ClientStatusUpdateRequest clientStatusUpdateRequest ) throws Throwable
	{
		if ( clientConnectionToServer == null ) {

			throw new IllegalStateException( "clientConnectionToServer is null" );
		}

		clientStatusUpdateRequest.setClientIdentifierDTO( ConfigFromServer.getInstance().getClientIdentifierDTO() );

		clientStatusUpdateRequest.setNodeName( ClientConfigDTO.getSingletonInstance().getClientNodeName() );

		return clientConnectionToServer.clientStatusUpdate( clientStatusUpdateRequest );
	}





	/**
	 * @param jobRequest
	 * @return
	 * @throws Throwable
	 */
	public JobResponse getNextJob (JobRequest jobRequest ) throws Throwable {

		if ( clientConnectionToServer == null ) {

			throw new IllegalStateException( "clientConnectionToServer is null" );
		}

		jobRequest.setClientIdentifierDTO( ConfigFromServer.getInstance().getClientIdentifierDTO() );

		jobRequest.setNodeName( ClientConfigDTO.getSingletonInstance().getClientNodeName() );


		return clientConnectionToServer.getNextJobToProcess( jobRequest );
	}




	/**
	 * Update job status
	 *
	 * @param updateJobStatusRequest
	 * @return
	 * @throws Throwable
	 */
	public UpdateServerFromJobRunOnClientResponse updateServerFromJobRunOnClient( UpdateServerFromJobRunOnClientRequest updateJobStatusRequest ) throws Throwable {


		if ( clientConnectionToServer == null ) {

			throw new IllegalStateException( "clientConnectionToServer is null" );
		}

		updateJobStatusRequest.setClientIdentifierDTO( ConfigFromServer.getInstance().getClientIdentifierDTO() );

		updateJobStatusRequest.setNodeName( ClientConfigDTO.getSingletonInstance().getClientNodeName() );

		return clientConnectionToServer.updateServerFromJobRunOnClient( updateJobStatusRequest );

	}




	/**
	 * Get a particular run object
	 *
	 * @param getRunRequest
	 * @return
	 * @throws Throwable
	 */
	public GetRunResponse getRunRequest( GetRunRequest getRunRequest ) throws Throwable {


		if ( clientConnectionToServer == null ) {

			throw new IllegalStateException( "clientConnectionToServer is null" );
		}

		getRunRequest.setClientIdentifierDTO( ConfigFromServer.getInstance().getClientIdentifierDTO() );

		getRunRequest.setNodeName( ClientConfigDTO.getSingletonInstance().getClientNodeName() );

		return clientConnectionToServer.getRunRequest( getRunRequest );

	}


	/**
	 * Get a list of run ids for a job id
	 *
	 * @param getRunIdListRequest
	 * @return
	 * @throws Throwable
	 */
	public GetRunIdListResponse getRunIdListRequest( GetRunIdListRequest getRunIdListRequest ) throws Throwable {

		if ( clientConnectionToServer == null ) {

			throw new IllegalStateException( "clientConnectionToServer is null" );
		}

		getRunIdListRequest.setClientIdentifierDTO( ConfigFromServer.getInstance().getClientIdentifierDTO() );

		getRunIdListRequest.setNodeName( ClientConfigDTO.getSingletonInstance().getClientNodeName() );

		return clientConnectionToServer.getRunIdListRequest( getRunIdListRequest );

	}




	/**
	 * Submit a job to the server
	 *
	 * @param submitJobRequest
	 * @return SubmitJobResponse
	 * @throws Throwable - throws an error if any errors related to submitting the job
	 */

	public SubmitJobResponse submitJob( SubmitJobRequest submitJobRequest ) throws Throwable {


		if ( clientConnectionToServer == null ) {

			throw new IllegalStateException( "clientConnectionToServer is null" );
		}

		submitJobRequest.setClientIdentifierDTO( ConfigFromServer.getInstance().getClientIdentifierDTO() );

		submitJobRequest.setNodeName( ClientConfigDTO.getSingletonInstance().getClientNodeName() );

		return clientConnectionToServer.submitJob( submitJobRequest );
	}

}
