package phenomizeralgorithm;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import phenomizer.algorithm.Ontology;
import phenomizer.io.FileUtilitiesPhenomizer;

public class TestOntology {
	
	private Ontology ontology;
	
	@Before
	public void prepareTest(){
		int [][] edges = FileUtilitiesPhenomizer.readInOntology("../TestData/Phenomizer/DiseasesAndSymptoms/Ontology.txt");
		this.ontology = new Ontology(edges);
	}

	@Test
	public void testAncestorRetrievalForSingleNode() {
		
		//leaf
		HashSet<Integer> res = ontology.getAllAncestors(34);
		int [] expected= new int [] {1,4,5,6,7,38,37,36,34,19,18,13,9,3};
		checkdAncestorSingleNode(34, expected, res);
		
		//root
		res = ontology.getAllAncestors(1);
		expected = new int []{1};
		checkdAncestorSingleNode(1, expected, res);
		
		//internal node with multiple parents
		res = ontology.getAllAncestors(11);
		expected = new int []{11,9,10,3,1};
		checkdAncestorSingleNode(11, expected, res);
		
		//node that is not part of the ontology
		res = ontology.getAllAncestors(42);
		expected = new int []{42};
		checkdAncestorSingleNode(42, expected, res);
	}
	
	@Test
	public void testAncestorRetrievalForPairOfNodes(){
		
		// two leaf node: 34 and 35
		HashSet<Integer> ancestors_long = ontology.getAllCommonAncestors(34, 35);
		int [] expected_long = new int[]{36,37,38,7,6,5,4,1};
		checkAncestorsPairs(34, 35, expected_long, ancestors_long);
		
		// parent child pair: 1 and 2
		ancestors_long = ontology.getAllCommonAncestors(1, 2);
		expected_long = new int[]{1};
		checkAncestorsPairs(1, 2, expected_long, ancestors_long);
		
		//two leaf nodes: 34 and 40
		ancestors_long = ontology.getAllCommonAncestors(34, 40);
		expected_long = new int[]{9,3,1};
		checkAncestorsPairs(34, 40, expected_long, ancestors_long);
		
		//two internal nodes: 24 and 25
		ancestors_long = ontology.getAllCommonAncestors(24, 25);
		expected_long = new int[]{21,22,16,13,9,3,1};
		checkAncestorsPairs(24, 25, expected_long, ancestors_long);
		
		//one node that is not part of the ontology: 2 and 42
		ancestors_long = ontology.getAllCommonAncestors(2, 42);
		expected_long = new int[]{};
		checkAncestorsPairs(2, 42, expected_long, ancestors_long);
		
		//both nodes are not part of the ontology:41 ,42
		ancestors_long = ontology.getAllCommonAncestors(41, 42);
		expected_long = new int[]{};
		checkAncestorsPairs(41, 42, expected_long, ancestors_long);
	}
	
	@Test
	public void testRelevantAncestorRetrievalForPair(){
		
		//two internal nodes: 23, 26 -> commutative
		HashSet<Integer> ancestors_short = ontology.getRelevantCommonAncestors(23, 26);
		int [] expected_short = new int []{21,22};
		checkRelevantAncestorsPairs(23, 26, expected_short, ancestors_short);
		ancestors_short = ontology.getRelevantCommonAncestors(26, 23);
		checkRelevantAncestorsPairs(26,23, expected_short, ancestors_short);
		
		//two internal nodes: 14,19 -> not commutative
		ancestors_short = ontology.getRelevantCommonAncestors(14, 19);
		expected_short = new int []{9};
		checkRelevantAncestorsPairs(14, 19, expected_short, ancestors_short);
		ancestors_short = ontology.getRelevantCommonAncestors(19, 14);
		expected_short = new int []{3,9};
		checkRelevantAncestorsPairs(19,14, expected_short, ancestors_short);
		
		//two leaf nodes: 8, 34 -> not commutative
		ancestors_short = ontology.getRelevantCommonAncestors(8, 34);
		expected_short = new int []{1,6};
		checkRelevantAncestorsPairs(8, 34, expected_short, ancestors_short);
		ancestors_short = ontology.getRelevantCommonAncestors(34, 8);
		expected_short = new int []{6};
		checkRelevantAncestorsPairs(34,8, expected_short, ancestors_short);

		//one node is not part of the ontology 2, 42
		ancestors_short = ontology.getRelevantCommonAncestors(2, 42);
		expected_short = new int []{};
		checkRelevantAncestorsPairs(2, 42, expected_short, ancestors_short);
		ancestors_short = ontology.getRelevantCommonAncestors(42,2);
		expected_short = new int []{};
		checkRelevantAncestorsPairs(42,2, expected_short, ancestors_short);
		
		//both nodes are not part of the ontology 41,42
		ancestors_short = ontology.getRelevantCommonAncestors(41, 42);
		expected_short = new int []{};
		checkRelevantAncestorsPairs(41, 42, expected_short, ancestors_short);
	}
	
	private void checkdAncestorSingleNode(int node, int [] expected, HashSet<Integer> res){
		assertEquals("Number of ancestors of "+node+ " is incorrect", expected.length, res.size());
		for (int i: expected){
			assertTrue("ancestor "+i+" of "+node+" is missing", res.contains(i));
		}
	}
	
	private void checkAncestorsPairs(int node1, int node2, int [] expected, HashSet<Integer> res){
		assertEquals("Number of common ancestors of "+node1+" and "+node2+" is incorrect",
				expected.length, res.size());
		for (int i: expected){
			assertTrue("Common ancestor "+i+" of "+node1+" and "+node2+" is missing", res.contains(i));
		}
	}
	
	private void checkRelevantAncestorsPairs(int node1, int node2, int [] expected, HashSet<Integer> res){
		assertEquals("Number of relevant common ancestors of "+node1+" and "+node2+" is incorrect",
				expected.length, res.size());
		for (int i: expected){
			assertTrue("Relevant Common ancestor "+i+" of "+node1+" and "+node2+" is missing", res.contains(i));
		}
	}

}
