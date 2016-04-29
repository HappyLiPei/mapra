package geneticnetwork.algorithm;

import geneticnetwork.datastructures.SparseMatrix;
import geneticnetwork.datastructures.Vector;

public class RandomWalkWithRestart {
	
	/** transition matrix (sparse format) */
	private SparseMatrix matrix;
	/** intial probability distribution and restart vector */
	private Vector start;
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
	public void setMatrix(SparseMatrix matrix){
		this.matrix = matrix;
	}
	
	/**
	 * adds an initial probability distribution to this RandomWalkWithRestart, the vector represents the original score of
	 * each gene
	 * @param vector normalized vector representing a probability distribution
	 */
	public void setVector(Vector vector){
		this.start=vector;
	}
	
	/**
	 * performs the random walk with restart on the specified matrix and restart vector	
	 * @return a vector representing the final probability distribution after the walk (final gene scores)
	 */
	public Vector doRandomWalkWithRestart(){
		
		//formula: v(t+1)=(r-1)*Matrix*v(t)+r*v(0)
		
		//restartTerm = r*v(0) -> no modification of v(0)
		Vector restartTerm = start.multiplyScalar(restartProbability);
		//current = v(t) -> init with v(0)
		Vector current = start;
		
		//TODO: iterate until convergence?? 
		for(int iteration=1; iteration<=numberOfIterations; iteration++){
			//matrix*vector -> new copy of the vector : Matrix*v(t)
			current = matrix.multiply(current);
			//do multiplication in place: (1-r)*Matrix*v(t)
			current.multiplyScalarInPlace(1-restartProbability);
			//do addition in place: (1-r)*Matrix*v(t)+r*v(0)
			current.addVectorInPlace(restartTerm);
		}
		return current;
	}

}
