package phenotogeno.algo;

import java.util.Comparator;

public class ScoredGeneComparator implements Comparator<ScoredGene>{

	@Override
	/**
	 * method to compare 2 ScoredGene objects
	 * the genes are compared according to their score (considering 5 decimal places, higher scores are better)
	 * and according to their id (if scores are equal)
	 * @param o1 first ScoredGene
	 * @param o2 second ScoredGene
	 * @return positive value if o1 has a higher rank than o2,
	 * 			negative value if o1 has a lower rank than o2,
	 * 			0 if the genes are identical (should never happen)
	 */
	public int compare(ScoredGene o1, ScoredGene o2) {
		
		//consider 5 decimal places of the socres
		int score1 = (int) Math.round(o1.getScore()*1E5);
		int score2 = (int) Math.round(o2.getScore()*1E5);
		
		//if scores differ in less than 5 decimal places -> consider them as equal
		if(score1==score2){
		//if(Math.abs(score1-score2)<1E-5){
			//compare ids
			return o1.getId().compareTo(o2.getId());
		}
		//else -> compare scores
		return -Integer.compare(score1, score2);
	}

}
