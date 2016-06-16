package scorecombination.algo;

import java.util.HashMap;
import java.util.LinkedList;

import geneticnetwork.datastructures.ScoredGenes;
import togeno.ScoredGene;

/** driver to run the {@link CombineScoresBayes} algorithm */
public class CombineScoresDriver {
	
	/** list for storing the gene scores, each set of scores is represented as a mapping gene_id -> score */
	private LinkedList<HashMap<String, Double>> input_raw;
	/** array of {@link ScoredGenes} objects representing the data of input_raw,
	 * this data structure is created by prepareData() and is required for running {@link CombineScoresBayes} */
	private ScoredGenes[] scores;
	
	/**
	 * creates a driver for running {@link CombineScoresBayes}
	 */
	public CombineScoresDriver (){
		input_raw = new LinkedList<HashMap<String, Double>>();
	}
	
	/** 
	 * method to add a set of gene scores to the input data
	 * @param scoreSet mapping gene_id->scores
	 */
	public void addInput(HashMap<String, Double> scoreSet){
		input_raw.add(scoreSet);
	}
	
	/**
	 * transforms the raw input data into the data structures requires for running {@link CombineScoresBayes}
	 */
	private void prepareData(){
		DataTransformerCS dt = new DataTransformerCS();
		scores = dt.transformAllScores(input_raw);
	}
	
	/**
	 * executes the {@link CombineScoresBayes} algorithm, note that you have to add input data via addInput()
	 * before you call this method
	 * @return list of {@link ScoredGene} objects, sorted in descending order according to the combined scores
	 */
	public LinkedList<ScoredGene> runCombineScores(){
		
		prepareData();
		CombineScoresBayes algorithm = new CombineScoresBayes(scores);
				
		return algorithm.combineScores();
	}
	
	

}
