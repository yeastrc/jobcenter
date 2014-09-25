package org.jobcenter.util;

import java.util.Calendar;

public class CurrentDateTimeImpl implements CurrentDateTime {

	@Override
	public int computeNowAsOffsetFromMidnightSunday() {

		Calendar todayCalendar = Calendar.getInstance();

		int todayDay = todayCalendar.get( Calendar.DAY_OF_WEEK );
		int todayHour = todayCalendar.get( Calendar.HOUR_OF_DAY );
		int todayMinute = todayCalendar.get( Calendar.MINUTE );

		int dayOffset = todayDay - Calendar.SUNDAY;

		int nowAsOffsetFromMidnightSunday = ( dayOffset * 24 * 60 ) + ( todayHour * 60 ) + todayMinute;

		return nowAsOffsetFromMidnightSunday;
	}

	@Override
	public long getNowInMilliseconds() {
		
		long nowInMilliseconds = System.currentTimeMillis();
		
		return nowInMilliseconds;
	}
}
