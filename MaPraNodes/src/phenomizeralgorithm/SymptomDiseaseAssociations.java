package phenomizeralgorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class SymptomDiseaseAssociations {
	
	private LinkedList<Integer> symptomIds;
	private HashMap<Integer,LinkedList<Integer[]>> kszD;
	private HashMap<Integer,HashSet<Integer>> kszS;
	
	public SymptomDiseaseAssociations(LinkedList<Integer> symptomIds,
			HashMap<Integer,LinkedList<Integer[]>> kszD,
			HashMap<Integer,HashSet<Integer>> kszS){
		
		this.symptomIds = symptomIds;
		this.kszD =kszD;
		this.kszS = kszS;
		
	}
	
	public int numberOfDiseases(){
		return kszD.size();
	}
	
	public int numberOfDiseases(int term){
		return kszS.get(term).size();
	}
	
	public LinkedList<Integer> getSymptoms(){
		return symptomIds;
	}
	
	public Set<Integer> getDiseases(){
		return kszD.keySet();
	}
	
	public LinkedList<Integer []> getSymptoms(int disease){
		return kszD.get(disease);
	}

}
