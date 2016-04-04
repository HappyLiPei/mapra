package metabolites.types;

public class ScoredMetaboliteConcentration extends ScoredMetabolite{
	
	/**
	 * generates a ScoredMetabolite of type concentration
	 * @param id
	 * 		id of the metabolite
	 * @param score
	 * 		Z score for the measured concentration of the metabolite
	 * @param probability
	 * 		probability of observing the Z score at random
	 */
	public ScoredMetaboliteConcentration(String id, double score, double probability) {
		super(id, score, probability);
	}

	@Override
	public String getType() {
		return "concentration";
	}

}
