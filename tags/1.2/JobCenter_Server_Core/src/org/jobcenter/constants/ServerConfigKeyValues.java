package org.jobcenter.constants;

public interface ServerConfigKeyValues {

	public static final String CLIENT_CHECKIN_WAIT_TIME = "client.checkin.wait.time";

	public static final String CLIENT_CHECKIN_OVERAGE_BEFORE_LATE_PERCENT = "client.checkin.overage.before.late.percent";

	public static final String CLIENT_CHECKIN_OVERAGE_BEFORE_LATE_MAX_VALUE = "client.checkin.overage.before.late.max.value";


	public static final String CLIENT_CHECKIN_NOTIFICATION_FROM_EMAIL_ADDRESS = "client.checkin.notification.from.email";

	public static final String CLIENT_CHECKIN_NOTIFICATION_TO_EMAIL_ADDRESS_LIST = "client.checkin.notification.to.email";

	public static final String CLIENT_CHECKIN_NOTIFICATION_SMTP_EMAIL_HOST = "client.checkin.notification.smtp.email.host";


	public static final String CLIENT_NORMAL_STARTUP_NOTIFICATION = "client.normal.startup.notification";
	
	public static final String CLIENT_STARTUP_CLIENT_NOT_PREV_SHUTDOWN_NOTIFICATION = "client.startup.not.prev.shutdown.notification";
	
	public static final String CLIENT_SHUTDOWN_NOTIFICATION = "client.shutdown.notification";
	

	//  Special default values

	public static final String CLIENT_CHECKIN_EMAIL_IN_PROVIDED_FILE = "***";
}

//INSERT INTO `job_processor`.`config_system` (`config_key`, `config_value`) VALUES ('client.normal.startup.notification', 'yes');
//
//INSERT INTO `job_processor`.`config_system` (`config_key`, `config_value`) VALUES ('client.startup.not.prev.shutdown.notification', 'yes');
//
//INSERT INTO `job_processor`.`config_system` (`config_key`, `config_value`) VALUES ('client.shutdown.notification', 'yes');