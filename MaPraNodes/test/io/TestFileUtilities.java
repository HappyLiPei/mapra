package io;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import phenomizer.io.FileUtilitiesPhenomizer;

public class TestFileUtilities {

	@Test
	public void TestQueryParser() {
		LinkedList<Integer> q = FileUtilitiesPhenomizer.readInQuery("../TestData/Phenomizer/Queries/query1.txt");
		assertEquals("Query Element 0 ist not parsed correctly", new Integer(23), q.get(0));
		assertEquals("Query Element 1 ist not parsed correctly", new Integer(24), q.get(1));
		assertEquals("Query Element 2 ist not parsed correctly", new Integer(11), q.get(2));
		assertEquals("Query Element 3 ist not parsed correctly", new Integer(34), q.get(3));
		assertEquals("Query Element 4 ist not parsed correctly", new Integer(2), q.get(4));
		assertEquals("Query Element 5 ist not parsed correctly", new Integer(33), q.get(5));
		assertEquals("Query Element 6 ist not parsed correctly", new Integer(39), q.get(6));
		assertEquals("Query Element 7 ist not parsed correctly", new Integer(30), q.get(7));
		assertEquals("Query List size does not match query size", 8,q.size());
	}
	
	@Test
	public void TestOntologyParser(){
		int [][] o = FileUtilitiesPhenomizer.readInOntology("../TestData/Phenomizer/DiseasesAndSymptoms/Ontology.txt");
		assertArrayEquals("Ontology entry 0 is not parsed correctly", new int[]{2,1}, o[0]);
		assertArrayEquals("Ontology entry 23 is not parsed correctly", new int[]{22,16}, o[23]);
		assertArrayEquals("Ontology entry 45 is not parsed correctly", new int[]{40,14}, o[45]);
		assertEquals("Ontology Array size does not match ontology size", 46,o.length);
	}
	
	@Test
	public void TestSymptomParser(){
		LinkedList<Integer> s = FileUtilitiesPhenomizer.readInSymptoms("../TestData/Phenomizer/DiseasesAndSymptoms/symptoms.txt");
		assertEquals("Symptom 0 is not parsed correctly", new Integer(1), s.get(0));
		assertEquals("Symptom 1 is not parsed correctly", new Integer(2), s.get(1));
		assertEquals("Symptom 2 is not parsed correctly", new Integer(2), s.get(2));
		assertEquals("Symptom 24 is not parsed correctly", new Integer(19), s.get(24));
		assertEquals("Symptom 49 is not parsed correctly", new Integer(40), s.get(49));
		assertEquals("Symptom List size does not match symptom number", 50, s.size());
	}
	
	@Test
	public void TestKszParserWithoutFrequency(){
		HashMap<Integer,LinkedList<Integer>> k = FileUtilitiesPhenomizer.readInKSZ("../TestData/Phenomizer/DiseasesAndSymptoms/ksz.txt");
		assertEquals("Ksz hashmap size does not match disease number",11 ,k.size());
		assertEquals("Symptom of disease 100 is not parsed correctly", new Integer(1), k.get(100).get(0));
		
		LinkedList<Integer>s110=k.get(110);
		assertTrue("Symptoms of disease 110 are not parsed correctly",
				s110.size()==5 && s110.get(0)==20 && s110.get(1)==13 && s110.get(2)==34 &&
				s110.get(3)==8 && s110.get(4)==14);
		
		LinkedList<Integer>s108=k.get(108);
		assertTrue("Symptoms of disease 108 are not parsed correctly",
				s108.size()==2 && s108.get(0)==14 && s108.get(1)==39);
	}
	
	@Test
	public void TestKszParserWithFrequency(){
		HashMap<Integer,LinkedList<String[]>> k = FileUtilitiesPhenomizer.readInKSZFrequency
				("../TestData/Phenomizer/DiseasesAndSymptoms/ksz_freq.txt");
		assertEquals("Ksz hashmap size does not match disease number",11 ,k.size());
		
		assertEquals("Symptom of disease 100 is not parsed correctly", "1", k.get(100).get(0)[0]);
		assertEquals("Symptom frequency of disease 100 is not parsed correctly", "occasional [Orphanet]", k.get(100).get(0)[1]);
		
		LinkedList<String[]>s110=k.get(110);
		assertEquals("Symptoms of disease 110 are not parsed correctly", 5, s110.size());
		assertArrayEquals("Symptom 20 of disease 110 is not parsed correctly",
				new String[]{"20","very frequent [Orphanet]"}, s110.get(0));
		assertArrayEquals("Symptom 20 of disease 110 is not parsed correctly",
				new String[]{"13","frequent [Orphanet]"}, s110.get(1));
		assertArrayEquals("Symptom 20 of disease 110 is not parsed correctly",
				new String[]{"34","occasional [Orphanet]"}, s110.get(2));
		assertArrayEquals("Symptom 20 of disease 110 is not parsed correctly",
				new String[]{"8","occasional [Orphanet]"}, s110.get(3));
		assertArrayEquals("Symptom 20 of disease 110 is not parsed correctly",
				new String[]{"14","frequent [Orphanet]"}, s110.get(4));

		LinkedList<String[]>s108=k.get(108);
		assertEquals("Symptoms of disease 108 are not parsed correctly", 2, s108.size());
		assertArrayEquals("Symptom 14 of disease 108 is not parsed correctly",
				new String[]{"14","frequent [Orphanet]"}, s108.get(0));
		assertArrayEquals("Symptom 39 of disease 108 is not parsed correctly",
				new String[]{"39","very frequent [Orphanet]"}, s108.get(1));
	}
	
	@Test
	public void TestMatrixParser(){
		String [][] m = FileUtilitiesPhenomizer.readInMatrix("../TestData/Phenomizer/Clustering/allAgainstAll_avg.txt");
		assertArrayEquals("Matrix header is not parsed correctly", 
				new String[]{"id","102","103","100","101","110","108","109","106","107","104","105"}, m[0]);
		assertArrayEquals("Row 0 of matrix is not parsed correctly", 
				new String[]{"102","0.0","1.0","1.0","1.0","1.0","1.0","1.0","1.0","1.0","0.817","1.0"}, m[1]);
		assertArrayEquals("Row 10 of matrix is not parsed correctly", 
				new String[]{"105","1.0","1.0","1.0","1.0","0.928","0.92","1.0","1.0","1.0","1.0","0.0"}, m[11]);
	}

}
