package geneticnetwork;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import geneticnetwork.datastructures.Vector;

public class TestVector {
	
	private Vector [] vectorobjects;
	private double [][] vectors =  {{1, 5, 3,-1},{0.01, 0.02, -0.01, -0.02},{-0.5,-1.5,-0.1, -0.2}, {1}};

	@Before
	public void prepareVectors(){
		vectorobjects = new Vector[vectors.length];
		for(int i=0; i<vectors.length; i++){
			vectorobjects[i]= new Vector(vectors[i].length);
			for(int j=0; j<vectors[i].length; j++){
				vectorobjects[i].setEntry(j, vectors[i][j]);
			}
		}
	}
	
	@Test
	public void testGetter(){
		//getLength
		for(int i=0; i<vectorobjects.length; i++){
			assertEquals("Vector "+(i+1)+" length is incorrect", vectors[i].length, vectorobjects[i].getLength());
		}
		//getEntry
		for(int i=0; i<vectors[0].length; i++){
			assertEquals("Entry "+(i+1)+" of vector 1 is incorrect",
					vectors[0][i], vectorobjects[0].getEntry(i), 1E-10); 
		}
		//special cases
		assertTrue("Access at position -1 is not handled correctly", Double.isNaN(vectorobjects[0].getEntry(-1)));
		assertTrue("Access at position 10 is not handled correctly", Double.isNaN(vectorobjects[0].getEntry(10)));
	}
	
	@Test
	public void testMultiplication(){
		//-v1
		Vector res = vectorobjects[0].multiplyScalar(-1);
		assertArrayEquals("Multiplication -vector1 is incorrect", 
				new double []{-1, -5, -3, 1}, res.getData(), 1E-10); 
		//2v2
		res = vectorobjects[1].multiplyScalar(2);
		assertArrayEquals("Multiplication 2vector2 is incorrect", 
				new double []{0.02, 0.04, -0.02, -0.04}, res.getData(), 1E-10); 
		//0.1v3
		res = vectorobjects[2].multiplyScalar(0.1);
		assertArrayEquals("Multiplication 0.1vector3 is incorrect", 
				new double []{-0.05, -0.15,-0.01,-0.02}, res.getData(), 1E-10); 
		//10v3
		res = vectorobjects[2].multiplyScalar(10);
		assertArrayEquals("Multiplication 10vector3 is incorrect", 
				new double []{-5, -15,-1,-2}, res.getData(), 1E-10); 
		//inplace 10v4
		vectorobjects[3].multiplyScalarInPlace(10);
		assertArrayEquals("Multiplication 10vector4 is incorrect", 
				new double []{10}, vectorobjects[3].getData(), 1E-10); 
	}
	
	@Test
	public void testAddition() {
		//v1+v2
		Vector res = vectorobjects[0].addVector(vectorobjects[1]);
		assertArrayEquals("Addtion of vector 1 and 2 is incorrect", 
				new double []{1.01, 5.02, 2.99, -1.02}, res.getData(), 1E-10); 
		//v1+v3
		res = vectorobjects[0].addVector(vectorobjects[2]);
		assertArrayEquals("Addition of vector 1 and 3 is incorrect", 
				new double []{0.5, 3.5, 2.9, -1.2}, res.getData(), 1E-10);
		//v2+v3
		res = vectorobjects[1].addVector(vectorobjects[2]);
		assertArrayEquals("Addition of vector 2 and 3 is incorrect", 
				new double []{-0.49, -1.48, -0.11, -0.22}, res.getData(), 1E-10); 
		//v1+v4 -> dimension mismatch
		res = vectorobjects[1].addVector(vectorobjects[3]);
		assertNull("Addition of vector 1 and 4 is incorrect (dimension mismatch)", res);
		//inplace v1+v2
		vectorobjects[0].addVectorInPlace(vectorobjects[1]);
		assertArrayEquals("In place addition of vector 1 and 2 is incorrect",
				new double[]{1.01, 5.02, 2.99, -1.02}, vectorobjects[0].getData(), 1E-10); 
		//inplace v2+v4 -> dimension mismatch
		vectorobjects[1].addVectorInPlace(vectorobjects[3]);
		assertArrayEquals("In place addition of vector 2 and 4 is incorrect (dimension mismatch)",
				vectors[1], vectorobjects[1].getData(), 1E-10); 
	}
	
	@Test
	public void testSubtraction(){
		//v1-v2
		Vector res = vectorobjects[0].subtractVector(vectorobjects[1]);
		assertArrayEquals("Subtraction vector1-vector2 is incorrect",
				new double[]{0.99, 4.98, 3.01, -0.98}, res.getData(), 1E-10);
		//v1-v3
		res = vectorobjects[0].subtractVector(vectorobjects[2]);
		assertArrayEquals("Subtraction vector1-vector3 is incorrect",
				new double[]{1.5, 6.5, 3.1, -0.8}, res.getData(), 1E-10);
		//v2-v3
		res = vectorobjects[1].subtractVector(vectorobjects[2]);
		assertArrayEquals("Subtraction vector2-vector3 is incorrect",
				new double[]{0.51, 1.52, 0.09, 0.18}, res.getData(), 1E-10);
		//v1-v4 -> dimension mismatch
		res = vectorobjects[0].subtractVector(vectorobjects[3]);
		assertNull("Subtraction of vector 1 and 4 is incorrect (dimension mismatch)", res);
		//inplace v1-v2
		vectorobjects[0].subtractVectorInPlace(vectorobjects[1]);
		assertArrayEquals("In place Subtraction vector1-vector2 is incorrect",
				new double[]{0.99, 4.98, 3.01, -0.98}, vectorobjects[0].getData(), 1E-10);
	}
	
	@Test
	public void testMaxNorm(){
		//v1
		assertEquals("Max norm of vector 1 is incorrect", 5, vectorobjects[0].calculateMaxNorm(),1E-10);
		//v2
		assertEquals("Max norm of vector 2 is incorrect", 0.02, vectorobjects[1].calculateMaxNorm(),1E-10);
		//v3
		assertEquals("Max norm of vector 3 is incorrect", 1.5, vectorobjects[2].calculateMaxNorm(),1E-10);
		//v4
		assertEquals("Max norm of vector 4 is incorrect", 1, vectorobjects[3].calculateMaxNorm(),1E-10);
		//special case v=(0)
		assertEquals("Max norm of 0 vector is incorrect", 0, new Vector(new double[]{0}).calculateMaxNorm(),1E-10);
	}

}
