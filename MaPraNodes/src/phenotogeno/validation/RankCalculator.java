package phenotogeno.validation;

import java.util.HashMap;
import java.util.LinkedList;

import togeno.ScoredGene;
import togeno.ScoredGeneComparator;

/** RankCalculator provides helpful methods for calculating ranks */
public class RankCalculator {
	
	/**
	 * method to calculate the rank of gene in the result of PhenoToGeno,
	 * the rank is the position of the gene within result,
	 * if there are several genes with the same score as the gene of interest, the average position 
	 * of all the genes with the score is chosen as rank,
	 * if the gene does not appear in the result of PhenoToGeno it gets a rank of 0
	 * @param genes
	 * 		array of gene ids for which the rank should be calculated
	 * @param geneRes
	 * 		result from PhenoToGeno as list of ScoredGenes
	 * @return
	 * 		array of ranks (double values), the position within the array corresponds to the position in genes 
	 */
	public static double[] getRanks(String[] genes, LinkedList<ScoredGene> geneRes){
		
		//list of the genes that were not found yet
		LinkedList<String> remaining = new LinkedList<String>();
		for(String id: genes){
			remaining.add(id);
		}		
		//list of genes that were found
		LinkedList<String> found = new LinkedList<String>();
		//stores resulting ranks
		HashMap<String, Double> ranking = new HashMap<String, Double>(genes.length);
		
		//saves previous scored gene
		ScoredGene prev = new ScoredGene("Dummy", -1, "");
		//number of genes with identical score
		int count = 1;
		//current position in the list of scored genes
		int rank =0;
		
		ScoredGeneComparator comparator = new ScoredGeneComparator();
		for(ScoredGene g: geneRes){

			//check if current score equals that of the previous gene
			if(comparator.compareWithoutID(g,prev)!=0){
				//some of the genes were found -> calculate rank
				if(found.size()!=0){
					getFinalRank(count, rank, found, remaining, ranking);
					found=new LinkedList<String>();
				}
				//leave loop if no genes left
				if(remaining.size()==0){
					break;
				}
				count=1;
			}
			else{
				count++;
			}
			
			//check if current result corresponds to a gene of interest
			for(String id: remaining){
				if(id.equals(g.getId())){
					found.add(id);
				}
			}
			
			//go to the next scored gene
			rank++;
			prev=g;
		}
		
		//get rank for last genes
		getFinalRank(count, rank, found, remaining, ranking);
		
		//copy from HashMap to array of ranks
		double [] ranks = new double [genes.length];
		for(int i=0; i<genes.length; i++){
			if(ranking.containsKey(genes[i])){
				ranks[i] = ranking.get(genes[i]);
			}
			//gene was not found
			else{
				ranks[i] =0;
			}
		}
		
		return ranks;
	}
	
	/**
	 * auxiliary method for rank calculation, calculates the final rank
	 * @param count number of genes with the current score
	 * @param rank position of the last gene with the current score
	 * @param found list of genes to rank with the current score
	 * @param remaining list of genes that are not ranked yet
	 * @param ranking mapping gene id->rank
	 */
	private static void getFinalRank(int count, int rank, LinkedList<String> found, LinkedList<String> remaining,
			HashMap<String, Double> ranking){
		
		//calculate current rank
		int sum=0;
		for(int i=0; i<count; i++){
			sum+=rank-i;
		}
		double resrank= (double) sum/count;
		
		//update data structures
		for(String id:found){
			ranking.put(id, resrank);
			remaining.remove(id);
		}
	}
	
	/**
	 * finds the best rank (minimum value)
	 * @param ranks array of ranks e.g obtained by getRanks
	 * @return the value of the best rank in ranks
	 */
	public static double getBestRank(double [] ranks){
		double min = Double.MAX_VALUE;
		for(double rank: ranks){
			if(rank<min){
				min = rank;
			}
		}
		return min;
	}
	
	/**
	 * finds the worst rank (maximum value)
	 * @param ranks array of ranks e.g obtained by getRanks
	 * @return the value of the worst rank in ranks
	 */
	public static double getWorstRank(double [] ranks){
		double max = -1;
		for(double rank:ranks){
			if(rank>max){
				max=rank;
			}
		}
		return max;
	}
	
	/**
	 * calculates the average rank over a set of ranks
	 * @param ranks array of ranks e.g obtained by getRanks
	 * @return the average over all ranks in ranks
	 */
	public static double getAverageRank(double [] ranks){
		double sum =0;
		for(double rank:ranks){
			sum+=rank;
		}
		double average = (double) sum/ranks.length;
		return average;
	}

}
