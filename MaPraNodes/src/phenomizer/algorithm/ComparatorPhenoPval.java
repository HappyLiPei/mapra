package phenomizer.algorithm;

import java.util.Comparator;

public class ComparatorPhenoPval implements Comparator<String[]> {

	@Override
	/**
	 * Method to compare the results of PhenomizerAlgorithmWithPval for two diseases
	 * @param arg0: array with id (pos 0), score (pos 1) and pvalue (pos 2) of a disease
	 * @param arg1: array with id (pos 0), score (pos 1) and pvalue (pos 2) of a second disease
	 * @return: returns a positive value if arg0 has a higher rank than arg1
	 * 	returns a negative value if arg0 has a lower rank than arg1
	 *  returns 0 if the arrays are identical (should never happen in PhenomizerAlgorithmWithPval)
	 */
	public int compare(String[] arg0, String[] arg1) {
		
		//compare pvalues -> ascending order
		int comparison_pval = Double.compare(Double.valueOf(arg0[2]), Double.valueOf(arg1[2]));
		if(comparison_pval!=0){
			return comparison_pval;
		}
		
		//equal pvalues
		else{
			//compare scores
			int comparison_score = Double.compare(Double.valueOf(arg0[1]), Double.valueOf(arg1[1]));
			if(comparison_score!=0){
				return -comparison_score;
			}
			
			//equal pvalues +equal scores
			else{
				return Integer.compare(Integer.valueOf(arg0[0]), Integer.valueOf(arg1[0]));
			}
		}
	}
	

}
