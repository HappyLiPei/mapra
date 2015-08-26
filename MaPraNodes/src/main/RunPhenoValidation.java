package main;

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
		String weightedOut = dataOut+"weights.txt";
		
		HashMap<Integer,LinkedList<Integer>>kszTmp = FileUtilities.readInKSZ(kszIn);
		HashMap<Integer,LinkedList<Integer[]>>ksz= CalcPValueMaria.addWeights(kszTmp);
		LinkedList<Integer> symptoms = FileUtilities.readInSymptoms(symptomsIn);
		int[][]ontology = FileUtilities.readInOntology(ontoIn);
		
		HashMap<Integer,LinkedList<String[]>>kszTmpFreq = FileUtilities.readInKSZFrequency(kszInFreq);
		HashMap<Integer,LinkedList<Integer[]>>kszFreq=FrequencyConverter.convertAll(kszTmpFreq);
		
		PhenoValidation.setQueries(kszFreq);
		LinkedList<String[]> resNoWeight = PhenoValidation.validatePheno(symptoms, ksz, ontology);
		LinkedList<String[]> resWeight = PhenoValidation.validatePheno(symptoms, kszFreq, ontology);
		
		String resultNW = listToString(resNoWeight);
		String resultW = listToString(resWeight);
		
		FileUtilities.writeString(unWeightedOut, resultNW);
		FileUtilities.writeString(weightedOut, resultW);

	}
	
	public static String listToString(LinkedList<String[]>tmp){
		StringBuilder sb = new StringBuilder();
		
		for(String[] el : tmp){
			sb.append(el[0]+"\t"+el[1]+"\n");
		}
		
		return sb.toString();
	}

}
