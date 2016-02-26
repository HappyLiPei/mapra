package phenomizeralgorithm;

import java.util.LinkedList;

public class SimilarityCalculatorOneSidedWeight extends SimilarityCalculator{

	@Override
	protected double calculateSymmetricSimilarity(LinkedList<Integer>query ,LinkedList<Integer[]> disease){
		
		double sim1 = weightedSimilarityQueryToDisease(query, disease);
		double sim2 = similarityDiseaseToQuery(query, disease);
		double similarity=(sim1+sim2)/2;
		
		return similarity;
	}

}
