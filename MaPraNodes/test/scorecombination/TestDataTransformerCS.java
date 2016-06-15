package scorecombination;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import geneticnetwork.datastructures.ScoredGenes;
import scorecombination.algo.DataTransformerCS;
import scorecombination.io.FileUtilitiesCS;

public class TestDataTransformerCS {

	@Test
	public void testOnEmptyList() {
		
		DataTransformerCS dt = new DataTransformerCS();
		LinkedList<HashMap<String, Double>> empty = new LinkedList<HashMap<String, Double>>();
		ScoredGenes [] res = dt.transformAllScores(empty);
		assertEquals("Empty list is not transformed correctly", 0, res.length);
	}
	
	@Test
	public void testTransform(){
		
		HashMap<String, Double> s1= FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputGeno.txt");
		HashMap<String, Double> s2 = FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputPheno.txt");
		HashMap<String, Double> s3 = FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputMetabo.txt");
		
		LinkedList<HashMap<String, Double>> list = new LinkedList<HashMap<String, Double>>();
		list.add(s1);
		list.add(s2);
		list.add(s3);
		
		ScoredGenes [] array = (new DataTransformerCS()).transformAllScores(list);
		assertEquals("Number of ScoredGenes objects is incorred", 3, array.length);
		
		checkScoredGenes(array[0],
				new String []{"MTG1", "MTG2", "MTG3", "MTG4", "MTG5", "MTG6", "MTG7", "MTG8", "MTG9", "MTG10"},
				new double []{0.1, 0.05, 0.15, 0.1, 0.1, 0.2, 0.05, 0.05, 0.1, 0.1},
				"Geno");
		checkScoredGenes(array[1],
				new String []{"MTG1", "MTG2", "MTG3", "MTG4", "MTG5", "MTG6", "MTG9", "MTG10",
						"MTG11", "MTG14", "MTG15", "MTG16", "MTG12", "MTG13", "MTG17", "MTG60", "MTG18", "MTG61",
						"MTG19", "MTG20", "MTG7", "MTG8"},
				new double []{0.1, 0.1, 0.2, 0.2, 0.3, 0.3, 0.4, 0.4, 0.5, 0.5, 0.5, 0.6, 0.6, 0.6,
						0.7, 0.7, 0.8, 0.8, 0.9, 0.9, 0.9, 0.9},
				"Pheno");
		checkScoredGenes(array[2],
				new String []{"MTG1", "MTG2", "MTG3", "MTG4", "MTG5", "MTG6", "MTG9", "MTG10",
						"MTG11", "MTG14", "MTG15", "MTG16", "MTG12", "MTG13", "MTG17", "MTG60", "MTG18", "MTG61",
						"MTG19", "MTG20", "MTG7", "MTG8"},
				new double []{0.025, 0.05, 0.05, 0.05, 0.1, 0.05, 0.025, 0.05,
						0.05, 0.05, 0.05, 0.05, 0.025, 0.025, 0.05, 0.05, 0.025, 0.05,
						0.05, 0.05, 0.025, 0.05},
				"Metabo");
	}
	
	private void checkScoredGenes (ScoredGenes g, String[] expected_ids, double [] expected_scores, String mode){
		
		assertEquals("Number of genes is incorrect in "+mode, expected_ids.length, g.size());
		for(int i=0; i<expected_ids.length; i++){
			assertTrue("Gene "+expected_ids[i]+" is missing in "+mode, g.hasGene(expected_ids[i]));
			assertEquals("Score of "+expected_ids[i]+" is incorrect in "+mode, expected_scores[i],
					g.getScoreof(expected_ids[i]), 1E-10);
		}
	}

}
