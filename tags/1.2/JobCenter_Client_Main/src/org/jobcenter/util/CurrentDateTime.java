package org.jobcenter.util;


public interface CurrentDateTime {

	public int MINUTES_TO_SATURDAY_EVENING_MIDNIGHT = ( 7 /*days*/ * 24 /*hours */ * 60 /* minutes */  );
	
	/**
	 * @return
	 */
	public int computeNowAsOffsetFromMidnightSunday();
	
	
	public long getNowInMilliseconds();
}
