package main;

import java.util.HashMap;
import java.util.LinkedList;

import phenomizer.algorithm.FrequencyConverter;
import phenomizer.io.FileUtilitiesPhenomizer;
import phenomizer.validation.PhenomizerWithFrequentSymptoms;
import phenomizer.validation.PhenomizerWithFrequentSymptomsNoPval;
import phenomizer.validation.PhenomizerWithFrequentSymptomsWithPval;

public class RunValidationWithFrequentWeights {
	
	public static void main(String args[]) throws Exception{
		
		//read commandline
		String weight = args[0];
		String onto_file=args[1];
		String ksz_file =args[2];
		String symptom_file =args[3];
		String result_file =args[4];
		
		//check pvalue option
		String pvalFolder ="";
		boolean pval=false;
		if(args.length>5){
			System.out.println("Use Phenomizer with pvalues");
			pvalFolder=args[5];
			pval=true;
		}
		else{
			System.out.println("Use Phenomizer with scores");
		}
		
		//get weighting
		int weighting=-1;
		if(weight.equals("noweight")){
			System.out.println("not weighted");
			weighting=0;
		}
		else if(weight.equals("weight")){
			System.out.println("symmetric weight");
			weighting=2;
		}
		else if(weight.equals("asymmweight")){
			System.out.println("asymmetric weight");
			weighting=1;
		}
		else{
			throw new Exception("Incorrect argument at pos 2!");
		}
		
		//read in files
		int[][] onto_raw = FileUtilitiesPhenomizer.readInOntology(onto_file);
		LinkedList<Integer> symptoms_raw = FileUtilitiesPhenomizer.readInSymptoms(symptom_file);
		HashMap<Integer, LinkedList<String[]>> ksz_in = FileUtilitiesPhenomizer.readInKSZFrequency(ksz_file);
		HashMap<Integer, LinkedList<Integer[]>> ksz_raw = (new FrequencyConverter()).convertAll(ksz_in);
		
		//prepare correct PhenomizerWithFrequentSymptomsObject
		PhenomizerWithFrequentSymptoms p = null;
		if(pval){
			p = new PhenomizerWithFrequentSymptomsWithPval(weighting, onto_raw, symptoms_raw, ksz_raw,
					result_file, pvalFolder);
		}
		else{
			p = new PhenomizerWithFrequentSymptomsNoPval(weighting, onto_raw, symptoms_raw, ksz_raw, result_file);
		}
		
		//execute calculation
		p.prepareData();
		p.runValidation();		
	}

}
