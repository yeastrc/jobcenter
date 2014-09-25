package org.jobcenter.dao;

import java.util.List;

import org.jobcenter.dto.Node;

public interface NodeDAO {

	// property constants
	public static final String ID = "Id";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";

	/**
	 * @param transientInstance
	 */
	public abstract void save(Node transientInstance);

	/**
	 * @param transientInstance
	 */
	public abstract void saveOrUpdate(Node transientInstance);

	/**
	 * @param persistentInstance
	 */
	public abstract void delete(Node persistentInstance);

	/**
	 * @param id
	 * @return
	 */
	public abstract Node findById(java.lang.Integer id);

	/**
	 * @param instance
	 * @return
	 */
	public abstract List findByExample(Node instance);

	/**
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public abstract List findByProperty(String propertyName, Object value);

	/**
	 * @param id
	 * @return
	 */
	public abstract List findById(Object id);

	/**
	 * Return a single object since there is a UNIQUE index on Name
	 *
	 * @param name
	 * @return
	 */
	public Node findByName(String name);

	/**
	 * @param description
	 * @return
	 */
	public abstract List findByDescription(Object description);

	/**
	 * @return
	 */
	public abstract List findAll();

	/**
	 * @param detachedInstance
	 * @return
	 */
	public abstract Node merge(Node detachedInstance);

	/**
	 * @param instance
	 */
	public abstract void attachDirty(Node instance);

	/**
	 * @param instance
	 */
	public abstract void attachClean(Node instance);

}