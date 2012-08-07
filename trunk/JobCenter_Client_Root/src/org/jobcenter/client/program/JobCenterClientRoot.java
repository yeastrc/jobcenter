package org.jobcenter.client.program;

import java.util.Date;

import org.jobcenter.client.coreinterfaces.ClientMainIF;


import org.jobcenter.util.JobCenterClientRootBuildURLClassLoader;


public class JobCenterClientRoot {


	private static final String CLIENT_MAIN_FILE_PATH = "jobcenter_client_code/client_main";

	private static final String CLIENT_MAIN_LAUNCH_CLASS = "org.jobcenter.main.ClientMain";


	private ClientMainIF clientMainIF;


	public void execute()   throws Throwable {


		System.out.print( "System.getProperty(\"java.class.path\"): " );
		System.out.println( System.getProperty("java.class.path") );



		try {


			JobCenterClientRootBuildURLClassLoader buildURLClassLoader = new JobCenterClientRootBuildURLClassLoader();

			ClassLoader mainClassLoader = buildURLClassLoader.getURLClassLoaderForBaseDirectory( CLIENT_MAIN_FILE_PATH );


			Class<ClientMainIF> classToRun  = (Class<ClientMainIF>) mainClassLoader.loadClass( CLIENT_MAIN_LAUNCH_CLASS );

			clientMainIF = classToRun.newInstance();

			clientMainIF.runClientMain( JobCenterClientRoot.class.getClassLoader() );


		} catch (Throwable e1) {

			System.out.println( "JobCenterClientRoot: Exception: now(): " + new Date() );

			e1.printStackTrace();
		}

	}




	/**
	 * calls clientMainIF.shutdown();
	 */
	public void shutdown() {

		if ( clientMainIF != null ) {

			clientMainIF.shutdown();
		}
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) throws Throwable {


		System.out.println( "JobCenterClientRoot::main() Starting,  now(): " + new Date() );

		try {



			JobCenterClientRoot jobCenterClientRoot = new JobCenterClientRoot();


			JobCenterClientRootShutdown jobCenterClientRootShutdown = new JobCenterClientRootShutdown();

			jobCenterClientRootShutdown.setMainThread( Thread.currentThread() );

			jobCenterClientRootShutdown.setJobCenterClientRootProgram( jobCenterClientRoot );



			//   add a shutdown hook that will be called either when the operating system sends a SIGKILL signal on Unix or all threads terminate ( normal exit )

			//           Also called when ctrl-c is pressed on Unix or Windows

			//  public void addShutdownHook(Thread hook)

			Runtime runtime = Runtime.getRuntime();
			runtime.addShutdownHook( jobCenterClientRootShutdown );


			jobCenterClientRoot.execute( );




		} catch ( Throwable t ) {


			String msg = "jobCenterClientRoot:main(...): Exception = " + t.toString();

			System.out.println( msg );
			t.printStackTrace();

			System.exit(1);
		}

	}



	/**
	 *
	 *  Class for processing kill signal. This is also run when all the threads in the application die/exit run()
	 */
	public static class JobCenterClientRootShutdown extends Thread {


		private volatile Thread mainThread;

		private volatile JobCenterClientRoot jobCenterClientRootProgram;


        /*
         * method that will run when kill signal is received
         */
        public void run() {

    		System.out.println( "JobCenterClientRootShutdown::run() called, calling jobCenterClientRootProgram.shutdown( ) now(): " + new Date());

    		Thread thisThread = Thread.currentThread();

    		thisThread.setName( "Thread-Process-Shutdown-Request" );

        	jobCenterClientRootProgram.shutdown( );


    		System.out.println( "JobCenterClientRootShutdown::run() exiting now(): " + new Date() );

        }

		public Thread getMainThread() {
			return mainThread;
		}

		public void setMainThread(Thread mainThread) {
			this.mainThread = mainThread;
		}

		public JobCenterClientRoot getJobCenterClientRootProgram() {
			return jobCenterClientRootProgram;
		}

		public void setJobCenterClientRootProgram(
				JobCenterClientRoot jobCenterClientRootProgram) {
			this.jobCenterClientRootProgram = jobCenterClientRootProgram;
		}


	}
}
