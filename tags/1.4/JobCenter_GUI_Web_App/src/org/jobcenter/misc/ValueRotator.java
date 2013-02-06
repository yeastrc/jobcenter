package org.jobcenter.misc;

/**
 * This class will accept a colon delimited list and return each value when the getValue() method is called.
 * @author DanJ
 *
 */
public class ValueRotator {
	
	private int currentItem = 0;
	
	private String value;
	private String[] values;

	public String getValue() {
		String currentValue = values[currentItem];
		currentItem++;
		if ( currentItem >= values.length ) {
			currentItem = 0;
		}
		return currentValue;
	}

	public void setValue(String value) {
		this.value = value;
		values = value.split(":");
	}
	
}
