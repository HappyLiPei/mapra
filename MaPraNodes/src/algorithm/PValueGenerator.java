package algorithm;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

import main.FileInputReader;

public class PValueGenerator {

	public static LinkedList<String[]>phenomizerWithPValues(int num, LinkedList<Integer> query, LinkedList<Integer>symptoms,
			HashMap<Integer,LinkedList<Integer[]>> ksz,int[][]onto){
		LinkedList<String[]> result = new LinkedList<String[]>();
		
		AlgoPheno.setInput(query, symptoms, ksz, onto);
		HashMap<Integer,Double> resPhenomizer = AlgoPheno.runPhenomizerWithPValue();
		
		int queryLength = AlgoPheno.getQueryLength();
		String path = PValueFolder.getPvalFile(queryLength);
		FileInputReader reader = new FileInputReader(path);
		String line ="";
		
		Comparator<String> comp = new MaxPValueComparator();
		PriorityQueue<String> maxQueue = new PriorityQueue<String>(6000,comp);
		
		int numScores = 0;
		int numDiseases = 0;
		
		while((line=reader.read())!=null){
			String[] scores = line.split("\t");
			numScores = scores.length;
			numDiseases++;
			int disease = Integer.valueOf(scores[0]);
			double simScore = resPhenomizer.get(disease);
			int counter = 0;
			for(int i=1; i<scores.length; i++){
				if(Double.compare(Double.valueOf(scores[i]),simScore)>=0){
					counter++;
				}
			}
			String tmpRes = counter+","+simScore+","+disease;
			maxQueue.add(tmpRes);
		}
		
		String score = "";
		String p = "";
		if(num>maxQueue.size()){
			num = maxQueue.size();
		}
		
		for(int i=0; i<num; i++){
			String currEl = maxQueue.remove();
			String[]parts = currEl.split(",");
			String[]res = new String[3];
			res[0]=parts[2];
			res[1]=parts[1];
			double pValue = (double)Integer.valueOf(parts[0])/numScores;
			pValue = pValue*numDiseases;
			if(Double.compare(pValue, 1)>0){
				pValue=1;
			}
			res[2]=pValue+"";
			result.add(res);
			
			score=res[1];
			p=res[2];
		}
		while(!maxQueue.isEmpty()&&maxQueue.element().split(",")[1].equals(score)&&maxQueue.element().split(",")[0].equals(p)){
			String currEl = maxQueue.remove();
			String[]parts = currEl.split(",");
			String[]res = new String[3];
			res[0]=parts[2];
			res[1]=parts[1];
			double pValue = (double)Integer.valueOf(parts[0])/numScores;
			pValue = pValue*numDiseases;
			if(Double.compare(pValue, 1)>0){
				pValue=1;
			}
			res[2]=pValue+"";
			result.add(res);
		}

		return result;
	}
}
