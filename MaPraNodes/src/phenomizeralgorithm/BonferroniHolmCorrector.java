package phenomizeralgorithm;

public class BonferroniHolmCorrector implements PValueCorrector{

	@Override
	public double correctPval(double pvalue, int number_of_tests, int rank) {
		return pvalue*(number_of_tests-rank+1);
	}

}
