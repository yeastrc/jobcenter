package org.jobcenter.dao;

import java.util.List;

import org.jobcenter.dto.JobType;

public interface JobTypeDAO {

	//property constants
	public static final String PRIORITY = "priority";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String ENABLED = "enabled";
	public static final String MODULE_NAME = "moduleName";
	public static final String MINIMUM_MODULE_VERSION = "minimumModuleVersion";

	
	/**
	 * Find by name will always return only 1 record or null since name field is unique index
	 * @param name
	 * @return
	 */
	public abstract JobType findOneRecordByName(Object name);

	public abstract void save(JobType transientInstance);

	public abstract void delete(JobType persistentInstance);

	public List<JobType> findAllOrderedByName();

	public abstract JobType findById(java.lang.Integer id);

	public abstract List findByExample(JobType instance);

	public abstract List findByProperty(String propertyName, Object value);

	public abstract List findByPriority(Object priority);

	public abstract List findByName(Object name);

	public abstract List findByDescription(Object description);

	public abstract List findByEnabled(Object enabled);

	public abstract List findByModuleName(Object moduleName);

	public abstract List findByMinimumModuleVersion(Object minimumModuleVersion);

	public abstract List findAll();

	public abstract JobType merge(JobType detachedInstance);

	public abstract void attachDirty(JobType instance);

	public abstract void attachClean(JobType instance);

}