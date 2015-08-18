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
	public static HashMap<Integer,LinkedList<Integer[]>> kszD = new HashMap<Integer,LinkedList<Integer[]>>();
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
			HashMap<Integer,LinkedList<Integer[]>> ksz,int[][]onto){

		ontology = new Ontology(onto);
		queryIds = removeAncestors(query);
		queryIds.size();

		//build disease-symptom map without duplicates and without ancestors whose successors are also in the annotation
		symptomIds = symptoms;
		for(int key : ksz.keySet()){
			LinkedList<Integer[]>value = removeAncestors2(ksz.get(key));
			kszD.put(key, value);
		}

		//build symptom-disease map listing for each symptom the diseases it is annotated to
		for(int symp : symptoms){
			kszS.put(symp, new HashSet<Integer>());
		}

		for(int disease : ksz.keySet()){
			LinkedList<Integer[]> tmpSymp = ksz.get(disease);
			for(Integer[] symp : tmpSymp){
				//System.out.println(symp);
				HashSet<Integer>ancestorsOrSelf = ontology.getAllAncestors(symp[0]);
				//System.out.println(ancestorsOrSelf.size());
				for(int nextSymp : ancestorsOrSelf){
					//System.out.println(nextSymp);
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

		//calculate information content
		for(int symp : symptomIds){
			if(!ic.containsKey(symp)){
				double icS = calculateIC(symp);
				ic.put(symp,icS);
			}
		}

		//identify results leading to the highest similiarty scores
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

	/**
	 * generate a distance matrix given the distances between all diseases in the database
	 * @return distance matrix
	 */
	public static double[][] allAgainstAll(){
		int[][]results = new int[kszD.size()][kszD.size()];

		//calculate information content
		setIC();
		
		int[]keys = getKeys();
		int maximum = Integer.MIN_VALUE;
		
		for(int i=0; i<keys.length; i++){
			int num = i+1;
			System.out.println(num);
			
			for(int j=i; j<keys.length; j++){
				if(i==j){
					results[i][j]=0;
				}
				else{
					int element1 = keys[i];
					int element2 = keys[j];
					LinkedList<Integer> onlySymptoms = new LinkedList<Integer>();
					for(Integer[]element : kszD.get(element1)){
						onlySymptoms.add(element[0]);
					}
					double sim = calculateSymmetricSimilarity(onlySymptoms,kszD.get(element2));
					sim = sim*100;
					int similarity = (int) Math.round(sim);
					if(similarity>maximum){
						maximum=similarity;
					}
					results[i][j]=  similarity;
					results[j][i] = similarity;
				}
			}
			//delete the pre-calculated values to ensure that the hash map doesn't get too large 
			//during the execution of the algorithm
			setCalculatedSim();
		}
		
		double[][]distanceMatrix = convertAdjacencyToDistance(results,maximum);
		
		return distanceMatrix;
	}
	
	/**
	 * @return array of keys in the ksz table
	 */
	public static int[] getKeys(){
		
		int[]keys = new int[kszD.size()];
		int i=0;
		for(int value : kszD.keySet()){
			keys[i]=value;
			i++;
		}
		
		return keys;
	}

	/**
	 * set the query to a certain list of symptom ids
	 * @param query
	 */
	public static void setQuery(LinkedList<Integer>query){
		queryIds=removeAncestors(query);
	}
	
	/**
	 * delete pre-calculated pairwise similarity values
	 */
	public static void setCalculatedSim(){
		calculatedSim = new HashMap<String,Double>();
	}
	
	public static void setIC(){
		for(int symp : symptomIds){
			if(!ic.containsKey(symp)){
				double icS = calculateIC(symp);
				ic.put(symp,icS);
			}
		}
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
		HashSet<Integer>commonAncestors = ontology.getRelevantCommonAncestors(term1,term2); 

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
	 * @return similarity score
	 */
	public static double calculateSymmetricSimilarity(LinkedList<Integer>symptoms1,LinkedList<Integer[]>symptoms2){

		HashMap<String,Double> calculatedSim = new HashMap<String,Double>();
		
		//calculate the similarity symptoms1->symptoms2
		double sim1 = 0;

		for(int symp1 : symptoms1){
			double currMax = Double.MIN_VALUE;
			int maxWeight = Integer.MIN_VALUE;
			for(Integer[]symp2:symptoms2){
				String key = "";
				double currSym = 0;
				if(symp1<symp2[0]){
					key = symp1 + "," + symp2[0];
				}
				else{
					key = symp2[0] + "," +symp1;
				}
				if(calculatedSim.containsKey(key)){
					//similarity was already calculated, use pre-calculated result
					currSym = calculatedSim.get(key);
				}
				else{
					currSym = calculatePairwiseSim(symp1,symp2[0]);
					//save calculated similarity values globally to use again later
					calculatedSim.put(key, currSym);
				}
				if(Double.compare(currMax, currSym)<0)
					currMax = currSym;
					maxWeight = symp2[1];
			}
			//System.out.println(maxWeight);
			sim1 = sim1+ currMax*(double)maxWeight/10;
		}
		sim1 = sim1/symptoms1.size();

		//calculate the similarity symptoms2->symptoms1
		double sim2 = 0;
		for(Integer[] symp1 : symptoms2){
			double currMax = Double.MIN_VALUE;
			int maxWeight = symp1[1];
			for(int symp2: symptoms1){
				String key = "";
				double currSym = 0;
				if(symp1[0]<symp2){
					key = symp1[0] + "," + symp2;
				}
				else{
					key = symp2 + "," +symp1[0];
				}
				if(calculatedSim.containsKey(key)){
					//similarity was already calculated, use pre-calculated result
					currSym = calculatedSim.get(key);
				}
				else{
					currSym = calculatePairwiseSim(symp1[0],symp2);
					//save calculated similarity values globally to use again later
					calculatedSim.put(key, currSym);
				}
				if(Double.compare(currMax, currSym)<0)
					currMax = currSym;
			}
			//System.out.println(maxWeight);
			sim2 = sim2+ currMax*(double)maxWeight/10;
		}
		sim2 = sim2/symptoms2.size();

		double similarity=(sim1+sim2)/2;
		return similarity;
	}

	/**
	 * remove symptoms whose successor(s) are also in the list
	 * @param symptoms
	 * @return list without any ancestors of query terms
	 */
	public static LinkedList<Integer>removeAncestors(LinkedList<Integer>symptoms){

		LinkedList<Integer>result = new LinkedList<Integer>();
		//generate duplicate-free list
		for(int element : symptoms){
			if(!result.contains(element)){
				result.add(element);
			}
		}

		for(int element : symptoms){
			HashSet<Integer>ancestors=ontology.getAllAncestors(element);
			ancestors.remove(element);
			for(int element1 : symptoms){
				//check if element from list is ancestors of another element and remove the element
				if(ancestors.contains(element1)&&result.contains(element1)){
					int index = result.indexOf(element1);
					result.remove(index);
				}
			}
		}
		return result;
	}

	private static LinkedList<Integer[]> removeAncestors2(LinkedList<Integer[]> symptoms){
		
		LinkedList<Integer[]>result = new LinkedList<Integer[]>();
		//generate duplicate-free list
		for(Integer[] element : symptoms){
			if(!result.contains(element)){
				result.add(element);
			}
		}

		for(Integer[] element : symptoms){
			HashSet<Integer>ancestors=ontology.getAllAncestors(element[0]);
			ancestors.remove(element[0]);
			for(Integer[] element1 : symptoms){
				//check if element from list is ancestors of another element and remove the element
				if(ancestors.contains(element1[0])&&result.contains(element1)){
					int index = result.indexOf(element1);
					result.remove(index);
				}
			}
		}
		return result;
	}
	
	/**
	 * convert a given similarity matrix to a distance matrix
	 * @param adjacency
	 * @param maximum in the similarity matrix
	 * @return distance matrix
	 */
	private static double[][] convertAdjacencyToDistance(int[][]adjacency, int maximum){
		double[][]distance = new double[adjacency.length][adjacency.length];
		for(int i=0;i<adjacency.length;i++){
			for(int j=i; j<adjacency[i].length;j++){
				if(i==j){
					distance[i][j]=0;
				}
				else{
					int value = maximum+1-adjacency[i][j];
					distance[i][j]= (double)value/100;
					distance[j][i]=distance[i][j];
				}
			}
		}
		
		return distance;
	}
}
