package phenomizeralgorithm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import io.FileInputReader;
import phenomizer.algorithm.FrequencyConverter;
import phenomizer.algorithm.SimilarityMatrixCalculator;
import phenomizer.io.FileUtilitiesPhenomizer;

public class TestSimMatrixCalculator {
	
	private SimilarityMatrixCalculator matrixCalc;
	private String file;
	
	@Rule
	public TemporaryFolder folder =  new TemporaryFolder();
	
	@Before
	public void setMatrixCalculator() throws IOException{
		int [][] o = FileUtilitiesPhenomizer.readInOntology(
				"../TestData/Phenomizer/DiseasesAndSymptoms/Ontology.txt");
		LinkedList<Integer> s = FileUtilitiesPhenomizer.readInSymptoms(
				"../TestData/Phenomizer/DiseasesAndSymptoms/symptoms.txt");
		HashMap<Integer, LinkedList<Integer []>> k= (new FrequencyConverter()).addWeights(
				FileUtilitiesPhenomizer.readInKSZ("../TestData/Phenomizer/DiseasesAndSymptoms/ksz.txt"));
		
		file = folder.newFile().getAbsolutePath();
		matrixCalc = new SimilarityMatrixCalculator(o, s, k, file);
	}

	@Test
	public void testDataPreparation() throws NoSuchFieldException, SecurityException,
	IllegalArgumentException, IllegalAccessException {
		
		matrixCalc.prepareData();
		Field f1 = SimilarityMatrixCalculator.class.getDeclaredField("diseaseIdOrder");
		Field f2 = SimilarityMatrixCalculator.class.getDeclaredField("IdToPos");
		Field f3 = SimilarityMatrixCalculator.class.getDeclaredField("queries");
		f1.setAccessible(true);
		f2.setAccessible(true);
		f3.setAccessible(true);
		
		int [] ids = (int[]) f1.get(matrixCalc);
		@SuppressWarnings("unchecked")
		HashMap<Integer, Integer> map = (HashMap<Integer, Integer>) f2.get(matrixCalc);
		@SuppressWarnings("unchecked")
		LinkedList<Integer>[] qs = (LinkedList<Integer> []) f3.get(matrixCalc);
		
		int [] ids_exp = new int []{100,101,102,103,104,105,106,107,108,109,110};
		assertArrayEquals("Ids for matrix calculation are incorrect",
				ids_exp, ids);
		assertEquals("Size of mapping id->position is incorrect", ids_exp.length, map.size());
		for(int i=0; i<ids_exp.length; i++){
			assertEquals("Id "+ids_exp[i]+"is mapped to wrong position", i, (int) map.get(ids_exp[i])); 
		}
		
		Integer [][] symp_exp= new Integer[][]{{1},{35,8,17,19,24,25,11,12},{28},{23,24,11,34},{28,2,19,6},
			{6,40,20},{11,5,16},{37,38,33,25,15,2},{14,39},{23,24,11,34,2,33,39,30},{20,34,8,14} };
		assertEquals("Number of queries is incorrect", symp_exp.length, qs.length);
		for(int i=0; i<symp_exp.length; i++){
			assertArrayEquals("Query "+i+" is incorrect", symp_exp[i], qs[i].toArray(new Integer[0]));
		}
	}

	@Test
	public void checkCalculatedMatrix(){
		
		matrixCalc.prepareData();
		matrixCalc.calculateSimilarityMatrix();
		
		String[] actual = FileInputReader.readAllLinesFrom(file).toArray(new String [0]);
		String[] expected = FileInputReader.readAllLinesFrom(
				"../TestData/Phenomizer/ExpectedResults/allAgainstAll_sim.txt").toArray(new String[0]);
		
		assertEquals("Size of the matrix file is incorrect", expected.length, actual.length);
		assertArrayEquals("Header of the matrix is incorrect", expected[0].split("\t"), actual[0].split("\t"));
		
		for(int i=1; i<expected.length; i++){
			System.out.println(actual[i]);
			String [] line_expected = expected[i].split("\t");
			String [] line_actual = actual[i].split("\t");
			assertEquals("Length of line "+(i+1)+" is incorrect", line_expected.length, line_actual.length);
			for(int j=0; j<line_expected.length; j++){
				//values originally rounded to 2 decimal places, now 3 decimal places
				assertEquals("Element "+(j+1)+" in line "+(i+1)+" is incorrect", Double.valueOf(line_expected[j]),
						Double.valueOf(line_actual[j]),1E-2);
			}
		}

	}

}
