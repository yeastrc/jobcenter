package org.jobcenter.dao;

import java.util.List;
import org.hibernate.LockMode;
import org.jobcenter.dto.Node;
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
 * @author MyEclipse Persistence Tools
 */

/**
 * @author DanJ
 *
 */
public class NodeDAOImpl extends HibernateDaoSupport implements NodeDAO {
	private static final Logger log = LoggerFactory.getLogger(NodeDAOImpl.class);

	protected void initDao() {
		// do nothing
	}

	public static NodeDAO getFromApplicationContext(ApplicationContext ctx) {
		return (NodeDAO) ctx.getBean("nodeDAO");
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeDAO#save(org.jobcenter.dto.Node)
	 */
	@Override
	public void save(Node transientInstance) {
		log.debug("saving Node instance");
		try {

			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeDAO#saveOrUpdate(org.jobcenter.dto.Node)
	 */
	@Override
	public void saveOrUpdate(Node transientInstance) {
		log.debug("saving or updating Node instance");
		try {

			getHibernateTemplate().saveOrUpdate(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("call to saveOrUpdate failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeDAO#delete(org.jobcenter.dto.Node)
	 */
	@Override
	public void delete(Node persistentInstance) {
		log.debug("deleting Node instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeDAO#findById(java.lang.Integer)
	 */
	@Override
	public Node findById(java.lang.Integer id) {
		log.debug("getting Node instance with id: " + id);
		try {
			Node instance = (Node) getHibernateTemplate().get(
					"org.jobcenter.dto.Node", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeDAO#findByExample(org.jobcenter.dto.Node)
	 */
	@Override
	public List findByExample(Node instance) {
		log.debug("finding Node instance by example");
		try {
			List results = getHibernateTemplate().findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeDAO#findByProperty(java.lang.String, java.lang.Object)
	 */
	@Override
	public List findByProperty(String propertyName, Object value) {
		log.debug("finding Node instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from Node as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeDAO#findById(java.lang.Object)
	 */
	@Override
	public List findById(Object id) {
		return findByProperty(ID, id);
	}

	/*
	 * Return a single object since there is a UNIQUE index on Name
	 * (non-Javadoc)
	 * @see org.jobcenter.dao.NodeDAO#findByName(String)
	 */
	@Override
	public Node findByName(String name) {

		List findResults = findByProperty(NAME, name);

		if ( findResults == null || findResults.isEmpty() ) {

			return null;
		}

		Node node = (Node) findResults.get( 0 );

		return node;
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeDAO#findByDescription(java.lang.Object)
	 */
	@Override
	public List findByDescription(Object description) {
		return findByProperty(DESCRIPTION, description);
	}


	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeDAO#findAll()
	 */
	@Override
	public List findAll() {
		log.debug("finding all Node instances");
		try {
			String queryString = "from Node";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}



	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeDAO#merge(org.jobcenter.dto.Node)
	 */
	@Override
	public Node merge(Node detachedInstance) {
		log.debug("merging Node instance");
		try {
			Node result = (Node) getHibernateTemplate().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeDAO#attachDirty(org.jobcenter.dto.Node)
	 */
	@Override
	public void attachDirty(Node instance) {
		log.debug("attaching dirty Node instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeDAO#attachClean(org.jobcenter.dto.Node)
	 */
	@Override
	public void attachClean(Node instance) {
		log.debug("attaching clean Node instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

}