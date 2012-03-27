package org.jobcenter.internalservice;

/**
 *
 *
 */
public interface SendClientsCheckinLateNotificationService {

	/**
	 * Process the node_client_status table and send emails for the clients that are late
	 */
	public abstract void sendClientsCheckinLateNotification();

}