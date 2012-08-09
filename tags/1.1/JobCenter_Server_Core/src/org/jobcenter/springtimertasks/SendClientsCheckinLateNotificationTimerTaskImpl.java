package org.jobcenter.springtimertasks;

import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.jobcenter.internalservice.ProcessClientsStatusForLateCheckinsService;

/**
 *
 *
 */
public class SendClientsCheckinLateNotificationTimerTaskImpl extends TimerTask {





	private static Logger log = Logger.getLogger(SendClientsCheckinLateNotificationTimerTaskImpl.class);


	private ProcessClientsStatusForLateCheckinsService processClientsStatusForLateCheckinsService;


	public ProcessClientsStatusForLateCheckinsService getProcessClientsStatusForLateCheckinsService() {
		return processClientsStatusForLateCheckinsService;
	}
	public void setProcessClientsStatusForLateCheckinsService(
			ProcessClientsStatusForLateCheckinsService processClientsStatusForLateCheckinsService) {
		this.processClientsStatusForLateCheckinsService = processClientsStatusForLateCheckinsService;
	}


	@Override
	public void run() {

		// Hibernate session is created because SendClientsCheckinLateNotificationService has Spring "@Transactional ... " annotation


		try {
			if ( log.isDebugEnabled() ) {

				log.debug("calling processClientsStatusForLateCheckinsService.processClientsStatusForLateCheckins();");
			}
			processClientsStatusForLateCheckinsService.processClientsStatusForLateCheckins();

		} catch ( Throwable t ) {

			log.error( "Failed in call processClientsStatusForLateCheckinsService.processClientsStatusForLateCheckins();  exception = " + t.toString(), t );

		} finally {



		}


	}

}
