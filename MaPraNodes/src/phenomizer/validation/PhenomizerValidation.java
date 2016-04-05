package phenomizer.validation;

import java.util.HashMap;
import java.util.LinkedList;

import io.FileOutputWriter;
import phenomizer.algorithm.ComparatorPheno;
import phenomizer.algorithm.Ontology;
import phenomizer.algorithm.PhenomizerAlgorithm;
import phenomizer.algorithm.SimilarityCalculator;
import phenomizer.algorithm.SymptomDiseaseAssociations;

public abstract class PhenomizerValidation {
	
	/** option for weighting: 0-> no weighting, 1-> one-sided weighting, 2-> 2-sided weighting*/
	protected int weighting;
	/** symptom ontoloyg that is read from file*/
	protected int [][] ontology_raw;
	/** list of symptoms from file (may contain duplicates) */
	protected LinkedList<Integer> symptoms_raw;
	/** associations between diseases and symptoms, read from file*/
	protected HashMap<Integer, LinkedList<Integer[]>> ksz_raw;
	
	/** file to write output to*/
	protected String output_path;
	
	/** ontology object used by the PhenomizerAlgorithm, generated from ontology_raw*/
	protected Ontology ontology;
	/** associations between diseases and symptoms as object, this is used by the PhenomizerAlgorithm*/
	protected SymptomDiseaseAssociations sda;
	/** similarity calculator for calculation of similarity scores*/
	protected SimilarityCalculator sc;
	/** comparator for sorting and comparing the diseases of one Phenomizer prediction*/
	protected ComparatorPheno comparator;
	
	/** array of all queries to run*/
	protected LinkedList<Integer>[] queries;
	/** array of the id of the correct disease*/
	protected int [] query_ids;
	
	/**
	 * constructor of abstract class PhenomizerValidation
	 * @param weighting integer indicating if weighted similarity scores are calculated
	 * 		0: unweighted	1: one-sided weighting	2: double-sided weighting
	 * @param onto matrix of PhenoDis symptom ids representing a is-a hierarchy
	 * @param symptoms list of all PhenoDis symptom ids
	 * @param ksz Mapping between PhenoDis disease ids and associated symptoms (list of integer arrays, containing
	 * 			PhenoDis symptom id and a frequency annotation)
	 * @param file file to which the resulting ranks are written
	 */
	public PhenomizerValidation(int weighting, int [][] onto,
			LinkedList<Integer> symptoms, HashMap<Integer, LinkedList<Integer[]>> ksz,
			String file){
		
		this.weighting=weighting;
		this.ontology_raw=onto;
		this.symptoms_raw=symptoms;
		this.ksz_raw= ksz;
		this.output_path = file;
	}
	
	/**
	 * method to prepare all data structures used for the validation
	 * the method has to be called before running the validation!
	 */
	public abstract void prepareData();
	
	/**
	 * method to generate a PhenomizerAlgorithm (with or without p values)
	 * @param query query symptoms as a list of PhenoDis symptom ids without ancestors
	 * @param ic Mapping of symptom id to information content (reusable Hashmap to save computing time)
	 * @param sim Mapping of pairs of symptom id to similarity score (reusable Hashmap to save computing time)
	 * @return a PhenomizerAlgorithm ready for execution
	 */
	protected abstract PhenomizerAlgorithm initPhenomizer(LinkedList<Integer> query, HashMap<Integer, Double> ic,
			HashMap<String, Double> sim);
	
	
	/**
	 * makes output for the current query if it contains no symptoms -> no rank calculated
	 * @param fow
	 * 			file handle to the output file
	 * @param cur_position
	 * 			position in the query arrays of the current query
	 */
	protected abstract void outputEmptyQuery(FileOutputWriter fow, int cur_position);
	
	
	/**
	 * makes output for the current query if ia rank was calculated for it
	 * @param fow
	 * 			file handle to the output file
	 * @param cur_position
	 * 			position in the query arrays of the current query
	 * @param rank
	 * 			rank of the disease corresponding to the query
	 */
	protected abstract void outputRankedQuery(FileOutputWriter fow, int cur_position, double rank);
	
	
	/**
	 * method to start the validation
	 * outputs the rank of the correct disease for all specified queries
	 * note that one has to call prepareData() before running this method!
	 */
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
				outputEmptyQuery(fow, pos);
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
				outputRankedQuery(fow, pos, rank);
			}
			
		}
		fow.closew();
	}
	
	/**
	 * method to calculuate the rank of a given disease in a result of Phenomizer
	 * @param disease PhenoDis disease id of the disease of interest
	 * @param pheno_result list of scored diseases returned by PhenomizerAlgorithm
	 * @return the rank of the disease (according to pvalue and/or score), if there are diseases with the same score
	 * 		each disease obtains their average rank
	 */
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

}
