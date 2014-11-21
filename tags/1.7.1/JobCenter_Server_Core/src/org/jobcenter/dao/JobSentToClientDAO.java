package org.jobcenter.dao;

import java.util.List;

import org.jobcenter.dto.JobSentToClient;

public interface JobSentToClientDAO {

	public abstract void save(JobSentToClient transientInstance);

	public abstract void saveOrUpdate(JobSentToClient transientInstance);

	public abstract void delete(JobSentToClient persistentInstance);

	public abstract JobSentToClient findById(java.lang.Integer id);

	public abstract List findByExample(JobSentToClient instance);

	public abstract List findByProperty(String propertyName, Object value);

	public abstract List findAll();

	public abstract JobSentToClient merge(JobSentToClient detachedInstance);

	public abstract void attachDirty(JobSentToClient instance);

	public abstract void attachClean(JobSentToClient instance);

}