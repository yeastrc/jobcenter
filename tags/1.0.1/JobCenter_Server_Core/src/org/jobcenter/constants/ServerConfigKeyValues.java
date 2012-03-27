package org.jobcenter.constants;

public interface ServerConfigKeyValues {

	public static final String CLIENT_CHECKIN_WAIT_TIME = "client.checkin.wait.time";

	public static final String CLIENT_CHECKIN_OVERAGE_BEFORE_LATE_PERCENT = "client.checkin.overage.before.late.percent";

	public static final String CLIENT_CHECKIN_OVERAGE_BEFORE_LATE_MAX_VALUE = "client.checkin.overage.before.late.max.value";


	public static final String CLIENT_CHECKIN_NOTIFICATION_FROM_EMAIL_ADDRESS = "client.checkin.notification.from.email";

	public static final String CLIENT_CHECKIN_NOTIFICATION_TO_EMAIL_ADDRESS_LIST = "client.checkin.notification.to.email";

	public static final String CLIENT_CHECKIN_NOTIFICATION_SMTP_EMAIL_HOST = "client.checkin.notification.smtp.email.host";



	//  Special default values

	public static final String CLIENT_CHECKIN_EMAIL_IN_PROVIDED_FILE = "***";
}
