package algorithm;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

import main.FileInputReader;

public class PValueGenerator {

	/**
	 * run the phenomizer algorithm and calculate p values for each result.
	 * @param num - number of results to return
	 * @param query - query of symptoms
	 * @param symptoms -list of symptoms in database
	 * @param ksz - association between diseases and symptoms
	 * @param onto - ontology
	 * @return top num results
	 */
	public static LinkedList<String[]>phenomizerWithPValues(int num, LinkedList<Integer> query, LinkedList<Integer>symptoms,
			HashMap<Integer,LinkedList<Integer[]>> ksz,int[][]onto){
		LinkedList<String[]> result = new LinkedList<String[]>();
		
		AlgoPheno.setInput(query, symptoms, ksz, onto);
		HashMap<Integer,Double> resPhenomizer = AlgoPheno.runPhenomizerWithPValue();
		
		int queryLength = AlgoPheno.getQueryLength();
		String path = PValueFolder.getPvalFile(queryLength);
		//result = getValuesForGeneralFiles(path,resPhenomizer,num);
		result = getValuesForCompressedFiles(path,resPhenomizer,num);

		return result;
	}
	
	/**
	 * for validation of phenomizer with p values for OMIM diseases
	 * @param resPhenomizer
	 * @param num
	 * @return
	 */
	public static LinkedList<String[]> getResultsWithPvaluesForOMIM(HashMap<Integer,Double> resPhenomizer, int num){
		int queryLength = AlgoPheno.getQueryLength();
		String path = PValueFolder.getPvalFile(queryLength);
		LinkedList<String []> result = getValuesForCompressedFiles(path,resPhenomizer,num);
		return result;
	}
	
	/**
	 * get the p values for a certain result from Phenomizer to be a certain line in an asmmetric p value matrix
	 * @param line
	 * @param keys
	 * @param queryLength
	 * @return
	 */
	public static double[]getNextOfAsymmetricMatrix(double[]line,int[]keys,int queryLength){
		double[]matrix = new double[line.length];
		HashMap<Integer,Double> phenoRes = new HashMap<Integer,Double>();
		HashMap<Integer,Integer>keyToIndex = new HashMap<Integer,Integer>();
		for(int i=0;i <keys.length; i++){
			phenoRes.put(keys[i], line[i]);
			keyToIndex.put(keys[i], i);
		}
		int num= keys.length;
		
		String path = PValueFolder.getPvalFile(queryLength);
		LinkedList<String[]> result = getValuesForCompressedFiles(path,phenoRes,num);
		
		for(String[] res : result){
			int index = keyToIndex.get(Integer.valueOf(res[0]));
			matrix[index]=Double.valueOf(res[2]);
		}
		
		return matrix;
	}
	
	/**
	 * get a symmetric p value matrix where the value for a pair (i,j) is the minimum p value for this pair
	 * @param adj
	 * @return
	 */
	public static double[][] getMinimumMatrix(double[][]adj){
		double[][] matrix = new double[adj.length][adj[1].length];
		for(int i=0; i<matrix.length;i++){
			for(int j=i; j<matrix.length;j++){
				if(i==j){
					matrix[i][j]=0;
				}
				else{
					if(Double.compare(adj[i][j],adj[j][i])>0){
						matrix[i][j]=adj[j][i];
					}
					else{
						matrix[i][j]=adj[i][j];
					}
					matrix[j][i]=matrix[i][j];
				}
			}
		}
		
		return matrix;
	}
	
	/**
	 * get a symmetric p value matrix where the value for a pair (i,j) is the maxmimum p value for this pair
	 * @param adj
	 * @return
	 */
	public static double[][] getMaximumMatrix(double[][]adj){
		double[][] matrix = new double[adj.length][adj[1].length];
		for(int i=0; i<matrix.length;i++){
			for(int j=i; j<matrix.length;j++){
				if(i==j){
					matrix[i][j]=0;
				}
				else{
					if(Double.compare(adj[i][j],adj[j][i])>0){
						matrix[i][j]=adj[i][j];
					}
					else{
						matrix[i][j]=adj[j][i];
					}
					matrix[j][i]=matrix[i][j];
				}
				
			}
		}
		return matrix;
	}
	
	/**
	 * get a symmetric p value matrix where the value for a pair (i,j) is the average p value for this pair
	 * @param adj
	 * @return
	 */
	public static double[][]getAverageMatrix(double[][]adj){
		double[][] matrix = new double[adj.length][adj[1].length];
		for(int i=0; i<matrix.length;i++){
			for(int j=i; j<matrix.length;j++){
				if(i==j){
					matrix[i][j]=0;
				}
				else{
					double avgVal = (adj[i][j]+adj[j][i])/2;
					avgVal = avgVal*1000;
					int tmpVal = (int)Math.round(avgVal);
					avgVal = tmpVal/1000.0;
					matrix[i][j]= avgVal;
					matrix[j][i]=matrix[i][j];
				}
			}
		}
		
		return matrix;
	}
	
	/**
	 * get the top num results and their p values for the Phenomizer result when using compressed files containing the precalculated scores
	 * @param path - path to file containing precalculated scores
	 * @param resPhenomizer - result from Phenomizer
	 * @param num - number of results to return
	 * @return
	 */
	private static LinkedList<String[]> getValuesForGeneralFiles(String path,HashMap<Integer,Double>resPhenomizer, int num){
		LinkedList<String[]> result = new LinkedList<String[]>();
		
		Comparator<String> comp = new MaxPValueComparator();
		PriorityQueue<String> maxQueue = new PriorityQueue<String>(8000,comp);
		
		FileInputReader reader = new FileInputReader(path);
		String line ="";

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
		reader.closer();
		result = resultFromQueue(maxQueue,num,numScores,numDiseases);
		
		return result;
	}
	
	/**
	 * get the top num results and their p values for the Phenomizer result when using compressed files containing the precalculated scores
	 * @param path - path to file containing precalculated scores
	 * @param resPhenomizer - result from Phenomizer
	 * @param num - number of results to return
	 * @return
	 */
	private static LinkedList<String[]> getValuesForCompressedFiles(String path, HashMap<Integer,Double> resPhenomizer, int num){
		LinkedList<String[]>result = new LinkedList<String[]>();
		
		Comparator<String> comp = new MaxPValueComparator();
		PriorityQueue<String> maxQueue = new PriorityQueue<String>(8000,comp);
		
		FileInputReader reader = new FileInputReader(path);
		String line ="";

		int numScores = 0;
		int numDiseases = 0;
		
		while((line=reader.read())!=null){
			String[] scores = line.split("\t");
			numScores = Integer.valueOf(scores[1]);
			numDiseases++;
			int disease = Integer.valueOf(scores[0]);
			double simScore = resPhenomizer.get(disease);
			int index = (int) Math.round(simScore*1000)+1;
			int counter = 0;
			if(index<scores.length){
				counter = Integer.valueOf(scores[index]);
			}
			String tmpRes = counter+","+simScore+","+disease;
			maxQueue.add(tmpRes);
		}
		reader.closer();
		result = resultFromQueue(maxQueue,num,numScores,numDiseases);
		
		return result;
	}
	
	/**
	 * get the top num results from a max priority queue
	 * @param queue
	 * @param num
	 * @param numScores
	 * @param numDiseases
	 * @return
	 */
	private static LinkedList<String[]>resultFromQueue(PriorityQueue<String>queue,int num, int numScores,int numDiseases){
		LinkedList<String[]> result = new LinkedList<String[]>();
		
		String score = "";
		String p = "";
		if(num>queue.size()){
			num = queue.size();
		}
		
		double maxPValue = Double.MIN_VALUE;
		double lastPValue = Double.MIN_VALUE;
		int currIndex = 1;
		int currRank = 0;
		
		for(int i=0; i<num; i++){
			String currEl = queue.remove();
			String[]parts = currEl.split(",");
			String[]res = new String[3];
			res[0]=parts[2];
			res[1]=parts[1];
			double pValue = (double)Integer.valueOf(parts[0])/numScores;
			if(Double.compare(lastPValue, pValue)<0){
				lastPValue = pValue;
				currRank += currIndex;
				currIndex = 0;
			}
			
			//Bonferroni-Holm
			pValue = pValue*(numDiseases-currRank+1);
			
			//Benjamini-Hochberg
			//pValue = (numDiseases/currRank)*pValue;
			
			if(Double.compare(pValue, 1)>0){
				pValue=1;
			}
			else if(Double.compare(pValue, maxPValue)<0){
				pValue = maxPValue;
			}
			
			if(Double.compare(pValue, maxPValue)>0){
				maxPValue = pValue;
			}
			currIndex++;
			
			res[2]=pValue+"";
			result.add(res);
			
			score=res[1];
			p=res[2];
		}
		
		while(!queue.isEmpty()&&queue.element().split(",")[1].equals(score)&&queue.element().split(",")[0].equals(p)){
			String currEl = queue.remove();
			String[]parts = currEl.split(",");
			String[]res = new String[3];
			res[0]=parts[2];
			res[1]=parts[1];
			double pValue = (double)Integer.valueOf(parts[0])/numScores;
			pValue = pValue*(numDiseases-currRank+1);
			if(Double.compare(pValue, 1)>0){
				pValue=1;
			}
			else if(Double.compare(pValue, maxPValue)<0){
				pValue = maxPValue;
			}

			res[2]=pValue+"";
			result.add(res);
		}
		
		return result;
	}

}
