package geneticnetwork.algorithm;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.OpenMapRealMatrix;

public class RandomWalkWithRestart {
	
	private OpenMapRealMatrix matrix;
	private ArrayRealVector start;
	private int numberOfIterations;
	private double restartProbability;
	
	//constructor: set iterations and reastart
	public RandomWalkWithRestart(int numberOfIterations, double restartProbability){
		this.numberOfIterations = numberOfIterations;
		this.restartProbability = restartProbability;
	}
	
	public void setMatrix(OpenMapRealMatrix matrix){
		this.matrix = matrix;
	}
	
	public void setVector(ArrayRealVector vector){
		this.start=vector;
	}
	
	//TODO: implement+test!
	//perfrom walk
	public ArrayRealVector doRandomWalkWithRestart(){
		
		//restartTerm = copy of start *(r-1)
		ArrayRealVector restartTerm = (ArrayRealVector) start.mapMultiply(restartProbability);
		ArrayRealVector current = start;
		
		//TODO: iterate until convergence??
		for(int iteration=1; iteration<=numberOfIterations; iteration++){
			//matrix*vector -> new copy of the vector
			current = (ArrayRealVector) matrix.operate(current);
			//do multiplication in places
			current.mapMultiplyToSelf(1-restartProbability);
			//TODO: addition in place
			current=current.add(restartTerm);
		}
		return current;
		
	}

}
