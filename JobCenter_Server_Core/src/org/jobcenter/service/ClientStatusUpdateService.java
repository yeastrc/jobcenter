package org.jobcenter.service;

import org.jobcenter.request.*;
import org.jobcenter.response.*;


public interface ClientStatusUpdateService  {

	public ClientStatusUpdateResponse clientStatusUpdate( ClientStatusUpdateRequest clientStatusUpdateRequest, String remoteHost );


}
