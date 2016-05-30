package geneticnetwork;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import geneticnetwork.algorithm.DataTransformerGeneticNetwork;
import geneticnetwork.datastructures.ScoredGenes;
import geneticnetwork.io.FileUtilitiesGeneticNetwork;
import io.FileInputReader;
import togeno.ScoredGene;

public class TestScoredGenes {

	@Test
	public void testScoredGenesFromFile() {
		
		HashMap<String, Double> mapping = 
				FileUtilitiesGeneticNetwork.readGeneScoresFrom("../TestData/PhenoToGeno/ExpectedResults/expRes_2.txt");
		ScoredGenes toTest = (new DataTransformerGeneticNetwork()).transformGeneScores(mapping);
		checkDataStructure(toTest);
	}
	
	@Test
	public void testScoredGenesFromList(){
		
		LinkedList<String> scoresFromFile=FileInputReader.readAllLinesFrom(
				"../TestData/PhenoToGeno/ExpectedResults/expRes_2.txt");
		scoresFromFile.remove(0);
		
		LinkedList<ScoredGene> list = new LinkedList<ScoredGene>();
		for(String line: scoresFromFile){
			String [] split = line.split("\t");
			ScoredGene entry = new ScoredGene(split[0], Double.parseDouble(split[1]), "");
			list.add(entry);
		}
		
		ScoredGenes toTest = (new DataTransformerGeneticNetwork()).transformGeneScoresFromAlgo(list);
		checkDataStructure(toTest);
	}
	
	private void checkDataStructure(ScoredGenes toTest){
		
		double [] expectedScores = new double []{0.0, 0.0, 0.08333, 0.0, 0.0, 0.0, 0.0, 0.0, 0.08333, 0.08333,
				0.0, 0.08333, 0.0, 0.0, 0.22975, 0.08333, 0.15972, 0.08333, 0.08333, 0.0, 
				0.15972, 0.08333, 0.08333, 0.0,	0.08333};
		String [] geneIds = new String[50];
		
		//test score retieval
		for(int i=1; i<=50; i++){
			String id="MTG"+i;
			if(i<=expectedScores.length){
				assertEquals("Score of gene "+id+" is incorrect", expectedScores[i-1], toTest.getScoreof(id), 1E-10);
			}
			else{
				assertEquals("Score of gene "+id+" is incorrect", 0.0, toTest.getScoreof(id), 1E-10);
			}
			geneIds[i-1]=id;
		}
		assertTrue("Score of gene MTG100 (not part of object) is incorrect", Double.isNaN(toTest.getScoreof("MTG100")));
		
		//test array of gene ids -> sort arrays to compare because order of ids is not important
		String [] ids = toTest.getAllScoredGenes();
		Arrays.sort(ids);
		Arrays.sort(geneIds);
		assertArrayEquals("Array of all gene ids is incorrect", geneIds, ids);
	}

}
