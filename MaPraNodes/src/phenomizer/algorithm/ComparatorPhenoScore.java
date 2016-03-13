package phenomizer.algorithm;

public class ComparatorPhenoScore extends ComparatorPheno{

	@Override
	/**
	 * Method to compare the results of PhenomizerAlgorithmNoPval for two diseases
	 * @param arg0: array with id (pos 0) and score (pos 1) of a disease
	 * @param arg1: array with id (pos 0) and score (pos 1) of a second disease
	 * @return: returns a positive value if arg0 has a higher rank than arg1
	 * 	returns a negative value if arg0 has a lower rank than arg1
	 *  returns 0 if the arrays are identical (should never happen in PhenomizerAlgorithmNoPval)
	 */
	public int compare(String[] arg0, String[] arg1) {
		
		//compare scores -> descending order
		int comparison_score = Double.compare(Double.valueOf(arg0[1]), Double.valueOf(arg1[1]));
		if(comparison_score!=0){
			return -comparison_score;
		}
		
		//equal scores -> order ascendingly by disease id 
		else{
			return Integer.compare(Integer.valueOf(arg0[0]), Integer.valueOf(arg1[0]));
		}
		
	}

	@Override
	/**
	 * Method to compare the results of PhenomizerAlgorithmNoPval for two diseases
	 * @param arg0: array with id (pos 0) and score (pos 1) of a disease
	 * @param arg1: array with id (pos 0) and score (pos 1) of a second disease
	 * @return: returns a positive value if arg0 has a higher rank than arg1
	 * 	returns a negative value if arg0 has a lower rank than arg1
	 *  returns 0 ar0 and arg1 have the same rank
	 */
	public int compareWithoutID(String[] arg0, String[] arg1) {
		return -Double.compare(Double.valueOf(arg0[1]), Double.valueOf(arg1[1]));
	}

}
