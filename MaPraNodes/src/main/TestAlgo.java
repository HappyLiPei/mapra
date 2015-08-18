package main;

import java.util.HashMap;
import java.util.LinkedList;
import algorithm.AlgoPheno;
import algorithm.FrequencyConverter;

public class TestAlgo {

	public static void main(String[]args){

		String dataPath = "C:/Users/xxx/Dropbox/Masterpraktikum/Testdatensatz/";

		//Test frequency converter
		/*String diseasesFreqIn = dataPath+"ksz_HPO_frequency.csv";
		HashMap<Integer,LinkedList<String[]>>ksz = FileUtilities.frequencyIn(diseasesFreqIn);
		FrequencyConverter.testConvert(ksz);*/

		//Test phenomizer
		String diseasesIn = dataPath + "Krankheiten.txt";
		String symptomsIn = dataPath + "Symptome.txt";
		String ontologyIn = dataPath + "Ontology.txt";
		String queryIn = dataPath + "query7.txt";

		HashMap<Integer,LinkedList<String[]>>kszTmp = FileUtilities.readInKSZFrequency(diseasesIn);
		HashMap<Integer,LinkedList<Integer[]>>ksz=FrequencyConverter.convertAll(kszTmp);
		LinkedList<Integer> symptoms = FileUtilities.readInSymptoms(symptomsIn);
		int[][]ontology = FileUtilities.readInOntology(ontologyIn);
		//LinkedList<Integer> query = FileUtilities.readInQuery(queryIn);

		/*AlgoPheno.setInput(query, symptoms, ksz, ontology);
		LinkedList<String[]>res = AlgoPheno.runPhenomizer(11);
		String tmpRes = resultToString(res);
		System.out.println(tmpRes);*/

		//Test phenomizer for all queries
		/*String output = dataPath + "PhenomizerTestResult2.txt";
		StringBuilder result = new StringBuilder();
		for(int i=1; i<=10;i++){
			String queryIn = dataPath + "query"+i+".txt";
			LinkedList<Integer> query = FileUtilities.readInQuery(queryIn);
			AlgoPheno.setInput(query, symptoms, ksz, ontology);
			LinkedList<String[]>res=AlgoPheno.runPhenomizer(11);
			String tmpRes = resultToString(res);
			result.append("Query"+i+":\n"+tmpRes);
		}

		FileUtilities.writeString(output, result.toString());*/

		//generate distance matrix
		String output = dataPath + "allAgainstAll_new.txt";
		LinkedList<Integer>query = new LinkedList<Integer>();
		query.add(1);
		AlgoPheno.setInput(query, symptoms, ksz, ontology);
		int[] keys = AlgoPheno.getKeys();
		double[][]result = AlgoPheno.allAgainstAll();
		String res = arrayToString(result,keys);
		FileUtilities.writeString(output, res);
	}

	public static String resultToString(LinkedList<String[]>res){
		StringBuilder sb = new StringBuilder();
		for(String[]element : res){
			sb.append(element[0]+"\t"+element[1]+"\n");
		}
		return sb.toString();
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

}


