package metabolites.types;

public class ReferenceMetaboliteBinary extends ReferenceMetabolite {
	
	/** fraction of missing values in the controls for that metabolite*/
	private double missingness;
	
	/**
	 * generates binary reference metabolie
	 * @param id
	 * 		metabolite id
	 * @param missingness
	 * 		missingness in % (fraction of missing values in all controls)
	 */
	public ReferenceMetaboliteBinary(String id, double missingness) {
		super(id);
		this.missingness = missingness;
	}
	
	/**
	 * retrieves the missingness associated for the binary reference metabolite
	 * @return
	 * 		missingness as fraction of missing values in %
	 */
	public double getMissingness(){
		return missingness;
	}
	
	//TODO: implement scoring method
	@Override
	public ScoredMetabolite scoreMeasurement(double measurement) {
		return new ScoredMetaboliteBinary(getId(),0.0, 0.0);
	}

}
