package phenotogeno.validation;

import java.util.LinkedList;

/** does simulation of patients by extracting the very frequent symptoms of a disease as a query*/
public class PatientSimulatorVeryFrequentSymptoms extends PatientSimulatorWriteToFile{
	
	/** 
	 * generates a PatientSimulator that simulates patients as collection of very frequent weights
	 * @param path path to the file to which the patients are written
	 */
	public PatientSimulatorVeryFrequentSymptoms(String path) {
		super(path);
	}

	@Override
	public SimulatedPatient simulatePatient(int diseaseId, LinkedList<Integer[]> diseaseSymptoms) {
		
		LinkedList<Integer> query = new LinkedList<Integer>();
		for(Integer[] symptom:diseaseSymptoms){
			if(symptom[1]>=15){
				query.add(symptom[0]);
			}
		}
		if(query.size()==0){
			return null;
		}
		
		SimulatedPatient patient = new SimulatedPatient(getCurrentId()+"", diseaseId, query);
		writePatient(patient);
		
		return patient;
	}

}
