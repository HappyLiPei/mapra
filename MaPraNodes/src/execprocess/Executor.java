/**
 * @author hastreiter
 */

package execprocess;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;


public class Executor {
	
	private boolean run;
	
	/**
	 * Executor object that handles the tool executions.  
	 */
	public Executor(){
		this.run = true;
	}

	/**
	 * Multithreaded execution of command line calls. Uses a monitoring thread for checking if user canceled the execution thread.
	 * @param command : The command string. Note: No /bin/sh is used, therefore do not use ">" for directing output into files, use the tool flag or executeCommand with StdOut/StdError support instead 
	 * @param CommandName : String that will be used to name the threads 
	 * @param exec : The Knime ExecutionContext
	 */
	public int executeCommand(String command,String CommandName, NodeLogger l, ExecutionContext exec){
		 if(run){
			//Create Threadpool + the execution and monitoring threads 
			ExecutorService pool = Executors.newFixedThreadPool(2);
			ExecuteThread ex = new ExecuteThread(CommandName+" Executor", command, l);
			MonitorThread m = new MonitorThread(CommandName+" Monitor", ex, exec, l);
	
			//Starts both threads and waits for successful termination
			pool.execute(ex);
			pool.execute(m);	
		
			//Now wait for them
			ex.waitUntilFinished();
			m.waitUntilFinished();
			pool.shutdown();
	        while (!pool.isTerminated()) {
	        }
	        
			l.info("ThreadPool closed.");
			l.info("Exit code:"+ex.getExitCode());
			
			//Check if execution was canceled to avoid follow-up processes
			if(m.isExecutionCanceled()){
				this.run = false;
			}
			
			return ex.getExitCode();
			
		 }else{
			l.info("Skipping command: "+command+"\nExcution was canceled..");
			return -1;
		 }
	}
	
	
	
	
	
	/**
	 * Multithreaded execution of command line calls. Uses a monitoring thread for checking if user canceled the execution thread.
	 * @param command : The command string. Note: No /bin/sh is used, therefore do not use ">" for directing output into files. Output to Stdout will be captured
	 * @param CommandName : String that will be used to name the threads 
	 * @param exec : The Knime ExecutionContext
	 * @param stdOutFile : File in which stdout will be written
	 */
		public int executeCommand(String command,String CommandName, ExecutionContext exec, NodeLogger l, String stdOutFile){
			 if(run){
				//Create Threadpool + the execution and monitoring threads 
				ExecutorService pool = Executors.newFixedThreadPool(2);
				ExecuteThread ex = new ExecuteThread(CommandName+" Executor",command, stdOutFile, l);
				MonitorThread m = new MonitorThread(CommandName+" Monitor", ex, exec, l);

				//Starts both threads and waits for successful termination
				pool.execute(ex);
				pool.execute(m);	
			
				//Now wait for them
				ex.waitUntilFinished();
				m.waitUntilFinished();
				pool.shutdown();
		        while (!pool.isTerminated()) {
		        }
		        
				l.info("ThreadPool closed.");
				l.info("Exit code:"+ex.getExitCode());
				
				//Check if execution was canceled to avoid follow-up processes
				if(m.isExecutionCanceled()){
					this.run = false;
				}
				
				return ex.getExitCode();
				
			 }else{
				l.info("Skipping command: "+command+"\nExcution was canceled..");
				return -1;
			 }
			}
	
	
	
	
	
	
	
/**
 * Multithreaded execution of command line calls. Uses a monitoring thread for checking if user canceled the execution thread.
 * @param command : The command string. Note: No /bin/sh is used, therefore do not use ">" for directing output into files. Output to Stdout and Stderr will be captured
 * @param CommandName : String that will be used to name the threads 
 * @param exec : The Knime ExecutionContext
 * @param stdOutFile : File in which stdout will be written
 * @param stdErrFile : File in which stderr will be written
 */
	public int executeCommand(String command,String CommandName, ExecutionContext exec, NodeLogger l, String stdOutFile, String stdErrFile){
		 if(run){
			//Create Threadpool + the execution and monitoring threads 
			ExecutorService pool = Executors.newFixedThreadPool(2);
			ExecuteThread ex = new ExecuteThread(CommandName+" Executor",command, stdOutFile, stdErrFile, l);
			MonitorThread m = new MonitorThread(CommandName+" Monitor", ex, exec, l);

			//Starts both threads and waits for successful termination
			pool.execute(ex);
			pool.execute(m);	
		
			//Now wait for them
			ex.waitUntilFinished();
			m.waitUntilFinished();
			pool.shutdown();
	        while (!pool.isTerminated()) {
	        }
			
	        l.info("ThreadPool closed.");
			l.info("Exit code: "+ex.getExitCode());
			
			//Check if execution was canceled to avoid follow-up processes
			if(m.isExecutionCanceled()){
				this.run = false;
			}
			
			return ex.getExitCode();
			
		 }else{
			l.info("Skipping command: "+command+"\nExcution was canceled..");
			return -1;
		 }
		}
	
	public int executeCommand(String command,String CommandName, ExecutionContext exec, NodeLogger l, String stdOutFile, String stdErrFile, String stdInFile){
		 if(run){
			//Create Threadpool + the execution and monitoring threads 
			ExecutorService pool = Executors.newFixedThreadPool(2);
			ExecuteThread ex = new ExecuteThread(CommandName+" Executor",command, stdOutFile, stdErrFile, stdInFile, l);
			MonitorThread m = new MonitorThread(CommandName+" Monitor", ex, exec, l);

			//Starts both threads and waits for successful termination
			pool.execute(ex);
			pool.execute(m);	
		
			//Now wait for them
			ex.waitUntilFinished();
			m.waitUntilFinished();
			pool.shutdown();
	        while (!pool.isTerminated()) {
	        }
			
	        l.info("ThreadPool closed.");
			l.info("Exit code: "+ex.getExitCode());
			
			//Check if execution was canceled to avoid follow-up processes
			if(m.isExecutionCanceled()){
				this.run = false;
			}
			
			return ex.getExitCode();
			
		 }else{
			l.info("Skipping command: "+command+"\nExcution was canceled..");
			return -1;
		 }
		}
	
	
}
