package phenotogeno.algo;

import java.util.Collections;
import java.util.LinkedList;

public class PhenoToGenoAlgo {
	
	private LinkedList<ScoredDisease> pheno_res;
	private DiseaseGeneAssociation dga;
	
	/**
	 * generates a PhenoToGenoAlgo for transferring Phenomizer p values to genes
	 * @param pheno_res result of Phenomizer als list of scored diseases (disease id + p value)
	 * @param dga DiseaseGeneAssociation stores associations between diseases and genes
	 */
	public PhenoToGenoAlgo (LinkedList<ScoredDisease> pheno_res, DiseaseGeneAssociation dga){
		this.pheno_res = pheno_res;
		this.dga = dga;
	}
	
	/**
	 * method to execute the PhenoToGenoAlgorithm
	 * @return a sorted list of ScoredGenes (sorted in descending order according to score)
	 */
	public LinkedList<ScoredGene> runPhenoToGene(){
		annotateGenes();
		LinkedList<ScoredGene> result= scoreGenes(dga.getAllGenes());
		//sort result according to score
		Collections.sort(result, new ScoredGeneComparator());
		return result;
	}
	
	/**
	 * method to transfer the Phenomizer p values to all associated genes of the corresponding disease,
	 * the method used by runPhenoToGene()
	 */
	private void annotateGenes(){
		for (ScoredDisease scoredDisease: pheno_res){
			//calculate score from p value
			double score = (double) 1/(1+dga.numberOfDiseases()*scoredDisease.getPval());
			//get gene annotations for the current diseases
			AnnotatedGene [] genes = dga.getGenesForDiseaseWithID(scoredDisease.getId());
			
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
			int [] ids = g.getDiseaseIds();
			String important_dis ="";
			for(int id:ids){
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
