package org.jobcenter.dao;

import java.util.List;

import org.jobcenter.dto.RunDTO;

/**
 	* A data access object (DAO) providing persistence and search support for Run entities.
 			* Transaction control of the save(), update() and delete() operations
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions.
		Each of these methods provides additional information for how to configure it for the desired type of transaction control.
	 * @see org.jobcenter.dto.Run
  * @author MyEclipse Persistence Tools
 */

public interface RunDAO  {

		//property constants
	public static final String NODE_ID = "nodeId";
	public static final String JOB_ID = "jobId";
//	public static final String STATUS_ID = "statusId";


    public void saveOrUpdate(RunDTO instance);

    public void save(RunDTO transientInstance);

	public void delete(RunDTO persistentInstance);

    public RunDTO findById( java.lang.Integer id);

    public List findByNodeIdAndStatusId( Integer nodeId, Integer statusId );
    
    
    public List findByExample(RunDTO instance);
        

    public List findByProperty(String propertyName, Object value);

	public List findByNodeId(Object nodeId);
	public List findByJobId(Object jobId);

	//  does not work since not in hbm file
//	public List findByStatusId(Object statusId);

	public List findAll();

    public RunDTO merge(RunDTO detachedInstance);

    public void attachDirty(RunDTO instance);

    public void attachClean(RunDTO instance);
    	
    
}