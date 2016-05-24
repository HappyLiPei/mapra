package phenotogeno.validation;

import java.util.LinkedList;

import phenotogeno.algo.ScoredDisease;

/** PhenomizerFilter that does not filter the Phenomizer result at all*/
public class PhenomizerFilterAllDiseases extends PhenomizerFilter {

	@Override
	/**
	 * Phenomizer result is not limited, result contains all diseases of the database used by Phenomizer
	 */
	public int getResultSize() {
		return totalNumberOfDiseases;
	}

	@Override
	/**
	 * the result of Phenomizer is not filtered
	 */
	public LinkedList<ScoredDisease> filter(LinkedList<ScoredDisease> phenomizerUnfiltered) {
		return phenomizerUnfiltered;
	}

}
