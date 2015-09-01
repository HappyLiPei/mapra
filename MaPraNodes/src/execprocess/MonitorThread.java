/**
 * @author hastreiter
 */


package execprocess;


import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;



public class MonitorThread implements Runnable {
	
		   private final String name; 						// name of the monitoring thread
		   private final Thread monitorThread;				// the new monitoring thread
		   private final ExecuteThread executingThread;		// the executing thread
		   private final ExecutionContext exec;				// knime execution context
		   private boolean executionCanceled;
		   
		   private NodeLogger l;							//knime node logger
		   
		   
		   
		  /**
		   * Requires a monitor thread name, an already existing execution thread and the knime execution context
		   * @param monitorthreadname
		   * @param executingThread
		   * @param exec
		   * @param logger
		   */
		  public MonitorThread(String monitorthreadname, ExecuteThread executingThread, ExecutionContext exec, NodeLogger logger) {
		      this.name = monitorthreadname;
		      this.monitorThread = new Thread(this, name);
		      this.executingThread = executingThread;
		      this.exec = exec; 
		      this.executionCanceled=false;
		      
		      this.l=logger;
		   }
		  
		   // This is the entry point for thread.
		   public void run() {
		        boolean cancelRequested = false;
		        // loop while the executor is not done and no cancel has been requested
		        while (!executingThread.isDone() && !cancelRequested) {
		            try {
		                // if cancel was requested, an exception will be thrown
		                exec.checkCanceled();
		            } catch (CanceledExecutionException e) {
		                cancelRequested = true;
		                l.info("Execution canceled! Trying to stop all processes...");
		                // Stop threads&process
		                executingThread.stop();
		                this.executionCanceled=true;
		                executingThread.setExitCode(-42);
		            }
		            // wait a bit before checking one again
		            if (!cancelRequested) {
		                try {
		                    Thread.sleep(100);
		                } catch (InterruptedException e) {
		                    // ignore
		                }
		            }
		        }
		        l.info(this.name+" thread will now exit");
		    }
		   
		   /**
		    * Starts the monitor and execution threads
		    */
		   public void start(){
			      monitorThread.start(); // Start the threads
			      executingThread.start();
			      l.info("New monitor thread: " + monitorThread+" will monitor the thread: " + executingThread.getName());
		   }
		   
		   /**
		    * Forces the main thread to wait until sub-threads finish
		    */
		   public void waitUntilFinished(){
			      try {
					monitorThread.join();
				} catch (InterruptedException e) {
				} 
		   }

		public boolean isExecutionCanceled() {
			return executionCanceled;
		}
		
}
