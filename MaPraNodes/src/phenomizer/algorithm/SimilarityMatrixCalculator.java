package phenomizer.algorithm;

import java.util.HashMap;
import java.util.LinkedList;

import io.FileOutputWriter;

public class SimilarityMatrixCalculator {
	
	private int [][] onto_raw;
	private LinkedList<Integer> symptoms_raw;
	private HashMap<Integer, LinkedList<Integer[]>> ksz_raw;
	
	private Ontology ontology;
	private SymptomDiseaseAssociations sda;
	
	private String matrixOutFile;
	private int [] diseaseIdOrder;
	private HashMap<Integer, Integer> IdToPos;
	private LinkedList<Integer>[] queries;
	
	/**
	 * generates an SimilarityMatrixCalculator for generating an all-against-all comparison of the diseases in PhenoDis,
	 * the resulting similarity matrix is symmetrical and contains unweighted similarity scores for all pairs of diseases
	 * @param onto_raw
	 * 		ontology of PhenoDis symptoms
	 * @param symptoms_raw
	 * 		list of all symptom ids in PhenoDis
	 * @param ksz_raw
	 * 		associations between disease id and a list of symptom ids
	 * @param outFile
	 * 		file to which the matrix is written
	 */
	public SimilarityMatrixCalculator(int [][] onto_raw, LinkedList<Integer> symptoms_raw,
			HashMap<Integer, LinkedList<Integer[]>> ksz_raw, String outFile){
		
		this.onto_raw = onto_raw;
		this.symptoms_raw = symptoms_raw;
		this.ksz_raw = ksz_raw;
		this.matrixOutFile = outFile;
	}
	
	/**
	 * method to create all required data structures for calculating the matrix,
	 * this method should be called before calculateSimilarityMatrix
	 */
	@SuppressWarnings("unchecked")
	public void prepareData(){
		
		//get basic data structures
		ontology = new Ontology(this.onto_raw);
		DataTransformer dt = new DataTransformer();
		sda = dt.generateSymptomDiseaseAssociation(this.ontology, this.symptoms_raw, this.ksz_raw);
		
		//disease id order
		diseaseIdOrder = new int [sda.numberOfDiseases()];
		IdToPos = new HashMap<Integer, Integer>(sda.numberOfDiseases()*3);
		int pos = 0;
		for(int disId: sda.getDiseases()){
			diseaseIdOrder[pos] = disId;
			IdToPos.put(disId, pos);
			pos++;
		}
		
		//generate queries
		queries = new LinkedList[diseaseIdOrder.length];
		for(int i = 0; i<diseaseIdOrder.length; i++){
			LinkedList<Integer []> annotation =sda.getSymptoms(diseaseIdOrder[i]);
			LinkedList<Integer> query = new LinkedList<Integer>();
			for(Integer [] symptom : annotation){
				query.add(symptom[0]);
			}
			queries[i] = query;
		}
		
	}
	
	/**
	 * method that calculates the similarity matrix and writes it to a file
	 */
	public void calculateSimilarityMatrix(){
		
		FileOutputWriter fow = new FileOutputWriter(matrixOutFile);
		
		String header ="id";
		for(int id:diseaseIdOrder){
			header+="\t"+id;
		}
		fow.writeFileln(header);
		
		SimilarityCalculatorNoWeight sc = new SimilarityCalculatorNoWeight();
		HashMap<Integer, Double> ic = new HashMap<Integer, Double> (sda.numberOfSymptoms()*3);
		HashMap<String, Double> sim = new HashMap<String, Double> (sda.numberOfSymptoms()*sda.numberOfSymptoms());
		
		for(int i=0; i<diseaseIdOrder.length; i++){
			
			System.out.println((i+1) + " out of " + diseaseIdOrder.length + " diseases");
			
			PhenomizerAlgorithmNoPvalue pheno= new PhenomizerAlgorithmNoPvalue(
					diseaseIdOrder.length, ontology, queries[i], sda, sc, ic, sim);
			LinkedList<String[]> res = pheno.runPhenomizer();
			
			String[] ScoresOrdered = new String[res.size()];
			for(String [] array: res){
				int matrixPos = IdToPos.get(Integer.valueOf(array[0]));
				ScoresOrdered[matrixPos] = array[1];
			}
			String line=diseaseIdOrder[i]+"";
			for(String el: ScoresOrdered){
				line+="\t"+el;
			}
			fow.writeFileln(line);	
		}
		
		fow.closew();
		
	}
	
	

}
