package phenomizer.validation;

import java.util.HashMap;
import java.util.LinkedList;

import phenomizer.algorithm.BenjaminiHochbergCorrector;
import phenomizer.algorithm.ComparatorPhenoPval;
import phenomizer.algorithm.PValueCorrector;
import phenomizer.algorithm.PValueFolder;
import phenomizer.algorithm.PhenomizerAlgorithm;
import phenomizer.algorithm.PhenomizerAlgorithmWithPval;

public class PhenomizerWithFrequentSymptomsWithPval extends PhenomizerWithFrequentSymptoms{
	
	private PValueFolder folder;
	private PValueCorrector corrector;
	
	/**
	 * constructor for validation with frequent symptoms using Phenomizer with p values
	 * @param weighting integer indicating if weighted similarity scores are calculated
	 * 		0: unweighted	1: one-sided weighting	2: double-sided weighting
	 * @param onto matrix of PhenoDis symptom ids representing a is-a hierarchy
	 * @param symptoms list of all PhenoDis symptom ids
	 * @param ksz Mapping between PhenoDis disease ids and associated symptoms (list of integer arrays, containing
	 * 			PhenoDis symptom id and a frequency annotation)
	 * @param file file to which the resulting ranks are written
	 * @param pvalfolder folder with pre-calculated score distribution
	 */
	public PhenomizerWithFrequentSymptomsWithPval(int weighting, int[][] onto, LinkedList<Integer> symptoms,
			HashMap<Integer, LinkedList<Integer[]>> ksz, String file, String pvalfolder) {
		
		super(weighting, onto, symptoms, ksz, file);
		folder = new PValueFolder(pvalfolder);
		corrector = new BenjaminiHochbergCorrector();
		comparator=new ComparatorPhenoPval();
		
	}

	@Override
	/**
	 * method to generate a Phenomizer algorithm with pval
	 */
	protected PhenomizerAlgorithm initPhenomizer(LinkedList<Integer> query, HashMap<Integer, Double> ic,
			HashMap<String, Double> sim) {

		return new PhenomizerAlgorithmWithPval(this.sda.numberOfDiseases(), this.ontology, query,
				this.sda, this.sc, folder, corrector, ic, sim);
	}

}
