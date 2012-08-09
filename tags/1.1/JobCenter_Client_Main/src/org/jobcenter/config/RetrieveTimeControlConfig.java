package org.jobcenter.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.jobcenter.client.timecontrol.TimeControl;
import org.jobcenter.client.timecontrol.TimeControlEntry;
import org.jobcenter.constants.ClientConstants;
import org.jobcenter.util.CurrentDateTime;

/**
 * 
 *
 */
public class RetrieveTimeControlConfig {

	private static Logger log = Logger.getLogger(RetrieveTimeControlConfig.class);

	private static final String MATCH_EVERY_DAY = "*";
	
	/**
	 * @throws Throwable
	 */
	public void loadTimeControlConfig() throws Throwable {


		if ( log.isInfoEnabled() ) {

	      URL fileURL = this.getClass().getClassLoader().getResource( ClientConstants.TIME_CONTROL_CONFIG_FILENAME );

	      if (fileURL != null && fileURL.getProtocol().equals("file")) {
	        /* A file path: easy enough */
	    	  
	    	  String filePath = fileURL.getPath();

		      log.info( "path to time control file:  " + filePath );
		      
		      log.info( "" );
		      log.info( "Start of processing of contents of time control file" );
	      }
		}
		

		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream( ClientConstants.TIME_CONTROL_CONFIG_FILENAME );
		
		if ( inputStream == null ) {
			
			log.error( "Time control file '" + ClientConstants.TIME_CONTROL_CONFIG_FILENAME + "' is not found so no time control in effect" );
			
			return;
			
		} 
		
		
		
		List<TimeControlEntry> timeControlEntries = new ArrayList<TimeControlEntry>();

		BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream ) );
		
		String line = null;
		
		while ( ( line = reader.readLine() ) != null ) {
			
			line = line.trim();
			
			if ( log.isInfoEnabled() ) {
				log.info( "Line in time control file:  " + line );
			}
			
			//  skip blank lines
			if ( line.isEmpty() ) {
				
				continue;
			}
			
			//  skip comment lines
			if ( line.startsWith( "#" ) ) {
				
				continue;
			}
			
			processLine( line, timeControlEntries );
		}
		
		log.info( "End of processing of contents of time control file" );

		
		
		TimeControl.setTimeControlEntries( timeControlEntries );
		
		if ( log.isInfoEnabled() ) {
			
			log.info( "Contents of timeControlEntries " + timeControlEntries );
			
			printTimeControlEntries( timeControlEntries );
		}
		
	}
	
	
	/**
	 * @param timeControlEntries
	 */
	private void printTimeControlEntries( List<TimeControlEntry> timeControlEntries ) {
		
		log.info( "Time control data as parsed.  Day zero is Sunday, Day 6 is Saturday, day 7 is the next Sunday." );
		
		for ( TimeControlEntry timeControlEntry : timeControlEntries ) {
			
			String formatStart = formatTime( timeControlEntry.getStartTimeMinutes() );
			String formatEnd = formatTime( timeControlEntry.getEndTimeMinutes() );
			
			log.info( "Entry Start: " + formatStart + ", End: " +formatEnd );
		}
	}
	

	/**
	 * @param minutes
	 * @return
	 */
	private String formatTime( int minutes ) {
		
		int day = ( minutes ) / ( 24 * 60 );
		int hour = ( minutes / 60 ) - ( day * 24);
		int minute = ( minutes ) - ( ( day * 24 * 60 ) + ( hour * 60 ) );
		
		String hourLeadSpaceString = "";
		
		if ( hour < 10 ) {
			
			hourLeadSpaceString = " ";
		}

		String minuteLeadSpaceString = "";
		
		if ( minute < 10 ) {
			
			minuteLeadSpaceString = " ";
		}
		

		String time = "Day: " + day + " Hour: " + hourLeadSpaceString + hour + " Minute: " + minuteLeadSpaceString + minute;
		
		return time;
	}
	
	
	
	/**
	 * @param line
	 * @param timeControlEntries
	 * @throws Throwable
	 */
	private  void processLine( String line, List<TimeControlEntry> timeControlEntries ) throws Throwable {
		

		// split line on white space

		String[] lineSplit =  line.split( "\\s+" );
		
		if ( lineSplit.length < 3 ) {
			
			
		} else {

			String dayOfWeekString = lineSplit[0];
			String startTimeString = lineSplit[1];
			String endTimeString = lineSplit[2];

			//			String dayOfWeekString = "sun".toLowerCase();
			//
			//			String startTimeString = "12:00";
			//			String endTimeString = "3:00";


			String[] startTimeSplit = startTimeString.split( ":" );
			String[] endTimeSplit = endTimeString.split( ":" );

			String startTimeHourString = startTimeSplit[0];
			String endTimeHourString = endTimeSplit[0];

			try {

				int startTimeHour = Integer.parseInt( startTimeHourString );
				int endTimeHour = Integer.parseInt( endTimeHourString );
				int startTimeMinute = 0;
				int endTimeMinute = 0;

				if ( startTimeSplit.length > 1 ) {

					String startTimeMinuteString = startTimeSplit[1];

					startTimeMinute = Integer.parseInt( startTimeMinuteString );
				}

				if ( endTimeSplit.length > 1 ) {

					String endTimeMinuteString = endTimeSplit[1];

					endTimeMinute = Integer.parseInt( endTimeMinuteString );
				}

				int startTimeDailyOffset = startTimeHour * 60 + startTimeMinute;
				int endTimeDailyOffset = endTimeHour * 60 + endTimeMinute;

				if ( endTimeDailyOffset < startTimeDailyOffset ) {
					
					//  add a day to end offset since it is the next day

					endTimeDailyOffset = endTimeDailyOffset + ( 24 * 60 );
				}
				
				
				

				if ( ! MATCH_EVERY_DAY.equals( dayOfWeekString ) ) {

					int dayOfWeek = getDayOfWeek( dayOfWeekString );
					
					createTimeControlEntries( timeControlEntries, dayOfWeek, startTimeDailyOffset, endTimeDailyOffset );
					
				} else {
					
					//  create entries for every day of the week
					
					for ( int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; dayOfWeek++ ) {
						
						createTimeControlEntries( timeControlEntries, dayOfWeek, startTimeDailyOffset, endTimeDailyOffset );
					}
					
				}


			} catch ( Throwable t ) {



				throw new Exception( "Unable to process entry in Time control file '" + ClientConstants.TIME_CONTROL_CONFIG_FILENAME + "' line = " + line );
			}

		}
	}
	
	
	
	/**
	 * @param timeControlEntries
	 * @param dayOfWeek
	 * @param startTimeDailyOffset
	 * @param endTimeDailyOffset
	 * @throws Throwable
	 */
	private void createTimeControlEntries( List<TimeControlEntry> timeControlEntries, int dayOfWeek, int startTimeDailyOffset, int endTimeDailyOffset ) 
	throws Throwable {
		

		//  TODO  Deal with removing overlaps

		int dayOffset = dayOfWeek - Calendar.SUNDAY;

		int startMinutes = ( dayOffset * 24 * 60 ) + startTimeDailyOffset;

		int endMinutes = ( ( dayOffset ) * 24 * 60 ) + endTimeDailyOffset;


		// if the end time is > Saturday night midnight, 
		//  split into two entries so first one ends at Saturday night midnight
		//  and second one starts at Sunday morning midnight
		
		if ( endMinutes > CurrentDateTime.MINUTES_TO_SATURDAY_EVENING_MIDNIGHT ) {
			
			createTimeControlEntriesPvt( timeControlEntries, startMinutes, CurrentDateTime.MINUTES_TO_SATURDAY_EVENING_MIDNIGHT );
			
			int endTimeNextWeek = endMinutes - CurrentDateTime.MINUTES_TO_SATURDAY_EVENING_MIDNIGHT;
			
			createTimeControlEntriesPvt( timeControlEntries, 0, endTimeNextWeek );
			
		} else {
			
			createTimeControlEntriesPvt( timeControlEntries, startMinutes, endMinutes );
		}
		

		
	}
	

	/**
	 * @param timeControlEntries
	 * @param dayOfWeek
	 * @param startTimeDailyOffset
	 * @param endTimeDailyOffset
	 * @throws Throwable
	 */
	private void createTimeControlEntriesPvt( List<TimeControlEntry> timeControlEntries, int startMinutes, int endMinutes ) 
	throws Throwable {
		

		
		int timeControlIndexForStart = 0;

		int timeControlIndexForEnd = 0;
		
		while ( timeControlIndexForStart < timeControlEntries.size() ) {
				
			TimeControlEntry entry = timeControlEntries.get( timeControlIndexForStart );

			//  exit at first entry where startMinutes is in this block or before it
			if ( entry.getEndTimeMinutes() >= startMinutes ) {
				
				break;
			}
			timeControlIndexForStart++;
		}
		
		
		while ( timeControlIndexForEnd < timeControlEntries.size() ) {
				
			TimeControlEntry entry = timeControlEntries.get( timeControlIndexForEnd );

			//  exit at first entry where endMinutes is in this entry or before it
			if ( entry.getEndTimeMinutes() >= endMinutes ) {
				
				break;
			}
			timeControlIndexForEnd++;
		}
		
		if ( timeControlIndexForStart >= timeControlEntries.size() ) {
			
			// add to end of list

			TimeControlEntry timeControlEntry = new TimeControlEntry();

			timeControlEntry.setStartTimeMinutes( startMinutes );
			timeControlEntry.setEndTimeMinutes( endMinutes );

			timeControlEntries.add( timeControlEntry );
			
		} else if ( timeControlIndexForEnd >= timeControlEntries.size() ) {
			
			// end is off list so update start entry and delete from there to end of list.
			
			TimeControlEntry startEntry = timeControlEntries.get( timeControlIndexForStart );
			
			if ( startEntry.getStartTimeMinutes() > startMinutes ) {
				
				startEntry.setStartTimeMinutes( startMinutes );
			}
			
			startEntry.setEndTimeMinutes( endMinutes );
			
			//  remove the rest of the entries on the list
			for ( int i = timeControlIndexForStart + 1; i < timeControlEntries.size(); i++ ) {
				timeControlEntries.remove( i );
			}
			
		} else if ( timeControlIndexForStart == timeControlIndexForEnd ) {
			
			//  start and end are either within or before the same entry
			
			TimeControlEntry entry = timeControlEntries.get( timeControlIndexForStart );

			if ( entry.getStartTimeMinutes() > endMinutes ) {
				
				//  the new entry is before this entry so insert a new entry before this entry
				
				TimeControlEntry timeControlEntry = new TimeControlEntry();

				timeControlEntry.setStartTimeMinutes( startMinutes );
				timeControlEntry.setEndTimeMinutes( endMinutes );

				timeControlEntries.add( timeControlIndexForStart, timeControlEntry );
				
			} else {
				
				//  the new block is within this entry so update this entry

				if ( entry.getStartTimeMinutes() > startMinutes ) {
					
					entry.setStartTimeMinutes( startMinutes );
				}
				
				if ( entry.getEndTimeMinutes() < endMinutes ) {
					
					entry.setEndTimeMinutes( endMinutes );
				}
			}

		} else {
			
			//  start and end are either within different entries or before them

			int lastIndexToUpdate = timeControlIndexForEnd;

			TimeControlEntry startEntry = timeControlEntries.get( timeControlIndexForStart );
			
			if ( startEntry.getStartTimeMinutes() > startMinutes ) {
				
				startEntry.setStartTimeMinutes( startMinutes );
			}
			

			TimeControlEntry endEntry = timeControlEntries.get( timeControlIndexForEnd );
			
			if ( endEntry.getStartTimeMinutes() > endMinutes ) {

				//  exclude this entry since it is outside the range of endMinutes
				lastIndexToUpdate--;
			}
			
			if ( timeControlIndexForStart == lastIndexToUpdate ) {
				
				//  now updating the same entry
				
				startEntry.setEndTimeMinutes( endMinutes );
				
			} else {
				
				//  combining entries
				
				//    set end minutes in the first entry
				
				startEntry.setEndTimeMinutes( endMinutes );
				
				endEntry = timeControlEntries.get( lastIndexToUpdate );
				
				if ( endEntry.getEndTimeMinutes() > endMinutes ) {
					
					//  update end minutes in the first entry if needed
					startEntry.setEndTimeMinutes( endEntry.getEndTimeMinutes() );
				}
				
				
				//  remove the entries on the list after the start and through the end
				for ( int i = timeControlIndexForStart + 1; i <= lastIndexToUpdate; i++ ) {
					timeControlEntries.remove( i );
				}

			}
			
		} 
		
		

	}
	
	
	/**
	 * @param dayOfWeekString
	 * @return
	 * @throws Throwable
	 */
	private int getDayOfWeek( String dayOfWeekString ) throws Throwable {
		
		int dayOfWeekNotSet = -1;
		
		boolean matchesMultipleDays = false;
		
		int dayOfWeek = dayOfWeekNotSet;
		
		String dayOfWeekStringLowerCase = dayOfWeekString.toLowerCase();
		
		if ( dayOfWeekStringLowerCase.startsWith( "su" ) ) {
			
			dayOfWeek = Calendar.SUNDAY;
			
		} else if ( dayOfWeekStringLowerCase.startsWith( "m" ) ){
			
			dayOfWeek = Calendar.MONDAY;
			
		} else if ( dayOfWeekStringLowerCase.startsWith( "tu" ) ){
			
			dayOfWeek = Calendar.TUESDAY;
			
		} else if ( dayOfWeekStringLowerCase.startsWith( "w" ) ){
			
			dayOfWeek = Calendar.WEDNESDAY;
			
		} else if ( dayOfWeekStringLowerCase.startsWith( "th" ) ){
			
			dayOfWeek = Calendar.THURSDAY;
			
		} else if ( dayOfWeekStringLowerCase.startsWith( "f" ) ){
			
			dayOfWeek = Calendar.FRIDAY;
			
		} else if ( dayOfWeekStringLowerCase.startsWith( "sa" ) ){
			
			dayOfWeek = Calendar.SATURDAY;
			
		} else {
			
			
			
		}
		
		
		if ( matchesMultipleDays ) {
			
			throw new Exception( "Day of week matches multiple days.  provided value = '" + dayOfWeekString + "'." );
		}

		if ( dayOfWeek == dayOfWeekNotSet ) {
			
			throw new Exception( "Day of week does not match a day.  provided value = '" + dayOfWeekString + "'." );
		}
		
		
		
		return dayOfWeek;
	}


}
