package org.jobcenter.dao;

import java.util.List;

import org.jobcenter.dto.RequestDTO;

public interface RequestDAO {

	public abstract void save(RequestDTO transientInstance);

	public abstract void delete(RequestDTO persistentInstance);

	public abstract RequestDTO findById(java.lang.Integer id);

	public abstract List findByExample(RequestDTO instance);

	public abstract List findByProperty(String propertyName, Object value);

	public abstract List findAll();

	public abstract RequestDTO merge(RequestDTO detachedInstance);

	public abstract void attachDirty(RequestDTO instance);

	public abstract void attachClean(RequestDTO instance);

}