package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;

import algorithm.AlgoPheno;

public class MainSophie {
	
	/*
	 * generates query lists from text mining results
	 * HashMap omim id -> symptom list extracted via text mining 
	 */
	private HashMap<String, LinkedList<Integer>> readQueriesTM(String file){
		
		FileInputReader fir = new FileInputReader(file);
		String line=fir.read();
		
		// process header: extract omim ids
		String [] split=line.split("\t");
		System.out.println(split.length);
		
		HashMap<String, LinkedList<Integer>> queries = new HashMap<String, LinkedList<Integer>>(split.length*3);
		String [] idpos = new String [split.length-1];
		
		for(int i=1; i<split.length; i++){
			String id = extractId(split[i]);
			queries.put(id, new LinkedList<Integer>());
			idpos[i-1]=id;
		}
		
		while((line=fir.read())!=null){
			//read and check one line of text mining table
			String [] row_cells = line.split("\t");
			if(row_cells.length!=idpos.length+1){
				System.out.println("Cannot parse line "+ line);
			}
			int symp =-1;
			
			try{
				symp = Integer.valueOf(row_cells[0]);
			}
			catch(NumberFormatException e){
				System.out.println("Cannot parse symptom id "+row_cells[0]);
			}
			
			if(symp==-1){
				continue;
			}
			
			for(int i=0; i<idpos.length;i++){
				// if: value 1.0 -> add symptom id (column 0) to omim id at idpos[i] (column at position i+1)
				// else: value 0.0: do not add symptom id
				if(!row_cells[i+1].equals("0.0")){
					queries.get(idpos[i]).add(symp);
				}
			}
		}
		fir.closer();
		
		return queries;
	}
	
	// extract omim id from header and checks correct formatting
	private String extractId(String header){
		if(header.startsWith("Max*(")){
			String tmp = header.substring(5);
			if(tmp.matches("[0-9]{6}.*")){
				tmp=tmp.substring(0,6);
				//System.out.println(tmp);
				return tmp;
			}
			else{
				System.out.println("header does not contain six digit omim id");
				return "";
			}
		}
		else{
			System.out.println("header does not start with Max*(");
			return "";
		}
	}
	
	private LinkedList<String [] > executePhenomizer(String file_isa, String file_symptom, String file_ksz, LinkedList<Integer> q){
		AlgoPheno.setInput(q, FileUtilities.readInSymptoms(file_symptom),
				FileUtilities.readInKSZ(file_ksz), FileUtilities.readInOntology(file_isa));
		LinkedList<String[]> res = AlgoPheno.runPhenomizer(20);
		return res;
	}
	
	
	public static void main(String args[]){
		
		String file= "/home/marie-sophie/Uni/mapra/omim/Test.txt";
		String isa="/home/marie-sophie/Uni/mapra/phenodis/Datenbank/isa_HPO_test.csv";
		String ksz="/home/marie-sophie/Uni/mapra/phenodis/Datenbank/ksz_HPO_test.csv";
		String symptom="/home/marie-sophie/Uni/mapra/phenodis/Datenbank/symptoms_HPO_test.csv";
		
		
		MainSophie ms = new MainSophie();
		HashMap<String, LinkedList<Integer>> queries = ms.readQueriesTM(file);
		
		//176000 (OMIM) - 6496 (PhenoDis)
		System.out.println(queries.get("176000"));
		
		LinkedList<String[]> res = ms.executePhenomizer(isa, symptom, ksz, queries.get("176000"));
		for(String []s: res){
			System.out.println(s[0]+"\t"+s[1]);
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private class FileInputReader {
		
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


}
