package main;

import io.FileUtilities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import algorithm.AlgoPheno;
import algorithm.Binner;
import algorithm.FrequencyConverter;

public class CalcPValue {


	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		int queryLength = Integer.valueOf(args[0]);
		int iterations = Integer.valueOf(args[1]);
		String weight =args[2]; // noweight, weight, asymmweight
		String ontoIn = args[3];
		String kszIn = args[4];
		String symptomsIn = args[5];
		String output = args[6];
		String progress = args[7];
		
		if(weight.equals("noweight")){
			System.out.println("not weighted");
		}
		else if(weight.equals("weight")){
			System.out.println("symmetric weight");
		}
		else if(weight.equals("asymmweight")){
			System.out.println("asymmetric weight");
		}
		else{
			throw new Exception("Incorrect argument at pos 2!");
		}
		
		HashMap<Integer, LinkedList<Integer[]>> ksz = new HashMap<Integer, LinkedList<Integer[]>>();
		if(weight.equals("noweight")){
			HashMap<Integer,LinkedList<Integer>>kszTmp = FileUtilities.readInKSZ(kszIn);
			ksz= addWeights(kszTmp);
		}
		else{
			HashMap<Integer,LinkedList<String[]>>kszTmp = FileUtilities.readInKSZFrequency(kszIn);
			ksz=FrequencyConverter.convertAll(kszTmp);
		}
		
		LinkedList<Integer> symptoms = FileUtilities.readInSymptoms(symptomsIn);
		int[][]ontology = FileUtilities.readInOntology(ontoIn);
		
		boolean symmetry =true;
		if(weight.equals("asymmweight")){
			symmetry=false;
		}
		
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
				query = calculateQuery(ids,queryLength);
				AlgoPheno.setQuery(query);
				double score = AlgoPheno.calculateSymmetricSimilarity(query, ksz.get(disease),symmetry);
				score = score * 100;
				score = Math.round(score);
				score = (double)score/100;
				scores.add(score);
			}
			String res = disease+listToString(scores);
			res = Binner.createString(res.split("\t"));
			res+="\n";
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
