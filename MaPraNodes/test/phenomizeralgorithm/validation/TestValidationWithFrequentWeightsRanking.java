package phenomizeralgorithm.validation;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import org.junit.Test;

import phenomizer.validation.PhenomizerWithFrequentSymptoms;
import phenomizer.validation.PhenomizerWithFrequentSymptomsNoPval;
import phenomizer.validation.PhenomizerWithFrequentSymptomsWithPval;

public class TestValidationWithFrequentWeightsRanking {

	@Test
	public void testRankingNoPval()
			throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		
		String[][] pheno_result = new String[][]{
			{"110", "5.2"}, {"100", "3.8"}, {"101", "3.8"}, {"105", "3.8"},
			{"107", "3.2"}, {"108", "3.2"}, {"104", "3.2"}, {"103", "3.2"},
			{"106", "2.1"}, {"102", "2.0"}, {"109", "1.4"}};
		LinkedList<String[]> pheno_list = arrayToList(pheno_result);
		
		PhenomizerWithFrequentSymptomsNoPval p = new PhenomizerWithFrequentSymptomsNoPval
				(0, null, null, null, "");
		
		Method m = PhenomizerWithFrequentSymptoms.class.getDeclaredMethod
				("calculateRank", int.class, LinkedList.class);
		m.setAccessible(true);
		
		checkRanks(m, p, pheno_list,
				new String []{"100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111"},
				new double[]{3, 3, 10, 6.5, 6.5, 3, 9, 6.5, 6.5, 11, 1, 0});
	}
	
	@Test
	public void testRankingWithPval()
			throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException{
		
		String [][] pheno_result = new String[][]{
			{"105", "3.3", "0.001"}, {"107", "3.3", "0.001"}, {"100", "3.3", "0.001"},{"101", "2.8", "0.001"},
			{"110", "2.8", "0.001"}, {"109", "0.4", "0.001"}, {"104", "1.1", "0.03"}, {"106", "1.5", "0.25"},
			{"108", "1.4", "0.25"}, {"102", "0.7", "0.25"}, {"103", "0.7", "0.25"}};
		LinkedList<String[]> pheno_list = arrayToList(pheno_result);
		
		PhenomizerWithFrequentSymptomsWithPval p = new PhenomizerWithFrequentSymptomsWithPval
				(0, null, null, null, "", "");
		
		Method m = PhenomizerWithFrequentSymptoms.class.getDeclaredMethod
				("calculateRank", int.class, LinkedList.class);
		m.setAccessible(true);
		
		checkRanks(m, p, pheno_list,
				new String []{"100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111"},
				new double[]{2, 4.5, 10.5, 10.5, 7, 2, 8, 2, 9, 6, 4.5, 0});
	}
	
	private void checkRanks(Method m, PhenomizerWithFrequentSymptoms p, LinkedList<String[]>list,
			String[] ids, double[] ranks)
					throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		for(int i=0; i<ids.length; i++){
			double rank_actual = (double) m.invoke(p, Integer.valueOf(ids[i]), list);
			assertEquals("Rank of disease "+ids[i]+ " is incorrect", ranks[i], rank_actual, 1E-10);	
		}
		
	}

	private LinkedList<String[]> arrayToList(String[][] pheno_result) {
		LinkedList<String[]> res = new LinkedList<String[]>();
		for(String[] s: pheno_result){
			res.add(s);
		}
		return res;
	}

}
