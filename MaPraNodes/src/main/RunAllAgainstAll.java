package main;

import java.util.HashMap;
import java.util.LinkedList;

import algorithm.AlgoPheno;
import algorithm.PValueFolder;
import algorithm.PValueGenerator;

public class RunAllAgainstAll {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String dataPath = "D:/Dropbox/Masterpraktikum/";

		String dataIn = dataPath+"Datenbank/";
		String ontoIn = dataIn + "isa_HPO_test.csv";
		String kszIn = dataIn + "ksz_HPO_test.csv";
		String symptomsIn = dataIn + "symptoms_HPO_test.csv";
		
		/*String dataIn = dataPath + "Testdatensatz/";
		String ontoIn = dataIn + "Ontology.txt";
		String kszIn = dataIn + "Krankheiten.txt";
		String symptomsIn = dataIn + "Symptome.txt";*/		

		HashMap<Integer,LinkedList<Integer>>kszTmp = FileUtilities.readInKSZ(kszIn);
		HashMap<Integer,LinkedList<Integer[]>>ksz= CalcPValueMaria.addWeights(kszTmp);
		LinkedList<Integer> symptoms = FileUtilities.readInSymptoms(symptomsIn);
		int[][]ontology = FileUtilities.readInOntology(ontoIn);

		//generate distance matrix
		String dataOut = dataPath+"Clustering/allAgainstAll_";
		//String outputSim = dataOut+"sim.txt";
		String outputMax = dataOut+"max_bh.txt";
		String outputMin = dataOut+"min_bh.txt";
		String outputAvg = dataOut+"avg_bh.txt";
		
		PValueFolder.setPvalFoder("D:/Dokumente/Studium/Master/Masterpraktikum 1. FS/pvalues/pval_big_noweight/");
		
		LinkedList<Integer>query = new LinkedList<Integer>();
		query.add(1);
		AlgoPheno.setInput(query, symptoms, ksz, ontology);
		int[] keys = AlgoPheno.getKeys();
		
		double[][]result = AlgoPheno.allAgainstAll();
		//String res = arrayToString(result,keys);
		//System.out.println(outputSim);
		//FileUtilities.writeString(outputSim, res);
				
		System.out.println("Asymmetric matrix...");
		double[][] asymmetric = new double[result.length][result.length];
		for(int i=0;i<result.length;i++){
			System.out.println(i);
			int disease = keys[i];
			int queryLength = ksz.get(disease).size();
			double []tmp = result[i];
			double[]line = PValueGenerator.getNextOfAsymmetricMatrix(tmp, keys, queryLength);
			asymmetric[i]=line;
		}
		
		System.out.println("Minimum matrix...");
		double[][]minMatrix = PValueGenerator.getMinimumMatrix(asymmetric);
		writeResult(minMatrix,keys,outputMin);
		//System.out.println(outputMin);
		//FileUtilities.writeString(outputMin, resMin);
		
		System.out.println("Maximum matrix...");
		double[][]maxMatrix = PValueGenerator.getMaximumMatrix(asymmetric);
		writeResult(maxMatrix,keys,outputMax);
		//System.out.println(outputMax);
		//FileUtilities.writeString(outputMax, resMax);
		
		System.out.println("Average matrix...");
		double[][]avgMatrix = PValueGenerator.getAverageMatrix(asymmetric);
		writeResult(avgMatrix,keys,outputAvg);
		//System.out.println(outputAvg);
		//FileUtilities.writeString(outputAvg, resAvg);

	}
	
	public static String arrayToString(double[][]array, int[] colNames){
		StringBuilder sb = new StringBuilder();
		sb.append("id,");
		for(int i=0; i<colNames.length-1; i++){
			sb.append(colNames[i]+",");
		}
		sb.append(colNames[colNames.length-1]);
		sb.append("\n");
		
		for(int i=0;i<array.length; i++){
			sb.append(colNames[i]+",");
			for(int j=0; j<array[i].length-1; j++){
				sb.append(array[i][j]+",");
			}
			sb.append(array[i][array[i].length-1]);
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public static void writeResult(double[][]array, int[]colNames, String path){
		StringBuilder sb = new StringBuilder();
		sb.append("id,");
		for(int i=0; i<colNames.length-1; i++){
			sb.append(colNames[i]+",");
		}
		sb.append(colNames[colNames.length-1]);
		sb.append("\n");
		FileUtilities.writeStringToExistingFile(path, sb.toString());
		
		for(int i=0;i<array.length; i++){
			sb = new StringBuilder();
			sb.append(colNames[i]+",");
			for(int j=0; j<array[i].length-1; j++){
				sb.append(array[i][j]+",");
			}
			sb.append(array[i][array[i].length-1]);
			sb.append("\n");
			FileUtilities.writeStringToExistingFile(path, sb.toString());
		}
	}

}
