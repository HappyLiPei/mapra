package phenotogeno.algo;

public class ScoredGene extends Gene {
	
	//final score of the gene
	private double score;
	//string representation of id list
	private String important_disease_ids;
	
	/**
	 * generates a ScoredGene
	 * @param id gene id (e.g ensembl id) as a string
	 * @param score score of the gene (probability between 0 and 1, rounded to 5 decimal places)
	 * @param ids String of one or several PhenoDis disease ids (separated by commas),
	 * 			String is empty if the score is 0,
	 * 			the String represents those ids that contributed most to the gene's score
	 */
	public ScoredGene(String id, double score, String ids) {
		super(id);
		this.score = score;
		this.important_disease_ids =ids;
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
	
	/**
	 * retrieves the disease ids of the diseases that contributed most to the gene's score
	 * @return a comma-seprarted String of PhenoDis ids,
	 * 			the String is empty if the gene's score is 0
	 */
	public String getImportantDiseases(){
		return this.important_disease_ids;
	}
}
