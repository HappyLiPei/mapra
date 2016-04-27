package geneticnetwork.algorithm;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.OpenMapRealMatrix;

public class RandomWalkWithRestart {
	
	/** transition matrix */
	private OpenMapRealMatrix matrix;
	/** intial probability distribution and restart vector */
	private ArrayRealVector start;
	/** number of iterations = steps within the networks*/
	private int numberOfIterations;
	/** probability of restart = part of the original score that is kept*/
	private double restartProbability;
	
	/**
	 * generates an RandomWalkWithRestart
	 * @param numberOfIterations number of steps of the random walk
	 * @param restartProbability restart probabilitiy
	 */
	public RandomWalkWithRestart(int numberOfIterations, double restartProbability){
		this.numberOfIterations = numberOfIterations;
		this.restartProbability = restartProbability;
	}
	
	/**
	 * adds a transition matrix to this RandomWalkWithRestart, the matrix specified the network structure and edge weights
	 * @param matrix column-normalized (stochastic) transition matrix for the random walk with restart
	 */
	public void setMatrix(OpenMapRealMatrix matrix){
		this.matrix = matrix;
	}
	
	/**
	 * adds an initial probability distribution to this RandomWalkWithRestart, the vector represents the original score of
	 * each gene
	 * @param vector normalized vector representing a probability distribution
	 */
	public void setVector(ArrayRealVector vector){
		this.start=vector;
	}
	
	/**
	 * performs the random walk with restart on the specified matrix and restart vector	
	 * @return a vector representing the final probability distribution after the walk (final gene scores)
	 */
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
