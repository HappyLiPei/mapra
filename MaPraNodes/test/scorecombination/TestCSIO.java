package scorecombination;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import scorecombination.io.FileUtilitiesCS;

public class TestCSIO {

	@Test
	public void readGeno() {
		HashMap<String, Double> scores = FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputGeno.txt");
		String [] expected_id=new String[]{"MTG1", "MTG2", "MTG3", "MTG4", "MTG5", "MTG6", "MTG7", "MTG8", "MTG9", "MTG10"};
		double [] expected_score = new double []{0.1, 0.05, 0.15, 0.1, 0.1, 0.2, 0.05, 0.05, 0.1, 0.1};
		compareToExpected(scores, expected_id, expected_score);
	}
	
	@Test
	public void readPheno() {
		HashMap<String, Double> scores = FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputPheno.txt");
		String [] expected_id=new String[]{"MTG1", "MTG2", "MTG3", "MTG4", "MTG5", "MTG6", "MTG9", "MTG10",
				"MTG11", "MTG14", "MTG15", "MTG16", "MTG12", "MTG13", "MTG17", "MTG60", "MTG18", "MTG61",
				"MTG19", "MTG20", "MTG7", "MTG8"};
		double [] expected_score = new double []{0.1, 0.1, 0.2, 0.2, 0.3, 0.3, 0.4, 0.4, 0.5, 0.5, 0.5, 0.6, 0.6, 0.6,
				0.7, 0.7, 0.8, 0.8, 0.9, 0.9, 0.9, 0.9};
		compareToExpected(scores, expected_id, expected_score);
	}
	
	@Test
	public void readMetabo() {
		HashMap<String, Double> scores = FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputMetabo.txt");
		String [] expected_id=new String[]{"MTG1", "MTG2", "MTG3", "MTG4", "MTG5", "MTG6", "MTG9", "MTG10",
				"MTG11", "MTG14", "MTG15", "MTG16", "MTG12", "MTG13", "MTG17", "MTG60", "MTG18", "MTG61",
				"MTG19", "MTG20", "MTG7", "MTG8"};
		double [] expected_score = new double []{0.025, 0.05, 0.05, 0.05, 0.1, 0.05, 0.025, 0.05,
				0.05, 0.05, 0.05, 0.05, 0.025, 0.025, 0.05, 0.05, 0.025, 0.05,
				0.05, 0.05, 0.025, 0.05};
		compareToExpected(scores, expected_id, expected_score);
	}
	
	private void compareToExpected(HashMap<String, Double> toTest, String [] expected_id, double [] expected_score){
		assertEquals("Number of genes is incorrect", toTest.size(), expected_id.length);
		for(int i=0; i<expected_id.length; i++){
			assertTrue("Gene "+expected_id[i]+"is missing", toTest.containsKey(expected_id[i]));
			assertEquals("Score of gene "+expected_id[i]+" is incorrect", toTest.get(expected_id[i]), expected_score[i], 1E-10);
		}
	}

}
