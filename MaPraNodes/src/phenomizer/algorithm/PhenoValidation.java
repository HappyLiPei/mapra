package phenomizer.algorithm;

import java.util.HashMap;
import java.util.LinkedList;

public class PhenoValidation {
	
	private static HashMap<Integer,LinkedList<Integer>> queries = new HashMap<Integer,LinkedList<Integer>>();
	
	public static void validatePheno(LinkedList<Integer>symptoms,
			HashMap<Integer,LinkedList<Integer[]>> ksz,int[][]onto,String path){
		//TODO: adapt to new structure
//		LinkedList<Integer>query = new LinkedList<Integer>();
//		
//		AlgoPheno.setInput(query, symptoms, ksz, onto);
//		
//		int num = 1;
//		for(Integer key : queries.keySet()){
//			System.out.println(num);
//			
//			LinkedList<Integer> nextQuery = queries.get(key);
//			AlgoPheno.setQuery(nextQuery);
//			LinkedList<String[]>res = AlgoPheno.runPhenomizer(ksz.size(),true);
//			double rank = calculateRank(key.toString(),res);
//			String result = key+"\t"+rank+"\n";
//			io.FileUtilities.writeStringToExistingFile(path, result);
//			num++;
//			AlgoPheno.setCalculatedSim();
//		}
	}

	public static void setQueries(HashMap<Integer,LinkedList<Integer[]>>ksz){
		for(int key : ksz.keySet()){
			LinkedList<Integer[]> symps = ksz.get(key);
			LinkedList<Integer>nextQuery = new LinkedList<Integer>();
			for(Integer[] el : symps){
				if(el[1]>=10){
					nextQuery.add(el[0]);
				}
			}
			if(nextQuery.size()>0){
				queries.put(key, nextQuery);
			}
			
		}
		System.out.println(queries.size());
	}
	
	private static double calculateRank(String disease_id, LinkedList<String []> pheno_res){
		
		String prev="";
		int count = 1;
		boolean found=false;
		int rank =0;
		
		//score[0]: disease_id, score[1]: score
		for(String [] score: pheno_res){
			if(!score[1].equals(prev)){
				if(found){
					//System.out.println(rank+"\t"+count);
					break;
				}
				rank++;
				prev=score[1];
				count=1;
			}
			else{
				rank++;
				count++;
			}
			if(score[0].equals(disease_id)){
				found=true;
			}
			//System.out.println(score[0]+"\t"+score[1]+"\t"+rank+"\t"+count);
		}
		
		if(!found){
			System.out.println("Error: could not calculate rank");
		}
		
		int sum=0;
		for(int i=0; i<count; i++){
			sum+=rank-i;
		}
		double d = ((double)sum)/count;
		//System.out.println(disease_id+" Rang: "+d);
		return d;
	}
}
