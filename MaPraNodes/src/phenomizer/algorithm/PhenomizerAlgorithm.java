package phenomizer.algorithm;

import java.util.HashMap;
import java.util.LinkedList;

public abstract class PhenomizerAlgorithm {
	
	//result size
	protected int num;
	
	//input data
	protected LinkedList<Integer> queryIds;
	protected SymptomDiseaseAssociations sda;
	
	//intermediate results
	private HashMap<Integer,Double> ic;
	private HashMap<String,Double> calculatedSim;
	
	//bridge pattern for calculating weighted similarity scores
	protected SimilarityCalculator similarityCalculator;
	//bridge pattern for comparing results for different diseases
	protected ComparatorPheno comparator;
	
	/**
	 * constructor for generating a PhenomizerAlgorithm
	 * @param num: desired output size (limits the result to the num top scoring diseases)
	 * @param ontology: representing the is-a hierarchy of PhenoDis symptoms
	 * @param queryIds: list of PhenoDis symptom ids representing the query for the algorithm
	 * @param sda: reporesentation of the association between symptoms and disease in PhenoDis
	 * @param similarityCalculator: object to calculate similarities between two symptoms
	 */
	public PhenomizerAlgorithm(int num, Ontology ontology, LinkedList<Integer> queryIds,
			SymptomDiseaseAssociations sda, SimilarityCalculator similarityCalculator){
		
		this.num=num;
		this.queryIds=queryIds;
		this.sda = sda;
		this.ic=new HashMap<Integer,Double>();
		this.calculatedSim=new HashMap<String, Double>();
		
		this.similarityCalculator = similarityCalculator;
		similarityCalculator.setOntology(ontology);
		similarityCalculator.setICValues(ic);
		similarityCalculator.setCalculatedSim(calculatedSim);
	}
	
	/**
	 * constructor for generating a PhenomizerAlgorithm with reusable ic and similarity hashmap
	 * @param num: desired output size (limits the result to the num top scoring diseases)
	 * @param ontology: representing the is-a hierarchy of PhenoDis symptoms
	 * @param queryIds: list of PhenoDis symptom ids representing the query for the algorithm
	 * @param sda: reporesentation of the association between symptoms and disease in PhenoDis
	 * @param similarityCalculator: object to calculate similarities between two symptoms
	 * @param ic: Hashmap storing the information content of all symptoms in Phenodis
	 * @param calculatedSim: Hashmap storing pre-calculated pairwise similarities for pairs of symptoms
	 */
	//constructor which allows to add ic and calculatedSim with initialized size or content -> use for sampling
	public PhenomizerAlgorithm(int num, Ontology ontology, LinkedList<Integer> queryIds,
			SymptomDiseaseAssociations sda, SimilarityCalculator similarityCalculator,
			HashMap<Integer, Double> ic, HashMap<String, Double> calculatedSim){
		
		this.num=num;
		this.queryIds=queryIds;
		this.sda = sda;
		this.ic=ic;
		this.calculatedSim=calculatedSim;
		
		this.similarityCalculator = similarityCalculator;
		similarityCalculator.setOntology(ontology);
		similarityCalculator.setICValues(ic);
		similarityCalculator.setCalculatedSim(calculatedSim);
	}
	
	public abstract LinkedList<String[]> runPhenomizer();
	
	/**
	 * calculates the information content of all symptoms and stores them in an internal HashMap
	 * if the internal HashMap is already filled it does nothing
	 */
	protected void setIC(){
		
		//skip ic calculation if ic HashMap is already filled with values (for repeated Phenomizer in validation)
		if(ic.size()==sda.numberOfSymptoms()){
			return;
		}
		
		//iteratre over all symptom ids
		for(int symptom : sda.getSymptoms()){
			
			//calculate information content of the current symptom
			double symptom_frequency = (double) sda.numberOfDiseases(symptom)/sda.numberOfDiseases();
			double symptom_ic = -Math.log(symptom_frequency);
			
			//add the information content to the internal HashMap
			ic.put(symptom,symptom_ic);
		}
	}
	
	/**
	 * transforms the 2d array into the standard output data structure of PhenomizerAlgorithm and 
	 * reduces the number of diseases in the output to a fixed number
	 * @param scores_pvals: 2d array 
	 * 	each row corresponds to a disease in PhenoDis
	 * 	columns: array[][0] disease id, array[][1] similariy score (PhenomizerAlgorithmNoPval)
	 * 	columns: array[][0] disease id, array[][1] similariy score, array[][2] pvalue (PhenomizerAlgorithmWithPval)
	 * @return: a list of String arrays with a fixed length
	 * 	array[0] disease id, array[1] similariy score (PhenomizerAlgorithmNoPval)
	 *  array[0] disease id, array[1] similariy score, array[2] pvalue (PhenomizerAlgorithmWithPval)
	 */
	protected LinkedList<String[]> generateResult(String [][] scores){
		LinkedList<String[]> res = new LinkedList<String[]>();
		
		//num: output size, cannot be larger than the total number of diseases or scores
		if(num>scores.length){
			num=scores.length;
		}
		
		//generate output of desired size num
		for(int i=0; i<num; i++){
			res.add(scores[i]);
		}
		
		//add additional results if they have the same rank (identical pvalues and/or scores)
		//as the last result (num-th result)
		String [] last_score = scores[num-1];
		int pos=num;
		while(pos < scores.length && comparator.compareWithoutID(scores[pos], last_score)==0){
			res.add(scores[pos]);
			pos++;
		}
		
		return res;
	}

}
