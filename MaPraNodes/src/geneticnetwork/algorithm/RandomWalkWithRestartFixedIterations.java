package geneticnetwork.algorithm;

public class RandomWalkWithRestartFixedIterations extends RandomWalkWithRestart{
	
	/** number of iterations to perform*/
	private double numberOfIterations;
	
	/**
	 * generates a random walk with restart object with a fixed number of iterations, i.e. the random walk is done
	 * for a defined number of steps
	 * @param restartProbability restart probability
	 * @param numberOfIterations number of iterations
	 */
	public RandomWalkWithRestartFixedIterations(double restartProbability, double numberOfIterations) {
		super(restartProbability);
		this.numberOfIterations = numberOfIterations;
	}
	
	/**
	 * method to check if the random walk with restart procedure continues, i.e. if the fixed number of iterations
	 * is reached
	 */
	@Override
	protected boolean nextIterationNeeded() {
		return getNumberOfIterations()<numberOfIterations;
	}

	@Override
	public RandomWalkWithRestart copy() {
		return new RandomWalkWithRestartFixedIterations(getRestartProbability(), numberOfIterations);
	}

}
