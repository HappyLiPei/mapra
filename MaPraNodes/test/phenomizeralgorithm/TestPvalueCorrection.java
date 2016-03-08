package phenomizeralgorithm;

import static org.junit.Assert.*;

import org.junit.Test;

import phenomizer.algorithm.BenjaminiHochbergCorrector;
import phenomizer.algorithm.BonferroniHolmCorrector;

public class TestPvalueCorrection {

	@Test
	public void testBenjaminiHochberg() {
		
		BenjaminiHochbergCorrector c = new BenjaminiHochbergCorrector();
		
		double[] pvals= new double[]{0.0, 0.0, 0.001, 0.001, 0.001, 0.02, 0.02, 0.1, 0.1, 0.2};
		double[] pvals2= new double[]{5.0E-4, 0.1289, 0.2626, 0.5834, 0.6039, 0.6524, 0.6806, 0.7157, 0.7213, 0.975, 1.0};
		//calculated by R p.adjust, method=BH
		double [] expected= new double[]
		{0.000, 0.000, 0.002, 0.002, 0.002, 0.02857143, 0.02857143, 0.11111111, 0.11111111, 0.200};
		double [] expected2= new double[]
				{0.0055, 0.70895, 0.8815889, 0.8815889, 0.8815889, 0.8815889, 0.8815889, 0.8815889, 0.8815889,
						1.0000000, 1.0000000};
		double [] expected3 = new double[]
				{0.000, 0.000, 0.004, 0.004, 0.004, 0.05714286, 0.05714286, 0.22222222, 0.22222222, 0.400};
		
		double [] result = c.correctPVals(pvals,pvals.length);
		assertArrayEquals("Benjamini Hochberg Correction incorrect for test case 1",
				expected, result, 1E-5);
		
		double [] result2 = c.correctPVals(pvals2,pvals2.length);
		assertArrayEquals("Benjamini Hochberg Correction incorrect for test case 2",
				expected2, result2, 1E-5);
		
		double [] result3 = c.correctPVals(pvals,20);
		assertArrayEquals("Benjamini Hochberg Correction incorrect for test case 3",
				expected3, result3, 1E-5);
		
	}
	
	@Test
	public void testBonferroniHolm() {
		
		BonferroniHolmCorrector c = new BonferroniHolmCorrector();
		
		double[] pvals= new double[]{0.0, 0.0, 0.001, 0.001, 0.001, 0.02, 0.02, 0.1, 0.1, 0.2};
		double[] pvals2= new double[]{5.0E-4, 0.1289, 0.2626, 0.5834, 0.6039, 0.6524, 0.6806, 0.7157, 0.7213, 0.975, 1.0};
		//calculated by R p.adjust, method=holm
		double [] expected= new double[]
				{0.000, 0.000, 0.008, 0.008, 0.008, 0.100, 0.100, 0.300, 0.300, 0.300};
		double [] expected2= new double[]
				{0.0055, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000, 1.0000};
		double [] expected3= new double[]
				{0.000, 0.000, 0.018, 0.018, 0.018, 0.300, 0.300, 1.000, 1.000, 1.000};
		
		double [] result = c.correctPVals(pvals, pvals.length);
		assertArrayEquals("Bonferroni Holm Correction incorrect for test case 1",
				expected, result, 1E-5);
		
		double [] result2 = c.correctPVals(pvals2, pvals2.length);
		assertArrayEquals("Bonferroni Holm Correction incorrect for test case 2",
				expected2, result2, 1E-5);
		
		double [] result3 = c.correctPVals(pvals, 20);
		assertArrayEquals("Bonferroni Holm Correction incorrect for test case 3",
				expected3, result3, 1E-5);
		
		
	}


}
