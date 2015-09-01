package execprocess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.knime.core.node.NodeLogger;

public class InputThread extends Thread{
	
    private InputStream input;		// InputStream generated from file -> read from STDIN
    private OutputStream output;	// OutputStream connected to STDIN of process
    
    private NodeLogger l;

    public InputThread (InputStream input, OutputStream output, NodeLogger l) {
        this.input = input;
        this.output = output;
        
        this.l=l;
    }
    
    public InputThread(String file, OutputStream output, NodeLogger l){
    	
    	try {
			this.input=new FileInputStream(new File(file));
		} 
    	catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	
    	this.output=output;
    	
    	this.l=l;
    }
    
    public void run() {
    	
    	//System.out.println("starting stdinthread");
    	
        try {
           
            byte[] b = new byte[32768];
            
            int read = 0;
            
            // read= -1 -> end of file
            while (read > -1) {
                read = input.read(b, 0, b.length);
                //System.out.println("read: " + new String(b));
                if (read > -1) {
                    output.write(b, 0, read);
                }
            }
            
            l.info("Closing InputThread");
            
            input.close();
            output.close();
        } 
       catch (IOException e) {
            e.printStackTrace();
       } 

    }

}
