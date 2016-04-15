package phenotogeno.validation;

import phenomizer.algorithm.SymptomDiseaseAssociations;

/** iterates over all diseases annotated in PhenoDis exactly once*/
public class DiseaseIteratorAll implements DiseaseIterator{
	
	/** stores all disease ids for which a patient should be simulated */
	private int [] allDiseases;
	/** indicates the current position within the array of all diseases */
	private int currentPosition;
	
	/** initializes the disease iterator with an empty set of diseases*/
	public DiseaseIteratorAll() {
		allDiseases = new int [0];
		currentPosition=0;
	}
	
	@Override
	public void setSDA(SymptomDiseaseAssociations sda) {
		allDiseases = new int [sda.numberOfDiseases()];
		int position=0;
		for(int id: sda.getDiseases()){
			allDiseases[position++]=id;
		}
	}

	@Override
	public int getNextDiseaseId() {
		return allDiseases[currentPosition++];
	}

	@Override
	public boolean hasNextId() {
		return currentPosition<allDiseases.length;
	}

	@Override
	public int totalIterations() {
		return allDiseases.length;
	}

}
