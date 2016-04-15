package phenotogeno.validation;

import java.util.LinkedList;

import io.FileOutputWriter;

/** does simulation of patients by extracting the very frequent symptoms of a disease as a query*/
public class PatientSimulatorVeryFrequentSymptoms implements PatientSimulator{
	
	/** counter for generating the id of the current patient*/
	private int currentId;
	/** writer for file of all generated patients */
	private FileOutputWriter patientWriter;
	
	/** 
	 * generates a PatientSimulator that simulates patients as collection of very frequent weights
	 * @param path path to the file to which the patients are written
	 */
	public PatientSimulatorVeryFrequentSymptoms(String path) {
		currentId = 0;
		patientWriter = new FileOutputWriter(path);
	}
	
	/** ends the simulation by closing the writer for the patient file*/
	public void endSimulation(){
		patientWriter.closew();
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
		
		SimulatedPatient patient = new SimulatedPatient(currentId+"", diseaseId, query);
		currentId++;
		patientWriter.writeFileln(patient.toString());
		
		return patient;
	}

}
