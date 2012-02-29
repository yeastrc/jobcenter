package org.jobcenter.springtimertasks;

import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.jobcenter.internalservice.SendClientsCheckinLateNotificationService;

/**
 *
 *
 */
public class SendClientsCheckinLateNotificationTimerTaskImpl extends TimerTask {



	private static Logger log = Logger.getLogger(SendClientsCheckinLateNotificationTimerTaskImpl.class);


	private SendClientsCheckinLateNotificationService sendClientsCheckinLateNotificationService;

	public SendClientsCheckinLateNotificationService getSendClientsCheckinLateNotificationService() {
		return sendClientsCheckinLateNotificationService;
	}
	public void setSendClientsCheckinLateNotificationService(
			SendClientsCheckinLateNotificationService sendClientsCheckinLateNotificationService) {
		this.sendClientsCheckinLateNotificationService = sendClientsCheckinLateNotificationService;

		if ( log.isDebugEnabled() ) {

			log.debug("setSendClientsCheckinLateNotificationService(...) called");
		}

	}

	@Override
	public void run() {

		// Hibernate session is created because SendClientsCheckinLateNotificationService has Spring "@Transactional ... " annotation


		try {
			if ( log.isDebugEnabled() ) {

				log.debug("calling sendClientsCheckinLateNotificationService.sendClientsCheckinLateNotification();");
			}
			sendClientsCheckinLateNotificationService.sendClientsCheckinLateNotification();

		} catch ( Throwable t ) {

			log.error( "Failed to call sendClientsCheckinLateNotificationService.sendClientsCheckinLateNotification();  exception = " + t.toString(), t );

		} finally {



		}


	}

}
