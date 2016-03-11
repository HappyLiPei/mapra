package phenotogeno.algo;

import java.util.LinkedList;

public class AnnotatedGene extends Gene {
	
	private LinkedList<Integer> disease_ids;
	private LinkedList<Double> scores;
	
	/**
	 * generates a gene object that can be annotated with results from Phenomizer
	 * @param id: id of the gene (e.g. ensembl id)
	 */
	public AnnotatedGene(String id) {
		super(id);
		disease_ids = new LinkedList<Integer>();
		scores = new LinkedList<Double>();
	}
	
	/**
	 * adds a result from Phenomizer to the gene
	 * @param disease_id: PhenoDis disease id
	 * @param score: gene score resulting from Phenomizer prediction for the disease with id disease_id
	 */
	public void add(int disease_id, double score) {
		disease_ids.add(disease_id);
		scores.add(score);
	}
	
	/**
	 * retrieves all diseases added to the gene
	 * should be called together with getScores
	 * @return: array of all disease_ids (PhenoDis)
	 * position in array returned by getScores corresponds to the position within this array
	 */
	public int [] getDiseaseIds(){
		int [] ids_return = new int [disease_ids.size()];
		int position=0;
		for(int disease_id:disease_ids){
			ids_return[position] = disease_id;
			position++;
		}
		return ids_return;
	}
	
	/**
	 * retrieves all scores added to the gene
	 * should be called together with getDiseaseIds
	 * @return: array of all scores
	 * position in array returned by getDiseaseIds corresponds to the position within this array
	 */
	public double [] getScores(){
		double [] scores_return = new double[scores.size()];
		int position=0;
		for(double s:scores){
			scores_return[position]=s;
			position++;
		}
		return scores_return;
	}
	
}
