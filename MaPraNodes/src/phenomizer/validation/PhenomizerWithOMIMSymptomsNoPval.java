package phenomizer.validation;

import java.util.HashMap;
import java.util.LinkedList;

import phenomizer.algorithm.ComparatorPhenoScore;
import phenomizer.algorithm.PhenomizerAlgorithm;
import phenomizer.algorithm.PhenomizerAlgorithmNoPvalue;

public class PhenomizerWithOMIMSymptomsNoPval extends PhenomizerWithOMIMSymptoms {
	
	/**
	 * generates an object for running the validation of Phenomizer with symptoms from OMIM without calculating pvalues
	 * @param weighting
	 * 		if 0: unweighted similarity score, if 1: one-sided weighting, if 2: two-sided weighting
	 * @param onto
	 * 		ontology of PhenoDis symptom ids as array of edges
	 * @param symptoms
	 * 		list of all PhenoDis symptom ids
	 * @param ksz
	 * 		mapping PhenoDis disease id to a list of PhenoDis symptom id, represents the associations
	 * 		between diseases and symptoms
	 * @param file
	 * 		file to which the results are written
	 * @param omimToPhenoDis
	 * 		mapping OMIM id -> PhenoDis id
	 * @param omimToSymptoms
	 * 		mapping OMIM id -> list of PhenoDis symptom id
	 */
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
