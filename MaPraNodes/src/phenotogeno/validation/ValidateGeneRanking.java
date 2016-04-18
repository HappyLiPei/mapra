package phenotogeno.validation;

import java.util.HashMap;
import java.util.LinkedList;

import io.FileOutputWriter;
import phenomizer.algorithm.BenjaminiHochbergCorrector;
import phenomizer.algorithm.DataTransformer;
import phenomizer.algorithm.Ontology;
import phenomizer.algorithm.PValueFolder;
import phenomizer.algorithm.PhenomizerAlgorithmWithPval;
import phenomizer.algorithm.SimilarityCalculatorOneSidedWeight;
import phenomizer.algorithm.SymptomDiseaseAssociations;
import phenotogeno.algo.AnnotatedGene;
import phenotogeno.algo.DiseaseGeneAssociation;
import phenotogeno.algo.PhenoToGenoAlgo;
import phenotogeno.algo.PhenoToGenoDataTransformer;
import phenotogeno.algo.ScoredDisease;
import phenotogeno.algo.ScoredGene;

/** structure for running a validation with simulated data on PhenoToGeno*/
public class ValidateGeneRanking {
	
	/** ontology of symptoms read from a file read by FileUtilitiesPhenomizer*/
	private int [][] onto_raw;
	/** list of symptoms read from a file read by FileUtilitiesPhenomizer*/
	private LinkedList<Integer> symptoms_raw;
	/** associations between diseases and symptoms from a file read by FileUtilitiesPhenomizer*/
	private HashMap<Integer, LinkedList<Integer[]>> ksz_raw;
	/** list of gene ids read from a file read by FileUtilitiesPTG*/
	private LinkedList<String> genes_raw;
	/** associations between diseases and genes from a file read by FileUtilitesPTG*/
	private HashMap<Integer, LinkedList<String>> associations_raw;
	
	/** pvalue folder object for PhenomizerWithPvalues*/
	private PValueFolder folder;
	/** data transformer for generating input for Phenomizer*/
	private DataTransformer dtPheno;
	/** data transformer for generating input for PhenoToGeno*/
	private PhenoToGenoDataTransformer dtPTG;
	/** Ontology object for Phenomizer*/
	private Ontology ontology;
	/** SymptomDiseaseAssociations object for Phenomizer*/
	private SymptomDiseaseAssociations sda;
	/** DiseaseGeneassociation object for PhenoToGeno*/
	private DiseaseGeneAssociation dga;

	/** file to write output to*/
	private String outFile;
	
	/** Simulator for generating patients*/
	private PatientSimulator simulator;
	/** iterator determining which disease is simulated next*/
	private DiseaseIterator iter;
	
	/**
	 * generates a object for validation PhenoToGenoe
	 * @param onto_raw ontology of symptoms read from a file read by FileUtilitiesPhenomizer
	 * @param symptoms_raw list of symptoms read from a file read by FileUtilitiesPhenomizer
	 * @param ksz_raw associations between diseases and symptoms from a file read by FileUtilitiesPhenomizer
	 * @param genes_raw list of gene ids read from a file read by FileUtilitiesPTG
	 * @param associations_raw associations between diseases and genes from a file read by FileUtilitesPTG
	 * @param pvalFolder path to folder with sampled score distributions for p value calculation
	 * @param simulator Simulator for generating patients
	 * @param iter iterator determining which disease is simulated next 
	 * @param outfile path to file to which all output is written
	 */
	public ValidateGeneRanking(int [][] onto_raw, LinkedList<Integer> symptoms_raw,
			HashMap<Integer, LinkedList<Integer[]>> ksz_raw, LinkedList<String> genes_raw,
			HashMap<Integer, LinkedList<String>> associations_raw, String pvalFolder,
			PatientSimulator simulator, DiseaseIterator iter, String outfile){
		
		this.onto_raw = onto_raw;
		this.symptoms_raw = symptoms_raw;
		this.ksz_raw = ksz_raw;
		this.genes_raw = genes_raw;
		this.associations_raw = associations_raw;
		
		this.simulator = simulator;
		this.iter=iter;
		folder = new PValueFolder(pvalFolder);
		dtPheno = new DataTransformer();
		dtPTG = new PhenoToGenoDataTransformer();
		
		this.outFile = outfile;
	}
	
	/** 
	 * initializes the validation by preparing all data structures for Phenomizer and PhenoToGeno 
	 */
	public void prepareData(){
		this.ontology = new Ontology(onto_raw);
		sda = dtPheno.generateSymptomDiseaseAssociation(ontology, symptoms_raw, ksz_raw);
		dga = dtPTG.getDiseaseGeneAssociation(genes_raw, associations_raw);
		iter.setSDA(sda);
	}
	
	/** 
	 * method to run the validation with simulated patients
	 */
	public void simulateAndRank(){
		
		//initialize reusable hashmaps for Phenomizer
		HashMap<Integer,Double> ic = new HashMap<Integer,Double>(sda.numberOfSymptoms()*3);
		HashMap<String, Double> sim = new HashMap<String, Double>(sda.numberOfSymptoms()*sda.numberOfSymptoms());
		
		//initialize outputwriter
		FileOutputWriter fow = new FileOutputWriter(outFile);
		
		int counter=1;
		//iterate over diseases -> iterator object
		while(iter.hasNextId()){
			int currentDiseaseId = iter.getNextDiseaseId();
			//progress output
			System.out.println(counter+" out of "+iter.totalIterations()+" diseases");
			AnnotatedGene[] currentGenes = dga.getGenesForDiseaseWithID(currentDiseaseId);
			
			//check if disease is appropriate : it should have some genes
			if(currentGenes.length==0){
				System.out.println("skip "+currentDiseaseId+": no genes");
				counter++;
				continue;
			}
			
			//simulate one patient
			SimulatedPatient patient =simulator.simulatePatient(currentDiseaseId, sda.getSymptoms(currentDiseaseId));
			//check if simulation was successful
			if(patient==null){
				System.out.println("skip "+currentDiseaseId+": empty query");
				counter++;
				continue;
			}
			
			//run Phenomizer: Phenomizer with pvalues and weights, reuse data structures
			PhenomizerAlgorithmWithPval phenomizer = new PhenomizerAlgorithmWithPval(
					sda.numberOfDiseases(), ontology, patient.getSymptoms(), sda, 
					new SimilarityCalculatorOneSidedWeight(), folder, new BenjaminiHochbergCorrector(), ic, sim);
			LinkedList<String[]> diseaseScores = phenomizer.runPhenomizer();
			//parse result -> data transformer -> input PTG
			LinkedList<ScoredDisease> inputPTG = dtPTG.getPhenomizerResultFromAlgo(diseaseScores, dga);
			
			// run PhenoToGeno
			PhenoToGenoAlgo ptg = new PhenoToGenoAlgo(inputPTG, dga);
			LinkedList<ScoredGene> geneScores = ptg.runPhenoToGene();
			//clear dga data structure for reuse
			dga.resetDiseaseScores();
			
			// calculate rank of associated genes
			String [] correctGeneIds = new String [currentGenes.length];
			for(int i=0; i<currentGenes.length; i++){
				correctGeneIds[i]=currentGenes[i].getId();
			}
			double [] currentRanks = RankCalculator.getRanks(correctGeneIds, geneScores);
			double best = RankCalculator.getBestRank(currentRanks);
			double worst = RankCalculator.getWorstRank(currentRanks);
			double average = RankCalculator.getAverageRank(currentRanks);
			
			//output result to file
			String outMessage = patient.getId()+"\t"+currentDiseaseId+"\t"+best+"\t"+worst+"\t"+average;
			fow.writeFileln(outMessage);
			System.out.println(outMessage);
			counter++;
		}
		fow.closew();

	}
	
	//TODO: different patient simulators, write patients to file, one simulator reads from file
	//TODO: disease iterator -> chooses disease to simulate, e.g. random, all disease, only mito diseases


}
