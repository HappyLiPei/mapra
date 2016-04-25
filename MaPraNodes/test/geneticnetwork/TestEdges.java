package geneticnetwork;

import static org.junit.Assert.*;

import org.junit.Test;

import geneticnetwork.algorithm.DataTransformerGeneticNetwork;
import geneticnetwork.datastructures.Edge;
import geneticnetwork.datastructures.UnweightedEdge;
import geneticnetwork.datastructures.WeightedEdge;
import geneticnetwork.io.FileUtilitiesGeneticNetwork;

public class TestEdges {
	
	private String[] node1 = new String []{"MTG1", "MTG1", "MTG1", "MTG1", "MTG2", "MTG5", "MTG9", "MTG9",
			"MTG10", "MTG10", "MTG10", "MTG10", "MTG11", "MTG11", "MTG12", "MTG13", "MTG17", "MTG18", "MTG19",
			"MTG60"};
	private String[] node2 = new String []{"MTG2", "MTG3", "MTG4", "MTG5", "MTG3", "MTG6", "MTG10", "MTG11", "MTG11",
			"MTG14", "MTG15", "MTG16", "MTG12", "MTG13", "MTG17", "MTG60", "MTG18", "MTG61", "MTG20", "MTG61"};
	private int [] weights = new int []{2, 2, 2, 2, 2, 2, 4, 4, 1, 1, 1, 1, 3, 2, 7, 6, 3, 1, 7, 4};
	
	private Edge [] array;
	
	private void prepareTest(boolean weighted, boolean robust){
		String[][] edgesFromFile = FileUtilitiesGeneticNetwork.readEdges(
				"../TestData/GeneticNetwork/MTGNetwork.txt", weighted);
		
		//add duplicate egdes and edges into different direction (undirected) -> should be removed by data transfomer
		if(robust){
			String [][] edgesOld = edgesFromFile;
			edgesFromFile = new String[edgesOld.length*3][];
			for(int i=0; i<edgesOld.length; i++){
				edgesFromFile[i]=edgesOld[i];
				edgesFromFile[i+edgesOld.length]= edgesOld[i];
				if(!weighted){
					edgesFromFile[i+2*edgesOld.length]= new String[]{ edgesOld[i][1], edgesOld[i][0] };
				}
				else{
					edgesFromFile[i+2*edgesOld.length]= new String[]{ edgesOld[i][1], edgesOld[i][0], edgesOld[i][2] };
				}
			}
		}
		
		array = (new DataTransformerGeneticNetwork()).transformEdges(edgesFromFile);
	}

	@Test
	public void testEdgeRepresentationUnweighted() {
		
		for(boolean mode : new boolean[]{false, true}){
			prepareTest(false, mode);
			String modeName="";
			if(mode){
				modeName=" (robust)";
			}
			assertEquals("Number of edges is incorrect"+modeName, node1.length, array.length);
			for(int i=0; i<node1.length;i++){
				assertTrue("Edge type of egde "+i+" is incorrect"+modeName, array[i] instanceof UnweightedEdge);
				assertEquals("Weight of egde "+i+" is incorrect"+modeName,1 , array[i].getWeight());
				assertEquals("First node of edge "+i+" is incorrect"+modeName, node1[i], array[i].getStartNode());
				assertEquals("Second node of edge "+i+" is incorrect"+modeName, node2[i], array[i].getEndNode());
			}
		}
	}
	
	@Test
	public void testEdgeRepresentationWeighted() {
		
		for(boolean mode : new boolean[]{false, true}){
			prepareTest(true, mode);
			String modeName="";
			if(mode){
				modeName=" (robust)";
			}
			assertEquals("Number of edges is incorrect"+modeName, node1.length, array.length);
			for(int i=0; i<node1.length;i++){
				assertTrue("Edge type of egde "+i+" is incorrect"+modeName, array[i] instanceof WeightedEdge);
				assertEquals("Weight of egde "+i+" is incorrect"+modeName, weights[i] , array[i].getWeight());
				assertEquals("First node of edge "+i+" is incorrect"+modeName, node1[i], array[i].getStartNode());
				assertEquals("Second node of edge "+i+" is incorrect"+modeName, node2[i], array[i].getEndNode());
			}
		}
	}

}
