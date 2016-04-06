package phenomizeralgorithm.validation;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import phenomizer.validation.FileUtilitiesValidation;

public class TestValidationIO {

	@Test
	public void testParserForIdMapping() {
		
		int[] omim = new int[]{1, 100000, 200000, 300000, 400000, 500000, 600000, 700000, 800000};
		int[] phenoDis = new int[] {101, 102, 103, 104, 105, 107, 108, 109, 110};
		
		HashMap<Integer, Integer> idMap = FileUtilitiesValidation.readOMIMIdMapping(
				"../TestData/Phenomizer/ValidationOMIM/mappingIds.txt");
		assertEquals("Incorrect number of OMIM ids read from file", omim.length ,idMap.size());
		for(int i= 0; i<omim.length; i++){
			assertEquals("PhenoDis id for omim id "+omim[i]+" is incorrect", phenoDis[i], (int) idMap.get(omim[i]));
		}
	}
	
	@Test
	public void testParserForTextMiningResults(){
		
		int[] omim = new int[]{1, 10010, 20020, 100000, 200000, 300000, 400000, 500000, 600000, 700000, 800000};
		Integer[][] queries = new Integer [][] {
			{8,13,14,20,34,39}, {11,23,30,33,34,39}, {4,7,10,12,19,23,27,34,40}, {1,9,10}, {2,15,25,26,31,33,37,38},
			{13}, {2,15,25,33,37,38},{2,6,11,19,23,24,28,34}, {8,11,12,17,19,24,25,35}, {2,11,23,24,30,33,34,39}, {}
		};
		
		HashMap<Integer, LinkedList<Integer>> queryMap = FileUtilitiesValidation.readQueriesFromTM(
				"../TestData/Phenomizer/ValidationOMIM/textMining.txt");
		assertEquals("Incorrect number of queries read from text mining", omim.length, queryMap.size());
		for(int i=0; i<omim.length; i++){
			assertArrayEquals("Symptoms for omim id "+omim[i]+" are incorrect",
					queries[i], queryMap.get(omim[i]).toArray(new Integer[0]));
		}
	}

}
