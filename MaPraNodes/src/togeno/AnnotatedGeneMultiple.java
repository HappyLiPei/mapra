package togeno;

/** gene object with annotations and intermediate scores from several diseases/ metabolites,
 * this subtype of {@link AnnotatedGene} considers the score of all annotated disease/metabolites for determining the final score
 */
public class AnnotatedGeneMultiple extends AnnotatedGene{
	
	/** intermediate score = product of (1-s) with s=score for disease/metabolite annotated to this gene */
	private double currentScore;
	
	/**
	 * generates a gene object that can be annotated with results from Phenomizer/MetaboliteScore
	 * this object combines the score of all diseases/metabolites annotated to it 
	 * @param id id of the gene (e.g. ensembl id)
	 */
	public AnnotatedGeneMultiple(String id) {
		super(id);
		currentScore = 1;
	}
	
	@Override
	public void add(String dis_or_met_id, double score){
		super.add(dis_or_met_id, score);
		currentScore = currentScore*(1-score);
	}
	
	@Override
	public void resetAnnotation(){
		super.resetAnnotation();
		currentScore = 1;
	}
	
	/**
	 * retrieves the current score summarizing all annotations to this gene (1-product of (1-s) for all annotated scores s)
	 * @return current score of the gene
	 */
	@Override
	public double getFinalScore() {
		return 1-currentScore;
	}

}
