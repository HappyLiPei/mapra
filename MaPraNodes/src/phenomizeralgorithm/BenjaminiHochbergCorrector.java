package phenomizeralgorithm;

public class BenjaminiHochbergCorrector implements PValueCorrector{

	@Override
	public double correctPval(double pvalue, int number_of_tests, int rank) {
		return (number_of_tests/rank)*pvalue;
	}

}
