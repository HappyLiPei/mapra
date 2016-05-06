package geneticnetwork.algorithm;

public class RandomWalkWithRestartUntilConvergence extends RandomWalkWithRestart {
	
	/** criterion of the max norm to indicate convergence*/
	private double convergenceCriterion;
	
	/**
	 * generates a random walk with restart object which iterates until convergence, i.e. the random walk is done
	 * until the result of step t+1 and step t do not differ any more significantly
	 * @param restartProbability restart probability
	 */
	public RandomWalkWithRestartUntilConvergence(double restartProbability) {
		super(restartProbability);
		convergenceCriterion =1E-11;
	}
	
	/**
	 * method to check if the random walk with restart procedure continues, i.e. if convergence is reached
	 */
	@Override
	protected boolean nextIterationNeeded() {
		return getDifferenceToPrevious()>convergenceCriterion;
	}

}
