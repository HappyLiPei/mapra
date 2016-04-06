package phenomizer.validation;

import java.util.HashMap;
import java.util.LinkedList;

import phenomizer.algorithm.BenjaminiHochbergCorrector;
import phenomizer.algorithm.ComparatorPhenoPval;
import phenomizer.algorithm.PValueCorrector;
import phenomizer.algorithm.PValueFolder;
import phenomizer.algorithm.PhenomizerAlgorithm;
import phenomizer.algorithm.PhenomizerAlgorithmWithPval;

public class PhenomizerWithOMIMSymptomsWithPval extends PhenomizerWithOMIMSymptoms {
	
	/** Pvalue folder with score distributions for calculation of pvalues*/
	private PValueFolder pFolder;
	/** object for correcting pvalues*/
	private PValueCorrector corrector;
	
	/**
	 * generates an object for running the validation of Phenomizer with symptoms from OMIM with calculating pvalues
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
	 * @param folder
	 * 		path folder containing the empirical score distributions for all disease in PhenoDis 
	 */
	public PhenomizerWithOMIMSymptomsWithPval(int weighting, int[][] onto, LinkedList<Integer> symptoms,
			HashMap<Integer, LinkedList<Integer[]>> ksz, String file, HashMap<Integer, Integer> omimToPhenoDis,
			HashMap<Integer, LinkedList<Integer>> omimToSymptoms, String folder) {
		
		super(weighting, onto, symptoms, ksz, file, omimToPhenoDis, omimToSymptoms);
		comparator = new ComparatorPhenoPval();
		pFolder = new PValueFolder(folder);
		corrector = new BenjaminiHochbergCorrector();
	}

	@Override
	protected PhenomizerAlgorithm initPhenomizer(LinkedList<Integer> query, HashMap<Integer, Double> ic,
			HashMap<String, Double> sim) {

		return new PhenomizerAlgorithmWithPval(sda.numberOfDiseases(), ontology, query, sda,
				sc, pFolder, corrector, ic, sim);
	}
	
	

}
