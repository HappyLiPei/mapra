package phenomizeralgorithm;

import java.util.HashMap;
import java.util.LinkedList;

public abstract class PhenomizerAlgorithm {
	
	//result size
	protected int num;
	
	//input data
	//private Ontology ontology;
	protected LinkedList<Integer> queryIds;
	protected SymptomDiseaseAssociations sda;
	
	//intermediate results
	private HashMap<Integer,Double> ic;
	private HashMap<String,Double> calculatedSim;
	
	//strategy pattern for calculating weighted similarity scores
	protected SimilarityCalculator similarityCalculator;
	
	public PhenomizerAlgorithm(int num, Ontology ontology, LinkedList<Integer> queryIds,
			SymptomDiseaseAssociations sda, SimilarityCalculator similarityCalculator){
		
		this.num=num;
		//this.ontology=ontology;
		this.queryIds=queryIds;
		this.sda = sda;
		this.ic=new HashMap<Integer,Double>();
		this.calculatedSim=new HashMap<String, Double>();
		
		this.similarityCalculator = similarityCalculator;
		similarityCalculator.setOntology(ontology);
		similarityCalculator.setICValues(ic);
		similarityCalculator.setCalculatedSim(calculatedSim);
	}
	
	public abstract LinkedList<String[]> runPhenomizer();
	
	/**
	 * set the information content for all symptoms
	 */
	protected void setIC(){
		for(int symp : sda.getSymptoms()){
			if(!ic.containsKey(symp)){
				double icS = calculateIC(symp);
				ic.put(symp,icS);
			}
		}
	}
	
	/**
	 * calculates the information content of a given term (symptom)
	 * @param term
	 * @return information content
	 */
	private double calculateIC(int term){

		double ic = 0;
		double freq = (double)sda.numberOfDiseases(term)/sda.numberOfDiseases();
		ic = -Math.log(freq);

		return ic;
	}

}
