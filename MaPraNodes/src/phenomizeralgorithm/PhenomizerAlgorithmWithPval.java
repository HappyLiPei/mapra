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
		PriorityQueue<String> maxQueue = new PriorityQueue<String>(sda.numberOfDiseases(),comp);
		
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
		
		for(int i=0; i<num; i++){
			String currEl = queue.remove();
			String[]parts = currEl.split(",");
			String[]res = new String[3];
			res[0]=parts[2];
			res[1]=parts[1];
			
			//uncorrected pvalue
			double pValue = (double)Integer.valueOf(parts[0])/numScores;
			res[2]=pValue+"";
			
			result.add(res);
			
			//for entry into while loop
			score=res[1];
			p=res[2];
		}
		
		//get elements with same pvalue and score than last element (num-th element)
		while(!queue.isEmpty()&&queue.element().split(",")[1].equals(score)&&queue.element().split(",")[0].equals(p)){
			String currEl = queue.remove();
			String[]parts = currEl.split(",");
			String[]res = new String[3];
			res[0]=parts[2];
			res[1]=parts[1];
			
			//uncorrected pvalue
			double pValue = (double)Integer.valueOf(parts[0])/numScores;
			res[2]=pValue+"";
			
			result.add(res);
		}
		
		double [] pvals = new double[result.size()];
		//current position in pvals array
		int pos=0;
		//iterate over all diseases in result
		for(String[] r : result){
			pvals[pos]=Double.valueOf(r[2]);
			pos++;
		}
		//correct pvalues
		double [] corrected_pvals = corrector.correctPVals(pvals,sda.numberOfDiseases());
		//add corrected pvalues to the result
		pos=0;
		for(String[] r : result){
			r[2]=String.valueOf(corrected_pvals[pos]);
			pos++;
		}
		
		return result;
	}
	
}
