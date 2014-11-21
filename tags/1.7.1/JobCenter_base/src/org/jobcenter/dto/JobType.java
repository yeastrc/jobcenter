package org.jobcenter.dto;

/**
 * DTO for job_type table
 *
 */
public class JobType {

	private int id;

	private String name;

	private String description;
	private int priority;
	private Integer maxRequiredExecutionThreads;

	private Boolean enabled;
	private String moduleName;
	private int minimumModuleVersion;


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public Integer getMaxRequiredExecutionThreads() {
		return maxRequiredExecutionThreads;
	}
	public void setMaxRequiredExecutionThreads(Integer maxRequiredExecutionThreads) {
		this.maxRequiredExecutionThreads = maxRequiredExecutionThreads;
	}

	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public int getMinimumModuleVersion() {
		return minimumModuleVersion;
	}
	public void setMinimumModuleVersion(int minimumModuleVersion) {
		this.minimumModuleVersion = minimumModuleVersion;
	}
}
