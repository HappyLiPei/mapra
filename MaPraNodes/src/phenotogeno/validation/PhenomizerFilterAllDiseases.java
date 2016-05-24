package phenotogeno.validation;

import java.util.LinkedList;

import phenotogeno.algo.ScoredDisease;

public class PhenomizerFilterAllDiseases extends PhenomizerFilter {

	@Override
	public int getResultSize() {
		return totalNumberOfDiseases;
	}

	@Override
	public LinkedList<ScoredDisease> filter(LinkedList<ScoredDisease> phenomizerUnfiltered) {
		return phenomizerUnfiltered;
	}

}
