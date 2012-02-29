package org.jobcenter.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.jobcenter.dto.NodeClientStatusDTO;
import org.jobcenter.dto.RunDTO;
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

public class NodeClientStatusDAOImpl extends HibernateDaoSupport implements NodeClientStatusDAO {
	private static final Logger log = LoggerFactory.getLogger(NodeClientStatusDAOImpl.class);

	// property constants
	public static final String NODE_ID = "nodeId";
	public static final String LAST_CHECKIN_TIME = "lastCheckinTime";
	public static final String LATE_FOR_NEXT_CHECKIN_TIME = "lateForNextCheckinTime";
	public static final String SECONDS_UNTIL_NEXT_CHECKIN = "secondsUntilNextCheckin";
	public static final String DB_RECORD_VERSION_NUMBER = "dbRecordVersionNumber";

	protected void initDao() {
		// do nothing
	}

	public static NodeClientStatusDAO getFromApplicationContext(ApplicationContext ctx) {
		return (NodeClientStatusDAO) ctx.getBean("nodeClientStatusDAO");
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeClientStatusDAO#save(org.jobcenter.dto.NodeClientStatusDTO)
	 */
	@Override
	public void save(NodeClientStatusDTO transientInstance) {
		log.debug("saving NodeClientStatusDTO instance");
		try {

			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeClientStatusDAO#saveOrUpdate(org.jobcenter.dto.NodeClientStatusDTO)
	 */
	@Override
	public void saveOrUpdate(NodeClientStatusDTO transientInstance) {
		log.debug("saving or updating NodeClientStatusDTO instance");
		try {

			getHibernateTemplate().saveOrUpdate(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("call to saveOrUpdate failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeClientStatusDAO#delete(org.jobcenter.dto.NodeClientStatusDTO)
	 */
	@Override
	public void delete(NodeClientStatusDTO persistentInstance) {
		log.debug("deleting NodeClientStatusDTO instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeClientStatusDAO#findById(java.lang.Integer)
	 */
	@Override
	public NodeClientStatusDTO findById(java.lang.Integer id) {
		log.debug("getting NodeClientStatusDTO instance with id: " + id);
		try {
			NodeClientStatusDTO instance = (NodeClientStatusDTO) getHibernateTemplate().get(
					"org.jobcenter.dto.NodeClientStatusDTO", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeClientStatusDAO#findByExample(org.jobcenter.dto.NodeClientStatusDTO)
	 */
	@Override
	public List findByExample(NodeClientStatusDTO instance) {
		log.debug("finding NodeClientStatusDTO instance by example");
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
	 * @see org.jobcenter.dao.NodeClientStatusDAO#findByProperty(java.lang.String, java.lang.Object)
	 */
	@Override
	public List findByProperty(String propertyName, Object value) {
		log.debug("finding NodeClientStatusDTO instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from NodeClientStatusDTO as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeClientStatusDAO#findByNodeId(java.lang.Object)
	 */
	@Override
	public List findByNodeId(Object nodeId) {
		return findByProperty(NODE_ID, nodeId);
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeClientStatusDAO#findByLastCheckinTime(java.lang.Object)
	 */
	@Override
	public List findByLastCheckinTime(Object lastCheckinTime) {
		return findByProperty(LAST_CHECKIN_TIME, lastCheckinTime);
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeClientStatusDAO#findByLateForNextCheckinTime(java.lang.Object)
	 */
	@Override
	public List findByLateForNextCheckinTime(Object lateForNextCheckinTime) {
		return findByProperty(LATE_FOR_NEXT_CHECKIN_TIME, lateForNextCheckinTime);
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeClientStatusDAO#findBySecondsUntilNextCheckin(java.lang.Object)
	 */
	@Override
	public List findBySecondsUntilNextCheckin(Object secondsUntilNextCheckin) {
		return findByProperty(SECONDS_UNTIL_NEXT_CHECKIN, secondsUntilNextCheckin);
	}


	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeClientStatusDAO#findByDbRecordVersionNumber(java.lang.Object)
	 */
	@Override
	public List findByDbRecordVersionNumber(Object dbRecordVersionNumber) {
		return findByProperty(DB_RECORD_VERSION_NUMBER, dbRecordVersionNumber);
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeClientStatusDAO#findAll()
	 */
	@Override
	public List findAll() {
		log.debug("finding all NodeClientStatusDTO instances");
		try {
			String queryString = "from NodeClientStatusDTO";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}



	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeClientStatusDAO#findByLastCheckinTimeLessThanParameter(java.lang.Long)
	 */
	@Override
    public List findByLastCheckinTimeLessThanParameter(Date lessThanLastCheckinTime) {
        log.debug("finding By LastCheckinTime less than provided parameter");

        DetachedCriteria detachedCriteria = DetachedCriteria.forClass( NodeClientStatusDTO.class );

        detachedCriteria.add( Property.forName(LAST_CHECKIN_TIME ).lt( lessThanLastCheckinTime ) );

        //  cannot use since not in hbm file
//        detachedCriteria.add( Property.forName(STATUS_ID ).eq( instance.getStatusId() ) );

        try {
            List results = getHibernateTemplate().findByCriteria(detachedCriteria);

            if ( log.isDebugEnabled() ) {
            	log.debug("finding By LastCheckinTime less than provided parameter successful, result size: " + results.size());
            }
            return results;
        } catch (RuntimeException re) {
            log.error("finding By LastCheckinTime less than provided parameter failed", re);
            throw re;
        }
    }

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeClientStatusDAO#merge(org.jobcenter.dto.NodeClientStatusDTO)
	 */
	@Override
	public NodeClientStatusDTO merge(NodeClientStatusDTO detachedInstance) {
		log.debug("merging NodeClientStatusDTO instance");
		try {
			NodeClientStatusDTO result = (NodeClientStatusDTO) getHibernateTemplate().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeClientStatusDAO#attachDirty(org.jobcenter.dto.NodeClientStatusDTO)
	 */
	@Override
	public void attachDirty(NodeClientStatusDTO instance) {
		log.debug("attaching dirty NodeClientStatusDTO instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.NodeClientStatusDAO#attachClean(org.jobcenter.dto.NodeClientStatusDTO)
	 */
	@Override
	public void attachClean(NodeClientStatusDTO instance) {
		log.debug("attaching clean NodeClientStatusDTO instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

}