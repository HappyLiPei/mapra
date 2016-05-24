package phenotogeno.validation;

import java.util.LinkedList;

import phenotogeno.algo.ScoredDisease;

/** PhenomizerFilter that only passes disease significant pvalues (p<=0.05) from Phenomizer to PhenoToGeno */
public class PhenomizerFilterSignificant extends PhenomizerFilter {

	@Override
	/**
	 * result size of Phenomizer is not limited
	 */
	public int getResultSize() {
		return totalNumberOfDiseases;
	}

	@Override
	/**
	 * the result of Phenomizer is filtered according to significant pvalues (p<=0.05)
	 */
	public LinkedList<ScoredDisease> filter(LinkedList<ScoredDisease> phenomizerUnfiltered) {
		
		LinkedList<ScoredDisease> phenomizerFiltered = new LinkedList<ScoredDisease>();
		for(ScoredDisease disease: phenomizerUnfiltered){
			if(disease.getPval()<=0.05){
				phenomizerFiltered.add(disease);
			}
		}
		return phenomizerFiltered;
	}
	
}
