package phenomizeralgorithm.validation;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import phenomizer.algorithm.FrequencyConverter;
import phenomizer.algorithm.SymptomDiseaseAssociations;
import phenomizer.io.FileUtilitiesPhenomizer;
import phenomizer.validation.PhenomizerWithFrequentSymptoms;
import phenomizer.validation.PhenomizerWithFrequentSymptomsNoPval;


public class TestValidationWithFrequentWeightsPreparation {
	
	private int [][] onto;
	private LinkedList<Integer> symptoms;
	private HashMap<Integer, LinkedList<Integer[]>> ksz_freq;
	
	@Before
	public void readInFiles(){
		
		onto = FileUtilitiesPhenomizer.readInOntology(
				"../TestData/Phenomizer/DiseasesAndSymptoms/Ontology.txt");
		symptoms = FileUtilitiesPhenomizer.readInSymptoms(
				"../TestData/Phenomizer/DiseasesAndSymptoms/symptoms.txt");
		HashMap<Integer, LinkedList<String[]>> ksz= FileUtilitiesPhenomizer.readInKSZFrequency(
				"../TestData/Phenomizer/DiseasesAndSymptoms/ksz_freq.txt");
		ksz_freq = (new FrequencyConverter()).convertAll(ksz);
		
	}

	@Test
	public void testDataPreparationWeighted()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		PhenomizerWithFrequentSymptomsNoPval p = new PhenomizerWithFrequentSymptomsNoPval(1, onto, symptoms, ksz_freq, "");
		p.prepareData();
		
		checkDiseaseIds(p);
		checkQueries(p);
		checkSDA(p, new Integer[][]{{20,15},{34,5},{8,5},{14,10}});
	}
	
	@Test
	public void testDataPreparationUnweighted()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		PhenomizerWithFrequentSymptomsNoPval p = new PhenomizerWithFrequentSymptomsNoPval(0, onto, symptoms, ksz_freq, "");
		p.prepareData();
		
		checkDiseaseIds(p);
		checkQueries(p);
		checkSDA(p, new Integer[][]{{20,10}, {34,10}, {8,10}, {14,10}});
	}
	
	private void checkSDA(PhenomizerWithFrequentSymptoms p, Integer [][] expected)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		
		Field g = PhenomizerWithFrequentSymptoms.class.getDeclaredField("sda");
		g.setAccessible(true);
		SymptomDiseaseAssociations sda = (SymptomDiseaseAssociations) g.get(p);
		LinkedList<Integer[]> s = sda.getSymptoms(110);
		assertEquals("Number of symptoms of disease 100 is incorrect", 4,s.size());
		
		for(int i=0; i<expected.length; i++){
			assertArrayEquals("Symptom "+(i+1) +" of disease 110 is incorrect",	
					expected[i],s.get(i));
		}
		
	}
	
	private void checkQueries(PhenomizerWithFrequentSymptoms p) throws NoSuchFieldException, SecurityException,
		IllegalArgumentException, IllegalAccessException{
		
		Field f = PhenomizerWithFrequentSymptoms.class.getDeclaredField("queries");
		f.setAccessible(true);
		@SuppressWarnings("unchecked")
		LinkedList<Integer> [] queries = (LinkedList<Integer>[]) f.get(p);
		
		assertEquals("Number of queries is incorrect", 11, queries.length);
		assertArrayEquals("Query 1 (disease 100A) is incorrect", new int []{}, listToArray(queries[0]));
		assertArrayEquals("Query 2 (disease 101B) is incorrect", new int []{35,8,12}, listToArray(queries[1]));
		assertArrayEquals("Query 3 (disease 102C) is incorrect", new int []{}, listToArray(queries[2]));
		assertArrayEquals("Query 4 (disease 103D) is incorrect", new int []{23}, listToArray(queries[3]));
		assertArrayEquals("Query 5 (disease 104E) is incorrect", new int []{28}, listToArray(queries[4]));
		assertArrayEquals("Query 6 (disease 105F) is incorrect", new int []{40}, listToArray(queries[5]));
		assertArrayEquals("Query 7 (disease 106G) is incorrect", new int []{}, listToArray(queries[6]));
		assertArrayEquals("Query 8 (disease 107H) is incorrect", new int []{37,15}, listToArray(queries[7]));
		assertArrayEquals("Query 9 (disease 108I) is incorrect", new int []{39}, listToArray(queries[8]));
		assertArrayEquals("Query 10 (disease 109J) is incorrect", new int []{34}, listToArray(queries[9]));
		//13 also frequent symptom of 110K but 13 is ancestor of 34 -> gets removed
		assertArrayEquals("Query 11 (disease 110K) is incorrect", new int []{20}, listToArray(queries[10]));
	}
	
	private void checkDiseaseIds(PhenomizerWithFrequentSymptoms p)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		
		Field f = PhenomizerWithFrequentSymptoms.class.getDeclaredField("query_ids");
		f.setAccessible(true);
		int [] ids = (int[]) f.get(p);
		assertArrayEquals("query ids are incorrect", new int[]{100,101,102,103,104,105,106,107,108,109,110}, ids );
	}
	
	private int [] listToArray(LinkedList<Integer> list){
		int [] array = new int [list.size()];
		int pos=0;
		for(int i:list){
			array[pos]=i;
			pos++;
		}
		return array;
	}

}
