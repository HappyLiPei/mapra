package phenomizer.algorithm;

import java.util.Comparator;

public abstract class ComparatorPheno implements Comparator<String[]>{
	
	/**
	 * method to compare the result of Phenomizer for two diseases without considering their id
	 * @param arg0: array representing the Phenomizer results for a disease
	 * @param arg1: array representing the Phenomizer results for a second disease
	 * @return: a positive value if arg0 has a higher rank than arg1
	 * 	returns a negative value if arg0 has a lower rank than arg1
	 *  returns 0 arg0 and arg1 have the same rank
	 */
	public abstract int compareWithoutID(String[] arg0, String[] arg1);

}
