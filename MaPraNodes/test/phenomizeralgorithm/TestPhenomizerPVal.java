package phenomizeralgorithm;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import io.FileOutputWriter;
import phenomizer.algorithm.BenjaminiHochbergCorrector;
import phenomizer.algorithm.ComparatorPhenoPval;
import phenomizer.algorithm.DataTransformer;
import phenomizer.algorithm.FrequencyConverter;
import phenomizer.algorithm.Ontology;
import phenomizer.algorithm.PhenomizerAlgorithmWithPval;
import phenomizer.algorithm.SimilarityCalculatorNoWeight;
import phenomizer.algorithm.SymptomDiseaseAssociations;
import phenomizer.io.FileUtilitiesPhenomizer;

public class TestPhenomizerPVal {

	@Test
	public void testSortingOrder() {
		
		String[][] toSort = new String [][]{
			{"10", "4.2",  "0.1" },
			{"11", "1.01", "0.05"},
			{"12", "6",    "0"},
			{"13", "4.2",  "0.1"},
			{"14", "7.1",  "0.5"},
			{"15", "0.2",  "0.05"},
			{"16", "1.8",  "0.4"},
			{"17", "3.2",  "1.0"},
			{"18", "0",    "0.1"},
			{"19", "3.1",  "1.0"}
		};
		
		Arrays.sort(toSort, new ComparatorPhenoPval());
		
		String[][] sorted = new String[][]{
			{"12", "6",    "0"},
			{"11", "1.01", "0.05"},
			{"15", "0.2",  "0.05"},
			{"10", "4.2",  "0.1" },
			{"13", "4.2",  "0.1"},
			{"18", "0",    "0.1"},
			{"16", "1.8",  "0.4"},
			{"14", "7.1",  "0.5"},
			{"17", "3.2",  "1.0"},
			{"19", "3.1",  "1.0"}
		};
		
		assertArrayEquals("Array is not sorted correctly",sorted, toSort);
	}
	
	@Rule
	public TemporaryFolder folder =  new TemporaryFolder();
	
	@Test
	public void testPValeCalculation()
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException{
		
		//dummy object
		PhenomizerAlgorithmWithPval pp = new PhenomizerAlgorithmWithPval(0, null, null, null,
				new SimilarityCalculatorNoWeight(), null, null);
		
		//input: phenomizer similarity scores
		Integer[] disease_ids = new Integer[]{100, 101, 102, 103, 104, 105};
		Double[] scores = new Double[]{0.0, 0.1, 0.001, 0.004, 0.002, 0.001};
		HashMap<Integer, Double> score_map = new HashMap<>(10);
		for(int i=0; i<disease_ids.length; i++){
			score_map.put(disease_ids[i], scores[i]);
		}
		//input: score distribution
		File f = folder.newFile();
		FileOutputWriter fow = new FileOutputWriter(f.getAbsolutePath());
		fow.writeFileln("100\t10\t9\t8\t7");
		fow.writeFileln("101\t10");
		fow.writeFileln("102\t10\t10\t10");
		fow.writeFileln("103\t10\t2\t2\t2\t1");
		fow.writeFileln("104\t10\t9\t4\t3\t3\t3\t3");
		fow.writeFileln("105\t10\t6");
		fow.closew();
		
		//expected result
		String [][] expected = new String[][]{
			{"101", "0.1", "0.0"},
			{"103", "0.004", "0.1"},
			{"104", "0.002", "0.4"},
			{"105", "0.001", "0.6"},
			{"102", "0.001", "1.0"},
			{"100", "0.0", "1.0"}
		};
		
		//reflection to execute private method of PhenomizerAlgorithmWithPVal
		Method target = PhenomizerAlgorithmWithPval.class.getDeclaredMethod("getPvalues", HashMap.class, String.class);
		target.setAccessible(true);		
		String [][] res = (String[][]) target.invoke(pp, score_map, f.getAbsolutePath());
		
		assertArrayEquals("Pvalues are not calculated correctly", expected, res);
	}
	
	@Test
	public void testPValueCorrection() throws 
		NoSuchMethodException, SecurityException, IllegalAccessException,
		IllegalArgumentException, InvocationTargetException{
		
		int [][] onto = FileUtilitiesPhenomizer.readInOntology("../TestData/Phenomizer/DiseasesAndSymptoms/Ontology.txt");
		Ontology o = new Ontology(onto);
		LinkedList<Integer> symptoms = FileUtilitiesPhenomizer.readInSymptoms("../TestData/Phenomizer/DiseasesAndSymptoms/symptoms.txt");
		HashMap<Integer, LinkedList<Integer[]>> ksz = (new FrequencyConverter()).addWeights(
				FileUtilitiesPhenomizer.readInKSZ("../TestData/Phenomizer/DiseasesAndSymptoms/ksz.txt"));
		SymptomDiseaseAssociations sda = (new DataTransformer()).generateSymptomDiseaseAssociation(o, symptoms, ksz);
		
		
		//dummy object
		PhenomizerAlgorithmWithPval pp = new PhenomizerAlgorithmWithPval(0, o, null, sda,
				new SimilarityCalculatorNoWeight(), null, new BenjaminiHochbergCorrector());
		
		//input
		String [][] input = new String[][]{
			{"100", "3", "0"},
			{"101", "4", "0"},
			{"102", "1", "0.001"},
			{"103", "2", "0.001"},
			{"104", "3", "0.001"},
			{"105", "7", "0.02"},
			{"106", "6", "0.02"},
			{"107", "6", "0.1"},
			{"108", "7", "0.1"},
			{"109", "1", "0.2"},
			{"110", "1", "0.3"}
		};
		
		//expected
		String[][] expected = new String[][]{
			{"101", "4", "0"},
			{"100", "3", "0"},
			{"104", "3", "0.0022"},
			{"103", "2", "0.0022"},
			{"102", "1", "0.0022"},
			{"105", "7", "0.03143"},
			{"106", "6", "0.03143"},
			{"108", "7", "0.12222"},
			{"107", "6", "0.12222"},
			{"109", "1", "0.22"},
			{"110", "1", "0.3"}
		};
		
		String[][] output = Arrays.copyOf(input, input.length);
		
		//reflection to execute private method of PhenomizerAlgorithmWithPVal
		Method target = PhenomizerAlgorithmWithPval.class.getDeclaredMethod("correctPvalues", String[][].class);
		target.setAccessible(true);
		@SuppressWarnings("all") 
		Object object = target.invoke(pp, output);
		
		for(int i=0; i<output.length; i++){
			assertEquals("Disease in line "+i+" is not correct",expected[i][0], output[i][0]);
			assertEquals("Score in line "+i+" is not correct", expected[i][1], output[i][1]);
			assertEquals("Pvalue in line "+i+" is not correct", Double.valueOf(expected[i][2]),
					Double.valueOf(output[i][2]), 1E-3);
		}	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testOutputSize() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		//input
		String[][] input = new String[][]{
			{"101", "4", "0"},
			{"100", "3", "0"},
			{"104", "3", "0.0022"},//cut
			{"103", "2", "0.0022"},
			{"102", "1", "0.0022"},
			{"105", "7", "0.03143"},// cut
			{"106", "7", "0.03143"},
			{"108", "7", "0.12222"},
			{"107", "6", "0.12222"},
			{"109", "1", "0.22"}, //cut
			{"110", "1", "0.22"}
		};
		
		//dummy objects
		PhenomizerAlgorithmWithPval pp1 = new PhenomizerAlgorithmWithPval(3, null, null, null,
				new SimilarityCalculatorNoWeight(), null, new BenjaminiHochbergCorrector());
		PhenomizerAlgorithmWithPval pp2 = new PhenomizerAlgorithmWithPval(6, null, null, null,
				new SimilarityCalculatorNoWeight(), null, new BenjaminiHochbergCorrector());
		PhenomizerAlgorithmWithPval pp3 = new PhenomizerAlgorithmWithPval(10, null, null, null,
				new SimilarityCalculatorNoWeight(), null, new BenjaminiHochbergCorrector());
		PhenomizerAlgorithmWithPval pp4 = new PhenomizerAlgorithmWithPval(100, null, null, null,
				new SimilarityCalculatorNoWeight(), null, new BenjaminiHochbergCorrector());
		
		//reflection to execute private method of PhenomizerAlgorithmWithPVal
		Method target = PhenomizerAlgorithmWithPval.class.getDeclaredMethod("getResult", String[][].class);
		target.setAccessible(true);
		
		@SuppressWarnings("all")
		LinkedList<String[]> res = (LinkedList<String[]>) target.invoke(pp1, input);
		assertEquals("Size of result 1 is not correct", 3, res.size());
		int pos=0;
		for(String[] r:res){
			assertArrayEquals("Result "+pos+" is not correct", input[pos], r);
			pos++;
		}
		
		@SuppressWarnings("all")
		LinkedList<String[]> res2 = (LinkedList<String[]>) target.invoke(pp2, input);
		assertEquals("Size of result 2 is not correct", 7, res2.size());
		pos=0;
		for(String[] r:res2){
			assertArrayEquals("Result "+pos+" is not correct", input[pos], r);
			pos++;
		}
		
		@SuppressWarnings("all")
		LinkedList<String[]> res3 = (LinkedList<String[]>) target.invoke(pp3, input);
		assertEquals("Size of result 3 is not correct", 11, res3.size());
		pos=0;
		for(String[] r:res3){
			assertArrayEquals("Result "+pos+" is not correct", input[pos], r);
			pos++;
		}
		
		@SuppressWarnings("all")
		LinkedList<String[]> res4 = (LinkedList<String[]>) target.invoke(pp4, input);
		assertEquals("Size of result 3 is not correct", 11, res4.size());
		pos=0;
		for(String[] r:res4){
			assertArrayEquals("Result "+pos+" is not correct", input[pos], r);
			pos++;
		}
	}

}
