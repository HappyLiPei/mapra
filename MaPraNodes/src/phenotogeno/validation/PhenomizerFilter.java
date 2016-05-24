package phenotogeno.validation;

import java.util.LinkedList;

import phenotogeno.algo.ScoredDisease;

public abstract class PhenomizerFilter {
	
	protected int totalNumberOfDiseases;
	
	public void setTotalDiseases(int numberOfDiseases){
		this.totalNumberOfDiseases=numberOfDiseases;
	}
	
	public abstract int getResultSize();
	public abstract LinkedList<ScoredDisease> filter(LinkedList<ScoredDisease> phenomizerUnfiltered);
	
}
