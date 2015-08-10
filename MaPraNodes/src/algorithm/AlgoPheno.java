package algorithm;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class AlgoPheno {
	
	private static LinkedList<Integer> queryIds;
	private static LinkedList<Integer> symptomIds;
	private static Ontology ontology;
	private static HashMap<Integer,LinkedList<Integer>> kszD = new HashMap<Integer,LinkedList<Integer>>();
	private static HashMap<Integer,HashSet<Integer>> kszS = new HashMap<Integer,HashSet<Integer>>();
	private static HashMap<Integer,Double> ic = new HashMap<Integer,Double>();
	private static HashMap<String,Double>calculatedSim = new HashMap<String,Double>();
	
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
		queryIds = query;
		symptomIds = symptoms;
		kszD = ksz;
		ontology = new Ontology(onto);
		
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
		main.FileUtilities.writeString("D:/Dokumente/Masterpraktikum/Testdatensatz/PairwiseSim.txt",hashMapToString(calculatedSim));
		
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
				if(currIC>pairwiseSim){
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
	
	private static String hashMapToString(HashMap<String,Double>map){
		StringBuilder sb = new StringBuilder();
		for(String key : map.keySet()){
			String[]parts = key.split(",");
			double val = map.get(key)*1000;
			val = Math.round(val);
			val = val/1000;
			sb.append(parts[0]+"\t"+parts[1]+"\t"+val+"\n");
		}
		return sb.toString();
	}
}
