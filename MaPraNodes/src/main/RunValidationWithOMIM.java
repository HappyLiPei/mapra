package main;

import java.util.HashMap;
import java.util.LinkedList;

import phenomizer.algorithm.FrequencyConverter;
import phenomizer.io.FileUtilitiesPhenomizer;
import phenomizer.validation.FileUtilitiesValidation;
import phenomizer.validation.PhenomizerWithOMIMSymptoms;
import phenomizer.validation.PhenomizerWithOMIMSymptomsNoPval;
import phenomizer.validation.PhenomizerWithOMIMSymptomsWithPval;


public class RunValidationWithOMIM {
	
	/**
	 * main method for running validation with symptoms from OMIM
	 * the program extracts symptoms from OMIM and uses them as query
	 * finally the rank of the disease for the corresponding query is recorded
	 * @param args
	 *		command line arguments, array of 8 Strings,
	 * 		position 0: mode for calculating the similarity score (noweight, asymmweight, weight)
	 * 		position 1: isa table of PhenoDis
	 * 		position 2: ksz table of PhenoDis
	 * 		position 3: symptom table of PhenoDis
	 * 		position 4: disease id table of PhenoDis
	 * 		position 5: results from text mining with OMIM data
	 * 		position 6: file to write output to
	 * 		position 7: folder containing empirically sampled score distributions for p value calculation
	 * @throws Exception
	 * 		if argument at position 0 is incorrect
	 */
	public static void main(String args[]) throws Exception{
		
		//read commandline
		String weight = args[0];
		String onto_file=args[1];
		String ksz_file =args[2];
		String symptom_file =args[3];
		String idMap_file = args[4];
		String textMining_file=args[5];
		String result_file =args[6];
		
		//check pvalue option
		String pvalFolder ="";
		boolean pval=false;
		if(args.length>7){
			System.out.println("Use Phenomizer with pvalues");
			pvalFolder=args[7];
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
		HashMap<Integer, LinkedList<Integer[]>> ksz_raw = null;
		if(weighting ==0){
			HashMap<Integer, LinkedList<Integer>> ksz_in = FileUtilitiesPhenomizer.readInKSZ(ksz_file);
			ksz_raw = (new FrequencyConverter()).addWeights(ksz_in);
		}
		else{
			HashMap<Integer, LinkedList<String[]>> ksz_in = FileUtilitiesPhenomizer.readInKSZFrequency(ksz_file);
			ksz_raw = (new FrequencyConverter()).convertAll(ksz_in);
		}
		HashMap<Integer, Integer> omimToPhenoDis = FileUtilitiesValidation.readOMIMIdMapping(idMap_file);
		HashMap<Integer, LinkedList<Integer>> omimToSymptoms = FileUtilitiesValidation.readQueriesFromTM(textMining_file);
		
		//prepare correct PhenomizerWithOMIMSymptoms object
		PhenomizerWithOMIMSymptoms p = null;
		if(pval){
			p=new PhenomizerWithOMIMSymptomsWithPval(weighting, onto_raw, symptoms_raw, ksz_raw,
					result_file, omimToPhenoDis, omimToSymptoms, pvalFolder);
		}
		else{
			p=new PhenomizerWithOMIMSymptomsNoPval(weighting, onto_raw, symptoms_raw, ksz_raw,
					result_file, omimToPhenoDis, omimToSymptoms);
		}
		
		//start validation
		p.prepareData();
		p.runValidation();
	}

}
