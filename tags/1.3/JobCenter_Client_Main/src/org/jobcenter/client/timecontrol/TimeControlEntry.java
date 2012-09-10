package org.jobcenter.client.timecontrol;

public class TimeControlEntry {

	private int startTimeMinutes;  // time in minutes from midnight on Sunday
	private int endTimeMinutes;    // time in minutes from midnight on Sunday
	
	@Override
	public String toString() {
		return "TimeControlEntry [startTimeMinutes=" + startTimeMinutes
				+ ", endTimeMinutes=" + endTimeMinutes + "]";
	}
	
	
	public int getStartTimeMinutes() {
		return startTimeMinutes;
	}
	public void setStartTimeMinutes(int startTimeMinutes) {
		this.startTimeMinutes = startTimeMinutes;
	}
	public int getEndTimeMinutes() {
		return endTimeMinutes;
	}
	public void setEndTimeMinutes(int endTimeMinutes) {
		this.endTimeMinutes = endTimeMinutes;
	}

}
