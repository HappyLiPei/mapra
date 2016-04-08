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
	
	//TODO: round probability!
	@Override
	public ScoredMetabolite scoreMeasurement(double measurement, int group) {
		//group does not matter for a binary metabolite
		//measured metabolite is missing
		if(Double.isNaN(measurement)){
			return new ScoredMetaboliteBinary(getId(), 0, getMissingness()/100d);
		}
		//measured metabolite is present
		else{
			return new ScoredMetaboliteBinary(getId(), 1, 1-getMissingness()/100d);
		}
	}

}
