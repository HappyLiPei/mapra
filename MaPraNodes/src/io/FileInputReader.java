package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileInputReader {
	
	private BufferedReader reader;
	private String path;
	
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
	
	public void closer(){
		try{
			reader.close();
		}
		catch(IOException e){
			System.out.println("Error while closing reader for file "+path);
			System.exit(1);
		}
	}


}
