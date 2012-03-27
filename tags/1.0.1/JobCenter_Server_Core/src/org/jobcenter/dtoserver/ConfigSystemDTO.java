package org.jobcenter.dtoserver;

public class ConfigSystemDTO {

	private Integer id;
	private String configKey;
	private String configValue;
	private Integer version;


	@Override
	public String toString() {
		return "ConfigSystemDTO [configKey=" + configKey + ", configValue="
				+ configValue + ", id=" + id + ", version=" + version + "]";
	}
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getConfigKey() {
		return configKey;
	}
	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}
	public String getConfigValue() {
		return configValue;
	}
	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
}
