package org.jobcenter.dao;

import java.util.List;
import org.jobcenter.dto.StatusDTO;

/**
 	* A data access object (DAO) providing persistence and search support for Status entities.
 			* Transaction control of the save(), update() and delete() operations 
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions. 
		Each of these methods provides additional information for how to configure it for the desired type of transaction control. 	
	 * @see org.jobcenter.dto.Status
  * @author MyEclipse Persistence Tools 
 */

public interface StatusDAO  {

	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";


    public void save(StatusDTO transientInstance);
    
	public void delete(StatusDTO persistentInstance);
    
    public StatusDTO findById( java.lang.Integer id);
    
    public List findByExample(StatusDTO instance);
    
    public List findByProperty(String propertyName, Object value);

	public List findByName(Object name );
	
	public List findByDescription(Object description );
	

	public List findAll();
	
    public StatusDTO merge(StatusDTO detachedInstance);

    public void attachDirty(StatusDTO instance);
    
    public void attachClean(StatusDTO instance);

}
