package phenotogeno.validation;

import java.util.LinkedList;

import togeno.ScoredDiseaseOrMetabolite;

/** object to model the interface between Phenomizer and PhenoToGeno, it handles to output size of Phenomizer
 * and filters the output of Phenomizer*/
public abstract class PhenomizerFilter {
	
	/** total number of disease in the database used by Phenomizer*/
	protected int totalNumberOfDiseases;
	
	/**
	 * method to initialize this filter object
	 * @param numberOfDiseases total number of disease in the database used by Phenomizer
	 */
	public void setTotalDiseases(int numberOfDiseases){
		this.totalNumberOfDiseases=numberOfDiseases;
	}
	
	/**
	 * retrieves the output size for Phenomizer
	 * @return output size for Phenomizer
	 */
	public abstract int getResultSize();
	
	/**
	 * filters the scored diseases from Phenomizer
	 * @param phenomizerUnfiltered unfiltered list of scored diseases
	 * @return filtered list of scored diseases
	 */
	public abstract LinkedList<ScoredDiseaseOrMetabolite> filter(LinkedList<ScoredDiseaseOrMetabolite> phenomizerUnfiltered);
	
}
