package phenomizer.validation;

import java.util.HashMap;
import java.util.LinkedList;

import phenomizer.algorithm.ComparatorPhenoScore;
import phenomizer.algorithm.PhenomizerAlgorithm;

public class PhenomizerWithFrequentSymptomsNoPval extends PhenomizerWithFrequentSymptoms {

	public PhenomizerWithFrequentSymptomsNoPval(int weighting, int[][] onto, LinkedList<Integer> symptoms,
			HashMap<Integer, LinkedList<Integer[]>> ksz, String file) {
		super(weighting, onto, symptoms, ksz, file);
		
		comparator=new ComparatorPhenoScore();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected PhenomizerAlgorithm initPhenomizer(LinkedList<Integer> query) {
		return null;
		// TODO Auto-generated method stub
		
	}

}
