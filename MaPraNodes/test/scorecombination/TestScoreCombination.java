package scorecombination;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

import io.FileInputReader;
import scorecombination.algo.CombineScoresDriver;
import scorecombination.io.FileUtilitiesCS;
import togeno.ScoredGene;

public class TestScoreCombination {
	
	private String [] ids_expected;
	private double [] scores_expected;
	private CombineScoresDriver driver;
	
	@Test
	public void testCase1() {
		prepareForCase(1);
		//pheno + metabo
		driver.addInput(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputPheno.txt"));
		driver.addInput(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputMetabo.txt"));
		checkResult();
	}
	
	@Test
	public void testCase2() {
		prepareForCase(2);
		//pheno + geno
		driver.addInput(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputPheno.txt"));
		driver.addInput(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputGeno.txt"));
		checkResult();
	}
	
	@Test
	public void testCase3() {
		prepareForCase(3);
		//pheno + geno + metabo
		driver.addInput(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputPheno.txt"));
		driver.addInput(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputGeno.txt"));
		driver.addInput(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputMetabo.txt"));
		checkResult();
	}
	
	@Test
	public void testCase4(){
		prepareForCase(4);
		//geno
		driver.addInput(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputGeno.txt"));
		checkResult();
	}
	
	@Test
	public void testCase5(){
		prepareForCase(5);
		//5*geno
		driver.addInput(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputGeno.txt"));
		driver.addInput(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputGeno.txt"));
		driver.addInput(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputGeno.txt"));
		driver.addInput(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputGeno.txt"));
		driver.addInput(FileUtilitiesCS.readScoresFromFile("../TestData/CombineScores/InputGeno.txt"));
		checkResult();
	}
	
	@Test
	public void testCase6() {
		prepareForCase(6);
		//empty input
		checkResult();
	}
	
	private void prepareForCase(int number){
		
		driver = new CombineScoresDriver();
		
		if(number==6){
			ids_expected= new String[0];
			scores_expected = new double[0];
		}
		else{
			LinkedList<String> lines = FileInputReader.readAllLinesFrom(
					"../TestData/CombineScores/ExpectedRes/resCase"+number+".txt");
			//remove header
			lines.remove(0);
			
			ids_expected= new String[lines.size()];
			scores_expected = new double[lines.size()];
			int counter=0;
			for(String line:lines){
				String[] split=line.split("\t");
				ids_expected[counter]=split[0];
				scores_expected[counter]=Double.parseDouble(split[1]);
				counter++;
			}
		}
	}
	
	private void checkResult(){
		
		LinkedList<ScoredGene> res = driver.runCombineScores();
		assertEquals("Result size is incorrect", ids_expected.length, res.size());
		int position=0;
		for(ScoredGene g: res){
			assertEquals("Id at position "+(position+1)+" is incorrect", ids_expected[position], g.getId());
			assertEquals("Score at position "+(position+1)+" is incorrect", scores_expected[position], g.getScore(), 1E-8);
			position++;
		}
	}

}
