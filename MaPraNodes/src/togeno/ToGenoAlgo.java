package togeno;

import java.util.Collections;
import java.util.LinkedList;

/** algorithm to transfer scores from diseases/metabolites to associated genes*/
public class ToGenoAlgo {
	
	/** result from Phenomizer or MetaboliteScore: ids associated with pvalues*/
	private LinkedList<ScoredDiseaseOrMetabolite> score_res;
	/** object representing associations between genes and diseases/metabolites*/
	private GeneAssociation dga;
	
	/**
	 * generates a ToGenoAlgo for transferring Phenomizer/MetaboliteScore p values to genes
	 * @param score_res result of Phenomizer/MetaboliteScore as list of scored diseases/metabolites (id + p value)
	 * @param dga GeneAssociation stores associations between diseases/metabolites and genes
	 */
	public ToGenoAlgo (LinkedList<ScoredDiseaseOrMetabolite> score_res, GeneAssociation dga){
		this.score_res = score_res;
		this.dga = dga;
	}
	
	/**
	 * method to execute the ToGenoAlgorithm
	 * @return a sorted list of ScoredGenes (sorted in descending order according to score)
	 */
	public LinkedList<ScoredGene> runToGene(){
		annotateGenes();
		LinkedList<ScoredGene> result= scoreGenes(dga.getAllGenes());
		//sort result according to score
		Collections.sort(result, new ScoredGeneComparator());
		return result;
	}
	
	/**
	 * method to transfer the Phenomizer/MetaboliteScore p values to all associated genes of the corresponding disease/metabolite,
	 * the method used by runToGene()
	 */
	private void annotateGenes(){
		for (ScoredDiseaseOrMetabolite scoredDisease: score_res){
			//calculate score from p value
			double score = (double) 1/(1+dga.numberOfDiseasesOrMetabolites()*scoredDisease.getPval());
			//get gene annotations for the current diseases
			AnnotatedGene [] genes = dga.getGenesForDiseaseMetaboliteWithID(scoredDisease.getId());
			
			//no genes known for the current disease -> distribute score equally on all genes
			if(genes.length==0){
				score=(double) score/dga.numberOfGenes();
				genes =dga.getAllGenes();
			}
			
			//add score to the diseases 
			for(AnnotatedGene g: genes){
				g.add(scoredDisease.getId(), score);
			}
		}
	}
	
	/**
	 * method to generate a final score for each gene
	 * @param annotatedGenes array of AnnotatedGenes
	 * @return list of ScoredGenes containing the final score of each gene
	 */
	private LinkedList<ScoredGene> scoreGenes(AnnotatedGene [] annotatedGenes){
		
		LinkedList<ScoredGene> result = new LinkedList<ScoredGene>();
		
		for(AnnotatedGene g: annotatedGenes){
			//get final score, rounded to 5 decimal places
			double combined_score = g.getFinalScore();
			combined_score =(double) Math.round(combined_score*100000)/100000;
			//get diseases with maximum contribution to the gene (at most 3 diseases)
			String [] ids = g.getContributorIds();
			String important_dis ="";
			for(String id:ids){
				if(important_dis.length()==0){
					important_dis=""+id;
				}
				else{
					important_dis+=","+id;
				}
			}
			if(g.moreMaxThanListed()){
				important_dis+="...";
			}
			//add new gene
			result.add(new ScoredGene(g.getId(), combined_score, important_dis));
		}
		return result;
	}

}
