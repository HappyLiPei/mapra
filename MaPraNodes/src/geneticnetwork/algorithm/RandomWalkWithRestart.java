package geneticnetwork.algorithm;

import geneticnetwork.datastructures.SparseMatrix;
import geneticnetwork.datastructures.Vector;

public abstract class RandomWalkWithRestart {
	
	/** transition matrix (sparse format) */
	private SparseMatrix matrix;
	/** initial probability distribution and restart vector */
	private Vector start;
	/** probability of restart = part of the original score that is kept*/
	private double restartProbability;
	/** number of iterations performed */
	private int currentNumberOfIterations;
	/** difference between result from iteration t (previous) and iteration t+1 (current) */
	private double differenceToPrevious;
	
	/**
	 * generates an RandomWalkWithRestart
	 * @param restartProbability restart probability
	 */
	public RandomWalkWithRestart(double restartProbability){
		this.restartProbability = restartProbability;
		currentNumberOfIterations =0;
		differenceToPrevious = Double.MAX_VALUE;
	}
	
	/**
	 * adds a transition matrix to this RandomWalkWithRestart, the matrix specifies the network structure and edge weights
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
		Vector previous = start;
		Vector current = start;
		
		while(nextIterationNeeded()){
			//matrix*vector -> new copy of the vector : Matrix*v(t)
			current = matrix.multiply(previous);
			//do multiplication in place: (1-r)*Matrix*v(t)
			current.multiplyScalarInPlace(1-restartProbability);
			//do addition in place: (1-r)*Matrix*v(t)+r*v(0)
			current.addVectorInPlace(restartTerm);
			
			//calculate difference between current and previous
			previous.subtractVectorInPlace(current);
			//save max norm of the difference
			differenceToPrevious = previous.calculateMaxNorm();
			
			//prepare next iteration
			previous=current;
			currentNumberOfIterations++;
		}
		return current;
	}
	
	/**
	 * method to check if the random walk with restart procedure continues (performs another iteration) according
	 * to some criterion (fixed number of iterations reached or convergence)
	 * @return true, if the random walk with restart should continue and false, if the random walk with restart is
	 * finished
	 */
	protected abstract boolean nextIterationNeeded();
	
	/**
	 * retrieves the number of iterations done in this random walk with restart, the method has to be called after
	 * doRandomWalkWithRestart()
	 * @return number of iterations that were needed to perform the random walk with restart
	 */
	public int getNumberOfIterations(){
		return this.currentNumberOfIterations;
	}
	
	/**
	 * retrieves the difference (max norm) between the result of the random walk with restart and the result of
	 * the previous step of the walk, this value gives information about the convergence of the random walk with restart,
	 * the method has to be called after doRandomWalkWithRestart()
	 * @return max norm between the result of the random walk and the previous step of the walk
	 */
	public double getDifferenceToPrevious(){
		return this.differenceToPrevious;
	}
	
	/**
	 * method to create a copy of the settings of this random walk with restart (restart probability, number of iterations
	 * to perform) and stores them in a new random walk with restart object,
	 * the method allows to run the same settings on different matrix-vector combinations
	 * @return random walk with restart object with the same settings as this object
	 */
	public abstract RandomWalkWithRestart copy();
	
	/**
	 * retrieves the restart probability used in this random walk with restart
	 * @return restart probability of the random walk
	 */
	public double getRestartProbability(){
		return this.restartProbability;
	}


}
