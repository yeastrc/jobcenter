package org.jobcenter.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import org.hibernate.LockMode;
import org.jobcenter.dto.Job;
import org.jobcenter.dtoserver.ConfigSystemDTO;
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

public class ConfigSystemDAOImpl extends HibernateDaoSupport implements ConfigSystemDAO {
	private static final Logger log = LoggerFactory.getLogger(ConfigSystemDAOImpl.class);

	// property constants
	public static final String CONFIG_KEY = "configKey";
	public static final String CONFIG_VALUE = "configValue";
	public static final String DB_RECORD_VERSION_NUMBER = "version";

	protected void initDao() {
		// do nothing
	}

	public static ConfigSystemDAO getFromApplicationContext(ApplicationContext ctx) {
		return (ConfigSystemDAO) ctx.getBean("configSystemDAO");
	}

	@Override
	public void save(ConfigSystemDTO transientInstance) {
		log.debug("saving ConfigSystemDTO instance");
		try {

			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	@Override
	public void saveOrUpdate(ConfigSystemDTO transientInstance) {
		log.debug("saving or updating ConfigSystemDTO instance");
		try {

			getHibernateTemplate().saveOrUpdate(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("call to saveOrUpdate failed", re);
			throw re;
		}
	}

	@Override
	public void delete(ConfigSystemDTO persistentInstance) {
		log.debug("deleting ConfigSystemDTO instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	@Override
	public ConfigSystemDTO findById(java.lang.Integer id) {
		log.debug("getting ConfigSystemDTO instance with id: " + id);
		try {
			ConfigSystemDTO instance = (ConfigSystemDTO) getHibernateTemplate().get(
					"org.jobcenter.dto.ConfigSystemDTO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	@Override
	public List findByExample(ConfigSystemDTO instance) {
		log.debug("finding ConfigSystemDTO instance by example");
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

	@Override
	public List findByProperty(String propertyName, Object value) {
		log.debug("finding ConfigSystemDTO instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from ConfigSystemDTO as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	@Override
	public List findByConfigKey(Object configKey) {
		return findByProperty(CONFIG_KEY, configKey);
	}

	@Override
	public List findByConfigValue(Object configValue) {
		return findByProperty(CONFIG_VALUE, configValue);
	}


	@Override
	public List findByDbRecordVersionNumber(Object dbRecordVersionNumber) {
		return findByProperty(DB_RECORD_VERSION_NUMBER, dbRecordVersionNumber);
	}

	@Override
	public List findAll() {
		log.debug("finding all ConfigSystemDTO instances");
		try {
			String queryString = "from ConfigSystemDTO";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	@Override
	public ConfigSystemDTO merge(ConfigSystemDTO detachedInstance) {
		log.debug("merging ConfigSystemDTO instance");
		try {
			ConfigSystemDTO result = (ConfigSystemDTO) getHibernateTemplate().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	@Override
	public void attachDirty(ConfigSystemDTO instance) {
		log.debug("attaching dirty ConfigSystemDTO instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	@Override
	public void attachClean(ConfigSystemDTO instance) {
		log.debug("attaching clean ConfigSystemDTO instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

}