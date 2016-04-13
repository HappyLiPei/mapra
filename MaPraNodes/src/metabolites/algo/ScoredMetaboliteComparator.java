package metabolites.algo;

import java.util.Comparator;

import metabolites.types.ScoredMetabolite;

public class ScoredMetaboliteComparator implements Comparator<ScoredMetabolite> {

	@Override
	/**
	 * compares the ScoredMetabolites according to their probability (5 decimal places, lower is better), 
	 * absolute score (2 decimal places, higher is better) and id
	 */
	public int compare(ScoredMetabolite sm0, ScoredMetabolite sm1) {
		
		//consider 5 decimal places of probabilities
		int sig0 = (int) Math.round(sm0.getProbability()*100000);
		int sig1 = (int) Math.round(sm1.getProbability()*100000);
		
		if(sig1!=sig0){
			return Integer.compare(sig0, sig1);
		}
		//equal probabilities
		else{
			//consider 2 decimal places of scores, use absolute values of z scores
			int score0 = (int) Math.abs(Math.round(sm0.getScore()*100));
			int score1 = (int) Math.abs(Math.round(sm1.getScore()*100));
			
			if(score0!=score1){
				return -Integer.compare(score0, score1);
			}
			//equal scores
			else{
				//compare ids
				return sm0.getId().compareTo(sm1.getId());
			}
		}
		

	}
	

}
