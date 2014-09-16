package org.jobcenter.response;


import javax.xml.bind.annotation.XmlRootElement;

import org.jobcenter.dto.RunDTO;

/**
 * The response to a get run request
 *
 */
@XmlRootElement(name = "getRunResponse")

public class GetRunResponse extends BaseResponse {

	private RunDTO run;

	public RunDTO getRun() {
		return run;
	}

	public void setRun(RunDTO run) {
		this.run = run;
	}


}
