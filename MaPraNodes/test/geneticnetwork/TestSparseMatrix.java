package geneticnetwork;

import static org.junit.Assert.*;

import org.junit.Test;

import geneticnetwork.datastructures.SparseMatrix;
import geneticnetwork.datastructures.Vector;

public class TestSparseMatrix {

	@Test
	public void testMatrixRepresentation() {
		
		double [][] matrix_final = {{1,2,3},{-1,-2,-3}};
		SparseMatrix mObj = new SparseMatrix(2, 3, 6);
		double[][] cur_matrix = new double[2][3];
		
		mObj.addEntry(1, -1, 1);
		assertArrayEquals("Incorrect column coordinate should not be added ", cur_matrix, mObj.getData());
		mObj.addEntry(5, 1, 1);
		assertArrayEquals("Incorrect row coordinate should not be added ", cur_matrix, mObj.getData());
		
		assertArrayEquals("Empty matrix is incorrect",cur_matrix, mObj.getData());
		for(int i=0; i<matrix_final.length; i++){
			for(int j=0; j<matrix_final[i].length; j++){
				mObj.addEntry(i, j, matrix_final[i][j]);
				cur_matrix[i][j]=matrix_final[i][j];
				assertArrayEquals("Matrix after filling row "+i+" column "+j+" is incorrect",
						cur_matrix, mObj.getData());
			}
		}
		
		mObj.addEntry(1, 1, 1);
		assertArrayEquals("Full matrix should not be changed ", cur_matrix, mObj.getData());
	}
	
	@Test
	public void testMultiplicationCase1() {
		
		double[][] input_matrix = new double [][] {{1,0,0},{0,1,0},{0,0,1}};
		double[] input_vector = new double []{1,2,3};
		
		checkEverything(3, input_matrix, input_vector, input_vector);
	}
	
	@Test
	public void testMultiplicationCase2() {
		
		double[][] input_matrix = new double [][] {{1,-1,1},{-1,-1,-1},{2,-2,2}};
		double[] input_vector = new double []{0.5,0.1,-1};
		double[] expected= new double[]{-0.6,0.4,-1.2};
		
		checkEverything(9, input_matrix, input_vector, expected);
	}
	
	@Test
	public void testMultiplicationCase3() {
		
		double[][] input_matrix = new double [][] {{1,2},{3,4},{5,6}};
		double[] input_vector = new double []{10,1};
		double[] expected= new double[]{12,34,56};
		
		checkEverything(6, input_matrix, input_vector, expected);
	}
	
	
	private void checkEverything(int entries, double [][] matrix, double [] vector, double [] expected){
		
		SparseMatrix m = new SparseMatrix(matrix.length, matrix[0].length, entries);
		for(int i=0; i<matrix.length; i++){
			for(int j=0; j<matrix[i].length;j++){
				if(Math.abs(matrix[i][j])>1E-15){
					m.addEntry(i, j, matrix[i][j]);
				}
			}
		}
		
		assertArrayEquals("Matrix is parsed correctly", matrix, m.getData());
		Vector v = new Vector(vector);
		Vector res = m.multiply(v);
		assertArrayEquals("Result of multiplication is incorrect", expected, res.getData(), 1E-10);
	}
	

}
