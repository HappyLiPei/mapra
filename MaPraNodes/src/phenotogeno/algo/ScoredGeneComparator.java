package phenotogeno.algo;

import java.util.Comparator;

public class ScoredGeneComparator implements Comparator<ScoredGene>{
	
	/** number of decimal places to consider for equality -> assume data is rounded to that number of decimal places*/
	private int decimalplaces;
	
	/**
	 * generates a comparator for scored genes considering 5 decimal places of the score
	 */
	public ScoredGeneComparator() {
		decimalplaces=5;
	}
	
	/**
	 * generates a comparator for scored genes considering a defined number decimal places of the score
	 * @param decimalplaces decimal places of the score to consider
	 */
	public ScoredGeneComparator(int decimalplaces){
		this.decimalplaces = decimalplaces;
	}
	

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
		
		//consider 5 decimal places of the scores
		long score1 = (long) Math.round(o1.getScore()*Math.pow(10,decimalplaces));
		long score2 = (long) Math.round(o2.getScore()*Math.pow(10,decimalplaces));
		
		//if scores differ in less than 5 decimal places -> consider them as equal
		if(score1==score2){
			//compare ids
			return o1.getId().compareTo(o2.getId());
		}
		//else -> compare scores
		return -Long.compare(score1, score2);
	}
	
	/**
	 * method to compare 2 ScoredGene objects
	 * the genes are compared according to their score (considering 5 decimal places, higher scores are better)
	 * @param g1 first ScoredGene
	 * @param g2 second ScoredGene
	 * @return positive value if g1 has a higher rank (lower score) than o2,
	 * 			negative value if g1 has a lower rank (higher score) than o2,
	 * 			0 if the genes have the same rank (scores identical)
	 */
	public int compareWithoutID(ScoredGene g1, ScoredGene g2){
		
		//consider 5 decimal places of the socres
		long score1 = (long) Math.round(g1.getScore()*Math.pow(10,decimalplaces));
		long score2 = (long) Math.round(g2.getScore()*Math.pow(10,decimalplaces));
		
		//if scores differ in less than 5 decimal places -> consider them as equal
		return -Long.compare(score1, score2);
		
	}

}
