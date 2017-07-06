package org.jobcenter.webservice_response_objects;

/**
 * Sent to JS code on browser
 *
 */
public class JobParameterForGUI {

	private String paramKey;
	private String paramValue;
	
	public String getParamKey() {
		return paramKey;
	}
	public void setParamKey(String paramKey) {
		this.paramKey = paramKey;
	}
	public String getParamValue() {
		return paramValue;
	}
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
}
