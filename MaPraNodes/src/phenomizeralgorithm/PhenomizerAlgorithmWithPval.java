package phenomizeralgorithm;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

import io.FileInputReader;

public class PhenomizerAlgorithmWithPval extends PhenomizerAlgorithm {
	
	private PValueFolder folder;
	private PValueCorrector corrector;

	public PhenomizerAlgorithmWithPval(int num, Ontology ontology, LinkedList<Integer> queryIds,
			SymptomDiseaseAssociations sda, SimilarityCalculator similarityCalculator, PValueFolder folder,
			PValueCorrector corrector) {
		
		super(num, ontology, queryIds, sda, similarityCalculator);
		this.folder=folder;
		this.corrector = corrector;
	}

	@Override
	public LinkedList<String[]> runPhenomizer() {
		
		setIC();
		HashMap<Integer,Double> resPhenomizer = scoreQuery();
		int queryLength = queryIds.size();
		String path = folder.getPvalFile(queryLength);
		LinkedList<String[]>result = getValuesForCompressedFiles(path,resPhenomizer);
		
		return result;
	}
	
	private HashMap<Integer,Double> scoreQuery(){
		
		HashMap<Integer,Double> result = new HashMap<Integer,Double>(sda.numberOfDiseases()*3);
		for(int disease : sda.getDiseases()){
			double similarity = similarityCalculator.calculateSymmetricSimilarity(queryIds,sda.getSymptoms(disease));
			similarity = similarity*1000;
			similarity = Math.round(similarity);
			similarity = similarity/1000;
			result.put(disease,similarity);
		}
		
		return result;
	}
	
	private LinkedList<String[]> getValuesForCompressedFiles(String path, HashMap<Integer,Double> resPhenomizer){
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
		result = resultFromQueue(maxQueue,numScores,numDiseases);
		
		return result;
	}
	
	private LinkedList<String[]>resultFromQueue(PriorityQueue<String>queue, int numScores,int numDiseases){
		LinkedList<String[]> result = new LinkedList<String[]>();
		
		String score = "";
		String p = "";
		if(num>queue.size()){
			num = queue.size();
		}
		
		double maxPValue = Double.MIN_VALUE;
		double lastPValue = -1.0;
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
			
//			//TODO: corrector object for different methods
//			//Bonferroni-Holm
//			//pValue = pValue*(numDiseases-currRank+1);
//			
//			//Benjamini-Hochberg
//			pValue = (numDiseases/currRank)*pValue;
			
			pValue= corrector.correctPval(pValue, numDiseases, currRank);
			
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
