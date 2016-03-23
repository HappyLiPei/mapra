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
	 * method to summarize the scores of a gene obtained from different diseases
	 * @param annotatedGenes array of AnnotatedGenes
	 * @return list of ScoredGenes containing the final score of each gene
	 */
	private LinkedList<ScoredGene> scoreGenes(AnnotatedGene [] annotatedGenes){
		
		LinkedList<ScoredGene> result = new LinkedList<ScoredGene>();
		for(AnnotatedGene g: annotatedGenes){
			int [] ids = g.getDiseaseIds();
			double [] scores = g.getScores();
			
			//gene without annotation
			if(ids.length==0){
				result.add(new ScoredGene(g.getId(), 0, ""));
			}
			
			else{
				//calculate combined score
				double combined_score = 1;
				for(double s:scores){
					//check if s==1 (pval=0) -> combined score =0 -> final score =1
					if(Math.abs(1-s)<1E-5){
						combined_score=0;
						break;
					}
					combined_score*=(1-s);
				}
				combined_score=1-combined_score;
				//round to 5 decimal places
				combined_score = (double) Math.round(combined_score*100000)/100000;
				
				//find maximum disease score
				double max =-1;
				String important_dis="";
				for(int i=0; i<scores.length; i++){
					//consider to scores as equal if the differ in less than 1E-5 -> more than one max
					if(Math.abs(max-scores[i])<1E-5){
						important_dis+=","+ids[i];
					}
					//new max is found
					else if(scores[i]>max){
						max=scores[i];
						important_dis=Integer.toString(ids[i]);
					}
				}
				
				//add new gene
				result.add(new ScoredGene(g.getId(), combined_score, important_dis));
			}
		}
		return result;
	}

}
