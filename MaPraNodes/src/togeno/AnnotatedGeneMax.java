package togeno;

/** gene object with annotations and intermediate scores from several diseases/ metabolites,
 * this subtype of {@link AnnotatedGene} determines the final score as maximum of all scores annotetated to this gene
 */
public class AnnotatedGeneMax extends AnnotatedGene{
	
	/**
	 * generates a gene object that can be annotated with results from Phenomizer/MetaboliteScore
	 * this determines the maximum score of all diseases/metabolites annotated to it 
	 * @param id id of the gene (e.g. ensembl id)
	 */
	public AnnotatedGeneMax(String id) {
		super(id);
	}
	
	/**
	 * retrieves the current score by determining the maximum of all annotations to this gene (max(s) for all annotated scores s)
	 * @return current score of the gene
	 */
	@Override
	public double getFinalScore() {
		return getCurrentMax();
	}

}
