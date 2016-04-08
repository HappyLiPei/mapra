package metabolites.types;

public abstract class ReferenceMetabolite extends Metabolite {
	
	/** fraction of missing values in % in the controls for that metabolite (before imputation)*/
	private double missingness;
	
	/**
	 * generates a ReferenceMetabolite object
	 * @param id
	 * 		metabolite id
	 * @param missingness
	 * 		missingness in % (fraction of missing values in all controls before imputation)
	 */
	public ReferenceMetabolite(String id, double missingness) {
		super(id);
		this.missingness = missingness;
	}
	
	/**
	 * retrieves the missingness associated for the reference metabolite
	 * @return
	 * 		missingness as fraction of missing values in % (before imputation)
	 */
	public double getMissingness(){
		return missingness;
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
