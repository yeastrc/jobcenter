package org.jobcenter.serverinterface;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jobcenter.config.ClientConfigDTO;
import org.jobcenter.config.ConfigFromServer;
import org.jobcenter.module.ModuleConfigDTO;
import org.jobcenter.request.ClientStartupRequest;
import org.jobcenter.request.JobRequestModuleInfo;
import org.jobcenter.response.ClientStartupResponse;

/**
 *
 *
 */
public class ClientStartupServerConnection {

	private static Logger log = Logger.getLogger(ClientStartupServerConnection.class);


	/**
	 * calls ServerConnection.getInstance().clientStartup( clientStartupRequest );
	 *
	 * @throws Throwable
	 */
	public static void connectToServerForStartupAction() throws Throwable {

		ClientStartupRequest clientStartupRequest = new ClientStartupRequest();

		ClientStartupResponse clientStartupResponse = null;

		clientStartupRequest.setNodeName( ClientConfigDTO.getSingletonInstance().getClientNodeName() );


		List<ModuleConfigDTO> moduleConfigList = ClientConfigDTO.getSingletonInstance().getModuleConfigList();

		List<JobRequestModuleInfo> clientModules = new ArrayList<JobRequestModuleInfo>( moduleConfigList.size() );

		JobRequestModuleInfo jobRequestModuleInfo = null;

		for ( ModuleConfigDTO configDTOModuleInList : moduleConfigList ) {

			jobRequestModuleInfo = new JobRequestModuleInfo();

			jobRequestModuleInfo.setModuleName( configDTOModuleInList.getModuleName() );
			jobRequestModuleInfo.setModuleVersion( configDTOModuleInList.getModuleVersion() );

			clientModules.add( jobRequestModuleInfo );
		}

		clientStartupRequest.setClientModules( clientModules );

		try {

			clientStartupResponse = ServerConnection.getInstance().clientStartup( clientStartupRequest );

		} catch ( Throwable t ) {

			log.error( "Exception calling ServerConnection.getInstance().clientStartup( clientStartupRequest ): " + t.toString(), t );

			throw t;
		}

		if ( clientStartupResponse == null ) {

			String msg = "Error  calling ServerConnection.getInstance().clientStartup( clientStartupRequest ), response == null ";

			log.error( msg );

			throw new Exception( msg );
		}

		if ( clientStartupResponse.isErrorResponse() ) {

			String errorMsg = "\n\nThe server returned an ERROR when the client made it's startup connection. \n"
				+ " Error code = " + clientStartupResponse.getErrorCode()
				+ ", error description = " + clientStartupResponse.getErrorCodeDescription()
				+ ".\n The client's configured node name sent to the server = '" + clientStartupResponse.getClientNodeName()
				+ "'.\n The client's IP address as seen by the server = " + clientStartupResponse.getClientIPAddressAtServer();

			log.error( errorMsg );


			String infoMsg = "Error  calling ServerConnection.getInstance().clientStartup( clientStartupRequest ) "
				+ " clientStartupResponse.isErrorResponse() == true . "
				+ " clientStartupResponse.getErrorCode() = " + clientStartupResponse.getErrorCode()
				+ ", clientStartupResponse.getErrorCodeDescription() = " + clientStartupResponse.getErrorCodeDescription()
				+ ", the client's IP address as seen by the server ( clientStartupResponse.getClientIPAddressAtServer() ) = " + clientStartupResponse.getClientIPAddressAtServer();

			log.error( infoMsg );

			throw new Exception( errorMsg );
		}


		ConfigFromServer configFromServer = ConfigFromServer.getInstance();

		configFromServer.setClientIdentifierDTO( clientStartupResponse.getClientIdentifierDTO() );
		configFromServer.setWaitTimeForNextClientCheckin( clientStartupResponse.getWaitTimeForNextClientCheckin() );

		configFromServer.setDifferenceWithServerTime( System.currentTimeMillis() - clientStartupResponse.getCurrentServerTime() );

		log.info( "!!!! Client Startup Call completed.  WaitTimeForNextClientCheckin = " + clientStartupResponse.getWaitTimeForNextClientCheckin()
				+ ", ClientIdentifierDTO = " + clientStartupResponse.getClientIdentifierDTO() );
	}

}
