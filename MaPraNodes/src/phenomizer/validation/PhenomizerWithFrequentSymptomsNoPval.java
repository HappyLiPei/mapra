package phenomizer.validation;

import java.util.HashMap;
import java.util.LinkedList;

import phenomizer.algorithm.ComparatorPhenoScore;
import phenomizer.algorithm.PhenomizerAlgorithm;
import phenomizer.algorithm.PhenomizerAlgorithmNoPvalue;

public class PhenomizerWithFrequentSymptomsNoPval extends PhenomizerWithFrequentSymptoms {

	public PhenomizerWithFrequentSymptomsNoPval(int weighting, int[][] onto, LinkedList<Integer> symptoms,
			HashMap<Integer, LinkedList<Integer[]>> ksz, String file) {
		
		super(weighting, onto, symptoms, ksz, file);
		comparator=new ComparatorPhenoScore();
	}


	@Override
	protected PhenomizerAlgorithm initPhenomizer(LinkedList<Integer> query, HashMap<Integer, Double> ic,
			HashMap<String, Double> sim) {

		return new PhenomizerAlgorithmNoPvalue(sda.numberOfDiseases(), this.ontology, query,
				this.sda, this.sc, ic, sim);
	}
}
