package phenomizer.algorithm;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class PhenomizerAlgorithmNoPvalue extends PhenomizerAlgorithm{

	public PhenomizerAlgorithmNoPvalue(int num, Ontology ontology, LinkedList<Integer> queryIds,
			SymptomDiseaseAssociations sda, SimilarityCalculator similarityCalculator) {
		super(num, ontology, queryIds, sda, similarityCalculator);

	}

	@Override
	public LinkedList<String[]> runPhenomizer() {
		setIC();
		PriorityQueue<String> queue = scoreQuery();
		return generateResult(queue);
	}
	
	private PriorityQueue<String> scoreQuery(){
		//identify results leading to the highest similiarty scores
		PriorityQueue<String> minQueue = new PriorityQueue<String>();

		for(int disease : sda.getDiseases()){
			double similarity = similarityCalculator.calculateSymmetricSimilarity(queryIds,sda.getSymptoms(disease));
			similarity = similarity*1000;
			similarity = Math.round(similarity);
			similarity = similarity/1000;
			String res = similarity+","+disease;
			if(minQueue.size()<num){
				minQueue.add(res);
			}
			else if(res.compareTo(minQueue.peek())>=0){
				minQueue.add(res);
			}
		}
		return minQueue;
	}
	
	private LinkedList<String []> generateResult(PriorityQueue<String> minQueue){

		//convert the min priority queue to a max priority queue and identify the top num results
		Comparator<String> comp = new MaxPriorityComparator();
		PriorityQueue<String> maxQueue = new PriorityQueue<String>(num,comp);
		while(!minQueue.isEmpty()){
			String nextEl = minQueue.remove();
			maxQueue.add(nextEl);
		}

		LinkedList<String[]>result = new LinkedList<String[]>();
		String score = "";
		for(int i=0; i<num; i++){
			String currEl = maxQueue.remove();
			String[]parts = currEl.split(",");
			String[]res = new String[2];
			res[0]=parts[1];
			res[1]=parts[0];
			score = parts[0];
			result.add(res);
		}
		while(!maxQueue.isEmpty()&&maxQueue.element().split(",")[0].equals(score)){
			String currEl = maxQueue.remove();
			String[]parts = currEl.split(",");
			String[]res = new String[2];
			res[0]=parts[1];
			res[1]=parts[0];
			result.add(res);
		}

		return result;
	}

}
