package phenotogeno.algo;

import java.util.Collections;
import java.util.LinkedList;

public class PhenoToGenoAlgo {
	
	private LinkedList<ScoredDisease> pheno_res;
	private DiseaseGeneAssociation dga;
	
	public PhenoToGenoAlgo (LinkedList<ScoredDisease> pheno_res, DiseaseGeneAssociation dga){
		this.pheno_res = pheno_res;
		this.dga = dga;
	}
	
	public LinkedList<ScoredGene> runPhenoToGene(){
		annotateGenes();
		LinkedList<ScoredGene> result= scoreGenes(dga.getAllGenes());
		//sort result according to score
		Collections.sort(result, new ScoredGeneComparator());
		return result;
	}
	
	private void annotateGenes(){
		
		for (ScoredDisease scoredDisease: pheno_res){
			
			//calculate score from p value
			double score = (double) 1/(1+dga.numberOfDiseases()*scoredDisease.getPval());
			//get gene annotations for the current diseases
			AnnotatedGene [] genes = dga.getGenesForDiseaseWithID(scoredDisease.getId());
			
			//no genes known for the current disease
			if(genes.length==0){
				score=(double) score/dga.numberOfDiseases();
				genes =dga.getAllGenes();
			}
			
			//add score to the diseases 
			for(AnnotatedGene g: genes){
				g.add(scoredDisease.getId(), score);
			}
		}
	}
	
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
				//TODO: better calculation of the score ?!
				//calculate combined score
				double combined_score = 1;
				for(double s:scores){
					combined_score*=(1-s);
					//check if a pvalue is 0
					if(Math.round(s*1000)==1){
						combined_score=0;
						break;
					}
				}
				combined_score=1-combined_score;
				//round to 5 decimal places
				combined_score = (double) Math.round(combined_score*100000)/100000;
				
				//find maximum
				int max=-1;
				String important_dis="";
				for(int i=0; i<scores.length; i++){
					int scoreInt = (int) Math.round(scores[i]*1000000000);
					if(scoreInt>max){
						max=scoreInt;
						important_dis=Integer.toString(ids[i]);
					}
					else if(scoreInt==max){
						important_dis+=","+ids[i];
					}
				}
				
				//add new gene
				result.add(new ScoredGene(g.getId(), combined_score, important_dis));
			}
		}
		return result;
	}

}
