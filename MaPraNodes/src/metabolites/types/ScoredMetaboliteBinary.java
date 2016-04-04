package metabolites.types;

public class ScoredMetaboliteBinary extends ScoredMetabolite {
	
	/**
	 * generates a binary ScoredMetabolite
	 * @param id
	 * 		id of the metabolite
	 * @param score
	 * 		score indicating whether the metabolite is present (1) or absent (0)
	 * @param probability
	 * 		probability of observing the score at random
	 */
	public ScoredMetaboliteBinary(String id, double score, double probability) {
		super(id, score, probability);
	}

	@Override
	public String getType() {
		return "binary";
	}

}
