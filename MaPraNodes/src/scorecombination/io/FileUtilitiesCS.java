package scorecombination.io;

import java.util.HashMap;

import geneticnetwork.io.FileUtilitiesGeneticNetwork;

public class FileUtilitiesCS {
	
	/**
	 * reads in a file with gene scores, e.g. the results from PhenoToGeno, MetaboToGeno or NetworkScore
	 * the file is tab-separated, has several comment lines at the beginning (#) and one table header,
	 * the method requires 2 columns: position 0 with gene_id and position 1 with score (5 or 10 digit probability),
	 * the method removes duplicate gene_ids and keeps only the first score for a gene
	 * @param path 
	 * 		path to file with results from PhenoToGeno
	 * @return
	 * 		mapping gene_id -> score for each gene in the file
	 */
	public static HashMap<String, Double> readScoresFromFile(String path){
		return FileUtilitiesGeneticNetwork.readGeneScoresFrom(path);
	}

}
