package phenotogeno.algo;

import java.util.Comparator;

public class ScoredGeneComparator implements Comparator<ScoredGene>{

	@Override
	/**
	 * method to compare 2 scored Genes
	 * the genes are first compared to their score (rounded to 5 decimal places) and than according to their id
	 * @param o1: first ScoredGene
	 * @param o2: second ScoredGene
	 * @return: positive value if o1 has a higher rank than o2
	 * 			negative value if o1 has a lower rank than o2
	 * 			0 if the genes are identical (should never happen)
	 */
	public int compare(ScoredGene o1, ScoredGene o2) {
		
		int score_o1= (int) Math.round(o1.getScore()*100000);
		int score_o2 = (int) Math.round(o2.getScore()*100000);
		
		//sort in descending order according to score
		int compareScores =Integer.compare(score_o1, score_o2);
		if(compareScores!=0){
			return -compareScores;
		}
		// scores are identical -> in lexicographic order
		else{
			return o1.getId().compareTo(o2.getId());
		}
	}

}
