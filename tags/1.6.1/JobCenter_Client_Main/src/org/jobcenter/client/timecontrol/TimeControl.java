package org.jobcenter.client.timecontrol;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jobcenter.config.ClientConfigDTO;
import org.jobcenter.util.CurrentDateTime;
import org.jobcenter.util.CurrentDateTimeImpl;

public class TimeControl {
	
	private static Logger log = Logger.getLogger(TimeControl.class);


	
	private static CurrentDateTime currentDateTime = new CurrentDateTimeImpl();

	private static List<TimeControlEntry> timeControlEntries = new ArrayList<TimeControlEntry>();
	
	
	private static long nextStartTimeMilliSeconds = 0;

	private static long nextStopTimeMilliSeconds = 0;

	
	//  The time blocks will be stored as the number of minutes since midnight Sunday.

	/**
	 * @return  true if currently in a block of time that is valid for retrieving jobs
	 */
	public static boolean inATimeWhenCanRetrieveJobs() {
		
		long nowInMilliseconds = currentDateTime.getNowInMilliseconds();
		
		if ( nowInMilliseconds < nextStartTimeMilliSeconds ) {
			
			return false;
		}

		if ( nowInMilliseconds > nextStartTimeMilliSeconds && nowInMilliseconds < nextStopTimeMilliSeconds ) {
			
			return true;
		}

		//  if time control not configured, return true
		
		if ( timeControlEntries == null || timeControlEntries.isEmpty() ) {
			
			return true;
		}
		
		int nowAsOffsetFromMidnightSunday = currentDateTime.computeNowAsOffsetFromMidnightSunday();
		
		for ( TimeControlEntry entry : timeControlEntries ) {
			
			if ( entry.getStartTimeMinutes() <= nowAsOffsetFromMidnightSunday 
					&& entry.getEndTimeMinutes() >= nowAsOffsetFromMidnightSunday ) {
				
				return true;
			}
			
			if ( entry.getStartTimeMinutes() > nowAsOffsetFromMidnightSunday ) {
				
				// found an entry that is after the current time so return false
				
				return false;
			}
		}
		
		
		return false;
	}
		
	/**
	 * @return  amount of time in minutes to wait to retrieve the next job
	 */
	public static int timeUntilCanRetrieveJob() {
		

		int waitTimeInSeconds = 0;

		//  if time control not configured, return normal
		
		if ( timeControlEntries == null || timeControlEntries.isEmpty() ) {
			
			waitTimeInSeconds = ClientConfigDTO.getSingletonInstance().getSleepTimeCheckingForNewJobs();
			return waitTimeInSeconds;
		}
		
		long nowInMilliseconds = currentDateTime.getNowInMilliseconds();

		if ( nowInMilliseconds < nextStartTimeMilliSeconds ) {
			
			long timeUntilNextStartTime = ( nextStartTimeMilliSeconds - nowInMilliseconds ) / 1000;
			waitTimeInSeconds = ( int ) timeUntilNextStartTime;
			return waitTimeInSeconds;
		}

		if ( nowInMilliseconds > nextStartTimeMilliSeconds && nowInMilliseconds < nextStopTimeMilliSeconds ) {
			
			waitTimeInSeconds = ClientConfigDTO.getSingletonInstance().getSleepTimeCheckingForNewJobs();
			return waitTimeInSeconds;
		}


		
		
		int nowAsOffsetFromMidnightSunday = currentDateTime.computeNowAsOffsetFromMidnightSunday();
		
//		Calendar.SUNDAY; // = 1
//		Calendar.SATURDAY; // = 7
		
		TimeControlEntry entry = null;
		
		int timeControlIndex = 0;
		
		while ( timeControlIndex < timeControlEntries.size() ) {
				
			entry = timeControlEntries.get( timeControlIndex );

			//  exit at first entry where end time is > now
			if ( entry.getEndTimeMinutes() >= nowAsOffsetFromMidnightSunday ) {
				
				break;
			}
			timeControlIndex++;
		}
		
		if ( timeControlIndex < timeControlEntries.size() ) {
			
			if ( entry.getStartTimeMinutes() <= nowAsOffsetFromMidnightSunday  ) {
				
				//  start time is < now so have now is within the range of can run
			
				waitTimeInSeconds = ClientConfigDTO.getSingletonInstance().getSleepTimeCheckingForNewJobs();
			
			} else {
				
				//  use next start time so can sleep until then
				
				waitTimeInSeconds = ( entry.getStartTimeMinutes() - nowAsOffsetFromMidnightSunday ) * 60 /* seconds */;
				
				nextStartTimeMilliSeconds = nowInMilliseconds + ( ( (long) (waitTimeInSeconds) ) * 1000 );
				
				nextStopTimeMilliSeconds = nextStartTimeMilliSeconds + ( ( (long) entry.getEndTimeMinutes() - entry.getStartTimeMinutes()) * 60 * 1000 );
			}
			
		} else {
			
			//  find next start time so can sleep until then, looped off end of list so start back at first entry
			
			entry = timeControlEntries.get( 0 );  //  get first entry
			
			int timeInMinutesFromNowToEndOfSaturday = CurrentDateTime.MINUTES_TO_SATURDAY_EVENING_MIDNIGHT - nowAsOffsetFromMidnightSunday;
			
			waitTimeInSeconds = ( timeInMinutesFromNowToEndOfSaturday + entry.getStartTimeMinutes() ) * 60 /* seconds */;

			nextStartTimeMilliSeconds = nowInMilliseconds + ( ( (long) (waitTimeInSeconds) ) * 1000 );
			
			nextStopTimeMilliSeconds = nextStartTimeMilliSeconds + ( ( (long) entry.getEndTimeMinutes() - entry.getStartTimeMinutes()) * 60 * 1000 );

		}
		
		return waitTimeInSeconds;
	}

	public static CurrentDateTime getCurrentDateTime() {
		return currentDateTime;
	}

	public static void setCurrentDateTime(CurrentDateTime currentDateTime) {
		TimeControl.currentDateTime = currentDateTime;
	}

	public static List<TimeControlEntry> getTimeControlEntries() {
		return timeControlEntries;
	}

	public static void setTimeControlEntries(
			List<TimeControlEntry> timeControlEntries) {
		TimeControl.timeControlEntries = timeControlEntries;
	}


}
