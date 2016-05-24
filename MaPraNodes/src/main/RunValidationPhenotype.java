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
import phenotogeno.validation.PatientSimulator;
import phenotogeno.validation.PatientSimulatorDrawSymptoms;
import phenotogeno.validation.PatientSimulatorVeryFrequentSymptoms;
import phenotogeno.validation.PatientSimulatorWriteToFile;
import phenotogeno.validation.SimulatorIteratorFromFile;
import phenotogeno.validation.ValidateGeneRanking;

public class RunValidationPhenotype {
	//TODO:integration option for using filter
public static void main(String args[]) throws Exception{
		
		//get command line arguments
		String mode = args[0];
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
			throw new Exception("Invalide random walk option: "+rwwrEl[0]+" !" );
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
	
		DiseaseIterator i = null;
		PatientSimulator s = null;
		
		//check mode of simulation
		if(mode.equals("veryFreq")){
			i = new DiseaseIteratorAll();
			s = new PatientSimulatorVeryFrequentSymptoms(outPatient);
		}
		else if(mode.equals("draw")){
			i = new DiseaseIteratorAll();
			s = new PatientSimulatorDrawSymptoms(outPatient);
		}
		else if(mode.equals("file")){
			SimulatorIteratorFromFile fromFile=new SimulatorIteratorFromFile(outPatient);
			i = fromFile;
			s = fromFile;
		}
		else{
			throw new Exception("Invalide mode: "+mode+" !" );
		}
		
		//run validation
		ValidateGeneRanking vgr = new ValidateGeneRanking(onto, symp, ksz, genes, asso, scoreDist, network, walk, s, i, outRes);
		vgr.prepareData();
		vgr.simulateAndRank();
		
		//end + close simulation -> file handle!!
		if(s instanceof PatientSimulatorWriteToFile){
			((PatientSimulatorWriteToFile) s).endSimulation();
		}
	}

}
