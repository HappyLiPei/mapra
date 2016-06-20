package geneticnetwork.algorithm;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import geneticnetwork.datastructures.Vector;
import togeno.ScoredGene;
import togeno.ScoredGeneComparator;

public class NetworkScoreAlgorithm {
	
	/** matrix vector builder object that provides the data structures for the random walk with restart*/
	private MatrixVectorBuilder mvb;
	/** random walk with restart object that performs the actual calculations*/
	private RandomWalkWithRestart rwwr;
	
	/**
	 * generates a NetworkScoreAlgorithm
	 * @param mvb MatrixVectorBuilder that generates matrix and vector
	 * @param rwwr RandwomWalkWithRestart for the actual calculations
	 */
	public NetworkScoreAlgorithm(MatrixVectorBuilder mvb, RandomWalkWithRestart rwwr){
		this.mvb = mvb;
		this.rwwr=rwwr;
	}
	
	/**
	 * method to run the whole network score procedure
	 * @return a list of ScoredGene objects
	 */
	public LinkedList<ScoredGene> runNetworkScoreAlgorithm(){
		
		rwwr.setVector(mvb.getRestartVector());
		rwwr.setMatrix(mvb.getStochasticMatrix());
		
		Vector res = rwwr.doRandomWalkWithRestart();
		
		return generateResult(res);
	}
	
	/**
	 * auxiliary method for transforming the output of RandomWalkWithRestart into a list of scored genes,
	 * the method rounds the scores up to 10 decimal places and sorts the genes according to score
	 * @param res vector returned by the RandomWalkWithRestart
	 * @return list of ScoredGene objects sorted according to the score within the vector res
	 */
	private LinkedList<ScoredGene> generateResult(Vector res){
		
		LinkedList<ScoredGene> geneList = new LinkedList<ScoredGene>();
		HashMap<String, Integer> idsToPos = mvb.getIdPositionMap();
		for(String gene:idsToPos.keySet()){
			int pos = idsToPos.get(gene);
			double score = (double) Math.round(res.getEntry(pos)*1E10)/1E10;
			ScoredGene sg = new ScoredGene(gene, score, "");
			geneList.add(sg);
		}
		Collections.sort(geneList, new ScoredGeneComparator(10));
		return geneList;
	}

}