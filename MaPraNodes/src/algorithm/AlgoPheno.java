package algorithm;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class AlgoPheno {
	
	public static LinkedList<Integer> queryIds;
	public static LinkedList<Integer> symptomIds;
	public static Ontology ontology;
	public static HashMap<Integer,LinkedList<Integer>> kszD = new HashMap<Integer,LinkedList<Integer>>();
	public static HashMap<Integer,HashSet<Integer>> kszS = new HashMap<Integer,HashSet<Integer>>();
	public static HashMap<Integer,Double> ic = new HashMap<Integer,Double>();
	public static HashMap<String,Double> calculatedSim = new HashMap<String,Double>();
	
	/**
	 * initialize the needed data structures using the given parameters
	 * 
	 * @param query - list of symptoms from the query
	 * @param symptoms - list of symptoms from the database
	 * @param ksz - association of diseases and symptoms
	 * @param onto - ontology given the hierarchical ordering of the symptoms
	 */
	public static void setInput(LinkedList<Integer> query, LinkedList<Integer>symptoms,
			HashMap<Integer,LinkedList<Integer>> ksz,int[][]onto){
		
		ontology = new Ontology(onto);
		
		//query = removeDuplicates(query);
		queryIds = removeAncestors(query);
		System.out.println(listToString(queryIds));
		
		symptomIds = symptoms;
		for(int key : ksz.keySet()){
			//LinkedList<Integer>value = removeAncestors(ksz.get(key));
			LinkedList<Integer>value=ksz.get(key);
			kszD.put(key, value);
		}
		
		for(int symp : symptoms){
			kszS.put(symp, new HashSet<Integer>());
		}
		
		for(int disease : ksz.keySet()){
			LinkedList<Integer> tmpSymp = ksz.get(disease);
			for(int symp : tmpSymp){
				HashSet<Integer>ancestorsOrSelf = ontology.getAllAncestors(symp);
				/*System.out.println(symp);
				System.out.println(listToString(ancestorsOrSelf));*/
				for(int nextSymp : ancestorsOrSelf){
					HashSet<Integer> tmp = kszS.get(nextSymp);
					if(!tmp.contains(disease)){
						tmp.add(disease);
					}
					kszS.put(nextSymp, tmp);
				}
			}
		}
	}
	
	/**
	 * execute the phenomizer algorithm and return the top num results
	 * @param num
	 * @return diseases with the highest similarity score
	 */
	public static LinkedList<String[]> runPhenomizer(int num){
		
		if(num>kszD.size()){
			num = kszD.size();
		}
		
		for(int symp : symptomIds){
			if(!ic.containsKey(symp)){
				double icS = calculateIC(symp);
				ic.put(symp,icS);
				//System.out.println(symp+"\t"+icS);
			}
		}
		
		PriorityQueue<String> minQueue = new PriorityQueue<String>();
		
		for(int disease : kszD.keySet()){
			double similarity = calculateSymmetricSimilarity(queryIds,kszD.get(disease));
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
		
		//String[]tmp = (String[]) minQueue.toArray();
		
		Comparator<String> comp = new MaxPriorityComparator();
		PriorityQueue<String> maxQueue = new PriorityQueue<String>(20,comp);
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
		while(!maxQueue.isEmpty()&&maxQueue.remove().split(",")[0].equals(score)){
			String currEl = maxQueue.remove();
			String[]parts = currEl.split(",");
			String[]res = new String[2];
			res[0]=parts[1];
			res[1]=parts[0];
			result.add(res);
		}
		
		return result;
	} 
	
	/**
	 * calculates the information content of a given term (symptom)
	 * @param term
	 * @return information content
	 */
	private static double calculateIC(int term){
		
		double ic = 0;
		double freq = (double)kszS.get(term).size()/kszD.size();
		ic = -Math.log(freq);
		
		return ic;
	}
	
	/**
	 * calculates the pairwise similarity of two terms (symptoms) as the information content of their most informative common ancestor
	 * @param term1
	 * @param term2
	 * @return pairwise similarity
	 */
	private static double calculatePairwiseSim(int term1, int term2){
		
		double pairwiseSim = Double.MIN_VALUE;
		HashSet<Integer>commonAncestors = ontology.getAllCommonAncestors(term1,term2); 
		
		if(!commonAncestors.isEmpty()){
			for(int symp : commonAncestors){
				double currIC = ic.get(symp);
				if(Double.compare(currIC,pairwiseSim)>0){
					pairwiseSim=currIC;
				}
			}
		}
		else{
			if(term1==term2){
				pairwiseSim=ic.get(term1);
			}
			else{
				pairwiseSim=0;
			}
		}
		
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
	
	/**
	 * remove duplicates from a given list
	 * @param symptoms
	 * @return duplicate-free list
	 */
	private static LinkedList<Integer> removeDuplicates(LinkedList<Integer>symptoms){
		LinkedList<Integer> result = new LinkedList<Integer>();
		for(int element: symptoms){
			if(!result.contains(element)){
				result.add(element);
			}
		}
		return result;
	}
	
	/**
	 * remove symptoms whose successor(s) are also in the list
	 * @param symptoms
	 * @return list without any ancestors of query terms
	 */
	private static LinkedList<Integer>removeAncestors(LinkedList<Integer>symptoms){
		LinkedList<Integer>result = new LinkedList<Integer>();
		for(int element : symptoms){
			if(!result.contains(element)){
				result.add(element);
			}
		}
		
		for(int element : symptoms){
			//System.out.println(element);
			HashSet<Integer>ancestors=ontology.getAllAncestors(element);
			ancestors.remove(element);
			for(int element1 : symptoms){
				if(ancestors.contains(element1)&&result.contains(element1)){
					int index = result.indexOf(element1);
					result.remove(index);
				}
			}
		}
		return result;
	}
	
	private static String listToString(LinkedList<Integer>list){
		StringBuilder sb = new StringBuilder();
		for(int element : list){
			sb.append(element+"\t");
		}
		return sb.toString();
	}
}
