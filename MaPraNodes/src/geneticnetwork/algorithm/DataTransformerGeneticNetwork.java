package geneticnetwork.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import geneticnetwork.datastructures.Edge;
import geneticnetwork.datastructures.ScoredGenes;
import geneticnetwork.datastructures.UnweightedEdge;
import geneticnetwork.datastructures.WeightedEdge;

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
	
	/**
	 * transforms the edges of the genetic network read from a file or a KNIME table into an array of Edge objects
	 * the method assumes an undirected network, it removes duplicate edges and keeps only one of the edges if
	 * n1->n2 and n2->n1 are listed 
	 * @param edges
	 * 		array of String array with 2 (unweighted network) or 3 (weighted network) elements (pos 0: start node,
	 * 		pos 1: end node, pos 2: weight)
	 * @return
	 * 		array of Edge objects (can be weighted or unweighted)
	 */
	public Edge[] transformEdges (String[][] edges){
		
		//length=2 -> unweighted, length=3 -> weighted
		int length = edges[0].length;
		LinkedList<Edge> listOfEdges = new LinkedList<Edge>();
		HashSet<String> duplicates = new HashSet<String>(edges.length);
		
		for(String[] edge:edges){
			
			//check for duplicate undirected edges
			String v1= edge[0];
			String v2=edge[1];
			String key="";
			if(v1.compareTo(v2)<0){
				key=v1+"+"+v2;
			}
			else{
				key=v2+"+"+v1;
			}
			if(!duplicates.add(key)){
				continue;
			}
			
			//unweighted
			if(length==2){
				UnweightedEdge e = new UnweightedEdge(edge[0], edge[1]);
				listOfEdges.add(e);
			}
			//weighted
			else if(length==3){
				int weight = Integer.parseInt(edge[2]);
				WeightedEdge e = new WeightedEdge(edge[0],edge[1], weight);
				listOfEdges.add(e);
			}
		}
		
		Edge[] arrayOfEdges = listOfEdges.toArray(new Edge[listOfEdges.size()]);
		return arrayOfEdges;
	}
}
