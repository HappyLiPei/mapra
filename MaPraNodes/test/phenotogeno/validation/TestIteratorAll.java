package phenotogeno.validation;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import phenomizer.algorithm.DataTransformer;
import phenomizer.algorithm.FrequencyConverter;
import phenomizer.algorithm.Ontology;
import phenomizer.algorithm.SymptomDiseaseAssociations;
import phenomizer.io.FileUtilitiesPhenomizer;

public class TestIteratorAll {

	@Test
	public void testDiseaseIteratorAll() {
		
		//test uninitialized iterator
		DiseaseIteratorAll toTest = new DiseaseIteratorAll();
		assertEquals("Uninitialized: number of iterations is incorrect", 0, toTest.totalIterations());
		assertFalse("Uninitialized: hasNext is incorrect", toTest.hasNextId());
		
		//initialize iterator
		LinkedList<Integer> symptoms = FileUtilitiesPhenomizer.readInSymptoms(
				"../TestData/Phenomizer/DiseasesAndSymptoms/symptoms.txt");
		Ontology ontology = new Ontology(FileUtilitiesPhenomizer.readInOntology(
				"../TestData/Phenomizer/DiseasesAndSymptoms/Ontology.txt"));
		HashMap<Integer, LinkedList<Integer[]>> ksz_with_freq = (new FrequencyConverter()).convertAll(
				FileUtilitiesPhenomizer.readInKSZFrequency("../TestData/Phenomizer/DiseasesAndSymptoms/ksz_freq.txt"));
		SymptomDiseaseAssociations sda = new DataTransformer().generateSymptomDiseaseAssociation(ontology, symptoms, ksz_with_freq);
		toTest.setSDA(sda);
		
		//test initialized iterator
		int [] expected = new int []{100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110};
		assertEquals("Initialized: number of iterations is incorrect", 11, toTest.totalIterations());
		for(int i=0; i<11; i++){
			assertTrue("Initialized: hasNext is incorrect for iteration"+i, toTest.hasNextId());
			assertEquals("Initialized: Disease for iteration "+i+" is incorrect", 
					expected[i], toTest.getNextDiseaseId());
		}
		assertTrue("Initialized: hasNext is incorrect for iteration 11", !toTest.hasNextId());
		assertEquals("Initialized: number of iterations is incorrect", 11, toTest.totalIterations());
	}

}
