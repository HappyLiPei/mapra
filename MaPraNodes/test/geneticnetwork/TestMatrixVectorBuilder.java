package geneticnetwork;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.junit.Before;
import org.junit.Test;

import geneticnetwork.algorithm.DataTransformerGeneticNetwork;
import geneticnetwork.algorithm.MatrixVectorBuilder;
import geneticnetwork.datastructures.Edge;
import geneticnetwork.datastructures.ScoredGenes;
import geneticnetwork.io.FileUtilitiesGeneticNetwork;
import io.FileInputReader;

public class TestMatrixVectorBuilder {
	
	private String [] ids;
	private Edge [] edges;
	private ScoredGenes scores1;
	private ScoredGenes scores2;
	private ScoredGenes scores3;
	
	//method to read network
	private void readEdges(boolean weight){
		String[][] edgesFromFile= FileUtilitiesGeneticNetwork.readEdges(
				"../TestData/GeneticNetwork/MTGNetwork.txt", weight);
		edges = (new DataTransformerGeneticNetwork()).transformEdges(edgesFromFile);	
	}
	
	@Before
	public void prepareMappings(){
		
		ids = FileInputReader.readAllLinesFrom("../TestData/GeneticNetwork/PositionMapping.txt").toArray(new String[52]);
		
		DataTransformerGeneticNetwork dt = new DataTransformerGeneticNetwork();
		
		//test mixture: singletons + network
		HashMap<String, Double> mapping1 = 
				FileUtilitiesGeneticNetwork.readGeneScoresFrom(
						"../TestData/PhenoToGeno/ExpectedResults/expRes_1.txt");
		scores1 = dt.transformGeneScores(mapping1);
		
		//test network only
		HashMap<String, Double> mapping2 = new HashMap<String, Double>(10);
		mapping2.put("MTG10", 0.02);
		mapping2.put("MTG11", 0.02);
		mapping2.put("MTG12", 0.02);
		scores2 = dt.transformGeneScores(mapping2);
		
		//test singletons only
		HashMap<String, Double> mapping3 = new HashMap<String, Double>(10);
		mapping3.put("MTG30", 0.02);
		scores3 = dt.transformGeneScores(mapping3);
	}
	
	@Test
	public void testMatrix(){
		
		for(boolean weighted: new boolean[] {false,true}){
			
			String mode ="weighted";
			if(!weighted){
				mode ="un"+mode;
			}
			
			readEdges(weighted);
			MatrixVectorBuilder mvb = new MatrixVectorBuilder(edges, scores1);
			OpenMapRealMatrix act_matrix = mvb.getStochasticMatrix();
			assertEquals("Matrix row dimension is incorrect ("+mode+")", 52, act_matrix.getRowDimension());
			assertEquals("Matrix column dimension is incorrect ("+mode+")", 52, act_matrix.getColumnDimension());
			String[] exp_matrix=FileInputReader.readAllLinesFrom(
					"../TestData/GeneticNetwork/matrix_"+mode+".txt").toArray(new String[20]);
			
			for(int row =0; row<52; row++){
				for(int column=0; column<52; column++){
					// in file
					if(row<20 && column<20){
						assertEquals("Entry in row "+row+", column "+column+" is incorrect ("+mode+")",
								Double.parseDouble(exp_matrix[row].split("\t")[column+1]),
								act_matrix.getEntry(row, column), 1E-10);
					}
					// singletons
					else{
						if(row==column){
							assertEquals("Entry in row "+row+", column "+column+" is incorrect ("+mode+")",
									1.0, act_matrix.getEntry(row, column), 1E-10);
						}
						else{
							assertEquals("Entry in row "+row+", column "+column+" is incorrect ("+mode+")",
									0.0, act_matrix.getEntry(row, column), 1E-10);
						}
					}
				}
			}
		}	
	}
	
	@Test
	public void testRestartVector(){
		
		readEdges(true);
		
		//sum: 25*0.00429+4*0.08727+3*0.09564+2*0.1059+0.13531+0.17101+0.18792+4*0.18868+0.22354+
		//0.24009+3*0.25629+2*0.90133+0.92698+0.94306 = 7.10921
		double sum_score=7.10921;
		double [] exp1={0.08727, 0.08727, 0.24009, 0.25629, 0.18868, 0.18868, 0.09564, 0.09564,0.08727, 0.25629,
				0.94306, 0.1059, 0.17101, 0.08727, 0.18792, 0d,0.22354, 0d, 0.1059, 0.13531,
				0.00429, 0.00429, 0.00429, 0.00429, 0.00429, 0.00429, 0.00429, 0.00429, 0.00429, 0.00429,
				0.00429, 0.00429, 0.00429, 0.00429, 0.18868, 0.00429, 0.00429, 0.00429, 0.00429, 0.25629,
				0.00429, 0.00429, 0.00429, 0.00429, 0.90133, 0.90133, 0.18868, 0.09564, 0.00429, 0.00429,
				0.00429, 0.92698};
		for(int i=0; i<exp1.length; i++){
			exp1[i]=exp1[i]/sum_score;
		}
		MatrixVectorBuilder mvb = new MatrixVectorBuilder(edges, scores1);
		ArrayRealVector v1 = mvb.getRestartVector();
		testSum(v1, "Restart vector (mapping 1) is not stochastic");
		assertArrayEquals("Restart vector (mapping 1) is not correct", exp1, v1.toArray(), 1E-10);
		
		mvb = new MatrixVectorBuilder(edges, scores2);
		ArrayRealVector v2 = mvb.getRestartVector();
		testSum(v2, "Restart vector (mapping 2) is not stochastic");
		double[] exp2 = new double[20];
		exp2[7]=exp2[8]=exp2[12]=1d/3d;
		assertArrayEquals("Restart vector (mapping 2) is not correct", exp2, v2.toArray(), 1E-10);
		
		mvb = new MatrixVectorBuilder(edges, scores3);
		ArrayRealVector v3 = mvb.getRestartVector();
		testSum(v3, "Restart vector (mapping 3) is not stochastic");
		double[] exp3 = new double[21];
		exp3[20]=1;
		assertArrayEquals("Restart vector (mapping 3) is not correct", exp3, v3.toArray(), 1E-10);
	}
	
	private void testSum(ArrayRealVector v, String message){
		double sum=0;
		for(double d :v.toArray()){
			sum+=d;
		}
		assertEquals("Restart vector (mapping 1) is not stochastic", 1.0, sum, 1E-10);
	}
	
	@Test
	public void testPositionMap() {
		
		readEdges(true);
		
		MatrixVectorBuilder mvb = new MatrixVectorBuilder(edges, scores1);
		int cp = mvb.getCriticalPosition();
		assertEquals("Critical Position (mapping 1) is incorrect", 20, cp);
		HashMap<String, Integer> posMap = mvb.getIdPositionMap();
		assertEquals("Size of position map (mapping 1) is incorrect",ids.length, posMap.size());
		for(int p=0; p<ids.length; p++){
			assertEquals("Position of gene "+ids[p]+" (mapping 1) is incorrect", p, (int) posMap.get(ids[p]));
		}
		
		mvb = new MatrixVectorBuilder(edges, scores2);
		assertEquals("CriticalPosition (mapping 2) is incorrect", 20, mvb.getCriticalPosition());
		posMap = mvb.getIdPositionMap();
		assertEquals("Size of position map (mapping 2) is incorrect",20, posMap.size());
		for(int p=0; p<20; p++){
			assertEquals("Position of gene "+ids[p]+" (mapping 2) is incorrect", p, (int) posMap.get(ids[p]));
		}
		
		mvb = new MatrixVectorBuilder(edges, scores3);
		assertEquals("CriticalPosition (mapping 3) is incorrect", 20, mvb.getCriticalPosition());
		posMap = mvb.getIdPositionMap();
		assertEquals("Size of position map (mapping 3) is incorrect",21, posMap.size());
		for(int p=0; p<20; p++){
			assertEquals("Position of gene "+ids[p]+" (mapping 3) is incorrect", p, (int) posMap.get(ids[p]));
		}
		assertEquals("Position of gene MTG30 (mapping 3) is incorrect", 20, (int) posMap.get("MTG30") );
		
	}

}
