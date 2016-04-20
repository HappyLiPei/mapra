package geneticnetwork.algorithm;

import java.util.HashMap;

import geneticnetwork.datastructures.Edge;
import geneticnetwork.datastructures.ScoredGenes;

public class DataTransformerGeneticNetwork {
	
	/**
	 * transforms the results of PhenoToGeno read from a file or a KNIME table into an ScoredGenes object
	 * that is required for the network scoring
	 * @param scores mapping of gene_id -> score of the gene
	 * @return a ScoredGenes object using the scores map
	 */
	public ScoredGenes transformGeneScores (HashMap<String, Double> scores){
		return new ScoredGenes(scores);
	}
	
	// parse into edge data structure (with vs. without weights)
	// duplicate edges node1 -> node2 vs node2 -> node 1?! remove ?!
	//evtl. option for undirected vs. directed
	public Edge[] transformEdges (String[][] edges){
		return null;
	}
}
