package phenomizer.validation;

import java.util.HashMap;
import java.util.LinkedList;

import io.FileOutputWriter;
import phenomizer.algorithm.DataTransformer;
import phenomizer.algorithm.FrequencyConverter;
import phenomizer.algorithm.Ontology;
import phenomizer.algorithm.SimilarityCalculatorNoWeight;
import phenomizer.algorithm.SimilarityCalculatorOneSidedWeight;
import phenomizer.algorithm.SimilarityCalculatorTwoSidedWeight;
import phenomizer.algorithm.SymptomDiseaseAssociations;


public abstract class PhenomizerWithFrequentSymptoms extends PhenomizerValidation{
	
	/**
	 * constructor of abstract class PhenomizerWithFrequentSymptoms
	 * @param weighting integer indicating if weighted similarity scores are calculated
	 * 		0: unweighted	1: one-sided weighting	2: double-sided weighting
	 * @param onto matrix of PhenoDis symptom ids representing a is-a hierarchy
	 * @param symptoms list of all PhenoDis symptom ids
	 * @param ksz Mapping between PhenoDis disease ids and associated symptoms (list of integer arrays, containing
	 * 			PhenoDis symptom id and a frequency annotation)
	 * @param file file to which the resulting ranks are written
	 */
	public PhenomizerWithFrequentSymptoms(int weighting, int [][] onto,
			LinkedList<Integer> symptoms, HashMap<Integer, LinkedList<Integer[]>> ksz,
			String file){
		
		super(weighting, onto, symptoms, ksz, file);
	}
	
	/**
	 * method to prepare all data structures used for the validation
	 * the method is used to generate the queries with very frequent symptoms (weight >=15) from the associations
	 * between diseases and symptoms
	 * the method has to be called before running the validation!
	 */
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
				//get only very frequent symptoms -> weight 15!!!!
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
	
	protected void outputEmptyQuery(FileOutputWriter fow, int cur_position){
		
		System.out.println(query_ids[cur_position]+"\t\t"+"no frequent symptoms");
		fow.writeFileln(query_ids[cur_position]+"\t\t"+"no frequent symptoms");
	}
	
	protected void outputRankedQuery(FileOutputWriter fow, int cur_position, double rank){
		
		LinkedList<Integer> cur_query= queries[cur_position];
		if(cur_query.size()==sda.getSymptoms(query_ids[cur_position]).size()){
			System.out.println(query_ids[cur_position]+"\t"+rank+"\t"+"all symptoms frequent");
			fow.writeFileln(query_ids[cur_position]+"\t"+rank+"\t"+"all symptoms frequent");
		}
		else{
			System.out.println(query_ids[cur_position]+"\t"+rank+"\t");
			fow.writeFileln(query_ids[cur_position]+"\t"+rank+"\t");
		}
	}
	
	

}
