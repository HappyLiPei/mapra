package geneticnetwork.io;

import java.util.HashMap;
import java.util.LinkedList;

import io.FileInputReader;

public class FileUtilitiesGeneticNetwork {
	
	/**
	 * reads in a file with the results from PhenoToGeno,
	 * the file is tab-separated, has several comment lines at the beginning (#) and one table header,
	 * the method requires 2 columns: position 0 with gene_id and position 1 with score (5 digit probability),
	 * the method removes duplicate gene_ids and keeps only the first score for a gene
	 * @param path
	 * 		path to file with results from PhenoToGeno
	 * @return
	 * 		mapping gene_id -> score for each gene in the file
	 */
	public static HashMap<String, Double> readGeneScoresFrom(String path){
		
		LinkedList<String> lines = FileInputReader.readAllLinesFrom(path);
		//remove comment lines
		while(lines.get(0).startsWith("#")){
			lines.remove(0);
		}
		//remove table header
		lines.remove(0);
		
		//mapping gene id -> score
		HashMap<String, Double> geneToScore = new HashMap<String, Double>(lines.size()*3);
		
		for(String line: lines){
			String[] split = line.split("\t");
			// line in wrong format -> missing id or score
			if(split.length<2){
				continue;
			}
			//get id and score
			String id = split[0];
			double score = Double.parseDouble(split[1]);
			//add to map if it is no duplicate
			if(!geneToScore.containsKey(id)){
				geneToScore.put(id, score);
			}
		}
		
		return geneToScore;
	}
	
	/**
	 * reads in an unweighted or weighted, undirected genetic network as a collection of edges,
	 * the file is tab-separated, has several comment lines at the beginning (#) and one table header,
	 * the network file should contain 2 columns for the edge topology (column 0: first node of the edge,
	 * column 1: second node of the edge) and an optional column with weights (position 2) 
	 * @param path
	 * 		path to file with undirected, unweighted/weighted genetic network
	 * @param weightedEdges
	 * 		flag to indicate if the network is weighted, if true the method reads in the weights of the network
	 * @return
	 * 		String array of arrays with 2 ( if !weightedEdges) or 3 (if weightedEdges) containing node1 (position 0),
	 * 		node 2 (position 1) and weight (optional position 2) of each edge
	 */
	public static String[][] readEdges(String path, boolean weightedEdges){
		
		//consider 2 columns for unweighted edges
		int length =2;
		//consider 3 columns if weights are used
		if(weightedEdges){
			length++;
		}
		
		FileInputReader fir = new FileInputReader(path);
		String line =fir.read();
		//remove comment lines + header
		while(line.startsWith("#")){
			line=fir.read();
		}
		
		//data structure for edges
		LinkedList<String []> edgeInfo = new LinkedList<String[]>();
		while((line=fir.read())!=null){
			String[] split = line.split("\t");
			// line in wrong format -> missing information
			if(split.length<length){
				continue;
			}
			String [] edge = new String[length];
			for(int i=0; i<length; i++){
				edge[i]=split[i];
			}
			edgeInfo.add(edge);
		}
		
		return edgeInfo.toArray(new String [edgeInfo.size()][]);
	}
}
