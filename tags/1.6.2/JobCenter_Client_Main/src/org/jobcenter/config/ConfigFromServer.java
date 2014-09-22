package org.jobcenter.config;

import org.jobcenter.nondbdto.ClientIdentifierDTO;


/**
 * This Singleton holds all the configuration received from the server
 *
 */
/**
 *
 *
 */
public class ConfigFromServer {


	private static ConfigFromServer instance = new ConfigFromServer();

	/**
	 * The identifier assigned to the client by the server when the client starts up.
	 */
	private ClientIdentifierDTO clientIdentifierDTO;

	/**
	 *  Time to wait until call server for next "Heart Beat" so server knows client is alive and current status of jobs being processed.
	 */
	private Integer waitTimeForNextClientCheckin;

	/**
	 * The time difference with the server in milliseconds:  (Client time) - (server time)      100 - 99 = 1  so subtract difference to get server time
	 */
	private long differenceWithServerTime;



	/**
	 * private constructor
	 */
	private ConfigFromServer() {

	}

	/**
	 * @return singleton
	 */
	public static ConfigFromServer getInstance()  {

		return instance;
	}

	public ClientIdentifierDTO getClientIdentifierDTO() {
		return clientIdentifierDTO;
	}

	public void setClientIdentifierDTO(ClientIdentifierDTO clientIdentifierDTO) {
		this.clientIdentifierDTO = clientIdentifierDTO;
	}


	/**
	 *
	 * @return The time difference with the server in milliseconds:  (Client time) - (server time)
	 */
	public long getDifferenceWithServerTime() {
		return differenceWithServerTime;
	}
	/**
	 * @param differenceWithServerTime : The time difference with the server in milliseconds:  (Client time) - (server time)
	 */
	public void setDifferenceWithServerTime(long differenceWithServerTime) {
		this.differenceWithServerTime = differenceWithServerTime;
	}

	public Integer getWaitTimeForNextClientCheckin() {
		return waitTimeForNextClientCheckin;
	}

	public void setWaitTimeForNextClientCheckin(Integer waitTimeForNextClientCheckin) {
		this.waitTimeForNextClientCheckin = waitTimeForNextClientCheckin;
	}

}
