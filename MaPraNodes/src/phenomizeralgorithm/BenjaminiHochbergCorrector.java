package phenomizeralgorithm;

public class BenjaminiHochbergCorrector implements PValueCorrector{
	
	public double[] correctPVals(double[] pvals, int number_of_tests){
		
		double[] res = new double[pvals.length];
		
		for(int i=0; i<pvals.length; i++){
			res[i]=pvals[i]*number_of_tests/(i+1);
		}
		
		double cur_min=Double.MAX_VALUE;
		for(int i=pvals.length-1; i>=0; i--){
			//ensure monotonicity
			if(Double.compare(cur_min, res[i])>0){
				cur_min=res[i];
			}
			else{
				res[i]=cur_min;
			}
			//no pvalue should be larger than 1
			if(Double.compare(1, res[i])<0){
				res[i]=1;
			}
		}
		
		return res;
	}

}
