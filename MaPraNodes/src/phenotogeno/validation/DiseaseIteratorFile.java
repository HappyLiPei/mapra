package phenotogeno.validation;

import java.util.LinkedList;

import io.FileInputReader;
import phenomizer.algorithm.SymptomDiseaseAssociations;

/** disease iterator for simulating patients with diseases read from a file (list of disease PhenoDis ids),
 * it is possible to adjust the number of simulated patients per disease entry in the file*/
public class DiseaseIteratorFile implements DiseaseIterator {
	
	/** number of patients per disease id*/
	private int numberOfPatients;
	/** array of all PhenoDis disease ids for which patients should be simulated*/
	private int [] diseaseIds;
	/** pointer to the current position within the diseaseIds array*/
	private int currentPosId;
	/** counts the number of patients that were already simulated for the disease at currentPosId in diseaseIds*/
	private int currentPatientCount;
	
	/**
	 * generates a {@link DiseaseIterator} that reads the disease ids to simulate from a file
	 * @param numberOfPatients number of patients to simulate per disease id in the file
	 * @param file path to a file with disease ids, each line should contain exactly one disease id without additional 
	 * 	information
	 */
	public DiseaseIteratorFile(int numberOfPatients, String file) {
		this.numberOfPatients = numberOfPatients;
		//read ids from file
		LinkedList<String> lines = FileInputReader.readAllLinesFrom(file);
		diseaseIds = new int[lines.size()];
		int position=0;
		for(String line:lines){
			diseaseIds[position++]=Integer.valueOf(line);
		}
		currentPosId=0;
		currentPatientCount=0;
	}

	@Override
	public int getNextDiseaseId() {
		if(currentPatientCount<numberOfPatients){
			currentPatientCount++;
		}
		else{
			currentPatientCount=1;
			currentPosId++;
		}
		return diseaseIds[currentPosId];
	}

	@Override
	public boolean hasNextId() {
		//all diseases but last disease
		if(currentPosId<diseaseIds.length-1){
			return true;
		}
		//last disease
		else if(currentPosId==diseaseIds.length-1){
			return currentPatientCount<numberOfPatients;
		}
		return false;
	}

	@Override
	public int totalIterations() {
		return numberOfPatients*diseaseIds.length;
	}

	@Override
	public void setSDA(SymptomDiseaseAssociations sda) {		
	}

}
