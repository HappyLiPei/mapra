package togeno;

/** object representing a scored gene, i.e. a gene assigned with a probability of being causal for a patient's disease*/
public class ScoredGene extends Gene {
	
	/** score of the gene: probability of being the causal gene */
	private double score;
	/** string representation of an id list with the metabolites or diseases contributing most to the score */
	private String important_disOrMet_ids;
	
	/**
	 * generates a ScoredGene
	 * @param id gene id (e.g ensembl id) as a string
	 * @param score score of the gene (probability between 0 and 1, rounded to a defined number of decimal places)
	 * @param ids String of one or several PhenoDis disease ids/metabolite ids (separated by commas),
	 * 			String is empty if the score is 0 or when the object is used for the network score algorithm
	 * 			the String represents those disease/metabolite ids that contributed most to the gene's score
	 */
	public ScoredGene(String id, double score, String ids) {
		super(id);
		this.score = score;
		this.important_disOrMet_ids =ids;
	}
	
	/**
	 * retrieves the score of the gene
	 * @return score of the gene
	 * 		the score is between 0 and 1 (probability),
	 * 		1 indicates a high score and 0 indicates a low score
	 */
	public double getScore(){
		return this.score;
	}
	
	//TODO: different log?!
	
	/**
	 * method to calculate the enrichment score of the gene given the total number of genes
	 * @param numberOfGenes total number of genes that are considered during the scoring process
	 * @return the enrichment score of the gene indicating if the score is better than expected by random
	 */
	public double getEnrichmentScore(int numberOfGenes){
		double enrichment = Math.log10(this.score)+Math.log10((double) numberOfGenes);
		if(Double.isInfinite(enrichment)){
			return enrichment;
		}
		else{
			return (double) Math.round(enrichment*1E3)/1E3;
		}
	}
	
	/**
	 * retrieves the ids of the diseases/metabolites that contributed most to the gene's score
	 * @return a comma-seprarted String of PhenoDis ids/metabolite ids,
	 * 			the String is empty if the gene's score is 0 or if this object is used in the network score algorithm
	 */
	public String getImportantContributors(){
		return this.important_disOrMet_ids;
	}
	
	/**
	 * generates a string representation of this scored gene consisting of gene id, score and contributors to the score
	 */
	public String toString(){
		return getId()+"\t"+score+"\t"+important_disOrMet_ids;
	}
}
