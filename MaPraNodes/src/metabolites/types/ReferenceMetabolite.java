package metabolites.types;

public abstract class ReferenceMetabolite extends Metabolite {
	
	/**
	 * generates a ReferenceMetabolite object
	 * @param id
	 * 		metabolite id
	 */
	public ReferenceMetabolite(String id) {
		super(id);
	}
	
	/**
	 * scores a measurement on the basis of this reference metabolite
	 * @param measurement
	 * 		measured concentration of the metabolites, is null if the metabolite is missing
	 * @return
	 * 		a ScoredMetabolite object representing score and probability of the measurement
	 */
	public abstract ScoredMetabolite scoreMeasurement(double measurement);
	
}
