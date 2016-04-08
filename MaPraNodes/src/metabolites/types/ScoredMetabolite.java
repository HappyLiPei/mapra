package metabolites.types;

public abstract class ScoredMetabolite extends Metabolite {
	
	/** score representing a Z score (concentration) or binary information 0/1 (binary) */
	private double score;
	/** probabilty of observing the score at random*/
	private double probability;
	
	/**
	 * generates a scoredMetabolite
	 * @param id
	 * 		id of the metabolite
	 * @param score
	 * 		score of that metabolite with respect to the reference
	 * @param probability
	 * 		probability associated with the score
	 */
	public ScoredMetabolite(String id, double score, double probability) {
		super(id);
		this.score = score;
		this.probability = probability;
	}
	
	/**
	 * retrieves the score of the metabolite
	 * @return
	 * 		score of the metabolite, either a Z score (concentration) or 0/1 (binary)
	 */
	public double getScore(){
		return score;
	}
	
	/**
	 * retrieves the probability for the metabolite score
	 * @return
	 * 		probability of observing the score of the metabolite at random
	 */
	public double getProbability(){
		return probability;
	}
	
	/**
	 * indicates the type of the scoring
	 * @return possible values:
	 * 		"concentration" if the object is of type Concentration
	 * 		"binary" if the object is of type Binary
	 */
	public abstract String getType();
	
	/**
	 * returns a String representing this scored metabolite
	 * the String contains the id, type, score and probability of the score
	 */
	public String toString(){
		return getId()+"\t"+getType()+"\t"+getScore()+"\t"+getProbability();
	}
}
