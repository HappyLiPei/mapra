package algorithm;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class AlgoPheno {
	
	static LinkedList<Integer> queryIds;
	static LinkedList<Integer> symptomIds;
	static HashMap<Integer,LinkedList<Integer>> kszD = new HashMap<Integer,LinkedList<Integer>>();
	static HashMap<Integer,HashSet<Integer>> kszS = new HashMap<Integer,HashSet<Integer>>();
	static HashMap<Integer,Double> ic = new HashMap<Integer,Double>();
	static Ontology ontology;
	
	/**
	 * initialize the needed data structures using the given inputs
	 * @param query - list of symptoms from the query
	 * @param symptoms - list of symptoms from the database
	 * @param ksz - association of diseases and symptoms
	 */
	public static void setInput(LinkedList<Integer> query, LinkedList<Integer>symptoms,
			HashMap<Integer,LinkedList<Integer>> ksz,int[][]onto){
		queryIds = query;
		symptomIds = symptoms;
		kszD = ksz;
		ontology = new Ontology(onto);
		
		for(int symp : symptoms){
			//FALSCH!!!
			double icS = calculateIC(symp);
			ic.put(symp,icS);
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
	
	/**
	 * execute the phenomizer algorithm and return the top num results
	 * @param num
	 * @return
	 */
	public static LinkedList<String[]> runPhenomizer(int num){
		PriorityQueue<String> minQueue = new PriorityQueue<String>();
		
		for(int disease : kszD.keySet()){
			double similarity = calculateSymmetricSimilarity(queryIds,kszD.get(disease));
			similarity = similarity*1000;
			Math.round(similarity);
			similarity = similarity/1000;
			String res = similarity+","+disease;
			if(res.compareTo(minQueue.peek())>=0){
				minQueue.add(res);
			}
		}
		
		String[]tmp = (String[]) minQueue.toArray();
		
		Comparator<String> comp = new MaxPriorityComparator();
		PriorityQueue<String> maxQueue = new PriorityQueue<String>(20,comp);
		maxQueue.addAll(Arrays.asList(tmp));
		
		LinkedList<String[]>result = new LinkedList<String[]>();
		//mehrere??
		for(int i=0; i<num; i++){
			String currEl = maxQueue.remove();
			String[]parts = currEl.split(",");
			String[]res = new String[2];
			res[0]=parts[1];
			res[1]=parts[2];
		}
		
		return result;
	} 
	
	/**
	 * calculates the information content of a given term (symptom)
	 * @param term
	 * @return
	 */
	private static double calculateIC(int term){
		
		double ic = 0;
		double freq = kszS.get(term).size()/kszD.size();
		ic = -Math.log(freq);
		
		return ic;
	}
	
	private static double calculatePairwiseSim(int term1, int term2){
		double pairwiseSim = 0;

		//calculate pairwise similarity
		
		return pairwiseSim;
	}
	
	/**
	 * calculates the symmetric similarity between a given query of symptoms and a disease in the database
	 * 
	 * @param symptoms1
	 * @param symptoms2
	 * @return similiarty score
	 */
	private static double calculateSymmetricSimilarity(LinkedList<Integer>symptoms1,LinkedList<Integer>symptoms2){
		
		//global speichern
		HashMap<String,Double>calculatedSim = new HashMap<String,Double>();
		
		double sim1 = 0;
		for(int symp1 : symptoms1){
			double currMax = Double.MIN_VALUE;
			for(int symp2: symptoms2){
				String key = "";
				double currSym = 0;
				if(symp1<symp2){
					key = symp1 + "," + symp2;
				}
				else{
					key = symp2 + "," +symp1;
				}
				if(calculatedSim.containsKey(key)){
					currSym = calculatedSim.get(key);
				}
				else{
					currSym = calculatePairwiseSim(symp1,symp2);
					calculatedSim.put(key, currSym);
				}
				if(Double.compare(currMax, currSym)<0)
					currMax = currSym;
			}
			sim1 = sim1+ currMax;
		}
		sim1 = sim1/symptoms1.size();
		
		double sim2 = 0;
		for(int symp1 : symptoms2){
			double currMax = Double.MIN_VALUE;
			for(int symp2: symptoms1){
				String key = "";
				double currSym = 0;
				if(symp1<symp2){
					key = symp1 + "," + symp2;
				}
				else{
					key = symp2 + "," +symp1;
				}
				if(calculatedSim.containsKey(key)){
					currSym = calculatedSim.get(key);
				}
				else{
					currSym = calculatePairwiseSim(symp1,symp2);
					calculatedSim.put(key, currSym);
				}
				if(Double.compare(currMax, currSym)<0)
					currMax = currSym;
			}
			sim2 = sim2+ currMax;
		}
		sim2 = sim2/symptoms2.size();
		
		double similarity=(sim1+sim2)/2;
		return similarity;
	}
}
