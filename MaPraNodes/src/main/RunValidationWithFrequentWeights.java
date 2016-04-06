package main;

import java.util.HashMap;
import java.util.LinkedList;

import phenomizer.algorithm.FrequencyConverter;
import phenomizer.io.FileUtilitiesPhenomizer;
import phenomizer.validation.PhenomizerWithFrequentSymptoms;
import phenomizer.validation.PhenomizerWithFrequentSymptomsNoPval;
import phenomizer.validation.PhenomizerWithFrequentSymptomsWithPval;

public class RunValidationWithFrequentWeights {
	
	/**
	 * main method to run validation with frequent weights
	 * the program extracts very frequent symptoms (weight = 15) of the diseases and uses them as query
	 * finally the rank of the disease for the corresponding query is recorded
	 * @param args
	 *		command line arguments, array of 6 Strings,
	 * 		position 0: mode for calculating the similarity score (noweight, asymmweight, weight)
	 * 		position 1: isa table of PhenoDis
	 * 		position 2: ksz table of PhenoDis
	 * 		position 3: symptom table of PhenoDis
	 * 		position 4: file to write output to
	 * 		position 5: folder containing empirically sampled score distributions for p value calculation
	 * @throws Exception
	 * 		if argument at position 0 is incorrect
	 */
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
			throw new Exception("Incorrect argument at pos 0!");
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
