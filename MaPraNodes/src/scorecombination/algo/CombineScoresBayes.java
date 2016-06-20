package scorecombination.algo;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

import geneticnetwork.datastructures.ScoredGenes;
import togeno.ScoredGene;
import togeno.ScoredGeneComparator;

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
	
	/**
	 * method for calculating the combined scores for the gene scores managed by this object
	 * @return list of {@link ScoredGene}s with the combined scores
	 */
	public LinkedList<ScoredGene> combineScores(){
		HashSet<String> allGenes = getAllGenes();
		LinkedList<ScoredGene> combination = calculateScores(allGenes);
		combination = normalizeAndRound(combination);
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
			ScoredGene gene = new ScoredGene(geneId, finalScore, "");
			result.add(gene);
		}
		
		return result;
	}
	
	/**
	 * auxiliary method that normalizes and rounds the combined scores calculated by calculateScores()
	 * @param scores_raw list of {@link ScoredGene}s, result of calculateScores()
	 * @return a list of {@link ScoredGene}s, the scores of all genes sum up to 1 and are rounded to 10 decimal places
	 */
	private LinkedList<ScoredGene> normalizeAndRound(LinkedList<ScoredGene> scores_raw){
		
		//calculate sum of all scores
		double sum=0;
		for(ScoredGene g: scores_raw){
			sum+=g.getScore();
		}
		
		//modify scores
		LinkedList<ScoredGene> newScores = new LinkedList<ScoredGene>();
		for(ScoredGene old: scores_raw){
			//normlize score such that the resulting sum is 1
			double finalScore = old.getScore()/sum;
			//round score to 10 decimal places
			finalScore = ((double) Math.round(finalScore*1E10))/1E10;
			ScoredGene newGene = new ScoredGene(old.getId(), finalScore, "");
			newScores.add(newGene);
		}
		
		return newScores;
	}

}
