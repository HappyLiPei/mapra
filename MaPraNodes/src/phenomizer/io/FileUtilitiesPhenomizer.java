package phenomizer.io;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import io.FileInputReader;

public class FileUtilitiesPhenomizer {
	
	/**
	 * reads in a matrix with pairwise similarity or distance scores for all PhenoDis diseases
	 * the file can have several comment lines starting with # at the beginning
	 * after the comment lines there should be one line with the table header
	 * each line starts with a PhenoDis id and contains comma-separated similarity or distance values
	 * @param path to file with the matrix
	 * @return matrix as 2-dimensional array of strings (including header and row names)
	 */
	public static String[][] readInMatrix(String path){
		List<String> content = FileInputReader.readAllLinesFrom(path);
		
		//remove comment lines
		while(content.get(0).startsWith("#")){
			content.remove(0);
		}
		
		int size = content.get(0).split(",").length;
		String[][]result = new String[size][size];
		
		for(int i=0; i<size; i++){
			result[i] = content.get(0).split(",");
			content.remove(0);
		}
		return result;
	}
	
	/**
	 * reads in the PhenoDis ksz table with frequencies from a file
	 * the file can have several comment lines starting with # at the beginning
	 * after the comment lines there should be one line with the table header
	 * the elements of a line a tab-separated,
	 * each line contains a disease id (column 0), a symptom id (column 1) and a frequency (column 2)
	 * @param path to a file with the ksz table
	 * @return hashmap mapping PhenoDis disease ids to a list of arrays
	 * 			array[0]: PhenoDis symptom id
	 * 			array[1]: PhenoDis frequency annotation
	 */
	public static HashMap<Integer,LinkedList<String[]>>readInKSZFrequency(String path){
		HashMap<Integer,LinkedList<String[]>> frequency = new HashMap<Integer,LinkedList<String[]>>();
		
		List<String> content = FileInputReader.readAllLinesFrom(path);
		//remove comment lines
		while(content.get(0).startsWith("#")){
			content.remove(0);
		}
		//remove table header
		content.remove(0);

		for(String line : content){
			String [] parts = line.split("\t");
			int id = Integer.valueOf(parts[0]);
			String[]newSymptom = new String[2];
			newSymptom[0]=parts[1];
			newSymptom[1]=parts[2];
			
			if(frequency.containsKey(id)){
				frequency.get(id).add(newSymptom);
			}
			else{
				LinkedList<String[]> symptoms = new LinkedList<String[]>();
				symptoms.add(newSymptom);
				frequency.put(id, symptoms);
			}
		}
		
		return frequency;
	}
	
	/**
	 * reads in a phenomizer query from a file
	 * the file can have several comment lines starting with # at the beginning
	 * after the comment lines there should be one line with the table header
	 * @param path to the file with the query
	 * @return a list of PhenoDis symptom ids
	 */
	public static LinkedList<Integer>readInQuery(String path){
		LinkedList<Integer> symptoms = new LinkedList<Integer>();
		
		List<String> content = FileInputReader.readAllLinesFrom(path);
		//remove comment lines
		while(content.get(0).startsWith("#")){
			content.remove(0);
		}
		//remove table header
		content.remove(0);
		
		for(String line : content){
			symptoms.add(Integer.valueOf(line));
		}
		
		return symptoms;
	}
	
	/**
	 * reads in the PhenoDis symptoms from a file
	 * the file can have several comment lines starting with # at the beginning
	 * after the comment lines there should be one line with the table header
	 * the elements of a line a tab-separated
	 * @param path to the file with the PhenoDis symptoms
	 * @return a list of all PhenoDis symptom ids (including duplicates)
	 */
	public static LinkedList<Integer> readInSymptoms(String path){
		LinkedList<Integer> symptoms = new LinkedList<Integer>();

		List<String> content = FileInputReader.readAllLinesFrom(path);
		//remove comment lines
		while(content.get(0).startsWith("#")){
			content.remove(0);
		}
		//remove table header
		content.remove(0);

		for(String line : content){
			String [] parts = line.split("\t");
			int id = Integer.valueOf(parts[0]);
			symptoms.add(id);
		}
		return symptoms;
	}
	
	/**
	 * reads in the PhenoDis ksz table from a file
	 * the file can have several comment lines starting with # at the beginning
	 * after the comment lines there should be one line with the table header
	 * the elements of a line a tab-separated,
	 * each line contains a disease id (column 0) and a symptom id (column 1)
	 * @param path to a file with the ksz table
	 * @return hashmap mapping PhenoDis disease ids to a list of PhenoDis symptom ids
	 */
	public static HashMap<Integer,LinkedList<Integer>> readInKSZ(String path){
		HashMap<Integer,LinkedList<Integer>> ksz = new HashMap<Integer,LinkedList<Integer>>();

		List<String> content = FileInputReader.readAllLinesFrom(path);
		//remove comment lines
		while(content.get(0).startsWith("#")){
			content.remove(0);
		}
		//remove table header
		content.remove(0);

		for(String line : content){
			String [] parts = line.split("\t");
			int id = Integer.valueOf(parts[0]);
			int s_id =Integer.valueOf(parts[1]);
			
			if(ksz.containsKey(id)){
				ksz.get(id).add(s_id);
			}
			else{
				LinkedList<Integer> symptoms = new LinkedList<Integer>();
				symptoms.add(s_id);
				ksz.put(id, symptoms);
			}
		}

		return ksz;
	}
	
	/**
	 * reads in the PhenoDis symptom ontology from a file
	 * the file can have several comment lines starting with # at the beginning
	 * after the comment lines there should be one line with the table header
	 * the elements of a line a tab-separated
	 * @param path to the file with the ontology
	 * @return a 2-dimensional array with PhenoDis Ids
	 * 			each line corresponds to an edge of the ontology
	 * 			column0: child
	 * 			column1: parent
	 */
	public static int[][] readInOntology(String path){
		List<String> content = FileInputReader.readAllLinesFrom(path);
		
		//remove comment lines
		while(content.get(0).startsWith("#")){
			content.remove(0);
		}
		//remove table header
		content.remove(0);
		
		int[][] ontologyArray = new int[content.size()][2];
		int position = 0;
		for(String line : content){
			String [] parts = line.split("\t");
			int idChild = Integer.valueOf(parts[0]);
			int idParent = Integer.valueOf(parts[1]);
			ontologyArray[position][0]=idChild;
			ontologyArray[position][1]=idParent;
			position++;
		}
		return ontologyArray;
	}

}

