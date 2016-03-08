package phenomizer.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class SymptomDiseaseAssociations {
	
	private LinkedList<Integer> symptomIds;
	private HashMap<Integer,LinkedList<Integer[]>> kszD;
	private HashMap<Integer,HashSet<Integer>> kszS;
	
	/**
	 * constructor
	 * @param symptomIds: list of all PhenoDis symptom ids (without duplicates!)
	 * @param kszD: hashmap disease id->list of symptom ids with weights (without ancestors)
	 * @param kszS: hashmap symptom id -> set of disease ids (annotations transferred to predecessor symptoms)
	 */
	public SymptomDiseaseAssociations(LinkedList<Integer> symptomIds,
			HashMap<Integer,LinkedList<Integer[]>> kszD,
			HashMap<Integer,HashSet<Integer>> kszS){
		
		this.symptomIds = symptomIds;
		this.kszD =kszD;
		this.kszS = kszS;
		
	}
	
	/**
	 * returns the total number of diseases
	 * @return total number of diseases
	 */
	public int numberOfDiseases(){
		return kszD.size();
	}
	
	/**
	 * returns the number of diseases annotated to a symptom
	 * @param symptom: PhenoDis symptom id
	 * @return: number of diseases annotated to the symptom
	 */
	public int numberOfDiseases(int symptom){
		return kszS.get(symptom).size();
	}
	
	/**
	 * returns the total number of symptoms (without duplicates)
	 * @return number of symptoms 
	 */
	public int numberOfSymptoms(){
		return symptomIds.size();
	}
	
	/**
	 * returns all symptom ids of PhenoDis
	 * @return a list of all symptom ids
	 */
	public LinkedList<Integer> getSymptoms(){
		return symptomIds;
	}
	
	/**
	 * returns all symptom ids of PhenoDis in an array
	 * @return an array of all symptom ids
	 */
	public int [] getAllSymptomsArray(){
		int [] symptomArray = new int [symptomIds.size()];
		int pos=0;
		for(Integer symptom_id:symptomIds){
			symptomArray[pos]=symptom_id;
			pos++;
		}
		return symptomArray;
	}
	
	/**
	 * returns all disease ids of PhenoDis
	 * @return a set of all disease ids
	 */
	public Set<Integer> getDiseases(){
		return kszD.keySet();
	}
	
	/**
	 * returns all symptoms annotated to a disease (ancestors in ontology not included)
	 * @param disease: PhenoDis ID of a disease
	 * @return List of Arrays with symptom ids (index 0) and with weights (index 1)
	 */
	public LinkedList<Integer []> getSymptoms(int disease){
		return kszD.get(disease);
	}

}
