package phenomizeralgorithm;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import io.FileInputReader;
import phenomizer.algorithm.AlgoPheno;
import phenomizer.algorithm.FrequencyConverter;
import phenomizer.algorithm.PhenomizerDriver;
import phenomizer.io.FileUtilitiesPhenomizer;

public class TestPhenomizer {
	
	private LinkedList<Integer> query;
	private LinkedList<Integer> symptoms;
	private int [][] ontology;
	private HashMap<Integer,LinkedList<Integer[]>> ksz_no_freq;
	private HashMap<Integer,LinkedList<Integer[]>> ksz_with_freq;
	private LinkedList<String> expected_res;
	
	private void readData(int i, boolean weight, boolean pval){
		query = FileUtilitiesPhenomizer.readInQuery("../TestData/Queries/query"+i+".txt");
		symptoms = FileUtilitiesPhenomizer.readInSymptoms("../TestData/DiseasesAndSymptoms/symptoms.txt");
		ontology = FileUtilitiesPhenomizer.readInOntology("../TestData/DiseasesAndSymptoms/Ontology.txt");
		
		ksz_no_freq= (new FrequencyConverter()).addWeights(
				FileUtilitiesPhenomizer.readInKSZ("../TestData/DiseasesAndSymptoms/ksz.txt"));
		ksz_with_freq = (new FrequencyConverter()).convertAll(
				FileUtilitiesPhenomizer.readInKSZFrequency("../TestData/DiseasesAndSymptoms/ksz_freq.txt"));	
		
		if(!weight){
			if(!pval){
				expected_res = FileInputReader.readAllLinesFrom("../TestData/ExpectedResults/NoWeightNoP/res_q"+i+".txt");
			}
			else{
				expected_res = FileInputReader.readAllLinesFrom("../TestData/ExpectedResults/NoWeightP/res_q"+i+".txt");
			}
		}
		else{
			expected_res = FileInputReader.readAllLinesFrom("../TestData/ExpectedResults/WeightNoP/res_q"+i+".txt");
		}
		//remove table header
		expected_res.remove(0);
	}

	@Test
	public void testPhenomizer_NoWeight_NoPVal() {
		
		for(int i=1; i<=10; i++){
			readData(i,false, false);
			PhenomizerDriver d = new PhenomizerDriver(query, symptoms, ksz_no_freq, ontology);
			d.setPhenomizerAlgorithm(11, false, 0, "");
			LinkedList<String[]> result = d.runPhenomizer();
			for(int j=0; j<=10; j++){
				String[] elements = expected_res.get(j).split("\t");
				assertEquals("Query "+i+" result "+j+" does not match expected disease id",
						elements[0], result.get(j)[0]);
				assertEquals("Query "+i+" result "+j+" does not match expected score",
						elements[2], result.get(j)[1]);
			}
		}
	}
	
	@Test
	public void testPhenomizer_Weight_NoPVal() {
		
		//TODO: calculate manually other queries (1,3,4,6)
		int[] array = new int[] {2,5,6,7,8,9,10};
//		for(int i=1; i<=1; i++){
		for (int i: array){
			readData(i,true, false);
			PhenomizerDriver d = new PhenomizerDriver(query, symptoms, ksz_with_freq, ontology);
			d.setPhenomizerAlgorithm(11, false, 1, "");
			LinkedList<String[]> result = d.runPhenomizer();
			for(int j=0; j<=10; j++){
				String[] elements = expected_res.get(j).split("\t");
				assertEquals("Query "+i+" result "+j+" does not match expected disease id",
						elements[0], result.get(j)[0]);
				assertEquals("Query "+i+" result "+j+" does not match expected score",
						elements[2], result.get(j)[1]);
			}
		}
	}
	
	@Test
	public void testPhenomizer_NoWeight_PVal() {
		
		for(int i=1; i<=10; i++){
			readData(i,false, true);
			PhenomizerDriver d = new PhenomizerDriver(query, symptoms, ksz_no_freq, ontology);
			d.setPhenomizerAlgorithm(11, true, 0, "../TestData/PValues");
			LinkedList<String[]> result = d.runPhenomizer();
			for(int j=0; j<=10; j++){
				String[] elements = expected_res.get(j).split("\t");
				assertEquals("Query "+i+" result "+j+" does not match expected disease id",
						elements[0], result.get(j)[0]);
				assertEquals("Query "+i+" result "+j+" does not match expected score",
						elements[2], result.get(j)[1]);
				assertEquals("Query "+i+" result "+j+" dose not match expected pvalue", 
						Double.valueOf(elements[3]), Double.valueOf(result.get(j)[2]), 1E-4);				
			}
		}	
	}
	
	@Test
	public void testPhenomizer_LimitedOutput(){
		
		//limited to 4, but output 5 because last 2 elements have the same score
		readData(5, false, false);
		expected_res=FileInputReader.readAllLinesFrom(
				"../TestData/ExpectedResults/ResultSizeLimited/res_q5_noweight_nopval.txt");
		expected_res.remove(0);
		
		PhenomizerDriver d = new PhenomizerDriver(query, symptoms, ksz_no_freq, ontology);
		d.setPhenomizerAlgorithm(4, false, 0, "");
		LinkedList<String[]> result = d.runPhenomizer();
		assertEquals("Output size (query 5 imited to 4, no weight, no pvalue) does not match expected output",
				5, result.size());
		for(int j=0; j<=4; j++){
			String[] elements = expected_res.get(j).split("\t");
			assertEquals("Query 5 (limited to 4, no weight, no pvalue) result "+j+" does not match expected disease id",
					elements[0], result.get(j)[0]);
			assertEquals("Query 5 (limited to 4, no weight, no pvalue) result "+j+" does not match expected score",
					elements[2], result.get(j)[1]);
		}
		
		//limited to 4, expected 4 elements
		readData(5, true, false);
		expected_res=FileInputReader.readAllLinesFrom(
				"../TestData/ExpectedResults/ResultSizeLimited/res_q5_weight_nopval.txt");
		expected_res.remove(0);
		AlgoPheno.setInput(query, symptoms, ksz_with_freq, ontology);
		result = AlgoPheno.runPhenomizer(4,false);
		assertEquals("Output size (query 5 imited to 4, weight, no pvalue) does not match expected output",
				4, result.size());
		for(int j=0; j<=3; j++){
			String[] elements = expected_res.get(j).split("\t");
			assertEquals("Query 5 (limited to 4, weight, no pvalue) result "+j+" does not match expected disease id",
					elements[0], result.get(j)[0]);
			assertEquals("Query 5 (limited to 4, weight, no pvalue) result "+j+" does not match expected score",
					elements[2], result.get(j)[1]);
		}
		
		//limited to 4, expected 5 elements because last two elements have the same pvalue and the same score
		readData(5, false, true);
		expected_res=FileInputReader.readAllLinesFrom(
				"../TestData/ExpectedResults/ResultSizeLimited/res_q5_noweight_pval.txt");
		expected_res.remove(0);
		
		d = new PhenomizerDriver(query, symptoms, ksz_no_freq, ontology);
		d.setPhenomizerAlgorithm(4, true, 0, "../TestData/PValues");
		result = d.runPhenomizer();
		
		assertEquals("Output size (query 5 imited to 4, no weight, pvalue) does not match expected output",
				5, result.size());
		for(int j=0; j<=3; j++){
			String[] elements = expected_res.get(j).split("\t");
			assertEquals("Query 5 (limited to 4, no weight, pvalue) result "+j+" does not match expected disease id",
					elements[0], result.get(j)[0]);
			assertEquals("Query 5 (limited to 4, no weight, pvalue) result "+j+" does not match expected score",
					elements[2], result.get(j)[1]);
			assertEquals("Query 5 (limited to 4, no weight, pvalue) result "+j+" dose not match expected pvalue", 
					elements[3], result.get(j)[2]);	
		}
		
	}

}
