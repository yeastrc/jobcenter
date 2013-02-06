package org.jobcenter.dao;

import java.util.List;
import org.jobcenter.dtoserver.ConfigSystemDTO;

/**
 * A data access object (DAO) providing persistence and search support for Job
 * entities. Transaction control of the save(), update() and delete() operations
 * can directly support Spring container-managed transactions or they can be
 * augmented to handle user-managed Spring transactions. Each of these methods
 * provides additional information for how to configure it for the desired type
 * of transaction control.
 *
 * @see org.jobcenter.dto.Job
 * @author MyEclipse Persistence Tools
 */

public interface ConfigSystemDAO  {

	public void save(ConfigSystemDTO transientInstance);

	public void saveOrUpdate(ConfigSystemDTO transientInstance);

	public void delete(ConfigSystemDTO persistentInstance);

	public ConfigSystemDTO findById(java.lang.Integer id);

	public List findByExample(ConfigSystemDTO instance);

	public List findByProperty(String propertyName, Object value);

	public List findByConfigKey(Object configKey);

	public List findByConfigValue(Object configValue);

	public List findByDbRecordVersionNumber(Object dbRecordVersionNumber);
	public List findAll();
	public ConfigSystemDTO merge(ConfigSystemDTO detachedInstance);

	public void attachDirty(ConfigSystemDTO instance);

	public void attachClean(ConfigSystemDTO instance);

}