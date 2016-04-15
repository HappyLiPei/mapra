package phenotogeno.validation;

import java.util.LinkedList;

public class SimulatedPatient {
	
	/** unique identifier of the patient*/
	private String id;
	/** disease of the patient */
	private int disease;
	/** symptoms of the patient*/
	private LinkedList<Integer> query;
	
	/**
	 * generates a SimulatedPatient object
	 * @param id unique identifier of the patient
	 * @param disease disease of the patient
	 * @param query list of symptom ids representing the symptoms of the patient
	 */
	public SimulatedPatient(String id, int disease, LinkedList<Integer> query){
		this.id=id;
		this.disease = disease;
		this.query=query;
	}
	
	/**
	 * retrieves the patient id
	 * @return unique identifier of the patient
	 */
	public String getId(){
		return id;
	}
	
	/**
	 * retrieves the disease of the patient
	 * @return PhenoDis disease id of the patient's disease
	 */
	public int getDisease(){
		return disease;
	}
	
	/**
	 * retrieves the symptoms of the patient
	 * @return list of PhenoDis symptoms ids representing the patients symptoms
	 */
	public LinkedList<Integer> getSymptoms(){
		return query;
	}
	
	/**
	 * generates a String representation of the patient
	 */
	public String toString(){
		String res =  id+"\t"+disease;
		for(int symp: query){
			res+="\t"+symp;
		}
		return res;
	}

}
