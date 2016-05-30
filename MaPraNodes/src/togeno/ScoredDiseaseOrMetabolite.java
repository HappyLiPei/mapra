package togeno;

/** object representing a disease or a metabolite with a pvalue */
public class ScoredDiseaseOrMetabolite {
	
	/** id of the disease/metabolite*/
	private String id;
	/** pvalue of the disease/metabolite calculated by Phenomizer/MetaboliteScore*/
	private double pval;

	/**
	 * generates a representation of one disease/metabolite in the result of Phenomizer/MetaboliteScore
	 * @param id PhenoDis id of a disease (integer)
	 * @param pval pvalue of the disease in the result of Phenomizer
	 */
	public ScoredDiseaseOrMetabolite(int id, double pval){
		this.id = id+"";
		this.pval = pval;
	}
	
	/**
	 * generates a representation of one disease/metabolite in the result of Phenomizer/MetaboliteScore
	 * @param id PhenoDis id of a disease or Metabolon id of a metabolite (String)
	 * @param pval pvalue of the disease/metabolite in the result of Phenomizer/MetaboliteScore
	 */
	public ScoredDiseaseOrMetabolite(String id, double pval){
		this.id =id;
		this.pval = pval;
	}
	
	/**
	 * retrieves the PhenoDis id of the disease or the Metabolon id of the metabolite represented by this object
	 * @return PhenoDis disease id/Metabolon metabolite id
	 */
	public String getId(){
		return id;
	}
	
	/**
	 * retrieves the pvalue of the disease/metabolite
	 * @return Pvalue calculated by Phenomizer/MetaboliteScore
	 */
	public double getPval(){
		return pval;
	}
	

}
