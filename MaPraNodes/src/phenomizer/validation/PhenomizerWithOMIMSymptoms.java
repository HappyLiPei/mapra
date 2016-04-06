package phenomizer.validation;

import java.util.HashMap;
import java.util.LinkedList;

import io.FileOutputWriter;
import phenomizer.algorithm.DataTransformer;
import phenomizer.algorithm.Ontology;
import phenomizer.algorithm.SimilarityCalculatorNoWeight;
import phenomizer.algorithm.SimilarityCalculatorOneSidedWeight;
import phenomizer.algorithm.SimilarityCalculatorTwoSidedWeight;

public abstract class PhenomizerWithOMIMSymptoms extends PhenomizerValidation {
	
	/** maps omim ids to PhenoDis disease ids*/
	protected HashMap<Integer, Integer> omimToPhenoDis;
	/** maps omim ids to lists of PhenoDis symptom ids*/
	protected HashMap<Integer, LinkedList<Integer>> omimToSymptoms;
	
	/**
	 * generates an abstract object for running the validation of Phenomizer with symptoms from OMIM
	 * @param weighting
	 * 		if 0: unweighted similarity score, if 1: one-sided weighting, if 2: two-sided weighting
	 * @param onto
	 * 		ontology of PhenoDis symptom ids as array of edges
	 * @param symptoms
	 * 		list of all PhenoDis symptom ids
	 * @param ksz
	 * 		mapping PhenoDis disease id to a list of PhenoDis symptom id, represents the associations
	 * 		between diseases and symptoms
	 * @param file
	 * 		file to which the results are written
	 * @param omimToPhenoDis
	 * 		mapping OMIM id -> PhenoDis id
	 * @param omimToSymptoms
	 * 		mapping OMIM id -> list of PhenoDis symptom id
	 */
	public PhenomizerWithOMIMSymptoms(int weighting, int[][] onto, LinkedList<Integer> symptoms,
			HashMap<Integer, LinkedList<Integer[]>> ksz, String file,
			HashMap<Integer, Integer> omimToPhenoDis, HashMap<Integer, LinkedList<Integer>> omimToSymptoms) {
		
		super(weighting, onto, symptoms, ksz, file);
		this.omimToPhenoDis = omimToPhenoDis;
		this.omimToSymptoms = omimToSymptoms;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void prepareData() {
		
		//generate ontology and symptom disease association
		ontology = new Ontology(ontology_raw);
		DataTransformer t = new DataTransformer();
		sda = t.generateSymptomDiseaseAssociation(ontology, symptoms_raw, ksz_raw);
				
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
		
		//queries: result from text mining + unique mapping to PhenoDis disease id
		int numberOfQueries =0;
		//determine number of queries
		for(int omimId: omimToSymptoms.keySet()){
			if(omimToPhenoDis.containsKey(omimId)){
				numberOfQueries++;
			}
		}
		//extract queries
		queries = new LinkedList[numberOfQueries];
		query_ids = new int [numberOfQueries];
		int position =0;
		for(int omimId: omimToSymptoms.keySet()){
			if(omimToPhenoDis.containsKey(omimId)){
				//remove ancestors from the query
				queries[position] = t.prepareQuery(ontology, omimToSymptoms.get(omimId));
				query_ids[position] = omimToPhenoDis.get(omimId);
				position++;
			}
		}		
	}
	
	protected void outputEmptyQuery(FileOutputWriter fow, int cur_position){
		
		System.out.println(query_ids[cur_position]+"\t\t"+"no symptoms found in omim");
		fow.writeFileln(query_ids[cur_position]+"\t\t"+"no symptoms found in omim");
	}
	
	protected void outputRankedQuery(FileOutputWriter fow, int cur_position, double rank){
		System.out.println(query_ids[cur_position]+"\t"+rank+"\t");
		fow.writeFileln(query_ids[cur_position]+"\t"+rank+"\t");
	}

}
