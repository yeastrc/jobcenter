package org.jobcenter.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.jobcenter.constants.ClientStatusUpdateTypeEnum;
import org.jobcenter.constants.ServerConfigKeyValues;
import org.jobcenter.constants.ServerCoreConstants;
import org.jobcenter.dto.NodeClientStatusDTO;
import org.jobcenter.dtoservernondb.MailConfig;
import org.jobcenter.dtoservernondb.NodeClientStatusDTOPrevCurrent;
import org.jobcenter.internalservice.ClientNodeNameCheck;
import org.jobcenter.internalservice.ClientStatusDBUpdateService;
import org.jobcenter.internalservice.ClientsConnectedTrackingService;
import org.jobcenter.internalservice.GetMailConfig;
import org.jobcenter.internalservice.GetValueFromConfigService;
import org.jobcenter.internalservice.SendEmailService;
import org.jobcenter.nondbdto.RunInProgressDTO;

import org.jobcenter.request.*;
import org.jobcenter.response.*;


/**
*  The database transaction is managed below this level in the class ClientStatusDBUpdate
*
*/

public class ClientStatusUpdateServiceImpl implements ClientStatusUpdateService {


	private static Logger log = Logger.getLogger(ClientStatusUpdateServiceImpl.class);

	
	private static final String EMAIL_SUBJECT_LINE_CLIENT_SHUTDOWN = "JobCenter client shut down.";


	//  Service

	private ClientStatusDBUpdateService clientStatusDBUpdateService;
	private GetValueFromConfigService getValueFromConfigService;

	private ClientNodeNameCheck clientNodeNameCheck;

	private ClientsConnectedTrackingService clientsConnectedTrackingService;

	private GetMailConfig getMailConfig;
	private SendEmailService sendEmailService;



	public ClientStatusDBUpdateService getClientStatusDBUpdateService() {
		return clientStatusDBUpdateService;
	}
	public void setClientStatusDBUpdateService(ClientStatusDBUpdateService clientStatusDBUpdateService) {
		this.clientStatusDBUpdateService = clientStatusDBUpdateService;
	}
	public GetValueFromConfigService getGetValueFromConfigService() {
		return getValueFromConfigService;
	}
	public void setGetValueFromConfigService(
			GetValueFromConfigService getValueFromConfigService) {
		this.getValueFromConfigService = getValueFromConfigService;
	}
	public ClientsConnectedTrackingService getClientsConnectedTrackingService() {
		return clientsConnectedTrackingService;
	}
	public void setClientsConnectedTrackingService(
			ClientsConnectedTrackingService clientsConnectedTrackingService) {
		this.clientsConnectedTrackingService = clientsConnectedTrackingService;
	}
	public ClientNodeNameCheck getClientNodeNameCheck() {
		return clientNodeNameCheck;
	}
	public void setClientNodeNameCheck(ClientNodeNameCheck clientNodeNameCheck) {
		this.clientNodeNameCheck = clientNodeNameCheck;
	}
	public GetMailConfig getGetMailConfig() {
		return getMailConfig;
	}
	public void setGetMailConfig(GetMailConfig getMailConfig) {
		this.getMailConfig = getMailConfig;
	}
	public SendEmailService getSendEmailService() {
		return sendEmailService;
	}
	public void setSendEmailService(SendEmailService sendEmailService) {
		this.sendEmailService = sendEmailService;
	}
	
	

	/**
	 * @param jobRequest
	 * @param remoteHost
	 * @return
	 */
	@Override
	public ClientStatusUpdateResponse clientStatusUpdateFromClient( ClientStatusUpdateRequest clientStatusUpdateRequest, String remoteHost )
	{
		final String method = "clientStatusUpdateFromClient";


		if ( clientStatusUpdateRequest == null ) {

			log.error( method + "  IllegalArgument:clientStatusUpdateRequest == null");

			throw new IllegalArgumentException( "clientStatusUpdateRequest == null" );
		}

		if ( log.isDebugEnabled() ) {

			log.debug( method + " clientStatusUpdateRequest.getNodeName() = |" + clientStatusUpdateRequest.getNodeName() + "|, remoteHost = |" + remoteHost + "|."  );
		}

		ClientStatusUpdateResponse clientStatusUpdateResponse = new ClientStatusUpdateResponse();

		if ( ! clientNodeNameCheck.validateNodeNameAndNetworkAddress( clientStatusUpdateResponse, clientStatusUpdateRequest.getNodeName(), remoteHost ) ) {

			return clientStatusUpdateResponse;
		}


		if ( log.isDebugEnabled() ) {


			log.debug( method + ": clientStatusUpdateRequest = " + clientStatusUpdateRequest );
		}

		if ( clientStatusUpdateRequest.isClientAcceptsServerValueForTimeUntilNextClientStatusUpdate() ) {


			long now = System.currentTimeMillis();

			int waitTimeForNextClientCheckin = ServerCoreConstants.DEFAULT_CLIENT_CHECKIN_TIME_IN_SECONDS;

			Integer waitTimeForNextClientCheckinFromConfig = getValueFromConfigService.getConfigValueAsInteger( ServerConfigKeyValues.CLIENT_CHECKIN_WAIT_TIME );

			if ( waitTimeForNextClientCheckinFromConfig != null ) {

				waitTimeForNextClientCheckin = waitTimeForNextClientCheckinFromConfig;
			}

			long nextExpectedStatusUpdatedTime = now + ( waitTimeForNextClientCheckin * 1000 );

			clientStatusUpdateResponse.setWaitTimeForNextClientCheckinSpecifiedByServer( waitTimeForNextClientCheckin );

			clientStatusUpdateRequest.setTimeUntilNextClientStatusUpdate( waitTimeForNextClientCheckin );

		}



		//  TODO  Update to update the DB to indicate that the client has shut down so that a client late email is not generated
		//            after the client has shut down.



		//  TODO    Need to actually do something with the provided info


//		long currentTime = System.currentTimeMillis();
//
//		long timeDiffOfClient = currentTime - clientStatusUpdateRequest.getClientCurrentTime();
//
//		log.info( method + " called. NodeName = " + clientStatusUpdateRequest.getNodeName()
//				+ ", ClientIdentifierDTO = "  + clientStatusUpdateRequest.getClientIdentifierDTO()
//				+ ", updateType = " + clientStatusUpdateRequest.getUpdateType()
//				+ ", timeDiffOfClient = " + timeDiffOfClient );



		clientsConnectedTrackingService.updateClientStatusAndInfo( clientStatusUpdateRequest.getClientIdentifierDTO(), clientStatusUpdateRequest.getUpdateType() );

		updateDBWithStatusFromClient( clientStatusUpdateRequest, remoteHost );

		List<RunInProgressDTO> runsInProgress = clientStatusUpdateRequest.getRunsInProgress();

		if ( runsInProgress != null ) {

			for ( RunInProgressDTO runInProgress : runsInProgress ) {

				log.info( method + " called. runInProgress:  JobId = " + runInProgress.getJobId()
						+ ", RunId = "  + runInProgress.getRunId()
						+ ", LastStatusTimeStampFromModule = "  + runInProgress.getLastStatusTimeStampFromModule()
						+ ", PercentageComplete = " + runInProgress.getPercentageComplete() );


				//  This tracking is available for a future enhancement.

				//   The "LastStatusTimeStampFromModule" may not be accurate.  The "PercentageComplete" is not being set by many modules

//				if ( runInProgress.getLastStatusTimeStampFromModule() != null ) {
//
//					long lastStatusTimeStampFromModuleAdjustedToServerTime = runInProgress.getLastStatusTimeStampFromModule() - timeDiffOfClient;
//
//
//					long timeSinceLastStatusTimeStampFromModule = currentTime - lastStatusTimeStampFromModuleAdjustedToServerTime;
//
//					long secondsSinceLastStatusTimeStampFromModule = timeSinceLastStatusTimeStampFromModule / 1000;
//
//					log.info( method + " called. lastStatusTimeStampFromModuleAdjustedToServerTime = " + lastStatusTimeStampFromModuleAdjustedToServerTime
//							+ ", timeSinceLastStatusTimeStampFromModule = " + timeSinceLastStatusTimeStampFromModule
//							+ ", secondsSinceLastStatusTimeStampFromModule = " + secondsSinceLastStatusTimeStampFromModule );
//
//				}
			}

		}

		return clientStatusUpdateResponse;
	}



	/**
	 * @param clientStatusUpdateRequest
	 * @param remoteHost
	 */
	private void updateDBWithStatusFromClient( ClientStatusUpdateRequest clientStatusUpdateRequest, String remoteHost ) {

		if ( log.isDebugEnabled() ) {

			log.debug( "Updating Client status DB for client node name = " + clientStatusUpdateRequest.getNodeName() );
		}

		boolean updatedEntry = false;

		int retryCount = 0;
		
		NodeClientStatusDTOPrevCurrent nodeClientStatusDTOPrevCurrent = null;

		while ( ! updatedEntry ) {

			try {

				nodeClientStatusDTOPrevCurrent = clientStatusDBUpdateService.updateDBWithStatusFromClient( clientStatusUpdateRequest, remoteHost );

				updatedEntry = true;

				if ( log.isDebugEnabled() ) {

					log.debug( "Update of Client status DB Successful for client node name = " + clientStatusUpdateRequest.getNodeName() );
				}

			} catch ( Throwable t ) {

				retryCount++;

				if ( retryCount > 5 ) {

					String msg = "Update of Client status DB FAILED for client node name = " + clientStatusUpdateRequest.getNodeName()
					+ ".  Retry count exceeded.  Exception: " + t.toString();

					log.error( msg, t );

					throw new RuntimeException( msg, t );
				}
			}
		}
		
		ClientStatusUpdateTypeEnum updateType = clientStatusUpdateRequest.getUpdateType();
		
		
		if ( updateType == ClientStatusUpdateTypeEnum.CLIENT_ABOUT_TO_EXIT 
			 || updateType == ClientStatusUpdateTypeEnum.CLIENT_SHUTDOWN_REQUESTED ) {


			if ( nodeClientStatusDTOPrevCurrent != null ) {

				NodeClientStatusDTO nodeClientStatusDTOPrev = nodeClientStatusDTOPrevCurrent.getPrevNodeClientStatusDTO();

				if ( nodeClientStatusDTOPrev != null ) {

					if ( nodeClientStatusDTOPrev.getClientStarted() ) {

						Boolean sendShutdownNotification
						= getValueFromConfigService.getConfigValueAsBoolean( ServerConfigKeyValues.CLIENT_SHUTDOWN_NOTIFICATION );

						if ( sendShutdownNotification != null && sendShutdownNotification ) {

							MailConfig mailConfig = getMailConfig.getClientCheckinMailConfig();

							if ( mailConfig != null ) {

								// create the message body

								StringBuilder emailBodySB = new StringBuilder(1000);

								emailBodySB.append( "The JobCenter client '" );

								emailBodySB.append( clientStatusUpdateRequest.getNodeName() );

								emailBodySB.append( "' has shut down.\n\n" );

								String emailBody = emailBodySB.toString();

								//  send to configured list of recipients in config table of the database

								for ( String toEmailAddress : mailConfig.getToAddresses() ) {

									if ( ! StringUtils.isEmpty( toEmailAddress ) ) {
										
										try {
											sendEmailService.sendEmail( mailConfig.getSmtpEmailHost(),
													mailConfig.getFromEmailAddress(), toEmailAddress, EMAIL_SUBJECT_LINE_CLIENT_SHUTDOWN, emailBody );
										} catch (Throwable e) {

											String msg = "Exception sending email for client startup when client not previously shut down";

											log.error( msg, e );

											throw new RuntimeException( msg, e );
										}
									}
								}


							} else {

								log.error( "Notification config key '" + ServerConfigKeyValues.CLIENT_STARTUP_CLIENT_NOT_PREV_SHUTDOWN_NOTIFICATION
										+ "' = true/yes/1 but the mail config is incomplete or empty " );
							}

						}
					}
				}
			}
		}
	}






}
