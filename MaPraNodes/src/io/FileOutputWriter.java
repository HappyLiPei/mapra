package io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileOutputWriter {
	
	public String path;
	public BufferedWriter w;
	
	public FileOutputWriter(String path){
		this.path=path;
		initiateWriter(path);
	}
	
	/**
	 * creates BufferedWriter that writes to the file specified by path
	 * @param path: path of the file to write to
	 */
	public void initiateWriter (String path){
		try{
			Charset c = Charset.forName("UTF-8");
			w = Files.newBufferedWriter(Paths.get(path), c);
		}
		catch(IOException e){
			System.out.println("Error creating the writer for file "+path);
			System.exit(1);
		}
	}
	
	/**
	 * writes a String to the file without flushing (buffering)
	 * @param s: String that is written to the file
	 */
	public void writeFile(String s){
		try{
			w.write(s);
		}
		catch(IOException e){
			System.out.println("Error while writing to file "+path);
			System.exit(1);
		}
	}
	
	/**
	 * writes a String to the file with flushing it, this method should be used to write to log files
	 * @param s: String that is written to the file
	 */
	public void writeFileAndFlush(String s){
		try{
			w.write(s);
			w.flush();
		}
		catch(IOException e){
			System.out.println("Error while writing to file "+path);
			System.exit(1);
		}
	}
	
	/**
	 * does the same as writeFile but appends a newline to the string
	 * @param s: String that is written to the file
	 */
	public void writeFileln(String s){
		writeFile(s+"\n");
	}
	
	/**
	 * does the same as writeFileAndFlush but appends a newline to the string
	 * @param s: String that is written to the file
	 */
	public void writeFilelnAndFlush(String s){
		writeFileAndFlush(s+"\n");
	}
	
	/**
	 * closes the writer and flushes the remaining buffer content
	 * this method should ALWAYS be called after the file is complete
	 */
	public void closew(){
		try{
			w.close();
		}
		catch(IOException e){
			System.out.println("Error closing the writer for file "+path);
		}
	}
	
}
