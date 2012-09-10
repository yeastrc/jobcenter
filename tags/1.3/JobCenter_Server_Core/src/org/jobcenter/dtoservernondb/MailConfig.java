package org.jobcenter.dtoservernondb;

/**
 * 
 *
 */
public class MailConfig {
	
	String[] toAddresses;
	String fromEmailAddress;
	String smtpEmailHost;


	public String[] getToAddresses() {
		return toAddresses;
	}
	public void setToAddresses(String[] toAddresses) {
		this.toAddresses = toAddresses;
	}
	public String getFromEmailAddress() {
		return fromEmailAddress;
	}
	public void setFromEmailAddress(String fromEmailAddress) {
		this.fromEmailAddress = fromEmailAddress;
	}
	public String getSmtpEmailHost() {
		return smtpEmailHost;
	}
	public void setSmtpEmailHost(String smtpEmailHost) {
		this.smtpEmailHost = smtpEmailHost;
	}

}
