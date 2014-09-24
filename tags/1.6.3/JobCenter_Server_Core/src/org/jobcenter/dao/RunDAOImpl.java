package org.jobcenter.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.jobcenter.dto.RunDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 	* A data access object (DAO) providing persistence and search support for Run entities.
 			* Transaction control of the save(), update() and delete() operations
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions.
		Each of these methods provides additional information for how to configure it for the desired type of transaction control.
	 * @see org.jobcenter.dto.Run
  * @author MyEclipse Persistence Tools
 */

public class RunDAOImpl extends HibernateDaoSupport implements RunDAO  {
	     private static final Logger log = LoggerFactory.getLogger(RunDAOImpl.class);
		//property constants
	public static final String NODE_ID = "nodeId";
	public static final String JOB_ID = "jobId";


	//  cannot use since not in hbm file
//	public static final String STATUS_ID = "statusId";


	public static RunDAOImpl getFromApplicationContext(ApplicationContext ctx) {
    	return (RunDAOImpl) ctx.getBean("runDAO");
	}


	protected void initDao() {
		//do nothing
	}


	@Override
    public void saveOrUpdate(RunDTO instance) {
        log.debug("saveOrUpdate RunDTO instance");
        try {
            getHibernateTemplate().saveOrUpdate(instance);
            log.debug("saveOrUpdate successful");
        } catch (RuntimeException re) {
            log.error("saveOrUpdate failed", re);
            throw re;
        }
    }


	@Override
    public void save(RunDTO transientInstance) {
        log.debug("saving RunDTO instance");
        try {
            getHibernateTemplate().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }

	@Override
	public void delete(RunDTO persistentInstance) {
        log.debug("deleting RunDTO instance");
        try {
            getHibernateTemplate().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }

	@Override
    public RunDTO findById( java.lang.Integer id) {
        log.debug("getting RunDTO instance with id: " + id);
        try {
            RunDTO instance = (RunDTO) getHibernateTemplate()
                    .get("org.jobcenter.dto.RunDTO", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }


	@Override
    public List findByNodeIdAndStatusId( Integer nodeId, Integer statusId ) {
        log.debug("finding By NodeId And StatusId");

        DetachedCriteria detachedCriteria = DetachedCriteria.forClass( RunDTO.class );

        detachedCriteria.add( Property.forName(NODE_ID ).eq( nodeId ) );

        //  create sub criteria on status type since the id is on that table in the hbm.xml mapping file
        detachedCriteria.createCriteria( "status" ).add( Property.forName( "id"  ).eq( statusId ) );

        //  cannot use since not in hbm file
//        detachedCriteria.add( Property.forName(STATUS_ID ).eq( instance.getStatusId() ) );

        try {
            List results = getHibernateTemplate().findByCriteria(detachedCriteria);

            if ( log.isDebugEnabled() ) {
            	log.debug("finding By NodeId And StatusId, result size: " + results.size());
            }
            return results;
        } catch (RuntimeException re) {
            log.error("Exception finding By NodeId And StatusId", re);
            throw re;
        }
    }


	@Override
    public List findByExample(RunDTO instance) {
        log.debug("finding RunDTO instance by example");
        try {
            List results = getHibernateTemplate().findByExample(instance);
            log.debug("find by example successful, result size: " + results.size());
            return results;
        } catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }

	@Override
    public List findByProperty(String propertyName, Object value) {
      log.debug("finding RunDTO instance with property: " + propertyName
            + ", value: " + value);
      try {
         String queryString = "from RunDTO as model where model."
         						+ propertyName + "= ?";
		 return getHibernateTemplate().find(queryString, value);
      } catch (RuntimeException re) {
         log.error("find by property name failed", re);
         throw re;
      }
	}

	@Override
	public List findByNodeId(Object nodeId
	) {
		return findByProperty(NODE_ID, nodeId
		);
	}

	@Override
	public List findByJobId(Object jobId
	) {
		return findByProperty(JOB_ID, jobId
		);
	}

//	@Override
//	public List findByStatusId(Object statusId
//	) {
//		return findByProperty(STATUS_ID, statusId
//		);
//	}

	@Override
	public List findAll() {
		log.debug("finding all RunDTO instances");
		try {
			String queryString = "from RunDTO";
		 	return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	@Override
    public RunDTO merge(RunDTO detachedInstance) {
        log.debug("merging RunDTO instance");
        try {
            RunDTO result = (RunDTO) getHibernateTemplate()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

	@Override
    public void attachDirty(RunDTO instance) {
        log.debug("attaching dirty RunDTO instance");
        try {
            getHibernateTemplate().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

	@Override
    public void attachClean(RunDTO instance) {
        log.debug("attaching clean RunDTO instance");
        try {
            getHibernateTemplate().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}