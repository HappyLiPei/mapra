package phenomizeralgorithm;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import io.FileInputReader;
import io.FileUtilities;
import main.CalcPValue;

public class TestPhenomizer {
	
	private LinkedList<Integer> query;
	private LinkedList<Integer> symptoms;
	private int [][] ontology;
	private HashMap<Integer,LinkedList<Integer[]>> ksz_no_freq;
	private HashMap<Integer,LinkedList<Integer[]>> ksz_with_freq;
	private LinkedList<String> expected_res;
	
	private void readData(int i, boolean weight){
		query = FileUtilities.readInQuery("../TestData/Queries/query"+i+".txt");
		symptoms = FileUtilities.readInSymptoms("../TestData/DiseasesAndSymptoms/symptoms.txt");
		ontology = FileUtilities.readInOntology("../TestData/DiseasesAndSymptoms/Ontology.txt");
		
		ksz_no_freq= CalcPValue.addWeights(
				FileUtilities.readInKSZ("../TestData/DiseasesAndSymptoms/ksz.txt"));
		ksz_with_freq = FrequencyConverter.convertAll(
				FileUtilities.readInKSZFrequency("../TestData/DiseasesAndSymptoms/ksz_freq.txt"));	
		
		if(!weight){
			expected_res = FileInputReader.readAllLinesFrom("../TestData/ExpectedResults/NoWeightNoP/res_q"+i+".txt");
		}
		else{
			expected_res = FileInputReader.readAllLinesFrom("../TestData/ExpectedResults/WeightNoP/res_q"+i+".txt");
		}
		//remove table header
		expected_res.remove(0);
	}

	@Test
	public void testPhenomizer_NoWeight_NoPVal() {
		
		for(int i=1; i<=1; i++){
			readData(i,false);
			AlgoPheno.setInput(query, symptoms, ksz_no_freq, ontology);
			LinkedList<String[]> result = AlgoPheno.runPhenomizer(11,false);
			for(int j=0; j<=10; j++){
				String[] elements = expected_res.get(j).split("\t");
				assertEquals("Query "+i+" result "+j+"does not match expected disease id",
						elements[0], result.get(j)[0]);
				assertEquals("Query "+i+" result "+j+"does not match expected score",
						elements[2], result.get(j)[1]);
			}
		}
	}
	
	@Test
	public void testPhenomizer_Weight_NoPVal() {
		
		for(int i=1; i<=1; i++){
			readData(i,true);
			AlgoPheno.setInput(query, symptoms, ksz_with_freq, ontology);
			LinkedList<String[]> result = AlgoPheno.runPhenomizer(11,false);
			for(int j=0; j<=10; j++){
				String[] elements = expected_res.get(j).split("\t");
				assertEquals("Query "+i+" result "+j+"does not match expected disease id",
						elements[0], result.get(j)[0]);
				assertEquals("Query "+i+" result "+j+"does not match expected score",
						elements[2], result.get(j)[1]);
			}
		}
	}

}
