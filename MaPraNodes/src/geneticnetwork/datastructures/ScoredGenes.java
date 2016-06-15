package geneticnetwork.datastructures;

import java.util.HashMap;

/** object representing the result of a prediction by PhenoToGeno so that it can be used for the network scoring */
public class ScoredGenes {
	
	/** mapping gene_id ->score */
	private HashMap<String, Double> idToScore;
	/** array of all gene ids of this object */
	private String [] allGeneIds;
	
	/**
	 * generates an object representing the results of PhenoToGeno for the network scoring
	 * @param idToScore mapping geneId-> score of the gene
	 */
	public ScoredGenes(HashMap<String, Double> idToScore){
		//keep hashmap
		this.idToScore = idToScore;
		//get array of all keys in the map -> precalculated
		allGeneIds = new String[idToScore.size()];
		int position=0;
		for(String id:idToScore.keySet()){
			allGeneIds[position++]=id;
		}
	}
	
	/**
	 * checks if a the score of a particular gene is stored in this object
	 * @param geneId id of the gene
	 * @return true if the gene has a score in this object, otherwise the method returns false
	 */
	public boolean hasGene(String geneId){
		return idToScore.containsKey(geneId);
	}
	
	/**
	 * retrieves the score for a particular gene
	 * @param geneId id of the gene
	 * @return the score of the corresponding gene as double or NaN if the gene is not part of the results of PhenoToGeno
	 */
	public double getScoreof(String geneId){
		if(hasGene(geneId)){
			return idToScore.get(geneId);
		}
		else{
			return Double.NaN;
		}
	}
	
	/**
	 * retrieves an array of all gene ids that got a score by PhenoToGeno
	 * @return array of all gene ids in this object
	 */
	public String[] getAllScoredGenes(){
		return allGeneIds;
	}
	
	/**
	 * retrieves the number of gene-score pairs stored in this object
	 * @return number of genes managed by this object
	 */
	public int size(){
		return allGeneIds.length;
	}

}
