package org.jobcenter.module;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.jobcenter.serverinterface.ServerConnection;

/**
 * pool of loaded and inactive modules
 *
 */
public class InactiveModulePool {

	private static Logger log = Logger.getLogger(InactiveModulePool.class);


	private Map<String,Queue<ModuleHolder>>  loadedModules = new HashMap<String,Queue<ModuleHolder>>();

	private static InactiveModulePool instance = new InactiveModulePool();

	/**
	 * private constructor for singleton
	 */
	private InactiveModulePool() {


	}

	/**
	 * @return singleton
	 */
	public static InactiveModulePool getInstance() {

		return instance;
	}


	/**
	 * removes the ModuleHolder object for a given module subdirectory and returns it if found, null if none found
	 *
	 * @param subdir
	 * @return
	 */
	public synchronized ModuleHolder pollModuleHolder( String subdir ) {

		Queue<ModuleHolder> moduleHolderQueue = loadedModules.get( subdir );

		if ( moduleHolderQueue == null ) {

			return null;
		}

		if ( moduleHolderQueue.isEmpty() ) {

			return null;
		}

		ModuleHolder moduleHolder = moduleHolderQueue.poll();

		return moduleHolder;
	}


	/**
	 * add the ModuleHolder object to the pool
	 *
	 * @param moduleHolder
	 */
	public synchronized void addModuleHolder( ModuleHolder moduleHolder ) {

		String subdir  = moduleHolder.getModuleConfigDTO().getModuleSubDirectory();

		Queue<ModuleHolder> moduleHolderQueue = loadedModules.get( subdir );

		if ( moduleHolderQueue == null ) {

			moduleHolderQueue = new LinkedList<ModuleHolder>();

			loadedModules.put( subdir, moduleHolderQueue );
		}

		moduleHolderQueue.offer( moduleHolder );


	}



	/**
	 * calls destroy() on all the modules and removes them from the pool.
	 *
	 */
	public synchronized void destroyModules( ) {
		

		for ( Map.Entry<String,Queue<ModuleHolder>> entry : loadedModules.entrySet() ) {

			Queue<ModuleHolder> moduleHolderQueue = entry.getValue();
			
			log.info( "Initial size of moduleHolderQueue queue  " + moduleHolderQueue.size() );
			
			// retrieve and remove an item from the queue 
			ModuleHolder moduleHolder = moduleHolderQueue.poll();

			while ( moduleHolder != null ) {

				try {
					log.info( "In destroyModules(): destroying module, name = " + moduleHolder.getModuleConfigDTO().getModuleName() 
							+ ", subdir = " + moduleHolder.getModuleConfigDTO().getModuleSubDirectory() );
				} catch (Throwable e) {

					log.info( "In destroyModules(): Error logging module name and subdir " + e.toString(), e );
				}
				
				
				try {
					moduleHolder.getModule().destroy();
				} catch (Throwable e) {

					log.info( "In destroyModules(): call to moduleHolder.getModule().destroy() threw Throwable " + e.toString(), e );
				}

				// retrieve and remove an item from the queue 
				moduleHolder = moduleHolderQueue.poll();
			}

		}


	}


}
