package org.jobcenter.dao;

import java.util.List;
import java.util.Set;
import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.jobcenter.dto.RequestTypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 	* A data access object (DAO) providing persistence and search support for RequestType entities.
 			* Transaction control of the save(), update() and delete() operations
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions.
		Each of these methods provides additional information for how to configure it for the desired type of transaction control.
	 * @see org.jobcenter.dto.RequestType
  * @author MyEclipse Persistence Tools
 */

/**
 * @author DanJ
 *
 */
public class RequestTypeDAOImpl extends HibernateDaoSupport implements RequestTypeDAO  {
	     private static final Logger log = LoggerFactory.getLogger(RequestTypeDAOImpl.class);
		/* (non-Javadoc)
	 * @see org.jobcenter.dao.RequestTypeDAO#getFromApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public RequestTypeDAO getFromApplicationContext(ApplicationContext ctx) {
    	return (RequestTypeDAO) ctx.getBean("requestTypeDAO");
	}

	protected void initDao() {
		//do nothing
	}

    /* (non-Javadoc)
	 * @see org.jobcenter.dao.RequestTypeDAO#save(org.jobcenter.dto.RequestTypeDTO)
	 */
    public void save(RequestTypeDTO transientInstance) {
        log.debug("saving RequestTypeDTO instance");
        try {
            getHibernateTemplate().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.RequestTypeDAO#delete(org.jobcenter.dto.RequestTypeDTO)
	 */
	public void delete(RequestTypeDTO persistentInstance) {
        log.debug("deleting RequestTypeDTO instance");
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
	public List<RequestTypeDTO> findAllOrderedByName() {

        log.debug("findAllOrderedByName() " );

		DetachedCriteria  criteria = DetachedCriteria.forClass( RequestTypeDTO.class );

		criteria.addOrder( Order.asc( NAME ) );

		List<RequestTypeDTO> requestTypes = getHibernateTemplate().findByCriteria(criteria);

        log.debug("findAllOrderedByName() size of  requestTypes = " + requestTypes.size() );



		return requestTypes;

	}

    /* (non-Javadoc)
	 * @see org.jobcenter.dao.RequestTypeDAO#findById(java.lang.Integer)
	 */
    public RequestTypeDTO findById( java.lang.Integer id) {
        log.debug("getting RequestTypeDTO instance with id: " + id);
        try {
            RequestTypeDTO instance = (RequestTypeDTO) getHibernateTemplate()
                    .get("org.jobcenter.dto.RequestTypeDTO", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }


    /* (non-Javadoc)
	 * @see org.jobcenter.dao.RequestTypeDAO#findByExample(org.jobcenter.dto.RequestTypeDTO)
	 */
    public List findByExample(RequestTypeDTO instance) {
        log.debug("finding RequestTypeDTO instance by example");
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
	 * @see org.jobcenter.dao.RequestTypeDAO#findByProperty(java.lang.String, java.lang.Object)
	 */
    public List findByProperty(String propertyName, Object value) {
      log.debug("finding RequestTypeDTO instance with property: " + propertyName
            + ", value: " + value);
      try {
         String queryString = "from RequestTypeDTO as model where model."
         						+ propertyName + "= ?";
		 return getHibernateTemplate().find(queryString, value);
      } catch (RuntimeException re) {
         log.error("find by property name failed", re);
         throw re;
      }
	}

	/* (non-Javadoc)
	 * @see org.jobcenter.dao.RequestTypeDAO#findByName(java.lang.Object)
	 */
	public List findByName(Object name
	) {
		return findByProperty(NAME, name
		);
	}


	/* (non-Javadoc)
	 * @see org.jobcenter.dao.RequestTypeDAO#findOneRecordByName(java.lang.Object)
	 */
	public RequestTypeDTO findOneRecordByName(Object name) {

		List<RequestTypeDTO> list = findByProperty(NAME, name);

		if ( list == null || list.isEmpty() ) {
			return null;
		}

		return list.get( 0 );
	}


	/* (non-Javadoc)
	 * @see org.jobcenter.dao.RequestTypeDAO#findAll()
	 */
	public List findAll() {
		log.debug("finding all RequestTypeDTO instances");
		try {
			String queryString = "from RequestTypeDTO";
		 	return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

    /* (non-Javadoc)
	 * @see org.jobcenter.dao.RequestTypeDAO#merge(org.jobcenter.dto.RequestTypeDTO)
	 */
    public RequestTypeDTO merge(RequestTypeDTO detachedInstance) {
        log.debug("merging RequestTypeDTO instance");
        try {
            RequestTypeDTO result = (RequestTypeDTO) getHibernateTemplate()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    /* (non-Javadoc)
	 * @see org.jobcenter.dao.RequestTypeDAO#attachDirty(org.jobcenter.dto.RequestTypeDTO)
	 */
    public void attachDirty(RequestTypeDTO instance) {
        log.debug("attaching dirty RequestTypeDTO instance");
        try {
            getHibernateTemplate().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

    /* (non-Javadoc)
	 * @see org.jobcenter.dao.RequestTypeDAO#attachClean(org.jobcenter.dto.RequestTypeDTO)
	 */
    public void attachClean(RequestTypeDTO instance) {
        log.debug("attaching clean RequestTypeDTO instance");
        try {
            getHibernateTemplate().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}