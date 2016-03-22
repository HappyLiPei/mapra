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
		
		double score1 = o1.getScore();
		double score2 = o2.getScore();
		
		//if scores differ in less than 5 decimal places -> consider them as equal
		if(Math.abs(score1-score2)<1E-5){
			//compare ids
			return o1.getId().compareTo(o2.getId());
		}
		//else -> compare scores
		return -Double.compare(score1, score2);
	}

}
