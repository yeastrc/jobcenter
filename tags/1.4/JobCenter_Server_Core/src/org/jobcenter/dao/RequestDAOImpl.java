package org.jobcenter.dao;

import java.util.List;
import org.hibernate.LockMode;
import org.jobcenter.dto.RequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 	* A data access object (DAO) providing persistence and search support for Request entities.
 			* Transaction control of the save(), update() and delete() operations 
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions. 
		Each of these methods provides additional information for how to configure it for the desired type of transaction control. 	
	 * @see org.jobcenter.dto.Request
  * @author MyEclipse Persistence Tools 
 */

public class RequestDAOImpl extends HibernateDaoSupport implements RequestDAO  {
	private static final Logger log = LoggerFactory.getLogger(RequestDAOImpl.class);
	//property constants

	public static RequestDAO getFromApplicationContext(ApplicationContext ctx) {
		return (RequestDAO) ctx.getBean("requestDAO");
	}


	protected void initDao() {
		//do nothing
	}
    
    public void save(RequestDTO transientInstance) {
        log.debug("saving RequestDTO instance");
        try {
            getHibernateTemplate().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
	public void delete(RequestDTO persistentInstance) {
        log.debug("deleting RequestDTO instance");
        try {
            getHibernateTemplate().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public RequestDTO findById( java.lang.Integer id) {
        log.debug("getting RequestDTO instance with id: " + id);
        try {
            RequestDTO instance = (RequestDTO) getHibernateTemplate()
                    .get("org.jobcenter.dto.RequestDTO", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
    
    
    public List findByExample(RequestDTO instance) {
        log.debug("finding RequestDTO instance by example");
        try {
            List results = getHibernateTemplate().findByExample(instance);
            log.debug("find by example successful, result size: " + results.size());
            return results;
        } catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }    
    
    /* (non-Javadoc)
	 * @see org.jobcenter.dao.RequestDTODAO#findByProperty(java.lang.String, java.lang.Object)
	 */
    public List findByProperty(String propertyName, Object value) {
      log.debug("finding RequestDTO instance with property: " + propertyName
            + ", value: " + value);
      try {
         String queryString = "from RequestDTO as model where model." 
         						+ propertyName + "= ?";
		 return getHibernateTemplate().find(queryString, value);
      } catch (RuntimeException re) {
         log.error("find by property name failed", re);
         throw re;
      }
	}


	/* (non-Javadoc)
	 * @see org.jobcenter.dao.RequestDTODAO#findAll()
	 */
	public List findAll() {
		log.debug("finding all RequestDTO instances");
		try {
			String queryString = "from RequestDTO";
		 	return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
    public RequestDTO merge(RequestDTO detachedInstance) {
        log.debug("merging RequestDTO instance");
        try {
            RequestDTO result = (RequestDTO) getHibernateTemplate()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(RequestDTO instance) {
        log.debug("attaching dirty RequestDTO instance");
        try {
            getHibernateTemplate().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(RequestDTO instance) {
        log.debug("attaching clean RequestDTO instance");
        try {
            getHibernateTemplate().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

}