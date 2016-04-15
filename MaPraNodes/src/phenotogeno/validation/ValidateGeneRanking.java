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

public class ValidateGeneRanking {
	
	//data read from files for Phenomizer
	private int [][] onto_raw;
	private LinkedList<Integer> symptoms_raw;
	private HashMap<Integer, LinkedList<Integer[]>> ksz_raw;
	//data read from files for PhenoToGeno
	private LinkedList<String> genes_raw;
	private HashMap<Integer, LinkedList<String>> associations_raw;
	
	//data structures for running tools
	private PValueFolder folder;
	private DataTransformer dtPheno;
	private PhenoToGenoDataTransformer dtPTG;
	private Ontology ontology;
	private SymptomDiseaseAssociations sda;
	private DiseaseGeneAssociation dga;

	//output location
	private String outFile;
	
	//settings
	private PatientSimulator simulator;
	private DiseaseIterator iter;
	
	
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
	
	public void prepareData(){
		this.ontology = new Ontology(onto_raw);
		sda = dtPheno.generateSymptomDiseaseAssociation(ontology, symptoms_raw, ksz_raw);
		dga = dtPTG.getDiseaseGeneAssociation(genes_raw, associations_raw);
		iter.setSDA(sda);
	}

	public void simulateAndRank(){
		
		HashMap<Integer,Double> ic = new HashMap<Integer,Double>(sda.numberOfSymptoms()*3);
		HashMap<String, Double> sim = new HashMap<String, Double>(sda.numberOfSymptoms()*sda.numberOfSymptoms());
		
		FileOutputWriter fow = new FileOutputWriter(outFile);
		int counter=1;
		while(iter.hasNextId()){
			int currentDiseaseId = iter.getNextDiseaseId();
			System.out.println(counter+" out of "+iter.totalIterations()+" diseases");
			AnnotatedGene[] currentGenes = dga.getGenesForDiseaseWithID(currentDiseaseId);
			if(currentGenes.length==0){
				System.out.println("skip "+currentDiseaseId);
				counter++;
				continue;
			}
			SimulatedPatient patient =simulator.simulatePatient(currentDiseaseId, sda.getSymptoms(currentDiseaseId));
			
			PhenomizerAlgorithmWithPval phenomizer = new PhenomizerAlgorithmWithPval(
					sda.numberOfDiseases(), ontology, patient.getSymptoms(), sda, 
					new SimilarityCalculatorOneSidedWeight(), folder, new BenjaminiHochbergCorrector(), ic, sim);
			LinkedList<String[]> diseaseScores = phenomizer.runPhenomizer();
			LinkedList<ScoredDisease> inputPTG = dtPTG.getPhenomizerResultFromAlgo(diseaseScores, dga);
			
			PhenoToGenoAlgo ptg = new PhenoToGenoAlgo(inputPTG, dga);
			LinkedList<ScoredGene> geneScores = ptg.runPhenoToGene();
			dga.resetDiseaseScores();
			
			String [] correctGeneIds = new String [currentGenes.length];
			for(int i=0; i<currentGenes.length; i++){
				correctGeneIds[i]=currentGenes[i].getId();
			}
			double [] currentRanks = RankCalculator.getRanks(correctGeneIds, geneScores);
			double best = RankCalculator.getBestRank(currentRanks);
			double worst = RankCalculator.getWorstRank(currentRanks);
			double average = RankCalculator.getAverageRank(currentRanks);
			String outMessage = patient.getId()+"\t"+currentDiseaseId+"\t"+best+"\t"+worst+"\t"+average;
			fow.writeFileln(outMessage);
			System.out.println(outMessage);
			counter++;
		}
		fow.closew();
		
		
		/*
		 * initialize reusable hashmaps for Phenomizer
		 * output writer
		 * 
		 * iterate over diseases
		 * 	check if disease is appropriate : has genes!
		 * 
		 * 	if yes:
		 * 	progress output
		 * 		simulate one patient
		 * 		run Phenomizer
		 * 			Phenomizer with pvalues and weights via PhenomizerAlgorithmWithPvalue and weighted similarity
		 * 			reuse data structures
		 * 		parse result -> data transformer
		 * 		run PhenoToGeno
		 * 		calculate rank of associated genes -> save them
		 * 		clear + reuse data structures
		 * 	average ranks over patients
		 * 	output result to file
		 * 	
		 */
	}
	
	//TODO: different patient simulators, write patients to file, one simulator reads from file
	//TODO: disease iterator -> chooses disease to simulate, e.g. random, all disease, only mito diseases
	//TODO: test!


}
