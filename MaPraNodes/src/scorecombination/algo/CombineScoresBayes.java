package scorecombination.algo;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

import geneticnetwork.datastructures.ScoredGenes;
import togeno.ScoredGene;
import togeno.ScoredGeneComparator;

//TODO: implement and test
/** CombineScoresBayes is an algorithm for combining several sets of gene scores, 
 * e.g. scores for each gene from phenotype, metabotype and genotype analysis,
 * it is able to handle empty input and genes that are not part of all sets of gene scores */
public class CombineScoresBayes {
	
	/** array {@link ScoredGenes}s objects of all gene scores to combine */
	private ScoredGenes [] scores;
	
	/**
	 * creates a {@link CombineScoresBayes} object for combining gene scores
	 * @param scores array of {@link ScoredGenes} objects storing the scores to combine
	 */
	public CombineScoresBayes(ScoredGenes[] scores){
		this.scores = scores;
	}
	
	//TODO: rounding of final scores?!? number of decimal places -> sorting, creation of scored genes!
	
	//TODO: call methods and sort result before returning: test and comment
	public LinkedList<ScoredGene> combineScores(){
		HashSet<String> allGenes = getAllGenes();
		LinkedList<ScoredGene> combination = calculateScores(allGenes);
		Collections.sort(combination, new ScoredGeneComparator(10));
		return combination;
	}
	
	/**
	 * auxiliary method to extract all gene ids (e.g. Ensembl ids) stored in the input data
	 * @return set of all gene ids to score
	 */
	private HashSet<String> getAllGenes(){
		
		//special case: not initialized or no scores available
		if(scores.length==0 || scores==null){
			return new HashSet<String>();
		}
		
		HashSet<String> allGenes = new HashSet<String>(scores[0].size()*3);
		for(ScoredGenes currentScores: scores){
			for(String geneId: currentScores.getAllScoredGenes()){
				allGenes.add(geneId);
			}
		}
		
		return allGenes;
	}
	
	/**
	 * auxiliary method that calculates combined scores for a given set of gene ids (e.g. Ensembl id)
	 * @param allGenes set of all genes to score
	 * @return a list of {@link ScoredGene}s containing the combined scores for all genes in "allGenes"
	 */
	private LinkedList<ScoredGene> calculateScores(HashSet<String> allGenes){
		
		LinkedList<ScoredGene> result = new LinkedList<ScoredGene>();
		
		//iterate over all genes
		for(String geneId: allGenes){
			
			//calculate score for current gene
			// product of probabilities p
			double productP =1;
			// product of opposite probabilities 1-p
			double product1_P=1;
			// empty input
			if(scores.length==0 || scores == null){
				productP=0;
			}
			//non-empty input
			else{
				for(ScoredGenes scoreSet:scores){
					// gene id has score -> multiply it
					if(scoreSet.hasGene(geneId)){
						double currentScore = scoreSet.getScoreof(geneId);
						productP = productP*currentScore;
						product1_P = product1_P*(1-currentScore);
					}
					// gene id does not have score -> set it to 0
					else{
						productP = productP*0;
					}
				}
			}
			
			// make new ScoredGene for result list
			double finalScore = productP/(productP+product1_P);
			finalScore = ((double) Math.round(finalScore*1E10))/1E10;
			ScoredGene gene = new ScoredGene(geneId, finalScore, "");
			result.add(gene);
		}
		
		return result;
	}

}
