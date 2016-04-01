package main;

import phenomizer.algorithm.FrequencyConverter;
import phenomizer.algorithm.SimilarityMatrixCalculator;
import phenomizer.io.FileUtilitiesPhenomizer;

import java.util.HashMap;
import java.util.LinkedList;

public class RunAllAgainstAll {
	
	/**
	 * main method for running calculation of the similarity matrix
	 * @param args
	 * 		command line arguments, array of 4 Strings,
	 * 		position 0: symptom table of PhenoDis
	 * 		position 1: isa table of PhenoDis
	 * 		position 2: ksz table of PhenoDis
	 * 		position 3: file to write output to
	 */
	public static void main(String[] args) {
		
		//get command line arguments
		String symptomsIn = args[0];
		String ontoIn = args[1];
		String kszIn = args[2];
		String outputFile = args[3];
		
		//read in files
		LinkedList<Integer> symptoms = FileUtilitiesPhenomizer.readInSymptoms(symptomsIn);
		int[][] ontology = FileUtilitiesPhenomizer.readInOntology(ontoIn);
		HashMap<Integer, LinkedList<Integer>> kszRaw = FileUtilitiesPhenomizer.readInKSZ(kszIn);
		HashMap<Integer, LinkedList<Integer[]>> ksz = (new FrequencyConverter()).addWeights(kszRaw);
		
		SimilarityMatrixCalculator matrixCalc = new SimilarityMatrixCalculator(ontology, symptoms, ksz, outputFile);
		matrixCalc.prepareData();
		matrixCalc.calculateSimilarityMatrix();
	}

}
