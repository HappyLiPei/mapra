package phenotogeno.validation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

/** patient simulator that simulates patients with choosing symptoms independtly using the frequency information*/
public class PatientSimulatorDrawSymptoms extends PatientSimulatorWriteToFile{
	
	/** pseudo random number generator */
	private Random randomNumberGenerator;
	
	/**
	 * generates a patient simulator that chooses symptoms according to their frequency
	 * @param path path to a file to which the patients are written
	 */
	public PatientSimulatorDrawSymptoms(String path) {
		super(path);
		randomNumberGenerator = new Random();
	}

	@Override
	public SimulatedPatient simulatePatient(int diseaseId, LinkedList<Integer[]> diseaseSymptoms) {
		
		//query should be have length at least 5
		if(diseaseSymptoms.size()<5){
			return null;
		}
		
		//list to save simulated symptoms
		LinkedList<Integer> result = new LinkedList<Integer>();
		
		//disease has exactly 5 symptoms
		if(diseaseSymptoms.size()==5){
			for(Integer[] symptom:diseaseSymptoms){
				result.add(symptom[0]);
			}
		}
		
		//disease has more than 5 symptoms
		HashSet<Integer> addedSymptoms = new HashSet<Integer> (diseaseSymptoms.size()*3);	
		while(result.size()<5){
			for(Integer[] symptom: diseaseSymptoms){
				if(!addedSymptoms.contains(symptom[0])){
					double randomNumber = randomNumberGenerator.nextDouble();
					//ATTENTION: assumes the weights are 5, 10 and 15 -> if not it could get stuck in endless loop
					if(		(symptom[1]==5 && randomNumber<=0.125) || 
							(symptom[1]==10 && randomNumber<=0.5) ||
							(symptom[1]==15 && randomNumber<=0.875)		){
						addedSymptoms.add(symptom[0]);
						result.add(symptom[0]);
					}
				}
			}
		}
		
		//generate patient and write it to file
		SimulatedPatient patient = new SimulatedPatient(getCurrentId()+"", diseaseId, result);
		writePatient(patient);
		
		return patient;
	}

}
