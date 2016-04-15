package phenotogeno.validation;

import java.util.LinkedList;

public interface PatientSimulator {
	
	/**
	 * method to simulate a patient with defined disease
	 * @param diseaseId PhenoDis id of the disease
	 * @param diseaseSymptoms symptoms associeated with the disease (as list of integer pairs (pos 0: symptom id of PhenoDis,
	 * 		pos 1: weighting factor for the symptom)
	 * @return
	 * 		a simulated patient object
	 */
	public SimulatedPatient simulatePatient(int diseaseId, LinkedList<Integer[]> diseaseSymptoms);

}
