package phenotogeno.io;

import java.util.HashMap;
import java.util.LinkedList;

import io.FileInputReader;

public class FileUtilitiesPTG {
	
	/**
	 * reads in the prediction results created by Phenomizer
	 * the file is tab separated
	 * the file starts with several comment lines (marked by #) and a table header
	 * each line should contain the following entries:
	 * 	pos 0: PhenoDis disease id
	 * 	pos 1: disease name
	 * 	pos 2: score
	 * 	pos 3: pvalue
	 * 	pos 4: significance
	 * @param path file containing the result of Phenomizer
	 * @return a list of String arrays
	 * 	each array has length 2: array[0] disease id, array[1] pvalue 
	 */
	public static LinkedList<String[]> readPhenomizerResult(String path){
		FileInputReader fir = new FileInputReader(path);
		
		//skip comment header + table header
		String line = fir.read();
		while(line.startsWith("#")){
			line = fir.read();
		}
		
		LinkedList<String[]> result = new LinkedList<String[]>();
		while((line=fir.read())!=null){
			String[] split = line.split("\t");
			String[] list_element = new String[2];
			//disease id
			list_element[0] = split[0];
			//pvalue
			list_element[1] = split[3];
			result.add(list_element);
		}
		fir.closer();
		
		return result;
	}
	
	/**
	 * reads a list of genes from a file, each line of the file contains one gene ID
	 * (assumption: each gene ID occurs exactly once in the file, assumption is assured by DataTransformer)
	 * the file starts with several comment lines (marked by #) and a table header
	 * @param path file with gene IDs
	 * @return a list of all genes IDs 
	 */
	public static LinkedList<String> readGeneList(String path){
		
		LinkedList<String> genes = FileInputReader.readAllLinesFrom(path);
		//remove comment lines and table header
		while(genes.getFirst().startsWith("#")){
			genes.remove(0);
		}
		genes.remove(0);
		
		return genes;
	}
	
	/**
	 * reads in a file representing associations between diseases and genes
	 * the file starts with several comment lines (marked by #) and a table header
	 * each line is tab-separated
	 * 	pos: 0 PhenoDis disease id
	 * 	pos: 1 gene id (optional)
	 * assumption: each PhenoDis disease is listed in the file (even if without known gene association)
	 * @param path file with associations between diseases and genes
	 * @return HashMap that maps each disease id to a list of gene ids
	 * 	if the disease has no genes, it is mapped to an empty list
	 */
	public static HashMap<Integer, LinkedList<String>> readDiseaseGeneAssociation(String path){
		
		LinkedList<String> associations = FileInputReader.readAllLinesFrom(path);
		//remove comment lines and table header
		while(associations.getFirst().startsWith("#")){
			associations.remove(0);
		}
		associations.remove(0);
		
		HashMap<Integer, LinkedList<String>> disease_to_gene =
				new HashMap<Integer, LinkedList<String>>(associations.size()*3);
		for(String line:associations){
			String[] split = line.split("\t");
			int id = Integer.valueOf(split[0]);
			if(disease_to_gene.containsKey(id)){
				if(split.length>1){
					LinkedList<String> genes =disease_to_gene.get(id);
					genes.add(split[1]);
				}
			}
			else{
				LinkedList<String> genes = new LinkedList<String>();
				//test if disease has an associated gene
				if(split.length>1){
					genes.add(split[1]);
				}
				disease_to_gene.put(id, genes);
			}
		}
		
		return disease_to_gene;
	}

}
