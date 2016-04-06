package phenomizeralgorithm.validation;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import phenomizer.algorithm.FrequencyConverter;
import phenomizer.io.FileUtilitiesPhenomizer;
import phenomizer.validation.FileUtilitiesValidation;
import phenomizer.validation.PhenomizerValidation;
import phenomizer.validation.PhenomizerWithOMIMSymptomsNoPval;

public class TestValidationOMIMPreparation {

	@Test
	public void testDataPreparation() throws NoSuchFieldException, SecurityException,
	IllegalArgumentException, IllegalAccessException {
		
		//generate input data
		int[][] onto = FileUtilitiesPhenomizer.readInOntology(
				"../TestData/Phenomizer/DiseasesAndSymptoms/Ontology.txt");
		LinkedList<Integer> symptoms = FileUtilitiesPhenomizer.readInSymptoms(
				"../TestData/Phenomizer/DiseasesAndSymptoms/symptoms.txt");
		HashMap<Integer, LinkedList<Integer[]>> ksz = (new FrequencyConverter()).convertAll(FileUtilitiesPhenomizer
				.readInKSZFrequency("../TestData/Phenomizer/DiseasesAndSymptoms/ksz_freq.txt"));
		HashMap<Integer, Integer> omimPhenoDis = FileUtilitiesValidation.readOMIMIdMapping(
				"../TestData/Phenomizer/ValidationOMIM/mappingIds.txt");
		HashMap<Integer, LinkedList<Integer>>queries = FileUtilitiesValidation.readQueriesFromTM(
				"../TestData/Phenomizer/ValidationOMIM/textMining.txt");
		
		//prepare data
		PhenomizerWithOMIMSymptomsNoPval pwos = new PhenomizerWithOMIMSymptomsNoPval(0, onto, symptoms, ksz,
				"", omimPhenoDis, queries);
		pwos.prepareData();
		
		//extract prepared data using reflection
		Field f1 = PhenomizerValidation.class.getDeclaredField("queries");
		Field f2 = PhenomizerValidation.class.getDeclaredField("query_ids");
		f1.setAccessible(true);
		f2.setAccessible(true);
		@SuppressWarnings("unchecked")
		LinkedList<Integer>[] qs = (LinkedList<Integer>[]) f1.get(pwos);
		int [] ids = (int[]) f2.get(pwos);
		
		//expected results
		int[] ids_exp = new int []{101,102,103,104,105,107,108,109,110};
		Integer[][] qs_exp = new Integer [][] {
			{8,14,20,34,39}, {9,10}, {2,15,25,26,31,33,37,38},{13}, {2,15,25,33,37,38},
			{2,11,23,28,34}, {8,11,12,17,19,24,25,35}, {2,11,23,24,30,33,34,39}, {}
		};
		
		//compare to expected results
		HashMap<Integer, Integer> idToPos = new HashMap<Integer, Integer>();
		for(int i=0; i<ids.length; i++){
			idToPos.put(ids[i], i);
		}
		assertEquals("Number of query ids is incorrect",ids_exp.length, ids.length);
		assertEquals("Number of queries is incorrect", ids_exp.length, qs.length);
		for(int i=0; i<ids_exp.length; i++){
			assertTrue("Id "+ids_exp[i]+" is missing", idToPos.containsKey(ids_exp[i]));
			assertArrayEquals("Query for id "+ids_exp[i]+" is incorrect",
					qs_exp[i], qs[idToPos.get(ids_exp[i])].toArray(new Integer[0]));
		}
		
	}

}
