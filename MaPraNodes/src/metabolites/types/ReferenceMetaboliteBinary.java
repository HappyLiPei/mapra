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
	
	@Override
	public ScoredMetabolite scoreMeasurement(double measurement, int group) {
		//group does not matter for a binary metabolite
		//measured metabolite is missing
		if(Double.isNaN(measurement)){
			double probability = getMissingness()/100d;
			probability = Math.round(probability*100000)/100000d;
			return new ScoredMetaboliteBinary(getId(), 0, probability);
		}
		//measured metabolite is present
		else{
			double probability = 1-getMissingness()/100d;
			probability = Math.round(probability*100000)/100000d;
			return new ScoredMetaboliteBinary(getId(), 1, probability);
		}
	}

}
