package org.jobcenter.service;

import org.jobcenter.request.*;
import org.jobcenter.response.*;


public interface ClientStartupService  {

	public ClientStartupResponse clientStartup( ClientStartupRequest clientStartupRequest, String remoteHost );


}
