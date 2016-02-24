package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

public class FileInputReader {
	
	private BufferedReader reader;
	private String path;
	
	/**
	 * creates BufferedReader reading a file from path path
	 * @param path: path to file that should be read
	 */
	public FileInputReader(String path){
		this.path=path;
		Charset c = Charset.forName("UTF-8");
		try{
			reader = Files.newBufferedReader(Paths.get(path),c);
		}
		catch(IOException e){
			System.out.println("Error while creating the reader for file "+path);
			System.exit(1);
		}
	}
	
	/**
	 * retrieves the next line of the file
	 * @return: next line
	 */
	public String read(){

			String res;
			try {
				res = reader.readLine();
				return res;
			}
			catch (IOException e) {
				System.out.println("Error while reading from file "+path);
				System.exit(1);
			}

		return "";
	}
	
	/**
	 * closes the reader
	 */
	public void closer(){
		try{
			reader.close();
		}
		catch(IOException e){
			System.out.println("Error while closing reader for file "+path);
			System.exit(1);
		}
	}
	
	/**
	 * Method to retrieve all lines of a file in a LinkedList
	 * @param path to the file that should be read
	 * @return List of Strings, each String corresponds to a line of the file
	 */
	public static LinkedList<String> readAllLinesFrom(String path){
		
		LinkedList<String> lineList = new LinkedList<String>();
		
		FileInputReader fir = new FileInputReader(path);
		String line="";
		
		while ((line=fir.read())!=null){
			lineList.add(line);
		}
		fir.closer();
		
		return lineList;
	}


}
