package geneticnetwork.algorithm;

import java.util.HashMap;
import java.util.LinkedList;

import geneticnetwork.datastructures.Edge;
import geneticnetwork.datastructures.ScoredGenes;
import phenotogeno.algo.ScoredGene;

public class NetworkScoreDriver {
	
	/** network read from a file or KNIME table */
	private String [][] network_raw;
	/** result from PhenoToGeno read from a file or KNIME table*/
	private HashMap<String, Double> scores_raw;
	
	/** matrix vector builder for generating restart vector and transition matrix*/
	private MatrixVectorBuilder builder;
	/** random walk with restart object for doing all the calculations*/
	private RandomWalkWithRestart rwwr;
	
	/**
	 * generates a driver for running the network score algorithm
	 * @param network_raw array of String arrays of length 2 or 3 representing a network of genes,
	 * 		each array represent an edge (position 0: gene1 of edge, position 1: gene2 of egde, 
	 * 		position 2: optional weight of edge)
	 * @param scores_raw mapping of gene_id->score produced by PhenoToGeno
	 */
	public NetworkScoreDriver( String [][] network_raw, HashMap<String, Double> scores_raw){
		this.network_raw = network_raw;
		this.scores_raw = scores_raw;
	}
	
	/**
	 * method to set the parameters for the network score algorithm,
	 * @param restart fraction of the original scores that is kept (restart probability) 
	 * @param convergence flag to indicate if the random walk is executed until convergence or for a fixed number of steps
	 * @param iterations maximum distance of nodes to consider (number of iterations in random walk) 
	 */
	public void SetNetworkScoreAlgorithm(double restart, boolean convergence, int iterations){
		
		DataTransformerGeneticNetwork dt = new DataTransformerGeneticNetwork();
		Edge[] network= dt.transformEdges(network_raw);
		ScoredGenes scores =dt.transformGeneScores(scores_raw);
		builder = new MatrixVectorBuilder(network, scores);
		
		if(!convergence){
			rwwr = new RandomWalkWithRestartFixedIterations(restart, iterations);
		}
		else{
			rwwr = new RandomWalkWithRestartUntilConvergence(restart);
		}
	}
	
	/**
	 * method to execute the network score algorithm
	 * @return list of scored genes sorted according to their score
	 */
	public LinkedList<ScoredGene> runNetworkScoreAlgorithm(){
		NetworkScoreAlgorithm n = new NetworkScoreAlgorithm(builder, rwwr);
		return n.runNetworkScoreAlgorithm();
	}
	
	/**
	 * retrieves the number of steps that were done in the random walk with restart
	 * @return number of steps of the random walk with restart
	 */
	public int getNumberOfIterationsDone(){
		return rwwr.getNumberOfIterations();
	}
	
	/**
	 * retrieves a parameter describing the convergence of the random walk with restart
	 * @return max norm of the difference between the final result and the result of the second last step
	 */
	public double getConvergenceNorm(){
		return rwwr.getDifferenceToPrevious();
	}

}
