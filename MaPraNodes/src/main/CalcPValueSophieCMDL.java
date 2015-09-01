package main;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import algorithm.AlgoPheno;
import algorithm.Binner;
import algorithm.FrequencyConverter;

public class CalcPValueSophieCMDL {


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int queryLength = Integer.valueOf(args[0]);
		int iterations = Integer.valueOf(args[1]);
		String ontoIn = args[2];
		String kszIn = args[3];
		String symptomsIn = args[4];
		String output = args[5];
		String progress = args[6];
		
		HashMap<Integer,LinkedList<String[]>>kszTmp = FileUtilities.readInKSZFrequency(kszIn);
		HashMap<Integer,LinkedList<Integer[]>>ksz=FrequencyConverter.convertAll(kszTmp);
		
//		HashMap<Integer,LinkedList<Integer>>kszTmp = FileUtilities.readInKSZ(kszIn);
//		HashMap<Integer,LinkedList<Integer[]>>ksz= addWeights(kszTmp);
		
		LinkedList<Integer> symptoms = FileUtilities.readInSymptoms(symptomsIn);
		int[][]ontology = FileUtilities.readInOntology(ontoIn);
		
		LinkedList<Integer>query = new LinkedList<Integer>();
		AlgoPheno.setInput(query, symptoms, ksz, ontology);
		AlgoPheno.setIC();
		int[] ids = getIds(symptoms);
		
		//int all = ksz.size()*iterations;
		int disNum = 0;
		FileUtilities.writeString(progress, "start\n");
		
		for(int disease : ksz.keySet()){
			disNum++;
			LinkedList<Double> scores = new LinkedList<Double>();
			FileUtilities.writeStringToExistingFile(progress, "\n"+disNum+" out of "+ksz.size());
			for(int i=0; i<iterations; i++){
//				if(i%10000==0){
//					FileUtilities.writeStringToExistingFile(progress, i+"\t");
//				}
//				int currIter = i+1+(disNum-1)*iterations;
//				double percentage = (double) currIter/all;
//				System.out.println(percentage);
				
				query = calculateQuery(ids,queryLength);
				AlgoPheno.setQuery(query);
				double score = AlgoPheno.calculateSymmetricSimilarity(query, ksz.get(disease),false);
				score = score * 100;
				score = Math.round(score);
				score = (double)score/100;
				scores.add(score);
			}
			//AlgoPheno.setCalculatedSim();
			String res = disease+listToString(scores);
			res = Binner.createString(res.split("\t"));
			res+="\n";
			//System.out.println(res);
			if(disNum==1){
				FileUtilities.writeString(output, res);
			}
			else{
				FileUtilities.writeStringToExistingFile(output, res);
			}
			
		}

	}

	public static HashMap<Integer, LinkedList<Integer []>> addWeights (HashMap<Integer, LinkedList<Integer>> ksz){

		HashMap<Integer, LinkedList<Integer[]>> res = new HashMap<Integer, LinkedList<Integer []>>(ksz.size()*3);
		for(Integer k: ksz.keySet()){
			LinkedList<Integer []> list = new LinkedList<Integer[]>();
			res.put(k, list);
			for(int i: ksz.get(k)){
				Integer [] symp_and_weight = new Integer [2];
				symp_and_weight[0]=i;
				symp_and_weight[1]=10;
				list.add(symp_and_weight);
			}			
		}
		return res;
	}
	
	private static LinkedList<Integer> calculateQuery(int [] ids, int length){
		LinkedList<Integer> query = new LinkedList<Integer>();
		
		while(query.size()<length){
			Random rnd = new Random();
			int next = rnd.nextInt(ids.length);
			int nextId = ids[next];
			query.add(nextId);
			query = AlgoPheno.removeAncestors(query);
		}
		return query;
	}
	
	private static int[] getIds(LinkedList<Integer> listIds){
		int[] ids = new int[listIds.size()];
		for(int i=0; i<ids.length; i++){
			ids[i]=listIds.get(i);
		}
		
		return ids;
	}
	
	private static String listToString(LinkedList<Double> list){
		StringBuilder sb = new StringBuilder();
		for(double el : list){
			sb.append("\t"+el);
		}
		return sb.toString();
	}

	
	
}
