package geneticnetwork;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

import geneticnetwork.algorithm.RandomWalkWithRestartFixedIterations;
import geneticnetwork.algorithm.RandomWalkWithRestartUntilConvergence;
import geneticnetwork.datastructures.SparseMatrix;
import geneticnetwork.datastructures.Vector;
import io.FileInputReader;

public class TestRandomWalk {
	
	private SparseMatrix matrix;
	private Vector vector;
	private double [] expected;
	private double norm;
	
	@Test
	public void testCase1() {
		
		prepareDataForCase(1);
		RandomWalkWithRestartFixedIterations r1 = new RandomWalkWithRestartFixedIterations(0.9,3);
		r1.setMatrix(matrix);
		r1.setVector(vector);
		Vector res1= r1.doRandomWalkWithRestart();
		
		assertArrayEquals("Results for test case 1 are incorrect", expected, res1.getData(), 1E-9);
		checkSum(res1.getData());
		assertEquals("Number of iterations for test case 1 is incorrect", 3, r1.getNumberOfIterations());
		assertEquals("Difference to previous step for test case 1 is incorrect", norm, r1.getDifferenceToPrevious(), 1E-10);
	}

	@Test
	public void testCase2() {
		
		prepareDataForCase(2);
		RandomWalkWithRestartFixedIterations r2 = new RandomWalkWithRestartFixedIterations(0.5,1);
		r2.setMatrix(matrix);
		r2.setVector(vector);
		Vector res2= r2.doRandomWalkWithRestart();
		
		assertArrayEquals("Results for test case 2 are incorrect", expected, res2.getData(), 1E-9);
		checkSum(res2.getData());
		assertEquals("Number of iterations for test case 2 is incorrect", 1, r2.getNumberOfIterations());
		assertEquals("Difference to previous step for test case 2 is incorrect", norm, r2.getDifferenceToPrevious(), 1E-10);
	}
	
	@Test
	public void testCase3() {
		
		prepareDataForCase(3);
		RandomWalkWithRestartFixedIterations r3 = new RandomWalkWithRestartFixedIterations(0.2,2);
		r3.setMatrix(matrix);
		r3.setVector(vector);
		Vector res3= r3.doRandomWalkWithRestart();
		
		assertArrayEquals("Results for test case 3 are incorrect", expected, res3.getData(), 1E-9);
		checkSum(res3.getData());
		assertEquals("Number of iterations for test case 3 is incorrect", 2, r3.getNumberOfIterations());
		assertEquals("Difference to previous step for test case 3 is incorrect", norm, r3.getDifferenceToPrevious(), 1E-10);
	}
	
	@Test
	public void testCase4(){
		
		prepareDataForCase(4);
		RandomWalkWithRestartUntilConvergence r4 = new RandomWalkWithRestartUntilConvergence(0.9);
		r4.setMatrix(matrix);
		r4.setVector(vector);
		Vector res4= r4.doRandomWalkWithRestart();
		
		assertArrayEquals("Results for test case 4 are incorrect", expected, res4.getData(), 1E-9);
		checkSum(res4.getData());
		assertEquals("Difference to previous step for test case 4 is incorrect", norm, r4.getDifferenceToPrevious(), 1E-11);
	}
	
	@Test
	public void testCase5(){
		
		prepareDataForCase(5);
		RandomWalkWithRestartUntilConvergence r5 = new RandomWalkWithRestartUntilConvergence(0.5);
		r5.setMatrix(matrix);
		r5.setVector(vector);
		Vector res5= r5.doRandomWalkWithRestart();
		
		assertArrayEquals("Results for test case 5 are incorrect", expected, res5.getData(), 1E-9);
		checkSum(res5.getData());
		assertEquals("Difference to previous step for test case 5 is incorrect", norm, r5.getDifferenceToPrevious(), 1E-11);
	}
	
	@Test
	public void testCase6(){
		
		prepareDataForCase(6);
		RandomWalkWithRestartUntilConvergence r6 = new RandomWalkWithRestartUntilConvergence(0.2);
		r6.setMatrix(matrix);
		r6.setVector(vector);
		Vector res6= r6.doRandomWalkWithRestart();
		
		assertArrayEquals("Results for test case 6 are incorrect", expected, res6.getData(), 1E-9);
		checkSum(res6.getData());
		assertEquals("Difference to previous step for test case 6 is incorrect", norm, r6.getDifferenceToPrevious(), 1E-11);
	}
	
	@Test
	public void testCase1Repeat(){
		
		prepareDataForCase(1);
		RandomWalkWithRestartFixedIterations r = new RandomWalkWithRestartFixedIterations(0.9,3);
		r.setMatrix(matrix);
		r.setVector(vector);
		r.doRandomWalkWithRestart();
		
		prepareDataForCase(1);
		RandomWalkWithRestartFixedIterations r2 = (RandomWalkWithRestartFixedIterations) r.copy();
		r2.setMatrix(matrix);
		r2.setVector(vector);
		Vector resRep = r2.doRandomWalkWithRestart();
		
		assertArrayEquals("Results for test case1 Repeat are incorrect", expected, resRep.getData(), 1E-9);
		checkSum(resRep.getData());
		assertEquals("Difference to previous step for test case1 Repeat is incorrect", norm, r2.getDifferenceToPrevious(), 1E-11);
	}
	
	@Test
	public void testCase6Repeat(){
		
		prepareDataForCase(6);
		RandomWalkWithRestartUntilConvergence r = new RandomWalkWithRestartUntilConvergence(0.2);
		r.setMatrix(matrix);
		r.setVector(vector);
		r.doRandomWalkWithRestart();
		
		prepareDataForCase(6);
		RandomWalkWithRestartUntilConvergence r2 = (RandomWalkWithRestartUntilConvergence) r.copy();
		r2.setMatrix(matrix);
		r2.setVector(vector);
		Vector resRep = r2.doRandomWalkWithRestart();
		
		assertArrayEquals("Results for test case6 Repeat are incorrect", expected, resRep.getData(), 1E-9);
		checkSum(resRep.getData());
		assertEquals("Difference to previous step for test case6 Repeat is incorrect", norm, r2.getDifferenceToPrevious(), 1E-11);
	}
	
	private void checkSum(double [] data){
		double sum=0;
		for(double d:data){
			sum+=d;
		}
		assertEquals("Result is not a valid probability distribution!", 1, sum, 1E-10); 
	}
	
	//cases 4-6 identical to 1-3, but 4-6 are run until convergence
	private void prepareDataForCase(int testCase){
		
		//read matrix
		String fileMatrix="";
		if(testCase==1||testCase==4){
			fileMatrix="../TestData/GeneticNetwork/matrices/matrix_unweighted_PTG1.txt";
		}
		else if(testCase==2||testCase==5){
			fileMatrix="../TestData/GeneticNetwork/matrices/matrix_weighted_PTG1.txt";
		}
		else if(testCase==3||testCase==6){
			fileMatrix="../TestData/GeneticNetwork/matrices/matrix_weighted_PTG2.txt";
		}
		String[] matrixLines = FileInputReader.readAllLinesFrom(fileMatrix).toArray(new String[0]);
		matrix = new SparseMatrix( matrixLines.length, matrixLines.length , matrixLines.length*matrixLines.length);
		for(int i=0; i<matrixLines.length; i++){
			for(int j=0; j<matrixLines.length; j++){
				matrix.addEntry(i, j, Double.parseDouble(matrixLines[i].split("\t")[j+1]));
			}
		}
		
		//read restart vector
		String fileRestart="";
		if(testCase==1|| testCase==2 || testCase==4 || testCase==5){
			fileRestart="../TestData/GeneticNetwork/restartVectors/restartVec_PTG1.txt";
		}
		else if(testCase==3||testCase==6){
			fileRestart="../TestData/GeneticNetwork/restartVectors/restartVec_PTG2.txt";
		}
		LinkedList<String> resVectorLines = FileInputReader.readAllLinesFrom(fileRestart);
		int pos=0;
		double [] vectorData = new double [resVectorLines.size()];
		for(String line:resVectorLines){
			vectorData[pos++]=Double.parseDouble(line.split("\t")[1]);
		}
		vector = new Vector(vectorData);
		
		//read expected result
		LinkedList<String> resFromFile = FileInputReader.readAllLinesFrom(
				"../TestData/GeneticNetwork/ExpectedRWWR/res"+testCase+".txt");
		expected = new double [resFromFile.size()];
		pos=0;
		for(String line:resFromFile){
			expected[pos++]=Double.parseDouble(line);
		}
		
		//read norm
		if(testCase<=3){
			LinkedList<String> normFromFile= FileInputReader.readAllLinesFrom(
					"../TestData/GeneticNetwork/maxnorm/norm"+testCase+".txt");
			norm=Double.parseDouble(normFromFile.peek());
		}
		else{
			norm=1E-11;
		}
	}

}
