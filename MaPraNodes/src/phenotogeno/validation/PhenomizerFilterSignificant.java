package phenotogeno.validation;

import java.util.LinkedList;

import phenotogeno.algo.ScoredDisease;

public class PhenomizerFilterSignificant extends PhenomizerFilter {

	@Override
	public int getResultSize() {
		return totalNumberOfDiseases;
	}

	@Override
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
