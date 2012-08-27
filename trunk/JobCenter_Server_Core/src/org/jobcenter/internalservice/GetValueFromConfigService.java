package org.jobcenter.internalservice;


public interface GetValueFromConfigService {
	/**
	 * Retrieve from config table the value from the key provided.
	 * If not found, returns null.
	 * 
	 * If config table value start with "Y", "y", "T", "t", or "1", returns true, otherwise returns false
	 * 
	 * @param configKey
	 * @return
	 */
	public Boolean getConfigValueAsBoolean( String configKey );

	/**
	 * Retrieve from config table the value from the key provided.
	 * If not found or unparsable, returns null.
	 * 
	 * @param configKey
	 *
	 * @return
	 */
	public abstract Integer getConfigValueAsInteger(String configKey);

	/**
	 * Retrieve from config table the value from the key provided.
	 * If not found returns null.
	 * 
	 * @param configKey
	 *
	 * @return
	 */
	public String getConfigValueAsString( String configKey );
}