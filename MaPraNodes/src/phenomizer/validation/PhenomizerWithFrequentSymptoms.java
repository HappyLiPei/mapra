package phenomizer.validation;

import java.util.HashMap;
import java.util.LinkedList;

import io.FileOutputWriter;
import phenomizer.algorithm.ComparatorPheno;
import phenomizer.algorithm.DataTransformer;
import phenomizer.algorithm.FrequencyConverter;
import phenomizer.algorithm.Ontology;
import phenomizer.algorithm.PhenomizerAlgorithm;
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
	
	protected Ontology ontology;
	protected SymptomDiseaseAssociations sda;
	protected SimilarityCalculator sc;
	protected ComparatorPheno comparator;
	
	//data structures to save queries and corresponding disease ids
	private LinkedList<Integer>[] queries;
	private int [] query_ids;
	
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
		query_ids = new int [temp.numberOfDiseases()];
		
		int pos =0;
		for(int disease:temp.getDiseases()){
			LinkedList<Integer> query = new LinkedList<Integer>();
			LinkedList<Integer[]> symp = temp.getSymptoms(disease);
			//anno[0]: disease id	anno[1] weight
			for(Integer[] anno:symp){
				if(anno[1]>=15){
					query.add(new Integer(anno[0]));
				}
			}
			queries[pos]=query;
			query_ids[pos]=disease;
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

	protected abstract PhenomizerAlgorithm initPhenomizer(LinkedList<Integer> query,
			HashMap<Integer, Double> ic, HashMap<String, Double> sim);
	
	protected double calculateRank(int disease, LinkedList<String[]> pheno_result){
		
		//saves result for previous disease
		String[] prev = new String[]{"-1","-1","-1"};
		//counts how many diseases have the same score and/or p value than the current disease
		int count = 1;
		//indicates the current position in the list of Phenomizer scores
		int rank =0;
		//flag to show if the desired disease is found
		boolean found=false;

		//score[0]: disease_id, score[1]: score, (score[2]:pvalue)
		for(String [] score: pheno_result){
			//check if current score equals that of the previous disease
			if(comparator.compareWithoutID(score, prev)!=0){
				//leave loop if last disease with the same score and/ or p value than the desired disease is passed
				if(found){
					break;
				}
				count=1;
			}
			else{
				count++;
			}
			//check if current result corresponds to disease of interest
			if(score[0].equals(Integer.toString(disease))){
				found=true;
			}
			//go to the next disease
			rank++;
			prev=score;
		}
		
		//disease is not listed in the results -> unable to calculate a rank
		if(!found){
			return 0;
		}
		//disease found -> calculate average rank of last count results (diseases with identical score)
		int sum=0;
		for(int i=0; i<count; i++){
			sum+=rank-i;
		}
		return (double) sum/count;
		
	}
	
	public void runValidation(){
		
		FileOutputWriter fow = new FileOutputWriter(output_path);
		
		//reuse ic values and similarity scores
		HashMap<Integer, Double> ic = new HashMap<Integer, Double> (sda.numberOfSymptoms()*3);
		HashMap<String, Double> allSimilarities = new HashMap<String, Double>
			(sda.numberOfSymptoms()*sda.numberOfSymptoms());
		
		for(int pos=0; pos<query_ids.length; pos++){
			System.out.println((pos+1)+" out of "+query_ids.length+" queries");
			//skip queries without any symptoms
			if(queries[pos].size()==0){
				System.out.println(query_ids[pos]+"\t\t"+"no frequent symptoms");
				fow.writeFileln(query_ids[pos]+"\t\t"+"no frequent symptoms");
			}
			else{
				//get current query and disease
				LinkedList<Integer> cur_query = queries[pos];
				int cur_disease=query_ids[pos];
				//run Phenomizer
				PhenomizerAlgorithm algo = initPhenomizer(cur_query, ic, allSimilarities);
				LinkedList<String[]> predictions = algo.runPhenomizer();
				//calculate and write rank
				double rank = calculateRank(cur_disease, predictions);
				if(cur_query.size()==sda.getSymptoms(cur_disease).size()){
					System.out.println(query_ids[pos]+"\t"+rank+"\t"+"all symptoms frequent");
					fow.writeFileln(query_ids[pos]+"\t"+rank+"\t"+"all symptoms frequent");
				}
				else{
					System.out.println(query_ids[pos]+"\t"+rank+"\t");
					fow.writeFileln(query_ids[pos]+"\t"+rank+"\t");
				}
			}
			
		}
		fow.closew();
	}
	
	

}
