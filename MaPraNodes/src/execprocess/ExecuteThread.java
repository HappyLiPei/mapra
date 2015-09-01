/**
 * @author hastreiter
 */


package execprocess;

import java.io.IOException;
//import java.lang.reflect.Field;

import org.knime.core.node.NodeLogger;






public class ExecuteThread implements Runnable {
	
	
		private String name; 					// name of thread
		private boolean finished; 				// Thread Status
		private Thread executingThread;			// The executing thread
		private String command;					// Command to execute
		private Process p;						// Process for execution
	    private String stdOutFile;				// Process stdOut Filepath
	    private String stdErrFile;				// Process stdErr Filepath
	    private boolean useStdOutStream;
	    private boolean useStdErrStream;
	    
	    private String stdInFile;
	    private boolean useStdInStream;
	    
	    private int exitcode=-1;
	    private NodeLogger l;
		
		/**
		 * ExecuteThread that does not! read the Error/Outstreams
		 * @param threadname
		 * @param command
		 */
		public ExecuteThread(String threadname, String command, NodeLogger l) {
		      
			  this.name = threadname;
		      this.executingThread = new Thread(this, name);
		      
		      l.info("New execute thread: " + executingThread);
		      
		      this.finished = false;
		      this.command = command;
		      
		      this.useStdOutStream=false;
		      this.useStdErrStream=false;
		      
		      
		      this.useStdInStream=false;
		      this.l=l;
		   }
		
		
		/**
		 * ExecuteThread that reads the StdOutstreams
		 * @param threadname
		 * @param command
		 * @param stdOutFile
		 * @param l
		 */
		public ExecuteThread(String threadname, String command, String stdOutFile, NodeLogger l) {
			
		      this.name = threadname;
		      this.executingThread = new Thread(this, name);
		    
		      l.info("New execute thread: " + executingThread);
		      
		      this.finished = false;
		      this.command = command;
		   
		      this.stdOutFile=stdOutFile;
		      
		      this.useStdOutStream=true;
		      this.useStdErrStream=false;
		      
		      this.useStdInStream=false;
		      this.l=l;
		   }
		
		
		/**
		 * ExecuteThread that reads the Error and StdOutstreams
		 * @param threadname
		 * @param command
		 * @param stdOutFile
		 * @param stdErrFile
		 * @param l
		 */
		public ExecuteThread(String threadname, String command, String stdOutFile, String stdErrFile, NodeLogger l) {
		      this.name = threadname;
		      this.executingThread = new Thread(this, name);
		      
		      l.info("New execute thread: " + executingThread);
		      
		      this.finished = false;
		      this.command = command;
		      
		      this.stdErrFile=stdErrFile;
		      this.stdOutFile=stdOutFile;
		      
		      this.useStdOutStream=true;
		      this.useStdErrStream=true;
		      
		      this.useStdInStream=false;
		      this.l=l;
		   }
		
		/**
		 * ExecuteThread that reads the Error and StdOutstreams
		 * @param threadname
		 * @param command
		 * @param stdOutFile
		 * @param stdErrFile
		 * @param stdInFile
		 * @param l
		 */
		public ExecuteThread(String threadname, String command, String stdOutFile, String stdErrFile, String stdInFile, NodeLogger l) {
		      this.name = threadname;
		      this.executingThread = new Thread(this, name);
		      
		      l.info("New execute thread: " + executingThread);
		      
		      this.finished = false;
		      this.command = command;
		      
		      this.stdErrFile=stdErrFile;
		      this.stdOutFile=stdOutFile;
		      
		      this.useStdOutStream=true;
		      this.useStdErrStream=true;
		      
		      this.stdInFile=stdInFile;
		      this.useStdInStream=true;
		      
		      this.l=l;
		   }
		
		
		   // This is the entry point for thread.
		   public void run() {
		      try {
		    	  l.info("Running command: "+command);
		    	  
		    	  //Start the process
		    	  p = Runtime.getRuntime().exec(command);
		    	  
		    	  //If Output is written to stdout/stderr
		    	  if(useStdOutStream){

			          StreamThread stdOutStream = new StreamThread(p.getInputStream(),stdOutFile, l);

			          if(useStdErrStream){
			        	  
			        	  StreamThread stdErrStream = new StreamThread(p.getErrorStream(),stdErrFile, l);
			        	  
			        	  if(useStdInStream){
			        		  
			        		  InputThread stdInStream = new InputThread(stdInFile, p.getOutputStream(), l);
			        		  
			        		  //runs process with stdout, stderr and stdin
			        		  
				        	  stdOutStream.start();
				        	  stdErrStream.start();
				        	  stdInStream.start();
					    	  //Wait for process 
							  setExitCode(p.waitFor());
							  //Process finished
					    	  finished = true;
					    	  //Close Stream Threads
					    	  stdOutStream.interrupt();
				        	  stdErrStream.interrupt();
				        	  stdInStream.interrupt();
				        	  
			        	  }
			        	  else{
			        		  
			        		  // runs process with stdout and stderr
			        		  
				        	  stdOutStream.start();
				        	  stdErrStream.start();
					    	  //Wait for process 
							  setExitCode(p.waitFor());
							  //Process finished
					    	  finished = true;
					    	  //Close Stream Threads
					    	  stdOutStream.interrupt();
				        	  stdErrStream.interrupt();
			        	  }
			          }else{
			        	  //Start Stdout only
			        	  
			        	  stdOutStream.start();
						  setExitCode(p.waitFor());
						  //Process finished
				    	  finished = true;
				    	  //Close Stream Threads
				    	  stdOutStream.interrupt();
			          } 
		    	  }else{
		    		  //No stdout/sterr threads
			    	  //Wait for process 
					  setExitCode(p.waitFor());
					  //Process finished
			    	  finished = true;
			    	  
		    	  }	     
		      } catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		        l.info(this.name+" thread will now exit");
		   }

		   /**
		    * Stops the execution thread and the called command
		    */
		    public void stop() {
		    	l.info("Stopping process...");
			    	p.destroy();
			    	//Close the Streams
			    	try {
				    	p.getErrorStream().close();
				    	p.getOutputStream().close();
						p.getInputStream().close();
					} catch (IOException e1) {

						e1.printStackTrace();
					}
			    	try {
						p.waitFor();
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
		    }
		    
		    /**
		     * Starts the execution thread
		     */
		    public void start(){
		    	executingThread.start();
		    }
		    
		    public String getName(){
		    	return executingThread.getName();
		    }
		    
		    /**
		     * Returns true if process finished
		     * @return
		     */
		    public boolean isDone(){
		    	return this.finished;
		    }
		    
		    public String getCommand(){
		    	return this.command;
		    }
		    
		    /**
		     * Forces the main thread to wait until sub-threads finish
		     */
		    public void waitUntilFinished(){
		    	try {
					executingThread.join();
				} catch (InterruptedException e) {
				}
		    }
		    
//		    private void getPID(Process p){
//		    	try {
//		    		Class clazz = Class.forName("java.lang.UNIXProcess");
//		    		Field pidField = clazz.getDeclaredField("pid");
//		    		pidField.setAccessible(true);
//		    		Object value = pidField.get(p);
//		    		System.err.println("pid = " + value);
//		    	} catch (Throwable e) {
//		    		e.printStackTrace();
//		    	}
//		    }
		    
		    public synchronized int getExitCode(){
		    	return this.exitcode;
		    }
		    
		    public synchronized void setExitCode(int e){
		    	if(this.getExitCode()!=-42){
		    		this.exitcode=e;
		    	}
		    }
}
