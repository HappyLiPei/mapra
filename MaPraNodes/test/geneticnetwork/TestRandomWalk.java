package geneticnetwork;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.junit.Test;

import geneticnetwork.algorithm.RandomWalkWithRestart;
import io.FileInputReader;

public class TestRandomWalk {
	
	private OpenMapRealMatrix matrix;
	private ArrayRealVector vector;
	private double [] expected;
	
	@Test
	public void testCase1() {
		
		prepareDataForCase(1);
		RandomWalkWithRestart r1 = new RandomWalkWithRestart(3, 0.9);
		r1.setMatrix(matrix);
		r1.setVector(vector);
		ArrayRealVector res1= r1.doRandomWalkWithRestart();
		
		assertArrayEquals("Results for test case 1 are incorrect", expected, res1.getDataRef(), 1E-9);
		checkSum(res1.getDataRef());
	}

	@Test
	public void testCase2() {
		
		prepareDataForCase(2);
		RandomWalkWithRestart r2 = new RandomWalkWithRestart(1, 0.5);
		r2.setMatrix(matrix);
		r2.setVector(vector);
		ArrayRealVector res2= r2.doRandomWalkWithRestart();
		
		assertArrayEquals("Results for test case 2 are incorrect", expected, res2.getDataRef(), 1E-9);
		checkSum(res2.getDataRef());
		
	}
	
	@Test
	public void testCase3() {
		
		prepareDataForCase(3);
		RandomWalkWithRestart r3 = new RandomWalkWithRestart(2, 0.2);
		r3.setMatrix(matrix);
		r3.setVector(vector);
		ArrayRealVector res3= r3.doRandomWalkWithRestart();
		
		assertArrayEquals("Results for test case 3 are incorrect", expected, res3.getDataRef(), 1E-9);
		checkSum(res3.getDataRef());
	}
	
	private void checkSum(double [] data){
		double sum=0;
		for(double d:data){
			sum+=d;
		}
		assertEquals("Result is not a valid probability distribution!", 1, sum, 1E-10); 
	}
	
	private void prepareDataForCase(int testCase){
		
		//read matrix
		String fileMatrix="";
		if(testCase==1){
			fileMatrix="../TestData/GeneticNetwork/matrices/matrix_unweighted_PTG1.txt";
		}
		else if(testCase==2){
			fileMatrix="../TestData/GeneticNetwork/matrices/matrix_weighted_PTG1.txt";
		}
		else if(testCase==3){
			fileMatrix="../TestData/GeneticNetwork/matrices/matrix_weighted_PTG2.txt";
		}
		String[] matrixLines = FileInputReader.readAllLinesFrom(fileMatrix).toArray(new String[0]);
		matrix = new OpenMapRealMatrix( matrixLines.length,matrixLines.length );
		for(int i=0; i<matrixLines.length; i++){
			for(int j=0; j<matrixLines.length; j++){
				matrix.setEntry(i, j, Double.parseDouble(matrixLines[i].split("\t")[j+1]));
			}
		}
		
		//read restart vector
		String fileRestart="";
		if(testCase==1|| testCase==2){
			fileRestart="../TestData/GeneticNetwork/restartVectors/restartVec_PTG1.txt";
		}
		else if(testCase==3){
			fileRestart="../TestData/GeneticNetwork/restartVectors/restartVec_PTG2.txt";
		}
		LinkedList<String> resVectorLines = FileInputReader.readAllLinesFrom(fileRestart);
		int pos=0;
		double [] vectorData = new double [resVectorLines.size()];
		for(String line:resVectorLines){
			vectorData[pos++]=Double.parseDouble(line.split("\t")[1]);
		}
		vector = new ArrayRealVector(vectorData);
		
		//read expected result
		LinkedList<String> resFromFile = FileInputReader.readAllLinesFrom(
				"../TestData/GeneticNetwork/ExpectedRWWR/res"+testCase+".txt");
		expected = new double [resFromFile.size()];
		pos=0;
		for(String line:resFromFile){
			expected[pos++]=Double.parseDouble(line);
		}
	}

}
