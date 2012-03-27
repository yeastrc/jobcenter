package org.jobcenter.moduleinterfaceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jobcenter.dto.RunMessageDTO;
import org.jobcenter.job_client_module_interface.ModuleInterfaceResponse;

public class ModuleInterfaceModuleResponseImpl implements ModuleInterfaceResponse {


	private int statusCode;

	private List<RunMessageDTO> runMessages;

	private Map<String, String> runOutputParams;


	@Override
	public void setStatusCode(int statusCode) {

		this.statusCode = statusCode;
	}


	@Override
	public void addRunMessage(int type, String message) {

		RunMessageDTO runMessageDTO = new RunMessageDTO();

		runMessageDTO.setType( type );
		runMessageDTO.setMessage( message );

		runMessages.add( runMessageDTO );
	}


	@Override
	public void addRunOutputParam(String key, String value) {

		runOutputParams.put( key, value );
	}


	public int getStatusCode() {
		return statusCode;
	}




	public List<RunMessageDTO> getRunMessages() {
		return runMessages;
	}


	public void setRunMessages(List<RunMessageDTO> runMessages) {
		this.runMessages = runMessages;
	}


	public Map<String, String> getRunOutputParams() {
		return runOutputParams;
	}


	public void setRunOutputParams(Map<String, String> runOutputParams) {
		this.runOutputParams = runOutputParams;
	}


}
