package phenotogeno.algo;

public class ScoredDisease {
	
	private String id;
	private double pval;

	/**
	 * generates a representation of one disease in the result of Phenomizer
	 * @param id PhenoDis id of a disease
	 * @param pval pvalue of the disease in the result of Phenomizer
	 */
	public ScoredDisease(int id, double pval){
		this.id = id+"";
		this.pval = pval;
	}
	
	public ScoredDisease(String id, double pval){
		this.id =id;
		this.pval = pval;
	}
	
	/**
	 * retrieves the PhenoDis id of the disease
	 * @return PhenoDis disease if
	 */
	public String getId(){
		return id;
	}
	
	/**
	 * retrieves the pvalue of the disease
	 * @return Pvalue calculated by Phenomizer
	 */
	public double getPval(){
		return pval;
	}
	

}
