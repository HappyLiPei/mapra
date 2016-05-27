package phenotogeno.validation;

import java.util.LinkedList;

import phenotogeno.algo.ScoredDisease;

/** Phenomizer filter that keeps only the diseases with the best (lowest) pvalue*/
public class PhenomizerFilterTopPvalue extends PhenomizerFilter {

	@Override
	/**
	 * Phenomizer result is not limited, result contains all diseases of the database used by Phenomizer
	 */
	public int getResultSize() {
		return totalNumberOfDiseases;
	}

	@Override
	/**
	 * the method returns all diseases with the best (lowest) pvalue
	 */
	public LinkedList<ScoredDisease> filter(LinkedList<ScoredDisease> phenomizerUnfiltered) {
		
		//find best pvalue
		double min = Double.MAX_VALUE;
		for(ScoredDisease d: phenomizerUnfiltered){
			if(d.getPval()<min){
				min = d.getPval();
			}
		}
		
		//collect all diseases with best pvalue
		LinkedList<ScoredDisease> phenomizerFiltered = new LinkedList<ScoredDisease>();
		for(ScoredDisease d: phenomizerUnfiltered){
			if(Math.abs(min-d.getPval())<1E-10){
				phenomizerFiltered.add(d);
			}
		}
		
		return phenomizerFiltered;
	}
	

}
