package algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class AlgoPheno {
	
	static LinkedList<Integer> queryIds;
	static LinkedList<Integer> symptomIds;
	static HashMap<Integer,LinkedList<Integer>> kszD = new HashMap<Integer,LinkedList<Integer>>();
	static HashMap<Integer,HashSet<Integer>> kszS = new HashMap<Integer,HashSet<Integer>>();
	
	public static void setInput(LinkedList<Integer> query, LinkedList<Integer>symptoms,
			HashMap<Integer,LinkedList<Integer>> ksz){
		queryIds = query;
		symptomIds = symptoms;
		kszD = ksz;
		for(int symp : symptoms){
			kszS.put(symp, null);
		}
		
		for(int disease : ksz.keySet()){
			LinkedList<Integer> tmpSymp = ksz.get(disease);
			for(int symp : tmpSymp){
				HashSet<Integer> tmp = kszS.get(symp);
				if(!tmp.contains(disease)){
					tmp.add(disease);
				}
				kszS.put(symp, tmp);
				//Add symptom to all parents using the ontology
			}
		}
	}
	
	public static PriorityQueue<String> runPhenomizer(){
		PriorityQueue<String> result = new PriorityQueue<String>();
		
		for(int disease : kszD.keySet()){
			double similarity = calculateSymmetricSimilarity(queryIds,kszD.get(disease));
		}
		
		return result;
	} 
	
	private static double calculateIC(int term){
		double ic = 0;
		//calculate information content
		return ic;
	}
	
	private static double calculatePairwiseSim(int term1, int term2){
		double pairwiseSim = 0;
		
		return pairwiseSim;
	}
	
	private static double calculateSymmetricSimilarity(LinkedList<Integer>symptoms1,LinkedList<Integer>symptoms2){
		
		HashMap<String,Double>calculatedSim = new HashMap<String,Double>();
		
		double sim1 = 0;
		for(int symp1 : symptoms1){
			double currMax = Double.MIN_VALUE;
			for(int symp2: symptoms2){
				
			}
		}
		
		double sim2 = 0;
		
		double similarity=(sim1+sim2)/2;
		return similarity;
	}
	
	
}
