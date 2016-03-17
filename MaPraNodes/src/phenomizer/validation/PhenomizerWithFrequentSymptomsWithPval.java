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

	public PhenomizerWithFrequentSymptomsWithPval(int weighting, int[][] onto, LinkedList<Integer> symptoms,
			HashMap<Integer, LinkedList<Integer[]>> ksz, String file, String pvalfolder) {
		
		super(weighting, onto, symptoms, ksz, file);
		folder = new PValueFolder(pvalfolder);
		corrector = new BenjaminiHochbergCorrector();
		comparator=new ComparatorPhenoPval();
		
	}

	@Override
	protected PhenomizerAlgorithm initPhenomizer(LinkedList<Integer> query, HashMap<Integer, Double> ic,
			HashMap<String, Double> sim) {

		return new PhenomizerAlgorithmWithPval(this.sda.numberOfDiseases(), this.ontology, query,
				this.sda, this.sc, folder, corrector, ic, sim);
	}

}
