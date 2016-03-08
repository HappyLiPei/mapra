package phenomizer.algorithm;

public class BonferroniHolmCorrector implements PValueCorrector{
	
	@Override
	public double[] correctPVals(double[] pvals, int number_of_tests) {
		
		double[] res = new double[pvals.length];
		
		double cur_max=Double.MIN_VALUE;
		for (int i=0;i<pvals.length; i++){
			//correct pvalue
			res[i]=pvals[i]*(number_of_tests-(i+1)+1);
			//ensure monotonicity
			if(Double.compare(res[i],cur_max)>0){
				cur_max=res[i];
			}
			else{
				res[i]=cur_max;
			}
			//no pvalue should be larger than 1
			if(Double.compare(res[i], 1)>0){
				res[i]=1;
			}
		}
		
		return res;
	}

}
