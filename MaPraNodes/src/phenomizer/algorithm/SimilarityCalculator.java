package phenomizer.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public abstract class SimilarityCalculator {
	
	private Ontology ontology;
	private HashMap<Integer,Double> ic;
	private HashMap<String,Double> calculatedSim;
	
	protected void setOntology(Ontology ontology){
		this.ontology=ontology;
	}
	
	protected void setICValues(HashMap<Integer,Double> ic){
		this.ic=ic;
	}
	
	protected void setCalculatedSim(HashMap<String,Double> calculatedSim){
		this.calculatedSim=calculatedSim;
	}
	
	protected abstract double calculateSymmetricSimilarity(LinkedList<Integer>query ,LinkedList<Integer[]> disease);
	
	protected double similarityQueryToDisease(LinkedList<Integer>query ,LinkedList<Integer[]> disease){
				
		double sim=0;
		
		//iterate over all query symptoms
		for(int symp1 : query){
			
			double currMax = Double.MIN_VALUE;
			
			//find the most similar disease symptom for the current query symptom
			for(Integer[]symp2:disease){
				
				String key = generateKey(symp1, symp2[0]);
				double currSym = 0;
				
				//similarity was already calculated, use pre-calculated result
				if(calculatedSim.containsKey(key)){
					currSym = calculatedSim.get(key);
				}
				//save calculated similarity values globally to use again later
				else{
					currSym = calculatePairwiseSim(symp1,symp2[0]);
					calculatedSim.put(key, currSym);
				}
				
				if(Double.compare(currMax, currSym)<0){
					currMax=currSym;
				}
			}
			
			sim = sim+currMax;
		}
		//take average
		sim = sim/query.size();
		return sim;
	}
	
	protected double weightedSimilarityQueryToDisease(LinkedList<Integer>query ,LinkedList<Integer[]> disease){
		
		double sim=0;
		
		//iterate over all query symptoms
		for(int symp1 : query){
			
			double currMax = Double.MIN_VALUE;
			int maxWeight = Integer.MIN_VALUE;
			
			//find the most similar disease symptom for the current query symptom
			for(Integer[]symp2:disease){
				
				String key = generateKey(symp1, symp2[0]);
				double currSym = 0;
				
				//similarity was already calculated, use pre-calculated result
				if(calculatedSim.containsKey(key)){
					currSym = calculatedSim.get(key);
				}
				//save calculated similarity values globally to use again later
				else{
					currSym = calculatePairwiseSim(symp1,symp2[0]);
					calculatedSim.put(key, currSym);
				}
				
				if(Double.compare(currMax, currSym)<0){
					currMax = currSym;
					maxWeight = symp2[1];
				}
			}
			sim = sim+ currMax*(double)maxWeight/10;
		}
		
		sim = sim/query.size();
		return sim;
	}
	
	protected double similarityDiseaseToQuery(LinkedList<Integer>query ,LinkedList<Integer[]> disease){
		
		double sim=0;
		
		for(Integer[] symp1 : disease){
			
			double currMax = Double.MIN_VALUE;
			//int maxWeight = symp1[1];
			
			for(int symp2: query){
				
				String key = generateKey(symp1[0], symp2);
				double currSym = 0;
				
				//similarity was already calculated, use pre-calculated result
				if(calculatedSim.containsKey(key)){
					currSym = calculatedSim.get(key);
				}
				//save calculated similarity values globally to use again later
				else{
					currSym = calculatePairwiseSim(symp1[0],symp2);
					calculatedSim.put(key, currSym);
				}
				if(Double.compare(currMax, currSym)<0)
					currMax = currSym;
			}
			
			sim= sim+currMax;
		}
		sim = sim/disease.size();
		return sim;

	}
	
	protected double weightedSimilarityDiseaseToQuery(LinkedList<Integer>query ,LinkedList<Integer[]> disease){
		
		double sim=0;
		
		//iterate over all disease symptoms
		for(Integer[] symp1 : disease){
			
			double currMax = Double.MIN_VALUE;
			
			//find most similar query symptom for current disease symptom
			for(int symp2: query){
				
				String key = generateKey(symp1[0], symp2);
				double currSym = 0;
				
				//similarity was already calculated, use pre-calculated result
				if(calculatedSim.containsKey(key)){
					currSym = calculatedSim.get(key);
				}
				//save calculated similarity values globally to use again later
				else{
					currSym = calculatePairwiseSim(symp1[0],symp2);
					calculatedSim.put(key, currSym);
				}
				
				if(Double.compare(currMax, currSym)<0){
					currMax = currSym;
				}
			}
			sim = sim+ currMax*(double)symp1[1]/10;

		}
		sim = sim/disease.size();
		return sim;

	}
	
	private double calculatePairwiseSim(int term1, int term2){

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
	
	private String generateKey(int symp1, int symp2){
		String key = "";
		if(symp1<symp2){
			key = symp1 + "," + symp2;
		}
		else{
			key = symp2 + "," +symp1;
		}
		return key;
	}
	
	
	
	
	

}
