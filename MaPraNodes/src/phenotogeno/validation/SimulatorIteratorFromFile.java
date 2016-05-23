package phenotogeno.validation;

import java.util.LinkedList;

import io.FileInputReader;
import phenomizer.algorithm.SymptomDiseaseAssociations;

public class SimulatorIteratorFromFile implements DiseaseIterator, PatientSimulator {
	
	private SimulatedPatient [] patients;
	private int currentPositionSimulator;
	private int currentPositionIterator;
		
	public SimulatorIteratorFromFile(String patientFile) {
		
		LinkedList<String> patientLines = FileInputReader.readAllLinesFrom(patientFile);
		patients = new SimulatedPatient[patientLines.size()];
		int pos=0;
		//read and parse patient file
		for(String line:patientLines){
			//parse tab-separated line, pos 0: patient id, pos 1: disease id, pos 2+: symptom ids 
			SimulatedPatient patient = SimulatedPatient.StringToPatient(line);
			//add patient to array
			patients[pos] = patient;
			pos++;
		}
	}
	
	@Override
	public SimulatedPatient simulatePatient(int diseaseId, LinkedList<Integer[]> diseaseSymptoms) {
		if(currentPositionSimulator<patients.length){
			SimulatedPatient nextPatient = patients[currentPositionSimulator];
			currentPositionSimulator++;
			return nextPatient;
		}
		//hastNextId=false -> invalid call of simulatePatient->return null, should not happen
		return null;
	}
	
	@Override
	public int getNextDiseaseId() {
		//id of next patient
		if(currentPositionIterator<patients.length){
			int nextDisease= patients[currentPositionIterator].getDisease();
			currentPositionIterator++;
			return nextDisease;
		}
		//hasNextId=false->invalid call of getNextDiseaseId->return -1, should not happen
		return -1;
	}

	@Override
	public boolean hasNextId() {
		return currentPositionIterator<patients.length;
	}

	@Override
	public int totalIterations() {
		return patients.length;
	}

	@Override
	public void setSDA(SymptomDiseaseAssociations sda) {		
	}

}
