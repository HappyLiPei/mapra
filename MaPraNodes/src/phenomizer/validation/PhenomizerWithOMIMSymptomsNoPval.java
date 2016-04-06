package phenomizer.validation;

import java.util.HashMap;
import java.util.LinkedList;

import phenomizer.algorithm.ComparatorPhenoScore;
import phenomizer.algorithm.PhenomizerAlgorithm;
import phenomizer.algorithm.PhenomizerAlgorithmNoPvalue;

public class PhenomizerWithOMIMSymptomsNoPval extends PhenomizerWithOMIMSymptoms {

	public PhenomizerWithOMIMSymptomsNoPval(int weighting, int[][] onto, LinkedList<Integer> symptoms,
			HashMap<Integer, LinkedList<Integer[]>> ksz, String file, HashMap<Integer, Integer> omimToPhenoDis,
			HashMap<Integer, LinkedList<Integer>> omimToSymptoms) {
		
		super(weighting, onto, symptoms, ksz, file, omimToPhenoDis, omimToSymptoms);
		comparator = new ComparatorPhenoScore();
	}

	@Override
	protected PhenomizerAlgorithm initPhenomizer(LinkedList<Integer> query, HashMap<Integer, Double> ic,
			HashMap<String, Double> sim) {

		return new PhenomizerAlgorithmNoPvalue(sda.numberOfDiseases(), ontology, query, sda, sc, ic, sim);
	}

}
