package togeno;

import java.util.LinkedList;

/** gene object with annotations and intermediate scores from several diseases/ metabolites*/
public abstract class AnnotatedGene extends Gene {

	/** saves the current maximum score */
	private double currentMax;
	/** saves list of at most 3 disease/metabolite ids */ 
	private LinkedList<String> currentMaxIds;
	/** indicates if currentMax is a truncated list, moreMax = true -> there are more than 3 ids with max score*/
	private boolean moreMax;
	
	
	/**
	 * generates a gene object that can be annotated with results from Phenomizer/MetaboliteScore
	 * @param id id of the gene (e.g. ensembl id)
	 */
	public AnnotatedGene(String id) {
		super(id);
		currentMax =-1;
		currentMaxIds = new LinkedList<String>();
		moreMax=false;
	}
	
	/**
	 * adds a result from Phenomizer/Metabolite Score to the gene
	 * @param dis_or_met_id PhenoDis disease id or metabolite id
	 * @param score gene score resulting from Phenomizer prediction for the disease with id dis_or_met_id or resulting
	 * 		from MetaboliteScore for the metabolite with id dis_or_met_id
	 */
	public void add(String dis_or_met_id, double score) {
		//new score is equal to max
		if(Math.abs(currentMax-score)<1E-5){
			//do not save more than 3 ids with max contribution
			if(currentMaxIds.size()<3){
				currentMaxIds.add(dis_or_met_id);
			}
			else{
				moreMax=true;
			}
		} else if(score>currentMax){
			currentMax=score;
			currentMaxIds=new LinkedList<String>();
			currentMaxIds.add(dis_or_met_id);
			moreMax=false;
		}
	}
	
	/**
	 * retrieves at most 3 disease/metabolite ids with maximum score annotated to this gene
	 * -> important disease/metabolite annotation
	 * @return array of disease_ids (PhenoDis) with maximum score
	 */
	public String [] getContributorIds(){
		String [] ids_return = new String [currentMaxIds.size()];
		int position=0;
		for(String disease_id:currentMaxIds){
			ids_return[position] = disease_id;
			position++;
		}
		return ids_return;
	}
	
	/**
	 * retrieves the current score of this gene, the score indicates the probability that the gene is causal for 
	 * the given metabotype
	 * @return current score of this gene
	 */
	public abstract double getFinalScore();
	
	/**
	 * indicates if there are more disease/metabolite ids leading to the maximum score than saved in this object
	 * @return true if the array of getContributorIds() does not contain all disease with maximum score
	 */
	public boolean moreMaxThanListed(){
		return moreMax;
	}
	
	/**
	 * auxiliary method to retrieve the current maximum score of all diseases/metabolites that have been annotated to this 
	 * object so far
	 * @return current maximum score
	 */
	protected double getCurrentMax(){
		return currentMax;
	}
	
	/**
	 * clears all disease/metabolite scores that were annotated so far to the gene
	 * method allows reuse of GeneAssociations for several runs of PhenoToGeno or MetaboToGeno
	 */
	public void resetAnnotation(){
		currentMax =-1;
		currentMaxIds = new LinkedList<String>();
		moreMax=false;
	}
	
}
