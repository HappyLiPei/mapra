package phenotogeno.validation;

import phenomizer.algorithm.SymptomDiseaseAssociations;

public interface DiseaseIterator {
	
	/**
	 * gets the next disease for which patients should be simulated
	 * @return PhenoDis disease id of the next disease to simulate
	 */
	public int getNextDiseaseId();
	
	/**
	 * checks if there are some diseases left that were not simulated yet
	 * @return true if there are disease to simulate, false if the simulation is done
	 */
	public boolean hasNextId();
	
	/**
	 * gets the total number of simulation runs for making progress output
	 * @return total number of patients to simulate
	 */
	public int totalIterations();
	
	/**
	 * initializes the iterator with information about disease - symptom association
	 * @param sda SymptomDiseaseAssociation object representing the annotations of symptoms to diseases in PhenoDis
	 */
	public void setSDA(SymptomDiseaseAssociations sda);

}
