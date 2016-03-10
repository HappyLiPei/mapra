package phenomizer.validation;

import java.util.HashMap;
import java.util.LinkedList;

import phenomizer.algorithm.DataTransformer;
import phenomizer.algorithm.FrequencyConverter;
import phenomizer.algorithm.Ontology;
import phenomizer.algorithm.SimilarityCalculator;
import phenomizer.algorithm.SimilarityCalculatorNoWeight;
import phenomizer.algorithm.SimilarityCalculatorOneSidedWeight;
import phenomizer.algorithm.SimilarityCalculatorTwoSidedWeight;
import phenomizer.algorithm.SymptomDiseaseAssociations;


public abstract class PhenomizerWithFrequentSymptoms {
	
	private int weighting;
	private int [][] ontology_raw;
	private LinkedList<Integer> symptoms_raw;
	//always ksz WITH frequency information
	private HashMap<Integer, LinkedList<Integer[]>> ksz_raw;
	
	private String output_path;
	
	private Ontology ontology;
	private SymptomDiseaseAssociations sda;
	private SimilarityCalculator sc;
	private LinkedList<Integer>[] queries;
	
	
	public PhenomizerWithFrequentSymptoms(int weighting, int [][] onto,
			LinkedList<Integer> symptoms, HashMap<Integer, LinkedList<Integer[]>> ksz,
			String file){
		
		this.weighting=weighting;
		this.ontology_raw=onto;
		this.symptoms_raw=symptoms;
		this.ksz_raw= ksz;
		this.output_path = file;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public void prepareData(){
		
		//generate ontology
		ontology = new Ontology(ontology_raw);
		
		//choose method to calculate similarities
		if(weighting==0){
			sc = new SimilarityCalculatorNoWeight();
		}
		else if(weighting==1){
			sc = new SimilarityCalculatorOneSidedWeight();
		}
		else if(weighting==2){
			sc = new SimilarityCalculatorTwoSidedWeight();
		}
		
		//generate queries
		DataTransformer t = new DataTransformer();
		SymptomDiseaseAssociations temp = t.generateSymptomDiseaseAssociation(ontology, symptoms_raw, ksz_raw);
		queries=new LinkedList[temp.numberOfDiseases()];
		int pos =0;
		for(int disease:temp.getDiseases()){
			LinkedList<Integer> query = new LinkedList<Integer>();
			LinkedList<Integer[]> symp = temp.getSymptoms(disease);
			//anno[0]: disease id	anno[1] weight
			for(Integer[] anno:symp){
				if(anno[1]>=10){
					query.add(new Integer(anno[0]));
				}
			}
			queries[pos]=query;
			pos++;
		}
		
		//set final sda object
		// weighted -> take annotated weights
		if(weighting!=0){
			this.sda=temp;
		}
		// unweighted -> remove weights -> overwrite ksz_raw
		else{
			for(int key:ksz_raw.keySet()){
				LinkedList<Integer[]> list = ksz_raw.get(key);
				for(Integer [] array: list){
					array[1]=FrequencyConverter.NO_WEIGHT;					
				}
			}
			this.sda = t.generateSymptomDiseaseAssociation(ontology, symptoms_raw, ksz_raw);
		}
	}
	


	protected abstract void intiPhenomizer(LinkedList<Integer> query);
	
	//TODO:perhaps with Comparator instead of abstract method!
	protected abstract double calculateRank(int disease, LinkedList<String[]> result);
	
	//TODO: implement!
	public void runValidation(){
		
		/*  global hash map with pairwise similarities
		 *  iterate over all queries
		 *  	check if query is empty! -> skip if empty
		 *  	generate Phenomizer object -> init method
		 *  	overwrite calculatedSim of SimilarityCalclator -> reuse existing similarities
		 * 		run Phenomizer
		 * 		get rank of the disease -> calculateRank method
		 * 		write disease and rank to file
		 */
	}
	
	

}
