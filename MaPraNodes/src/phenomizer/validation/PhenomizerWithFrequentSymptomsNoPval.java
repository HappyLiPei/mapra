package phenomizer.validation;

import java.util.HashMap;
import java.util.LinkedList;

public class PhenomizerWithFrequentSymptomsNoPval extends PhenomizerWithFrequentSymptoms {

	public PhenomizerWithFrequentSymptomsNoPval(int weighting, int[][] onto, LinkedList<Integer> symptoms,
			HashMap<Integer, LinkedList<Integer[]>> ksz, String file) {
		super(weighting, onto, symptoms, ksz, file);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void intiPhenomizer(LinkedList<Integer> query) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected double calculateRank(int disease, LinkedList<String[]> result) {
		// TODO Auto-generated method stub
		return 0;
	}

}
