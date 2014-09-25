package org.jobcenter.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import org.hibernate.LockMode;
import org.jobcenter.dto.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

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

public interface JobDAO  {

	public void save(Job transientInstance);

	public void saveOrUpdate(Job transientInstance);

	public void delete(Job persistentInstance);

	public Job findById(java.lang.Integer id);

	public List findByExample(Job instance);

	public List findByProperty(String propertyName, Object value);

	public List findByRequestId(Object requestId);

	public List findBySubmitter(Object submitter);

	public List findByPriority(Object priority);

	public List findByDbRecordVersionNumber(Object dbRecordVersionNumber);
	public List findAll();
	public Job merge(Job detachedInstance);

	public void attachDirty(Job instance);

	public void attachClean(Job instance);

}