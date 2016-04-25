package geneticnetwork;
import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import geneticnetwork.io.FileUtilitiesGeneticNetwork;

public class TestFileParserNetworkScore {

	@Test
	public void testPTGReader() {
		
		HashMap<String, Double> mapping = 
				FileUtilitiesGeneticNetwork.readGeneScoresFrom("../TestData/PhenoToGeno/ExpectedResults/expRes_2.txt");
		
		double [] expectedScores = new double []{0.0, 0.0, 0.08333, 0.0, 0.0, 0.0, 0.0, 0.0, 0.08333, 0.08333,
				0.0, 0.08333, 0.0, 0.0, 0.22975, 0.08333, 0.15972, 0.08333, 0.08333, 0.0, 
				0.15972, 0.08333, 0.08333, 0.0,	0.08333};
		
		assertEquals("PTG result file is not parsed completely: Incorrect number of genes", 50, mapping.size());
		
		for(int i=1; i<=50; i++){
			assertTrue("Gene MTG"+i+" is missing", mapping.containsKey("MTG"+i));
			if(i-1<expectedScores.length){
				assertEquals("Score for gene MTG"+i+" is incorrect", expectedScores[i-1], mapping.get("MTG"+i), 1E-10);
			}
			else{
				assertEquals("Score for gene MTG"+i+" is incorrect", 0.0, mapping.get("MTG"+i), 1E-10);
			}
		}	
	}
	
	@Test
	public void testNetworkReader() {
		
		String[][] edgesUnweighted = FileUtilitiesGeneticNetwork.readEdges(
				"../TestData/GeneticNetwork/MTGNetwork.txt", false);
		checkTopology(edgesUnweighted, "unweighted");
		
		String[][] edgesWeighted = FileUtilitiesGeneticNetwork.readEdges(
				"../TestData/GeneticNetwork/MTGNetwork.txt", true);
		checkTopology(edgesWeighted, "weighted");
		
		String [] weights = new String []{"2", "2", "2", "2", "2", "2", "4", "4", "1", "1", "1", "1",
				"3", "2", "7", "6", "3", "1", "7", "4"};
		for(int i=0; i<weights.length; i++){
			assertEquals("Edge "+(i+1)+" has wrong weight", weights[i], edgesWeighted[i][2]);
		}
		
	}
	
	private void checkTopology(String[][] edges, String mode){
		
		String[] node1 = new String []{"MTG1", "MTG1", "MTG1", "MTG1", "MTG2", "MTG5", "MTG9", "MTG9",
				"MTG10", "MTG10", "MTG10", "MTG10", "MTG11", "MTG11", "MTG12", "MTG13", "MTG17", "MTG18", "MTG19",
				"MTG60"};
		String[] node2 = new String []{"MTG2", "MTG3", "MTG4", "MTG5", "MTG3", "MTG6", "MTG10", "MTG11", "MTG11",
				"MTG14", "MTG15", "MTG16", "MTG12", "MTG13", "MTG17", "MTG60", "MTG18", "MTG61", "MTG20", "MTG61"};
		
		assertEquals("Number of edges in "+mode+" network is incorrect", node1.length, edges.length);
		for(int i=0; i<node1.length; i++){
			assertEquals("Edge "+(i+1)+" node 1 of "+mode+" network is incorrect", node1[i], edges[i][0]);
			assertEquals("Edge "+(i+1)+" node 2 of "+mode+" network is incorrect", node2[i], edges[i][1]);
		}
	}

}
