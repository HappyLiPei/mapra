package phenomizer.validation;

import java.util.HashMap;
import java.util.LinkedList;

import phenomizer.algorithm.ComparatorPhenoScore;
import phenomizer.algorithm.PhenomizerAlgorithm;
import phenomizer.algorithm.PhenomizerAlgorithmNoPvalue;

public class PhenomizerWithFrequentSymptomsNoPval extends PhenomizerWithFrequentSymptoms {
	
	/**
	 * constructor for validation with frequent symptoms using Phenomizer without p values
	 * @param weighting integer indicating if weighted similarity scores are calculated
	 * 		0: unweighted	1: one-sided weighting	2: double-sided weighting
	 * @param onto matrix of PhenoDis symptom ids representing a is-a hierarchy
	 * @param symptoms list of all PhenoDis symptom ids
	 * @param ksz Mapping between PhenoDis disease ids and associated symptoms (list of integer arrays, containing
	 * 			PhenoDis symptom id and a frequency annotation)
	 * @param file file to which the resulting ranks are written
	 */
	public PhenomizerWithFrequentSymptomsNoPval(int weighting, int[][] onto, LinkedList<Integer> symptoms,
			HashMap<Integer, LinkedList<Integer[]>> ksz, String file) {
		
		super(weighting, onto, symptoms, ksz, file);
		comparator=new ComparatorPhenoScore();
	}


	@Override
	/**
	 * method to generate a Phenomizer algorithm without p value
	 */
	protected PhenomizerAlgorithm initPhenomizer(LinkedList<Integer> query, HashMap<Integer, Double> ic,
			HashMap<String, Double> sim) {

		return new PhenomizerAlgorithmNoPvalue(sda.numberOfDiseases(), this.ontology, query,
				this.sda, this.sc, ic, sim);
	}
}
