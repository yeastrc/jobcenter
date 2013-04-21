package org.jobcenter.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.jobcenter.dto.JobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 	* A data access object (DAO) providing persistence and search support for JobType entities.
 			* Transaction control of the save(), update() and delete() operations 
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions. 
		Each of these methods provides additional information for how to configure it for the desired type of transaction control. 	
	 * @see org.jobcenter.dto.JobType
  * @author MyEclipse Persistence Tools 
 */

public class JobTypeDAOImpl extends HibernateDaoSupport implements JobTypeDAO { 
	
	private static final Logger log = LoggerFactory.getLogger(JobTypeDAOImpl.class);
	     
	     
	public static JobTypeDAO getFromApplicationContext(ApplicationContext ctx) {
    	return (JobTypeDAO) ctx.getBean("jobTypeDAO");
	}

	protected void initDao() {
		//do nothing
	}
    
	
	
	public JobType findOneRecordByName(Object name) {
		
		List<JobType> list = findByProperty(NAME, name);
		
		if ( list == null || list.isEmpty() ) {
			return null;
		}
		
		return list.get( 0 );
	}
	
	
    /* (non-Javadoc)
	 * @see org.jobcenter.dao.JobTypeDAO#save(org.jobcenter.dto.JobType)
	 */
    public void save(JobType transientInstance) {
        log.debug("saving JobType instance");
        try {
            getHibernateTemplate().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
	/* (non-Javadoc)
	 * @see org.jobcenter.dao.JobTypeDAO#delete(org.jobcenter.dto.JobType)
	 */
	public void delete(JobType persistentInstance) {
        log.debug("deleting JobType instance");
        try {
            getHibernateTemplate().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
	
	/**
	 * @return
	 */
	public List<JobType> findAllOrderedByName() {

        log.debug("findAllOrderedByName() " );

		DetachedCriteria  criteria = DetachedCriteria.forClass( JobType.class );
			
		criteria.addOrder( Order.asc( NAME ) );
			
		List<JobType> jobTypes = getHibernateTemplate().findByCriteria(criteria);
			
        log.debug("findAllOrderedByName() size of  jobTypes = " + jobTypes.size() );

		
		
		return jobTypes;
			
	}
    
    /* (non-Javadoc)
	 * @see org.jobcenter.dao.JobTypeDAO#findById(java.lang.Integer)
	 */
    public JobType findById( java.lang.Integer id) {
        log.debug("getting JobType instance with id: " + id);
        try {
            JobType instance = (JobType) getHibernateTemplate()
                    .get("org.jobcenter.dto.JobType", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
    
    
    /* (non-Javadoc)
	 * @see org.jobcenter.dao.JobTypeDAO#findByExample(org.jobcenter.dto.JobType)
	 */
    public List findByExample(JobType instance) {
        log.debug("finding JobType instance by example");
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
	 * @see org.jobcenter.dao.JobTypeDAO#findByProperty(java.lang.String, java.lang.Object)
	 */
    public List findByProperty(String propertyName, Object value) {
      log.debug("finding JobType instance with property: " + propertyName
            + ", value: " + value);
      try {
         String queryString = "from JobType as model where model." 
         						+ propertyName + "= ?";
		 return getHibernateTemplate().find(queryString, value);
      } catch (RuntimeException re) {
         log.error("find by property name failed", re);
         throw re;
      }
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.JobTypeDAO#findByPriority(java.lang.Object)
	 */
	public List findByPriority(Object priority
	) {
		return findByProperty(PRIORITY, priority
		);
	}
	
	/* (non-Javadoc)
	 * @see org.jobcenter.dao.JobTypeDAO#findByName(java.lang.Object)
	 */
	public List findByName(Object name
	) {
		return findByProperty(NAME, name
		);
	}
	
	/* (non-Javadoc)
	 * @see org.jobcenter.dao.JobTypeDAO#findByDescription(java.lang.Object)
	 */
	public List findByDescription(Object description
	) {
		return findByProperty(DESCRIPTION, description
		);
	}
	
	/* (non-Javadoc)
	 * @see org.jobcenter.dao.JobTypeDAO#findByEnabled(java.lang.Object)
	 */
	public List findByEnabled(Object enabled
	) {
		return findByProperty(ENABLED, enabled
		);
	}
	
	/* (non-Javadoc)
	 * @see org.jobcenter.dao.JobTypeDAO#findByModuleName(java.lang.Object)
	 */
	public List findByModuleName(Object moduleName
	) {
		return findByProperty(MODULE_NAME, moduleName
		);
	}
	
	/* (non-Javadoc)
	 * @see org.jobcenter.dao.JobTypeDAO#findByMinimumModuleVersion(java.lang.Object)
	 */
	public List findByMinimumModuleVersion(Object minimumModuleVersion
	) {
		return findByProperty(MINIMUM_MODULE_VERSION, minimumModuleVersion
		);
	}
	

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.JobTypeDAO#findAll()
	 */
	public List findAll() {
		log.debug("finding all JobType instances");
		try {
			String queryString = "from JobType";
		 	return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
    /* (non-Javadoc)
	 * @see org.jobcenter.dao.JobTypeDAO#merge(org.jobcenter.dto.JobType)
	 */
    public JobType merge(JobType detachedInstance) {
        log.debug("merging JobType instance");
        try {
            JobType result = (JobType) getHibernateTemplate()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    /* (non-Javadoc)
	 * @see org.jobcenter.dao.JobTypeDAO#attachDirty(org.jobcenter.dto.JobType)
	 */
    public void attachDirty(JobType instance) {
        log.debug("attaching dirty JobType instance");
        try {
            getHibernateTemplate().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    /* (non-Javadoc)
	 * @see org.jobcenter.dao.JobTypeDAO#attachClean(org.jobcenter.dto.JobType)
	 */
    public void attachClean(JobType instance) {
        log.debug("attaching clean JobType instance");
        try {
            getHibernateTemplate().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}