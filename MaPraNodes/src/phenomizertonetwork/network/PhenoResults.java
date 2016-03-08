package phenomizertonetwork.network;

import java.util.HashMap;

public class PhenoResults {
	
	//maps PhenoDis id to score (and aptional p value) obtained by Phenomizer
	private HashMap<Integer, String[]> idtoscore;
	//indicates if results contain p values
	//hasPval = true -> String [] of length 2, hasPval = false -> String [] of length 1
	boolean hasPval;
	
	/**
	 * constructor to generate an empty result
	 * @param size: expected number of diseases from Phenomizer results
	 * @param pval: if results contain p values
	 */
	private PhenoResults(int size, boolean pval){
		idtoscore = new HashMap<Integer, String[]>(size*3);
		hasPval=pval;
	}
	
	/**
	 * constructor to generate PhenoResults from the values read from a BufferedDataTable (Phenomizer with p values)
	 * @param ids: all PhenoDis ids (column disease_id)
	 * @param scores: all Phenomizer scores (column score)
	 * @param pvalues: alle Phenomizer p values (column p value)
	 */
	public PhenoResults(int [] ids, double [] scores, double [] pvalues){
		this(ids.length, true);
		for(int i=0; i<ids.length; i++){
			idtoscore.put(ids[i], new String[]{String.valueOf(scores[i]),String.valueOf(pvalues[i])});
		}
	}
	
	/**
	 * constructor to generate PhenoResults from the values read from a BufferedDataTable (Phenomizer without p values)
	 * @param ids: all PhenoDis ids (column disease_id)
	 * @param scores: all Phenomizer scores (column score)
	 */
	public PhenoResults(int [] ids, double [] scores){
		this(ids.length, false);
		for(int i=0; i<ids.length; i++){
			idtoscore.put(ids[i], new String[]{String.valueOf(scores[i])});
		}
	}
	
	/**
	 * returns the Phenomizer score (and p value) for a given PhenoDis id
	 * @param id: PhenoDis id 
	 * @return: position 0 of array: score
	 * 			position 1 of array: p value (only if hasPvalue = true)
	 * 			if no results are available, null is returned
	 */
	public String[] getResFor(String id){
		return getResFor(Integer.valueOf(id));
	}
	
	/**
	 * returns the Phenomizer score (and p value) for a given PhenoDis id
	 * @param id: PhenoDis id 
	 * @return: position 0 of array: score
	 * 			position 1 of array: p value (only if hasPvalue = true)
	 * 			if no results are available, null is returned
	 */
	public String[] getResFor(int id){
		if(idtoscore.containsKey(id)){
			return idtoscore.get(id);
		}
		else{
			return null;
		}
	}
	
	/**
	 * retrieves all PhenoDis ids stored in this PhenoResults object
	 * @return: array of all PhenoDis ids stored in this data structure
	 */
	public Integer [] getAllIds(){
		Integer [] res = new Integer[idtoscore.keySet().size()];
		return idtoscore.keySet().toArray(res);
	}
	

}
