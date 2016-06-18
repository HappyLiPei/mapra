package scorecombination;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.junit.Test;

import geneticnetwork.datastructures.ScoredGenes;
import scorecombination.algo.CombineScoresBayes;
import scorecombination.algo.DataTransformerCS;
import scorecombination.io.FileUtilitiesCS;
import togeno.ScoredGene;

public class TestCombineScoreBayes {

	
	@Test
	public void testEmptyGeneSet() throws NoSuchMethodException, SecurityException, 
		IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		LinkedList<HashMap<String, Double>> input_raw = new LinkedList<HashMap<String, Double>>();
		HashSet<String> res = getGeneSet(input_raw);
		
		assertEquals("Number of genes is incorrect", 0, res.size());
	}
	
	@Test
	public void testGeneSetComplete() throws NoSuchMethodException, SecurityException, 
			IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		LinkedList<HashMap<String, Double>> input_raw = new LinkedList<HashMap<String, Double>>();
		input_raw.add(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputGeno.txt"));
		input_raw.add(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputPheno.txt"));
		input_raw.add(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputMetabo.txt"));
		HashSet<String> res = getGeneSet(input_raw);
		
		assertEquals("Number of genes is incorrect", 22, res.size());
		for(int i=1; i<=20; i++){
			assertTrue("Gene MTG"+i+"is missing", res.contains("MTG"+i));
		}
		for(int i=60; i<=61; i++){
			assertTrue("Gene MTG"+i+"is missing", res.contains("MTG"+i));
		}
	}
	
	
	@Test
	public void testCombinationEmpty() 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		
		LinkedList<HashMap<String, Double>> input_raw = new LinkedList<HashMap<String, Double>>();
		HashSet<String> ids = new HashSet<String>();
		LinkedList<ScoredGene> res = getScores(input_raw, ids);
		assertEquals("Size of result (empty id set) is incorrect", 0, res.size());
		
		ids.add("MTG1");
		ids.add("MTG2");
		ids.add("MTG0");
		res = getScores(input_raw, ids);
		assertEquals("Size of result (empty id set) is incorrect", 3, res.size());
		for(ScoredGene g: res){
			assertEquals("Score of gene "+g.getId()+" is incorrect", 0, g.getScore(), 1E-10);
		}

	}
	
	@Test
	public void testCombination1Set() 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		
		LinkedList<HashMap<String, Double>> input_raw = new LinkedList<HashMap<String, Double>>();
		input_raw.add(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputGeno.txt"));
		
		double [] score_exp = new double []{0, 0.1, 0.05, 0.15};
		String [] id_exp = new String [] {"MTG0", "MTG1", "MTG2", "MTG3"};
		
		HashSet<String> ids = new HashSet<String>();
		for (String i:id_exp){
			ids.add(i);
		}
		
		LinkedList<ScoredGene> res = getScores(input_raw, ids);
		checkResult(res, id_exp, score_exp);
	}
	
	@Test
	public void testCombination2Sets()
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		
		LinkedList<HashMap<String, Double>> input_raw = new LinkedList<HashMap<String, Double>>();
		input_raw.add(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputGeno.txt"));
		input_raw.add(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputPheno.txt"));
		
		double [] score_exp = new double []{0, 0.012195122, 0.0058139535, 0.042253521, 0};
		String [] id_exp = new String [] {"MTG0", "MTG1", "MTG2", "MTG3", "MTG20"};
		
		HashSet<String> ids = new HashSet<String>();
		for (String i:id_exp){
			ids.add(i);
		}
		
		LinkedList<ScoredGene> res = getScores(input_raw, ids);
		checkResult(res, id_exp, score_exp);
	}
	
	//TODO: test method for normalization and rounding
	
	private void checkResult(LinkedList<ScoredGene> res, String [] id_exp, double[] score_exp){
		
		assertEquals("Size of result is incorrect", id_exp.length, res.size());
		int pos=0;
		for(ScoredGene g: res){
			assertEquals("Score of gene "+id_exp[pos]+" is incorrect", score_exp[pos], g.getScore(), 1E-9);
			pos++;
		}
	}
	
	@SuppressWarnings("all")
	private HashSet<String> getGeneSet(LinkedList<HashMap<String, Double>> input_raw) 
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		ScoredGenes [] input_final = (new DataTransformerCS()).transformAllScores(input_raw);
		CombineScoresBayes algo = new CombineScoresBayes(input_final);
		
		Method m = CombineScoresBayes.class.getDeclaredMethod("getAllGenes", null);
		m.setAccessible(true);
		HashSet<String> res = (HashSet<String>) m.invoke(algo, null);
		return res;
	}
	
	@SuppressWarnings("all")
	private LinkedList<ScoredGene> getScores(LinkedList<HashMap<String, Double>> input_raw, HashSet<String> ids) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		
		ScoredGenes [] input_final = (new DataTransformerCS()).transformAllScores(input_raw);
		CombineScoresBayes algo = new CombineScoresBayes(input_final);
		
		Method m = CombineScoresBayes.class.getDeclaredMethod("calculateScores", HashSet.class);
		m.setAccessible(true);
		LinkedList<ScoredGene> res = (LinkedList<ScoredGene>) m.invoke(algo, ids);
		return res;
	}
}
