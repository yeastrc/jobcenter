package org.jobcenter.moduleinterfaceimpl;


import org.jobcenter.job_client_module_interface.ModuleInterfaceJobProgress;

/**
 * Used to track the progress the module is making to process the job
 *
 */
public class ModuleInterfaceModuleJobProgressImpl implements  ModuleInterfaceJobProgress {

	private Long lastProgressPingTimestamp = null;

	private boolean supportsPercentComplete = false;

	private int percentComplete = 0;

	@Override
	public void progressPing() {

		lastProgressPingTimestamp = System.currentTimeMillis();
	}

	@Override
	public void supportsPercentComplete() {

		supportsPercentComplete = true;
	}

	@Override
	public void updatePercentComplete(int percentComplete) {

		lastProgressPingTimestamp = System.currentTimeMillis();

		this.percentComplete = percentComplete;
	}


	public boolean isSupportsPercentComplete() {
		return supportsPercentComplete;
	}

	public int getPercentComplete() {
		return percentComplete;
	}

	public Long getLastProgressPingTimestamp() {
		return lastProgressPingTimestamp;
	}

}
