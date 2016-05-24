package phenotogeno.validation;

import java.util.HashMap;
import java.util.LinkedList;

import geneticnetwork.algorithm.DataTransformerGeneticNetwork;
import geneticnetwork.algorithm.MatrixVectorBuilder;
import geneticnetwork.algorithm.NetworkScoreAlgorithm;
import geneticnetwork.algorithm.RandomWalkWithRestart;
import geneticnetwork.datastructures.Edge;
import geneticnetwork.datastructures.ScoredGenes;
import geneticnetwork.io.FileUtilitiesGeneticNetwork;
import io.FileOutputWriter;
import phenomizer.algorithm.BenjaminiHochbergCorrector;
import phenomizer.algorithm.DataTransformer;
import phenomizer.algorithm.Ontology;
import phenomizer.algorithm.PValueFolder;
import phenomizer.algorithm.PhenomizerAlgorithmWithPval;
import phenomizer.algorithm.SimilarityCalculatorOneSidedWeight;
import phenomizer.algorithm.SymptomDiseaseAssociations;
import phenomizer.io.FileUtilitiesPhenomizer;
import phenotogeno.algo.AnnotatedGene;
import phenotogeno.algo.DiseaseGeneAssociation;
import phenotogeno.algo.PhenoToGenoAlgo;
import phenotogeno.algo.PhenoToGenoDataTransformer;
import phenotogeno.algo.ScoredDisease;
import phenotogeno.algo.ScoredGene;
import phenotogeno.io.FileUtilitiesPTG;

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
	/** unweighted or weighted genetic network from a file read by FileUtilitesGeneticNetwork*/
	private String[][] network_raw;
	
	/** pvalue folder object for PhenomizerWithPvalues*/
	private PValueFolder folder;
	/** data transformer for generating input for Phenomizer*/
	private DataTransformer dtPheno;
	/** data transformer for generating input for PhenoToGeno*/
	private PhenoToGenoDataTransformer dtPTG;
	/** data transformer for generating input for Network Score */
	private DataTransformerGeneticNetwork dtNW;
	/** Ontology object for Phenomizer*/
	private Ontology ontology;
	/** SymptomDiseaseAssociations object for Phenomizer*/
	private SymptomDiseaseAssociations sda;
	/** PhenomizerFilter object handling the interface between Phenomizer and PhenoToGeno*/
	private PhenomizerFilter phenomizerFilter;
	/** DiseaseGeneassociation object for PhenoToGeno*/
	private DiseaseGeneAssociation dga;
	/** array of Edge objects for NetworkScore*/
	private Edge[] edges;
	/** random walk with restart object representing the settings for the random walk*/
	private RandomWalkWithRestart rwwrSettings;

	/** file to write output to*/
	private String outFile;
	
	/** Simulator for generating patients*/
	private PatientSimulator simulator;
	/** iterator determining which disease is simulated next*/
	private DiseaseIterator iter;
	
	/**
	 * generates an object for validation PhenoToGeno without running the network score algorithm
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

		this(onto_raw, symptoms_raw, ksz_raw, genes_raw, associations_raw, pvalFolder, null, null, simulator, iter, outfile);
	}
	
	/**
	 * generates an object for validation of PhenoToGeno and NetworkScore using the default filter (all diseases)
	 * @param onto_raw ontology of symptoms read from a file by {@link FileUtilitiesPhenomizer}
	 * @param symptoms_raw list of symptoms read from a file by {@link FileUtilitiesPhenomizer}
	 * @param ksz_raw associations between diseases and symptoms from a file read by {@link FileUtilitiesPhenomizer}
	 * @param genes_raw list of gene ids read from a file read by {@link FileUtilitiesPTG}
	 * @param associations_raw associations between diseases and genes from a file read by {@link FileUtilitiesPTG}
	 * @param pvalFolder path to folder with sampled score distributions for p value calculation
	 * @param network_raw genetic network as edge list read from a file by {@link FileUtilitiesGeneticNetwork}
	 * @param rwwrSettings settings for the random walk with restart (restart probabiliy, iterations)
	 * @param simulator Simulator for generating patients
	 * @param iter iterator determining which disease is simulated next 
	 * @param outfile path to file to which all output is written
	 */
	public ValidateGeneRanking(int [][] onto_raw, LinkedList<Integer> symptoms_raw,
			HashMap<Integer, LinkedList<Integer[]>> ksz_raw, LinkedList<String> genes_raw,
			HashMap<Integer, LinkedList<String>> associations_raw, String pvalFolder,
			String[][] network_raw, RandomWalkWithRestart rwwrSettings,
			PatientSimulator simulator, DiseaseIterator iter, String outfile){
		
		this(onto_raw, symptoms_raw, ksz_raw, genes_raw, associations_raw, pvalFolder, network_raw, rwwrSettings,
				new PhenomizerFilterAllDiseases(), simulator, iter, outfile);		
	}
	
	public ValidateGeneRanking(int [][] onto_raw, LinkedList<Integer> symptoms_raw,
			HashMap<Integer, LinkedList<Integer[]>> ksz_raw, LinkedList<String> genes_raw,
			HashMap<Integer, LinkedList<String>> associations_raw, String pvalFolder,
			String[][] network_raw, RandomWalkWithRestart rwwrSettings, PhenomizerFilter phenomizerFilter,
			PatientSimulator simulator, DiseaseIterator iter, String outfile){
		
		this.onto_raw = onto_raw;
		this.symptoms_raw = symptoms_raw;
		this.ksz_raw = ksz_raw;
		this.genes_raw = genes_raw;
		this.associations_raw =associations_raw;
		this.network_raw =network_raw;
		
		this.folder = new PValueFolder(pvalFolder);
		this.rwwrSettings = rwwrSettings;
		this.phenomizerFilter = phenomizerFilter;
		
		dtPheno = new DataTransformer();
		dtPTG = new PhenoToGenoDataTransformer();
		dtNW = new DataTransformerGeneticNetwork();
		
		this.simulator = simulator;
		this.iter = iter;
		this.outFile = outfile;
		
	}
	
	/** 
	 * initializes the validation by preparing all data structures for Phenomizer, PhenoToGeno and NetworkScore 
	 */
	public void prepareData(){
		
		//data for Phenomizer and PTG
		this.ontology = new Ontology(onto_raw);
		sda = dtPheno.generateSymptomDiseaseAssociation(ontology, symptoms_raw, ksz_raw);
		dga = dtPTG.getDiseaseGeneAssociation(genes_raw, associations_raw);
		iter.setSDA(sda);
		
		//interface Phenomizer and PTG
		phenomizerFilter.setTotalDiseases(sda.numberOfDiseases());
		
		//data for network score
		if(network_raw!=null){
			edges = dtNW.transformEdges(network_raw);
		}
	}
	
	/** 
	 * method to run the validation with simulated patients
	 */
	public void simulateAndRank(){
		
		//initialize reusable hashmaps for Phenomizer
		HashMap<Integer,Double> ic = new HashMap<Integer,Double>(sda.numberOfSymptoms()*3);
		HashMap<String, Double> sim = new HashMap<String, Double>(sda.numberOfSymptoms()*sda.numberOfSymptoms());
		//initialize reusable matrixvectorbuilder for network score
		MatrixVectorBuilder mvb=null;
		
		//initialize outputwriter and create header
		FileOutputWriter fow = new FileOutputWriter(outFile);
		fow.writeFile("patient_id\tdisease_id\trankPTG_best\trankPTG_worst\trankPTG_average");
		if(network_raw==null){
			fow.writeFile("\n");
		}
		else{
			fow.writeFileln("\trankNW_best\trankNW_worst\trankNW_average");
		}
		
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
					phenomizerFilter.getResultSize(), ontology, patient.getSymptoms(), sda, 
					new SimilarityCalculatorOneSidedWeight(), folder, new BenjaminiHochbergCorrector(), ic, sim);
			LinkedList<String[]> diseaseScores = phenomizer.runPhenomizer();
			//parse result -> data transformer -> input PTG
			LinkedList<ScoredDisease> inputPTG = dtPTG.getPhenomizerResultFromAlgo(diseaseScores, dga);
			inputPTG = phenomizerFilter.filter(inputPTG);
			
			// run PhenoToGeno
			PhenoToGenoAlgo ptg = new PhenoToGenoAlgo(inputPTG, dga);
			LinkedList<ScoredGene> geneScores = ptg.runPhenoToGene();
			//clear dga data structure for reuse
			dga.resetDiseaseScores();
			
			//evaluate results of PhenoToGeno
			double [] ranksPTG = calculateGeneRanks(geneScores, currentGenes);
			String outMessage = patient.getId()+"\t"+currentDiseaseId+"\t"+ranksPTG[0]+"\t"+ranksPTG[1]+"\t"+ranksPTG[2];
			
			// network score
			if(rwwrSettings!= null){
				// prepare data structures -> reuse existing matrix and settings for rwwr
				ScoredGenes ptgScores = dtNW.transformGeneScoresFromAlgo(geneScores);
				if(mvb==null){
					mvb = new MatrixVectorBuilder(edges, ptgScores);
				}
				else{
					mvb = new MatrixVectorBuilder(ptgScores, mvb);
				}
				//run random walk
				RandomWalkWithRestart rwwr = rwwrSettings.copy();
				NetworkScoreAlgorithm nw = new NetworkScoreAlgorithm(mvb, rwwr);
				LinkedList<ScoredGene> networkScored = nw.runNetworkScoreAlgorithm();
				double[] ranksNW = calculateGeneRanks(networkScored, currentGenes);
				outMessage+="\t"+ranksNW[0]+"\t"+ranksNW[1]+"\t"+ranksNW[2];
			}
			
			//output result to file
			fow.writeFileln(outMessage);
			System.out.println(outMessage);
			counter++;
		}
		fow.closew();

	}
	
	/**
	 * auxiliary method handling the evaluation of the results from PhenoToGeno and the network score,
	 * it calculates the ranks of the correct genes for the current simulates patient
	 * @param result list of ScoredGenes produced by PhenoToGeno or NetworkScoreAlgorithm
	 * @param currentGenes ids of the correct genes (genes associated with the disease of the patient)
	 * @return array containing the best rank (position 0), worst rank (position 1) and average rank (position 2) of
	 * 		the correct genes
	 */
	private double [] calculateGeneRanks(LinkedList<ScoredGene> result, AnnotatedGene [] currentGenes){
		String [] correctGeneIds = new String [currentGenes.length];
		for(int i=0; i<currentGenes.length; i++){
			correctGeneIds[i]=currentGenes[i].getId();
		}
		double [] currentRanks = RankCalculator.getRanks(correctGeneIds, result);
		double best = RankCalculator.getBestRank(currentRanks);
		double worst = RankCalculator.getWorstRank(currentRanks);
		double average = RankCalculator.getAverageRank(currentRanks);
		return new double[]{best, worst, average};
	}

}
