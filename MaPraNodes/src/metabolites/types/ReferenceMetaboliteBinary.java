package metabolites.types;

public class ReferenceMetaboliteBinary extends ReferenceMetabolite {
		
	/**
	 * generates binary reference metabolite
	 * @param id
	 * 		metabolite id
	 * @param missingness
	 * 		missingness in % (fraction of missing values in all controls)
	 */
	public ReferenceMetaboliteBinary(String id, double missingness) {
		super(id, missingness);
	}
	
	//TODO: implement scoring method
	@Override
	public ScoredMetabolite scoreMeasurement(double measurement) {
		return new ScoredMetaboliteBinary(getId(),0.0, 0.0);
	}

}
