package phenotogeno.validation;

import java.util.LinkedList;

import phenotogeno.algo.ScoredDisease;

public class PhenomizerFilterTop20 extends PhenomizerFilter{

	@Override
	public int getResultSize() {
		return 20;
	}

	@Override
	public LinkedList<ScoredDisease> filter(LinkedList<ScoredDisease> phenomizerUnfiltered) {
		return phenomizerUnfiltered;
	}
	
}
