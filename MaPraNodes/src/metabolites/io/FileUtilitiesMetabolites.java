package metabolites.io;

import java.util.HashMap;
import java.util.LinkedList;

import io.FileInputReader;

public class FileUtilitiesMetabolites {
	
	/**
	 * reads in a file with metabolite measurements of a single person, the file should be a tab-separated table
	 * with the columns metabolite_id at position 0, concentration at position 1 and group at position 2,
	 * the file can have several comment lines starting with # at the beginning,
	 * after the comment lines there should be one line with the table header
	 * @param path
	 * 		path to the file with the metabolite measurements
	 * @return
	 * 		a list of String arrays with 3 elements, position 0: metabolite id, position 1: concentration, 
	 * 		postion 2: group
	 */
	public static LinkedList<String[]> readMeasurements(String path){
		
		LinkedList<String[]> measurements = new LinkedList<String[]>();
		
		LinkedList<String> input = FileInputReader.readAllLinesFrom(path);
		//remove comment lines
		while(input.get(0).startsWith("#")){
			input.remove(0);
		}
		//remove table header
		input.remove(0);
		
		//iterate over all lines (rows of table)
		for(String line: input){
			String [] split = line.split("\t");
			//assumption: required information in first 3 columns
			if(split.length<3){
				continue;
			}
			String[] entry = new String [3];
			for(int i=0; i<3; i++){
				entry[i]=split[i];
			}
			measurements.add(entry);
		}
		
		return measurements;
	}
	
	/**
	 * reads in a file with reference metabolites, the file should be a tab-separated table with column metabolite_id
	 * at position 0, type at position 1, group at position 2, mean at position 3, standard deviation at position 4,
	 * minimum at position 5, missingness at position 6,
	 * the file can have several comment lines starting with # at the beginning,
	 * after the comment lines there should be one line with the table header
	 * @param path
	 * 		path to the file with the reference metabolites
	 * @return
	 * 		HashMap mapping metabolite ids to lists of String arrays with 7 elements, position 0: metabolite id,
	 * 		position 1: type, position 2: group, position 3: mean, position 4: standard deviation,
	 * 		position 5: missingness
	 */
	public static HashMap<String, LinkedList<String[]>> readReferences(String path){
		
		LinkedList<String> input = FileInputReader.readAllLinesFrom(path);
		//remove comment lines
		while(input.get(0).startsWith("#")){
			input.remove(0);
		}
		//remove table header
		input.remove(0);
		
		HashMap<String, LinkedList<String[]>> references = new HashMap<String, LinkedList<String []>> (
				input.size()*3);
		
		//iterate over all lines (rows of table)
		for(String line:input){
			String [] split = line.split("\t");
			
			//assumption: required information in first 6 columns
			if(split.length<6){
				continue;
			}
			String [] entry = new String[6];
			for(int i=0; i<6; i++){
				entry[i] = split[i];
			}
			
			//add line to HashMap
			String id = entry[0];
			if(references.containsKey(id)){
				LinkedList<String []> metabo = references.get(id);
				metabo.add(entry);
			}
			else{
				LinkedList<String []> metabo = new LinkedList<String []>();
				metabo.add(entry);
				references.put(entry[0], metabo);
			}
		}	
		return references;
	}

}
