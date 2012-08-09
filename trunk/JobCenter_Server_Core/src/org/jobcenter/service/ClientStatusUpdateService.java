package org.jobcenter.service;

import org.jobcenter.request.*;
import org.jobcenter.response.*;


public interface ClientStatusUpdateService  {

	public ClientStatusUpdateResponse clientStatusUpdateFromClient( ClientStatusUpdateRequest clientStatusUpdateRequest, String remoteHost );


}
