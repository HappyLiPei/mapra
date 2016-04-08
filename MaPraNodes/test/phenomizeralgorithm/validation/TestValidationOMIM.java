package phenomizeralgorithm.validation;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import io.FileInputReader;
import phenomizer.algorithm.FrequencyConverter;
import phenomizer.io.FileUtilitiesPhenomizer;
import phenomizer.validation.FileUtilitiesValidation;
import phenomizer.validation.PhenomizerWithOMIMSymptomsNoPval;
import phenomizer.validation.PhenomizerWithOMIMSymptomsWithPval;

public class TestValidationOMIM {
	
	private int[][] onto;
	LinkedList<Integer> symptoms;
	HashMap<Integer, LinkedList<Integer[]>> ksz;
	HashMap<Integer, Integer> omimPhenoDis;
	HashMap<Integer, LinkedList<Integer>>queries;
	
	private void readAndCreateInput(int weighting){
		onto = FileUtilitiesPhenomizer.readInOntology(
				"../TestData/Phenomizer/DiseasesAndSymptoms/Ontology.txt");
		symptoms = FileUtilitiesPhenomizer.readInSymptoms(
				"../TestData/Phenomizer/DiseasesAndSymptoms/symptoms.txt");
		if(weighting==0){
			ksz = (new FrequencyConverter()).addWeights(FileUtilitiesPhenomizer
				.readInKSZ("../TestData/Phenomizer/DiseasesAndSymptoms/ksz_freq.txt"));
		}
		else{
			ksz = (new FrequencyConverter()).convertAll(FileUtilitiesPhenomizer
				.readInKSZFrequency("../TestData/Phenomizer/DiseasesAndSymptoms/ksz_freq.txt"));
		}
		omimPhenoDis = FileUtilitiesValidation.readOMIMIdMapping(
				"../TestData/Phenomizer/ValidationOMIM/mappingIds.txt");
		queries = FileUtilitiesValidation.readQueriesFromTM(
				"../TestData/Phenomizer/ValidationOMIM/textMining.txt");		
	}
	
	@Rule
	public TemporaryFolder folder =  new TemporaryFolder();
	
	@Test
	public void testValidationUnweightedNoP() throws IOException {
		
		readAndCreateInput(0);		
		String path = folder.newFile().getAbsolutePath();
		
		PhenomizerWithOMIMSymptomsNoPval pwos = new PhenomizerWithOMIMSymptomsNoPval(0, onto, symptoms, ksz,
				path, omimPhenoDis, queries);
		pwos.prepareData();
		pwos.runValidation();
		
		LinkedList<String> expected = FileInputReader.readAllLinesFrom(
				"../TestData/Phenomizer/ExpectedResults/Validation/omim_noweight_nopval.txt");
		LinkedList<String> actual = FileInputReader.readAllLinesFrom(path);
		assertEquals("Size of the result of OMIM validation without weight and pvalue is incorrect",
				expected.size(), actual.size());
		for(int i=0;i<expected.size();i++){
			assertEquals("Line "+(i+1)+" of the result of OMIM validation without weight and pvalue is incorrect",
					expected.get(i), actual.get(i));
		}
	}
	
	@Test
	public void testValidationWeightedNoP() throws IOException {
		
		readAndCreateInput(1);		
		String path = folder.newFile().getAbsolutePath();
		
		PhenomizerWithOMIMSymptomsNoPval pwos = new PhenomizerWithOMIMSymptomsNoPval(1, onto, symptoms, ksz,
				path, omimPhenoDis, queries);
		pwos.prepareData();
		pwos.runValidation();
		
		LinkedList<String> expected = FileInputReader.readAllLinesFrom(
				"../TestData/Phenomizer/ExpectedResults/Validation/omim_weight_nopval.txt");
		LinkedList<String> actual = FileInputReader.readAllLinesFrom(path);
		assertEquals("Size of the result of OMIM validation without weight and pvalue is incorrect",
				expected.size(), actual.size());
		for(int i=0;i<expected.size();i++){
			assertEquals("Line "+(i+1)+" of the result of OMIM validation without weight and pvalue is incorrect",
					expected.get(i), actual.get(i));
		}
	}
	
	@Test
	public void testValidationUnweightedWithP() throws IOException {
		
		readAndCreateInput(0);		
		String path = folder.newFile().getAbsolutePath();
		
		PhenomizerWithOMIMSymptomsWithPval pwos = new PhenomizerWithOMIMSymptomsWithPval(0, onto, symptoms, ksz,
				path, omimPhenoDis, queries, "../TestData/Phenomizer/PValues");
		pwos.prepareData();
		pwos.runValidation();
		
		LinkedList<String> expected = FileInputReader.readAllLinesFrom(
				"../TestData/Phenomizer/ExpectedResults/Validation/omim_noweight_pval.txt");
		LinkedList<String> actual = FileInputReader.readAllLinesFrom(path);
		assertEquals("Size of the result of OMIM validation without weight and pvalue is incorrect",
				expected.size(), actual.size());
		for(int i=0;i<expected.size();i++){
			assertEquals("Line "+(i+1)+" of the result of OMIM validation without weight and pvalue is incorrect",
					expected.get(i), actual.get(i));
		}
	}

}
