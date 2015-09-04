package main;

import io.FileUtilities;

import java.util.HashMap;
import java.util.LinkedList;

import algorithm.FrequencyConverter;
import algorithm.PhenoValidation;

public class RunPhenoValidation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String dataPath = "D:/Dropbox/Masterpraktikum/";

		String dataIn = dataPath+"Datenbank/";
		String ontoIn = dataIn + "isa_HPO_test.csv";
		String kszIn = dataIn + "ksz_HPO_test.csv";
		String symptomsIn = dataIn + "symptoms_HPO_test.csv";

		String kszInFreq = dataIn + "ksz_HPO_frequency.csv";

		String dataOut = dataPath+"WeightValidation/";

		String unWeightedOut = dataOut+"no_weights.txt";
		String weightedOut = dataOut+"weights_asymmetric.txt";

		//HashMap<Integer,LinkedList<Integer>>kszTmp = FileUtilities.readInKSZ(kszIn);
		//HashMap<Integer,LinkedList<Integer[]>>ksz= CalcPValueMaria.addWeights(kszTmp);
		LinkedList<Integer> symptoms = FileUtilities.readInSymptoms(symptomsIn);
		int[][]ontology = FileUtilities.readInOntology(ontoIn);

		HashMap<Integer,LinkedList<String[]>>kszTmpFreq = FileUtilities.readInKSZFrequency(kszInFreq);
		HashMap<Integer,LinkedList<Integer[]>>kszFreq=FrequencyConverter.convertAll(kszTmpFreq);

		PhenoValidation.setQueries(kszFreq);

		/*System.out.println("Unweighted...");
		FileUtilities.writeString(unWeightedOut,"");
		PhenoValidation.validatePheno(symptoms, ksz, ontology,unWeightedOut);*/
		
		System.out.println("Weighted...");
		FileUtilities.writeString(weightedOut,"");
		PhenoValidation.validatePheno(symptoms, kszFreq, ontology,weightedOut);
	}

}
