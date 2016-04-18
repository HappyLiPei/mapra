package main;

import java.util.HashMap;
import java.util.LinkedList;

import phenomizer.algorithm.FrequencyConverter;
import phenomizer.io.FileUtilitiesPhenomizer;
import phenotogeno.io.FileUtilitiesPTG;
import phenotogeno.validation.DiseaseIterator;
import phenotogeno.validation.DiseaseIteratorAll;
import phenotogeno.validation.PatientSimulator;
import phenotogeno.validation.PatientSimulatorVeryFrequentSymptoms;
import phenotogeno.validation.ValidateGeneRanking;

public class RunValidationPTG {
	
	public static void main(String args[]) throws Exception{
		
		//get command line arguments
		String mode = args[0];
		String fileOnto = args[1];
		String fileSymp = args[2];
		String fileKsz = args[3];
		String fileGenes = args[4];
		String fileAsso = args[5];
		String scoreDist = args[6];
		String outRes = args[7];
		String outPatient = args[8];
		
		//read in files
		int [][] onto = FileUtilitiesPhenomizer.readInOntology(fileOnto);
		LinkedList<Integer> symp = FileUtilitiesPhenomizer.readInSymptoms(fileSymp);
		HashMap<Integer, LinkedList<Integer[]>> ksz = (new FrequencyConverter()).convertAll(
				FileUtilitiesPhenomizer.readInKSZFrequency(fileKsz));
		LinkedList<String> genes = FileUtilitiesPTG.readGeneList(fileGenes);
		HashMap<Integer, LinkedList<String>> asso = FileUtilitiesPTG.readDiseaseGeneAssociation(fileAsso);
	
		DiseaseIterator i = null;
		PatientSimulator s = null;
		
		//check mode of simulation
		if(mode.equals("veryFreq")){
			i = new DiseaseIteratorAll();
			s = new PatientSimulatorVeryFrequentSymptoms(outPatient);
		}
		else{
			throw new Exception("Invalide mode: "+mode+" !" );
		}
		
		//run validation
		ValidateGeneRanking vgr = new ValidateGeneRanking(onto, symp, ksz, genes, asso, scoreDist, s, i, outRes);
		vgr.prepareData();
		vgr.simulateAndRank();
		
		//end + close simulation -> file handle!!
		if(s instanceof PatientSimulatorVeryFrequentSymptoms){
			((PatientSimulatorVeryFrequentSymptoms) s).endSimulation();
		}
	}

}
