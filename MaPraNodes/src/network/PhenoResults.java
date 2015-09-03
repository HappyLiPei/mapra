package network;

import java.util.HashMap;

public class PhenoResults {
	
	private HashMap<Integer, String[]> idtoscore;
	boolean hasPval;
	
	private PhenoResults(int size, boolean pval){
		idtoscore = new HashMap<Integer, String[]>(size*3);
		hasPval=pval;
	}
	
	public PhenoResults(int [] ids, double [] scores, double [] pvalues){
		this(ids.length, true);
		for(int i=0; i<ids.length; i++){
			idtoscore.put(ids[i], new String[]{String.valueOf(scores[i]),String.valueOf(pvalues[i])});
		}
	}
	
	public PhenoResults(int [] ids, double [] scores){
		this(ids.length, false);
		for(int i=0; i<ids.length; i++){
			idtoscore.put(ids[i], new String[]{String.valueOf(scores[i])});
		}
	}
	
	public String[] getResFor(String id){
		return getResFor(Integer.valueOf(id));
	}
	
	public String[] getResFor(int id){
		if(idtoscore.containsKey(id)){
			return idtoscore.get(id);
		}
		else{
			return null;
		}
	}
	
	public Integer [] getAllIds(){
		Integer [] res = new Integer[idtoscore.keySet().size()];
		return idtoscore.keySet().toArray(res);
	}
	

}
