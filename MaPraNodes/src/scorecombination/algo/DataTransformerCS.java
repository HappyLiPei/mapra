package scorecombination.algo;

import java.util.HashMap;
import java.util.LinkedList;

import geneticnetwork.datastructures.ScoredGenes;

public class DataTransformerCS {
	
	/**
	 * transforms several results (gene scores) of PhenoToGeno, MetaboToGeno and/or NetworkScore read from files or
	 * KNIME tables into an array of ScoredGenes objects that is required for running the {@link CombineScoresBayes}
	 * algorithm
	 * @param scores_raw list of mappings gene_id -> score, each list entry represents a set of gene scores
	 * @return array of {@link ScoredGenes} objects
	 */
	public ScoredGenes[] transformAllScores(LinkedList<HashMap<String, Double>> scores_raw){
		
		ScoredGenes [] result = new ScoredGenes[scores_raw.size()];
		int position=0;
		for(HashMap<String, Double> scoreSet: scores_raw){
			result[position]=new ScoredGenes(scoreSet);
			position++;
		}
		
		return result;
	}

}
