package org.jobcenter.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.jobcenter.dto.JobType;
import org.jobcenter.dto.RequestTypeDTO;
import org.springframework.context.ApplicationContext;

public interface RequestTypeDAO {

	//property constants
	public static final String NAME = "name";

	public abstract void save(RequestTypeDTO transientInstance);

	public abstract void delete(RequestTypeDTO persistentInstance);

	public List<RequestTypeDTO> findAllOrderedByName();
	
	public abstract RequestTypeDTO findById(java.lang.Integer id);

	public abstract List findByExample(RequestTypeDTO instance);

	public abstract List findByProperty(String propertyName, Object value);

	public abstract List findByName(Object name);

	/**
	 * Find by name will always return only 1 record or null since name field is unique index
	 * @param name
	 * @return
	 */
	public abstract RequestTypeDTO findOneRecordByName(Object name);

	public abstract List findAll();

	public abstract RequestTypeDTO merge(RequestTypeDTO detachedInstance);

	public abstract void attachDirty(RequestTypeDTO instance);

	public abstract void attachClean(RequestTypeDTO instance);

}