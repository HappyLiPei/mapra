package metabotogeno.io;

import java.util.HashMap;
import java.util.LinkedList;

import io.FileInputReader;
import metabotogeno.algo.DataTransformerMTG;
import phenotogeno.io.FileUtilitiesPTG;

public class FileUtilitiesMTG {
	
	/**
	 * reads in a file representing associations between metabolites and genes,
	 * the file starts with several comment lines (marked by the symbol #) and has table header,
	 * each line is tab-separated and contains 3 entries, position 0: metabolite id,
	 * position 1: gene id (can be empty), position 2: source (can be empty),
	 * a metabolite-gene can occur several times in the file originating from different sources
	 * @param path path to file with the metabolite-gene associations
	 * @return mapping metabolite id -> list of associated gene ids (may contain duplicates)
	 */
	public static HashMap<String, LinkedList<String>> readMetaboliteGeneAssociations(String path){
		
		LinkedList<String> associationLines = FileInputReader.readAllLinesFrom(path);
		
		//remove comment lines with #
		while(associationLines.getFirst().startsWith("#")){
			associationLines.remove(0);
		}
		//remove header
		associationLines.remove(0);
		
		//return map metabolite -> list of genes
		HashMap<String, LinkedList<String>> result = new HashMap<String, LinkedList<String>>(associationLines.size()*3);
		
		//tab-separated line of length 3, pos 0: metabolite_id, pos 1: gene_id , pos 2: source
		for (String line: associationLines){
			String [] split = line.split("\t");
			String metaboliteId = split[0];
			//get gene list
			LinkedList<String> genes = null;
			if(result.containsKey(metaboliteId)){
				genes = result.get(metaboliteId);
			}
			else{
				genes = new LinkedList<String>();
				result.put(metaboliteId, genes);
			}
			//check if gene is associated to the current metabolite
			if(split.length>1 && !split[1].equals("")){
				genes.add(split[1]);
			}
		}
		
		return result;
	}
	
	/**
	 * reads in a file containing the results from MetaboliteScore
	 * the file starts with several comment lines (marked by the symbol #) and has table header,
	 * each line is tab-separated and contains 4-5 entries, position 0: metabolite id,
	 * position 1: metabolite name (optional), position 2: type, position 3: score, position 4: pvalue
	 * each metabolite should occur exactly once and should have a p value between 0 and 1
	 * @param path path to the file with the results from MetaboliteScore
	 * @return List of String arrays with 2 elements (pos 0: metabolite id, pos 1: pvalue)
	 */
	public static LinkedList<String[]> readMetaboliteScoreResult(String path){
		
		LinkedList<String> scoringLines = FileInputReader.readAllLinesFrom(path);
		
		//remove comment lines and table header
		while(scoringLines.getFirst().startsWith("#")){
			scoringLines.remove(0);
		}
		
		//get header and check number of columns (with or without metabolite name)
		String header = scoringLines.remove(0);
		boolean name=false;
		if(header.split("\t").length>=5){
			name=true;
		}
		
		//return list of arrays with pos 0: metabolite id, pos 1: pvalue
		LinkedList<String[]> result = new LinkedList<String[]>(); 
				
		//assumption: line tab-separated, pos 0: id, pos 1: name (optional), pos 2: type, pos 3: score, pos 4: probability
		for(String line:scoringLines){
			String [] split = line.split("\t");
			String metaboliteId = split[0];
			String pvalue="";
			if(name){
				pvalue=split[4];
			}
			else{
				pvalue=split[3];
			}
			result.add(new String[]{metaboliteId, pvalue});
		}
		
		return result;
	}
	
	/**
	 * reads a list of genes from a file, the file starts with several comment lines (marked by #) and a table header,
	 * each line of the file contains one gene id,
	 * each gene id should occur exactly once in the file (checked by {@link DataTransformerMTG}) 
	 * @param path path to the file with the gene ids
	 * @return a list of all genes ids 
	 */
	public static LinkedList<String> readGeneList(String path){
		return FileUtilitiesPTG.readGeneList(path);
	}
	


}
