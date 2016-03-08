package phenomizer.algorithm;
import java.util.HashMap;
import java.util.LinkedList;

import io.FileOutputWriter;


public class ScoreDistributionSampling {
	
	private int queryLength;
	private int iterations;
	private int weight;
	
	private int [][] onto_raw;
	private HashMap<Integer, LinkedList<Integer[]>> ksz;
	private LinkedList<Integer> symptoms;
	
	private String logFile;
	private String outFile;
	
	private Ontology ontology;
	private SymptomDiseaseAssociations symptoms_and_diseases;
	private SimilarityCalculator similarityCalculator;
	private DataTransformer dt;
	
	
	public ScoreDistributionSampling(int queryLength, int iterations, int weight,
			int [][] onto_raw, HashMap<Integer, LinkedList<Integer[]>> ksz, LinkedList<Integer> symptoms,
			String logFile, String outFile){
		
		this.queryLength=queryLength;
		this.iterations =iterations;
		this.weight=weight;
		
		this.onto_raw = onto_raw;
		this.ksz = ksz;
		this.symptoms = symptoms;
		
		this.logFile = logFile;
		this.outFile = outFile;
		
		dt = new DataTransformer();
	}
	
	public void prepareData(){
		
		ontology=new Ontology(onto_raw);
		symptoms_and_diseases=dt.generateSymptomDiseaseAssociation(ontology, symptoms, ksz);
		
		if(weight==0){
			similarityCalculator = new SimilarityCalculatorNoWeight();
		}
		else if(weight==1){
			similarityCalculator = new SimilarityCalculatorOneSidedWeight();
		}
		else if(weight==2){
			similarityCalculator = new SimilarityCalculatorTwoSidedWeight();
		}
		
		PhenomizerAlgorithm p = new PhenomizerAlgorithmSampling(ontology, symptoms_and_diseases, similarityCalculator);
		p.runPhenomizer();
	}
	
	//bin size required by Phenomizer: 3
	public void startSampling(){
		startSampling(3);
	}
	
	public void startSampling(int binsize){
		
		FileOutputWriter fow_log = new FileOutputWriter(logFile);
		fow_log.writeFilelnAndFlush("start sampling");
		FileOutputWriter fow_dist = new FileOutputWriter(outFile);
		
		//symptom ids for generating random queries
		int [] allSymptoms = symptoms_and_diseases.getAllSymptomsArray();
		
		//iterate over all diseases
		int counter=1;
		for(int disease:symptoms_and_diseases.getDiseases()){
			
			fow_log.writeFilelnAndFlush("disease "+counter+" of "+symptoms_and_diseases.numberOfDiseases());
			System.out.println("disease "+counter+" of "+symptoms_and_diseases.numberOfDiseases());
			
			//data structure for saving all scores of a disease
			String[] scores = new String[iterations+1];
			scores[0]=String.valueOf(disease);
			//perform iterations
			for(int i=1; i<=iterations; i++){
				
				LinkedList<Integer> query = dt.getRandomQuery(queryLength, ontology, allSymptoms);
				double score = similarityCalculator.calculateSymmetricSimilarity(
						query, symptoms_and_diseases.getSymptoms(disease));
				score = score * 1000;
				score = Math.round(score);
				score = (double)score/1000;
				scores[i]=String.valueOf(score);
			}
			
			String res = Binner.createString(scores, binsize);
			
			fow_dist.writeFileln(res);
			
			counter++;
		}
		
		fow_log.closew();
		fow_dist.closew();
	}
	
	

}
