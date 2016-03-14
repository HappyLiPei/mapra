package phenomizer.validation;

import java.util.HashMap;
import java.util.LinkedList;

import phenomizer.algorithm.ComparatorPhenoPval;
import phenomizer.algorithm.PhenomizerAlgorithm;

public class PhenomizerWithFrequentSymptomsWithPval extends PhenomizerWithFrequentSymptoms{

	public PhenomizerWithFrequentSymptomsWithPval(int weighting, int[][] onto, LinkedList<Integer> symptoms,
			HashMap<Integer, LinkedList<Integer[]>> ksz, String file) {
		super(weighting, onto, symptoms, ksz, file);
		
		comparator=new ComparatorPhenoPval();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected PhenomizerAlgorithm initPhenomizer(LinkedList<Integer> query) {
		// TODO Auto-generated method stub
		return null;
		
	}

}
