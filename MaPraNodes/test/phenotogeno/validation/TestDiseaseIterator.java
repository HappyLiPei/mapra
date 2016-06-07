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

public class TestDiseaseIterator {

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
		testIteration(toTest, expected);
	}
	
	@Test
	public void testDiseaseIteratorFile(){
		
		DiseaseIteratorFile iter = new DiseaseIteratorFile(2, "../TestData/PhenoToGeno/ValidationDiseaseIdList.txt");
		int [] expected = new int []{100, 100, 101, 101, 102, 102, 103, 103, 104, 104, 105, 105,
				106, 106, 107, 107, 108, 108, 109, 109, 110, 110};
		testIteration(iter, expected);
	}
	
	private void testIteration(DiseaseIterator iter, int [] expected){
		assertEquals("Nmber of iterations is incorrect", expected.length, iter.totalIterations());
		for(int i=0; i<expected.length; i++){
			assertTrue("HasNext is incorrect for iteration"+i, iter.hasNextId());
			assertEquals("Disease for iteration "+i+" is incorrect", 
					expected[i], iter.getNextDiseaseId());
		}
		assertTrue("Finished: hasNext is incorrect for iteration"+expected.length, !iter.hasNextId());
		assertEquals("Finished: number of iterations is incorrect", expected.length, iter.totalIterations());
	}

}
