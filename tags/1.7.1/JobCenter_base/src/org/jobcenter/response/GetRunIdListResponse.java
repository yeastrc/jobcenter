package org.jobcenter.response;


import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.jobcenter.dto.RunDTO;

/**
 * The response to a get run id list request
 *
 */
@XmlRootElement(name = "getRunIdListResponse")

public class GetRunIdListResponse extends BaseResponse {

	private List<Integer> runIdList;

	public List<Integer> getRunIdList() {
		return runIdList;
	}

	public void setRunIdList(List<Integer> runIdList) {
		this.runIdList = runIdList;
	}


}
