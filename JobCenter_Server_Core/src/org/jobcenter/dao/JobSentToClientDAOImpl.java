package org.jobcenter.dao;

import java.util.List;
import org.hibernate.LockMode;
import org.jobcenter.dto.JobSentToClient;
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
 * @see org.jobcenter.dto.JobSentToClientDAO
 * @author MyEclipse Persistence Tools
 */

public class JobSentToClientDAOImpl extends HibernateDaoSupport implements JobSentToClientDAO  {
	
	
	private static final Logger log = LoggerFactory.getLogger(JobSentToClientDAOImpl.class);
	// property constants

	protected void initDao() {
		// do nothing
	}

	public static JobSentToClientDAO getFromApplicationContext(ApplicationContext ctx) {
		return (JobSentToClientDAO) ctx.getBean("jobSentToClientDAO");
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.JobSentToClientDAO#save(org.jobcenter.dto.JobSentToClient)
	 */
	@Override
	public void save(JobSentToClient transientInstance) {
		log.debug("saving JobSentToClient instance");
		try {
			
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jobcenter.dao.JobSentToClientDAO#saveOrUpdate(org.jobcenter.dto.JobSentToClient)
	 */
	@Override
	public void saveOrUpdate(JobSentToClient transientInstance) {
		log.debug("saving or updating JobSentToClient instance");
		try {
			
			getHibernateTemplate().saveOrUpdate(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("call to saveOrUpdate failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.JobSentToClientDAO#delete(org.jobcenter.dto.JobSentToClient)
	 */
	@Override
	public void delete(JobSentToClient persistentInstance) {
		log.debug("deleting JobSentToClient instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.JobSentToClientDAO#findById(java.lang.Integer)
	 */
	@Override
	public JobSentToClient findById(java.lang.Integer id) {
		log.debug("getting Job instance with id: " + id);
		try {
			JobSentToClient instance = (JobSentToClient) getHibernateTemplate().get(
					"org.jobcenter.dto.JobSentToClient", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.JobSentToClientDAO#findByExample(org.jobcenter.dto.JobSentToClient)
	 */
	@Override
	public List findByExample(JobSentToClient instance) {
		log.debug("finding JobSentToClient instance by example");
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
	 * @see org.jobcenter.dao.JobSentToClientDAO#findByProperty(java.lang.String, java.lang.Object)
	 */
	@Override
	public List findByProperty(String propertyName, Object value) {
		log.debug("finding Job instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from JobSentToClient as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

//	@Override
//	public List findByRequestId(Object requestId) {
//		return findByProperty(REQUEST_ID, requestId);
//	}


	/* (non-Javadoc)
	 * @see org.jobcenter.dao.JobSentToClientDAO#findAll()
	 */
	@Override
	public List findAll() {
		log.debug("finding all JobSentToClient instances");
		try {
			String queryString = "from JobSentToClient";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.JobSentToClientDAO#merge(org.jobcenter.dto.JobSentToClient)
	 */
	@Override
	public JobSentToClient merge(JobSentToClient detachedInstance) {
		log.debug("merging JobSentToClient instance");
		try {
			JobSentToClient result = (JobSentToClient) getHibernateTemplate().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.JobSentToClientDAO#attachDirty(org.jobcenter.dto.JobSentToClient)
	 */
	@Override
	public void attachDirty(JobSentToClient instance) {
		log.debug("attaching dirty JobSentToClient instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.JobSentToClientDAO#attachClean(org.jobcenter.dto.JobSentToClient)
	 */
	@Override
	public void attachClean(JobSentToClient instance) {
		log.debug("attaching clean JobSentToClient instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

}