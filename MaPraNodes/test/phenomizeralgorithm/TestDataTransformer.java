package phenomizeralgorithm;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import phenomizer.algorithm.DataTransformer;
import phenomizer.algorithm.Ontology;
import phenomizer.algorithm.SymptomDiseaseAssociations;
import phenomizer.io.FileUtilitiesPhenomizer;

public class TestDataTransformer {
	
	private Ontology onto;
	private LinkedList<Integer> symptoms;
	private DataTransformer dt;
	
	@Before
	public void prepareTest(){
		onto=new Ontology(FileUtilitiesPhenomizer.readInOntology("../TestData/DiseasesAndSymptoms/Ontology.txt"));
		symptoms=FileUtilitiesPhenomizer.readInSymptoms("../TestData/DiseasesAndSymptoms/symptoms.txt");
		dt=new DataTransformer();	
	}

	@Test
	public void testQueryPreparation() {
		
		//remove duplicates and ancestors
		int [] query1 = new int []{1,1,3,3,9,9,13,13,18,18,19,19,34,34};
		int [] new_query1=convertLinkedListToArray(dt.prepareQuery(onto, convertArrayToLinkedList(query1)));
		assertArrayEquals("Query 1 is not processed correctly", new int[]{34}, new_query1);
		
		//query unchanged
		int [] query2 = new int []{23, 24, 11, 34, 2, 33, 39, 30};
		int [] new_query2=convertLinkedListToArray(dt.prepareQuery(onto, convertArrayToLinkedList(query2)));
		assertArrayEquals("Query 2 is not processed correctly", query2, new_query2);
		
		//remove ancestors
		int [] query3 = new int []{1,3,9,10};
		int [] new_query3=convertLinkedListToArray(dt.prepareQuery(onto, convertArrayToLinkedList(query3)));
		assertArrayEquals("Query 3 is not processed correctly", new int[]{9,10}, new_query3);
		
		//remove duplicates
		int [] query4 = new int []{35,34,35,34};
		int [] new_query4=convertLinkedListToArray(dt.prepareQuery(onto, convertArrayToLinkedList(query4)));
		assertArrayEquals("Query 4 is not processed correctly", new int[]{35,34}, new_query4);
	}
	
	@Test
	public void testKszPreparation() {
		
		HashMap<Integer, LinkedList<Integer[]> > ksz = new HashMap<Integer, LinkedList<Integer[]>>();
		
		LinkedList<Integer[]> l1 = convertArrayToListOfArray(new int []{1,1,3,3,9,9,13,13,18,18,19,19,34,34});	
		LinkedList<Integer[]> l2 = convertArrayToListOfArray(new int []{23, 24, 11, 34, 2, 33, 39, 30});
		LinkedList<Integer[]> l3 = convertArrayToListOfArray(new int []{1,3,9,10});
		LinkedList<Integer[]> l4 = convertArrayToListOfArray(new int []{35,34,35,34});
		ksz.put(1000, l1);
		ksz.put(2000, l2);
		ksz.put(3000, l3);
		ksz.put(4000,l4);
		int[][] expected_symptoms = new int[][] {{34},{23, 24, 11, 34, 2, 33, 39, 30}, {9,10}, {35,34}};
		int[] expected_symptom_counts = new int []{
				4,1,4,3,3,3,3,0,4,2,
				1,0,3,0,0,1,0,3,3,1,
				1,1,1,1,0,0,0,0,0,1,
				0,0,1,3,1,3,3,3,1,0
		};
		
		SymptomDiseaseAssociations sda = dt.generateSymptomDiseaseAssociation(onto, symptoms, ksz);
		
		assertEquals("Total number of diseases is incorrect",4 ,sda.numberOfDiseases());
		
		//test, if ancestors are removed, test if duplicates are removed from kszD
		for(int i=1; i<=4; i++){
			assertArrayEquals("Symptoms of disease "+i*1000+" are not processed correctly",
					expected_symptoms[i-1],convertListOfArrayToArray(sda.getSymptoms(i*1000)));
		}
		
		//test, if symptom-> disease works, iterate over all symptoms and query number of diseases
		for(int i=1; i<=40; i++){
			assertEquals("Number of annotated diseases incorrect for symptom "+i,
					expected_symptom_counts[i-1], sda.numberOfDiseases(i));
		}
		
		//test if symptom ids are transformed correctly
		int [] ids=sda.getAllSymptomsArray();
		assertEquals("Number of all symptoms is incorrect", 40, ids.length);
		for(int i=0; i<ids.length; i++){
			assertEquals("Symptom at position "+i+" does not match", i+1, ids[i]);
		}
		
	}
	
	private LinkedList<Integer> convertArrayToLinkedList(int [] array){
		LinkedList<Integer> res = new LinkedList<Integer>();
		for(int i: array){
			res.add(i);
		}
		return res;
	}
	
	private int[] convertLinkedListToArray(LinkedList<Integer> list){
		int[] res = new int [list.size()];
		int pos=0;
		for(int i: list){
			res[pos]=i;
			pos++;
		}
		return res;
	}
	
	private LinkedList<Integer[]> convertArrayToListOfArray(int [] array){
		LinkedList<Integer[]> res = new LinkedList<Integer[]>();
		for(int i:array){
			res.add(new Integer[]{i, 10});
		}
		return res;
	}
	
	private int [] convertListOfArrayToArray(LinkedList<Integer[]> list){
		int [] res = new int [list.size()];
		int pos=0;
		for(Integer[] i:list){
			res[pos]=i[0];
			pos++;
		}
		return res;
	}

}
