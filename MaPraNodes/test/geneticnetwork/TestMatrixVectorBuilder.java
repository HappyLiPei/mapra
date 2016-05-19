package geneticnetwork;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

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
	private ScoredGenes inputPTG;
	private double [] restartVec;
	private double [][] matrix;
	
	@Test
	public void runTestCase1(){
		readEdgesAndScores(false, 1);
		MatrixVectorBuilder mvb = new MatrixVectorBuilder(edges, inputPTG);
		checkPositionMap(mvb,20);
		checkRestartVector(mvb);
		checkMatrix(mvb);
	}
	
	@Test
	public void runTestCase2(){
		readEdgesAndScores(true, 1);
		MatrixVectorBuilder mvb = new MatrixVectorBuilder(edges, inputPTG);
		checkPositionMap(mvb,20);
		checkRestartVector(mvb);
		checkMatrix(mvb);
	}
	
	@Test
	public void runTestCase3(){
		readEdgesAndScores(true, 2);
		MatrixVectorBuilder mvb = new MatrixVectorBuilder(edges, inputPTG);
		checkPositionMap(mvb,ids.length);
		checkRestartVector(mvb);
		checkMatrix(mvb);
	}
	
	@Test
	public void runTestCaseRepeat(){
		readEdgesAndScores(true, 2);
		MatrixVectorBuilder mvb1 = new MatrixVectorBuilder(edges, inputPTG);
		checkPositionMap(mvb1,ids.length);
		checkRestartVector(mvb1);
		checkMatrix(mvb1);
		readEdgesAndScores(true, 3);
		MatrixVectorBuilder mvb2 = new MatrixVectorBuilder(inputPTG, mvb1);
		checkPositionMap(mvb2,ids.length);
		checkRestartVector(mvb2);
		checkMatrix(mvb2);
	}
	
	private void readEdgesAndScores(boolean weight, int input){
		
		//input data
		DataTransformerGeneticNetwork dt = new DataTransformerGeneticNetwork();
		String[][] edgesFromFile= FileUtilitiesGeneticNetwork.readEdges(
				"../TestData/GeneticNetwork/MTGNetwork.txt", weight);
		edges = dt.transformEdges(edgesFromFile);
		HashMap<String, Double> scoresFromFile = FileUtilitiesGeneticNetwork.readGeneScoresFrom(
				"../TestData/GeneticNetwork/Input_PTG"+input+".txt");
		inputPTG = dt.transformGeneScores(scoresFromFile);		
		
		//expected results
		ids = FileInputReader.readAllLinesFrom(
				"../TestData/GeneticNetwork/PositionMapping.txt").toArray(new String[0]);
		LinkedList<String> restartFile=FileInputReader.readAllLinesFrom(
				"../TestData/GeneticNetwork/restartVectors/restartVec_PTG"+input+".txt");
		restartVec = new double [restartFile.size()];
		int position=0;
		for(String line:restartFile){
			restartVec[position++]=Double.parseDouble(line.split("\t")[1]);
		}
		String w="weighted";
		if(!weight){
			w="un"+w;
		}
		
		LinkedList<String> matrixFile = FileInputReader.readAllLinesFrom(
				"../TestData/GeneticNetwork/matrices/matrix_"+w+"_PTG"+input+".txt");
		matrix = new double [matrixFile.size()][matrixFile.size()];
		position=0;
		for(String line:matrixFile){
			String [] split = line.split("\t");
			for(int i=1;i<split.length; i++){
				matrix[position][i-1]=Double.parseDouble(split[i]);
			}
			position++;
		}
	}
	
	private void checkPositionMap(MatrixVectorBuilder mvb, int Idpos){
		assertEquals("Critical Position is incorrect", 20, mvb.getCriticalPosition());
		HashMap<String, Integer> posMap = mvb.getIdPositionMap();
		for(int i=0; i<Idpos; i++){
			assertEquals("Position of id "+ids[i]+" is incorrect", i, (int) posMap.get(ids[i])); 
		}
	}
	
	private void checkRestartVector(MatrixVectorBuilder mvb){
		assertArrayEquals("Restart vector is incorrect", restartVec ,mvb.getRestartVector().getData(), 1E-10);
	}
	
	private void checkMatrix(MatrixVectorBuilder mvb){
		assertArrayEquals("Transition matrix is incorrect", matrix, mvb.getStochasticMatrix().getData());
	} 

}
