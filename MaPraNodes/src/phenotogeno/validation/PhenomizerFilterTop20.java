package phenotogeno.validation;

import java.util.LinkedList;

import togeno.ScoredDiseaseOrMetabolite;

/** PhenomizerFilter that passes only the top 20 diseases reported by Phenomizer to PhenoToGeno*/
public class PhenomizerFilterTop20 extends PhenomizerFilter{

	@Override
	/**
	 * result size is limited to 20, but the actual result might be larger because of ties at rank 20
	 */
	public int getResultSize() {
		return 20;
	}

	@Override
	/**
	 * does not filter the results returned by Phenomizer
	 */
	public LinkedList<ScoredDiseaseOrMetabolite> filter(LinkedList<ScoredDiseaseOrMetabolite> phenomizerUnfiltered) {
		return phenomizerUnfiltered;
	}
	
}
