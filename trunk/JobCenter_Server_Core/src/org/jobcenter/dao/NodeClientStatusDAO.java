package org.jobcenter.dao;

import java.util.List;

import org.jobcenter.dto.NodeClientStatusDTO;

public interface NodeClientStatusDAO {

	public abstract void save(NodeClientStatusDTO transientInstance);

	public abstract void saveOrUpdate(NodeClientStatusDTO transientInstance);

	public abstract void delete(NodeClientStatusDTO persistentInstance);

	public abstract NodeClientStatusDTO findById(java.lang.Integer id);

	public abstract List findByExample(NodeClientStatusDTO instance);

	public abstract List findByProperty(String propertyName, Object value);

	public abstract List findByNodeId(Object nodeId);

	public abstract List findByLastCheckinTime(Object lastCheckinTime);

	public abstract List findByLateForNextCheckinTime(
			Object lateForNextCheckinTime);

	public abstract List findBySecondsUntilNextCheckin(
			Object secondsUntilNextCheckin);

	public abstract List findByDbRecordVersionNumber(
			Object dbRecordVersionNumber);

	public abstract List findAll();

    public List findByClientStartedTrueAndNotificationSendClientLateFalseAndLateForNextCheckinLessThanNow();

	public abstract NodeClientStatusDTO merge(
			NodeClientStatusDTO detachedInstance);

	public abstract void attachDirty(NodeClientStatusDTO instance);

	public abstract void attachClean(NodeClientStatusDTO instance);

}