package phenotogeno.validation;

import java.util.LinkedList;

import togeno.ScoredDiseaseOrMetabolite;

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
	public LinkedList<ScoredDiseaseOrMetabolite> filter(LinkedList<ScoredDiseaseOrMetabolite> phenomizerUnfiltered) {
		
		//find best pvalue
		double min = Double.MAX_VALUE;
		for(ScoredDiseaseOrMetabolite d: phenomizerUnfiltered){
			if(d.getPval()<min){
				min = d.getPval();
			}
		}
		
		//collect all diseases with best pvalue
		LinkedList<ScoredDiseaseOrMetabolite> phenomizerFiltered = new LinkedList<ScoredDiseaseOrMetabolite>();
		for(ScoredDiseaseOrMetabolite d: phenomizerUnfiltered){
			if(Math.abs(min-d.getPval())<1E-10){
				phenomizerFiltered.add(d);
			}
		}
		
		return phenomizerFiltered;
	}
	

}
