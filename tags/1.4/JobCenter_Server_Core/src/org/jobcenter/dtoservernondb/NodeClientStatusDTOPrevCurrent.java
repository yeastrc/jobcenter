package org.jobcenter.dtoservernondb;

import org.jobcenter.dto.NodeClientStatusDTO;

/**
 * When updating a record for NodeClientStatusDTO, the prev values and the current/updated values.
 *
 */
public class NodeClientStatusDTOPrevCurrent {

	public NodeClientStatusDTO getPrevNodeClientStatusDTO() {
		return prevNodeClientStatusDTO;
	}
	public void setPrevNodeClientStatusDTO(
			NodeClientStatusDTO prevNodeClientStatusDTO) {
		this.prevNodeClientStatusDTO = prevNodeClientStatusDTO;
	}
	public NodeClientStatusDTO getCurrentNodeClientStatusDTO() {
		return currentNodeClientStatusDTO;
	}
	public void setCurrentNodeClientStatusDTO(
			NodeClientStatusDTO currentNodeClientStatusDTO) {
		this.currentNodeClientStatusDTO = currentNodeClientStatusDTO;
	}
	NodeClientStatusDTO prevNodeClientStatusDTO;
	NodeClientStatusDTO currentNodeClientStatusDTO;
}
