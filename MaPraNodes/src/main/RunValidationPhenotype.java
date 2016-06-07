package main;

import java.util.HashMap;
import java.util.LinkedList;

import geneticnetwork.algorithm.RandomWalkWithRestart;
import geneticnetwork.algorithm.RandomWalkWithRestartFixedIterations;
import geneticnetwork.algorithm.RandomWalkWithRestartUntilConvergence;
import geneticnetwork.io.FileUtilitiesGeneticNetwork;
import phenomizer.algorithm.FrequencyConverter;
import phenomizer.io.FileUtilitiesPhenomizer;
import phenotogeno.io.FileUtilitiesPTG;
import phenotogeno.validation.DiseaseIterator;
import phenotogeno.validation.DiseaseIteratorAll;
import phenotogeno.validation.DiseaseIteratorFile;
import phenotogeno.validation.PatientSimulator;
import phenotogeno.validation.PatientSimulatorDrawSymptoms;
import phenotogeno.validation.PatientSimulatorVeryFrequentSymptoms;
import phenotogeno.validation.PatientSimulatorWriteToFile;
import phenotogeno.validation.PhenomizerFilter;
import phenotogeno.validation.PhenomizerFilterAllDiseases;
import phenotogeno.validation.PhenomizerFilterSignificant;
import phenotogeno.validation.PhenomizerFilterTop20;
import phenotogeno.validation.PhenomizerFilterTopPvalue;
import phenotogeno.validation.SimulatorIteratorFromFile;
import phenotogeno.validation.ValidateGeneRanking;

public class RunValidationPhenotype {

public static void main(String args[]) throws Exception{
		//get command line arguments
		String runMode = args[0];
		String rwwrOpt = args[1];
		String fileOnto = args[2];
		String fileSymp = args[3];
		String fileKsz = args[4];
		String fileGenes = args[5];
		String fileAsso = args[6];
		String fileNetwork =args[7];
		String scoreDist = args[8];
		String outRes = args[9];
		String outPatient = args[10];
		String numberOfPatients = null;
		String mitoDiseaseFile = null;
		//TODO: args[12] obligatory argument -> adapt diseaseIterator all to simulate more than 1 patient per disease
		if(args.length>=13){
			numberOfPatients = args[11];
			mitoDiseaseFile = args[12];
		}
		
		//parse options for random walk: [uw]+[restartprob]+[iterations]
		RandomWalkWithRestart walk = null;
		String [] rwwrEl = rwwrOpt.split("\\+");
		boolean edgeWeight=false;
		if(rwwrEl[0].equals("u")){
			edgeWeight=false;
		}
		else if(rwwrEl[0].equals("w")){
			edgeWeight=true;
		}
		else{
			throw new Exception("Invalid random walk option: "+rwwrEl[0]+" !" );
		}
		double restart = Double.parseDouble(rwwrEl[1]);
		if(rwwrEl.length==2){
			walk = new RandomWalkWithRestartUntilConvergence(restart);
		}
		else if(rwwrEl.length==3){
			int iters = Integer.parseInt(rwwrEl[2]);
			walk = new RandomWalkWithRestartFixedIterations(restart, iters);
		}		
		
		//read in files
		int [][] onto = FileUtilitiesPhenomizer.readInOntology(fileOnto);
		LinkedList<Integer> symp = FileUtilitiesPhenomizer.readInSymptoms(fileSymp);
		HashMap<Integer, LinkedList<Integer[]>> ksz = (new FrequencyConverter()).convertAll(
				FileUtilitiesPhenomizer.readInKSZFrequency(fileKsz));
		LinkedList<String> genes = FileUtilitiesPTG.readGeneList(fileGenes);
		HashMap<Integer, LinkedList<String>> asso = FileUtilitiesPTG.readDiseaseGeneAssociation(fileAsso);
		String [][] network = FileUtilitiesGeneticNetwork.readEdges(fileNetwork, edgeWeight);
		
		//check mode of simulation [veryFreq|draw|file|drawFile]+[all|top20|sign|topP]
		String [] splitMode = runMode.split("\\+");
		String simMode = splitMode[0];
		DiseaseIterator i = null;
		PatientSimulator s = null;
		
		if(simMode.equals("veryFreq")){
			i = new DiseaseIteratorAll();
			s = new PatientSimulatorVeryFrequentSymptoms(outPatient);
		}
		else if(simMode.equals("draw")){
			i = new DiseaseIteratorAll();
			s = new PatientSimulatorDrawSymptoms(outPatient);
		}
		else if(simMode.equals("file")){
			SimulatorIteratorFromFile fromFile=new SimulatorIteratorFromFile(outPatient);
			i = fromFile;
			s = fromFile;
		}
		else if(simMode.equals("drawFile")){
			if(numberOfPatients==null || mitoDiseaseFile==null){
				throw new Exception("Mode drawFile requires numberOfPatients and file with disease id");
			}
			i = new DiseaseIteratorFile(Integer.parseInt(numberOfPatients), mitoDiseaseFile);
			s = new PatientSimulatorDrawSymptoms(outPatient);
		}
		else{
			throw new Exception("Invalide mode: "+simMode+" !" );
		}
		
		//check mode for interface Phenomizer-PTG
		PhenomizerFilter f = null;
		
		if(splitMode.length<2){
			System.out.println("No filter mode specified, no filtering is performed.");
			f = new PhenomizerFilterAllDiseases();
		}
		else{
			String filterMode = splitMode[1];
			if(filterMode.equals("all")){
				f = new PhenomizerFilterAllDiseases();
			}
			else if(filterMode.equals("top20")){
				f = new PhenomizerFilterTop20();
			}
			else if(filterMode.equals("sign")){
				f = new PhenomizerFilterSignificant();
			}
			else if(filterMode.equals("topP")){
				f = new PhenomizerFilterTopPvalue();
			}
			else{
				throw new Exception("Invalid filter mode "+filterMode);
			}
		}
		
		//run validation
		ValidateGeneRanking vgr = new ValidateGeneRanking(onto, symp, ksz, genes, asso, scoreDist, network, walk, f, s, i, outRes);
		vgr.prepareData();
		vgr.simulateAndRank();
		
		//end + close simulation -> file handle!!
		if(s instanceof PatientSimulatorWriteToFile){
			((PatientSimulatorWriteToFile) s).endSimulation();
		}
	}

}
