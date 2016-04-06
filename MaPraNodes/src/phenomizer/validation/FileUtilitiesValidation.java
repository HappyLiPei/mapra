package phenomizer.validation;

import java.util.HashMap;
import java.util.LinkedList;

import io.FileInputReader;

public class FileUtilitiesValidation {
	
	/**
	 * reads in a file containing a mapping from PhenoDis ids to OMIM ids,
	 * the file is a tab-separated table with the PhenoDis id at column 0 and OMIM id at column 2
	 * some PhenoDis ids do not have an omim id and some have more than one omim id, those PhenoDis ids are 
	 * not parsed by this method
	 * @param path
	 * 			path to the file with the table mapping PhenoDis and OMIM disease ids
	 * @return
	 * 			a map mapping OMIM -> PhenoDis id, the map contains only those ids with a 1-1 mapping
	 */
	public static HashMap<Integer, Integer> readOMIMIdMapping(String path){
		
		LinkedList<String> content = FileInputReader.readAllLinesFrom(path);
		HashMap<Integer,Integer> omim_to_pheno= new HashMap<Integer, Integer>(content.size()*3);
		
		//remove comment lines
		while(content.get(0).startsWith("#")){
			content.remove(0);
		}
		//remove table header
		content.remove(0);
		
		//iterate over lines
		for(String line: content){
			String [] split = line.split("\t");
			//no omim id + orphanet id
			if(split.length<3){
				continue;
			}
			//pos 0: phenodis id, pos 2: omim id
			//position 2 contains ; -> mapping not unique, position 2 ="" -> no omim id known
			if(!split[2].contains(";") && !split[2].equals("")){
				int phenoDis = Integer.parseInt(split[0]);
				int omim = Integer.parseInt(split[2]);
				omim_to_pheno.put(omim, phenoDis);	
			}
		}
		return omim_to_pheno;
	}
	
	/**
	 * reads in a file with results from text mining for OMIM entrys produced by KNIME, the file is a tab-separated table,
	 * each line corresponds to a symptom and each column to a OMIM entry, a cell containing 1.0 indicates that the
	 * the symptom is associated with the OMIM disease
	 * @param path
	 * 			path to the table with the results from text mining
	 * @return
	 * 			map OMIM id-> list of PhenDis symptom ids associated with the OMIM disease, the list might be empty
	 */
	public static HashMap<Integer, LinkedList<Integer>> readQueriesFromTM(String path){
		
		//generate file handle
		FileInputReader fir = new FileInputReader(path);
		
		// process header: extract omim ids
		String line=fir.read();
		String [] split=line.split("\t");
		int [] idpos = new int [split.length-1];
		//position 0: PhenoDis symptom id -> start at position 1 of header
		for(int i=1; i<split.length; i++){
			int id = extractId(split[i]);
			idpos[i-1]=id;
		}
		
		//initialize data structure to return
		HashMap<Integer, LinkedList<Integer>> queries = new HashMap<Integer, LinkedList<Integer>>(idpos.length*3);
		for(int id: idpos){
			if(!queries.containsKey(id)){
				queries.put(id, new LinkedList<Integer>());
			}
			else{
				System.out.println("Duplicate omim id: "+id);
			}
		}
		
		while((line=fir.read())!=null){
			//read and check one line of text mining table
			String [] row_cells = line.split("\t");
			if(row_cells.length!=idpos.length+1){
				System.out.println("Cannot parse line "+ line);
			}
			//extract symptom id for current line
			int symp =-1;
			try{
				symp = Integer.parseInt(row_cells[0]);
			}
			catch(NumberFormatException e){
				System.out.println("Cannot parse symptom id "+row_cells[0]);
			}
			
			if(symp==-1){
				continue;
			}
			//add current symptom id to all queries
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
	
	/**
	 * extracts omim id from header of text mining result and checks correct formatting
	 * @param header
	 * 			column name in the header of the table containing the text mining results
	 * @return
	 * 			omim id contained in the column name
	 */
	private static int extractId(String header){
		if(header.startsWith("Max*(")){
			String tmp = header.substring(5);
			if(tmp.matches("[0-9]{6}.*")){
				tmp=tmp.substring(0,6);
				return Integer.parseInt(tmp);
			}
			else{
				System.out.println("header does not contain six digit omim id");
				return -1;
			}
		}
		else{
			System.out.println("header does not start with Max*(");
			return -1;
		}
	}

}
