package main;

import java.util.HashMap;
import java.util.LinkedList;
import algorithm.AlgoPheno;
import algorithm.FrequencyConverter;

public class TestAlgo {

	public static void main(String[]args){

		String dataPath = "C:/Users/Maria Schelling/Dropbox/Masterpraktikum/";

		String dataIn = dataPath+"Datenbank/";
		String ontoIn = dataIn + "isa_HPO_test.csv";
		String kszIn = dataIn + "ksz_HPO_test.csv";
		String symptomsIn = dataIn + "symptoms_HPO_test.csv";

		//Test frequency converter
		/*String diseasesFreqIn = dataPath+"ksz_HPO_frequency.csv";
		HashMap<Integer,LinkedList<String[]>>ksz = FileUtilities.frequencyIn(diseasesFreqIn);
		FrequencyConverter.testConvert(ksz);*/

		//Test phenomizer
		/*String diseasesIn = dataPath + "Krankheiten.txt";
		String symptomsIn = dataPath + "Symptome.txt";
		String ontologyIn = dataPath + "Ontology.txt";
		String queryIn = dataPath + "query7.txt";*/

		/*HashMap<Integer,LinkedList<String[]>>kszTmp = FileUtilities.readInKSZFrequency(diseasesIn);
		HashMap<Integer,LinkedList<Integer[]>>ksz=FrequencyConverter.convertAll(kszTmp);*/
		HashMap<Integer,LinkedList<Integer>>kszTmp = FileUtilities.readInKSZ(kszIn);
		HashMap<Integer,LinkedList<Integer[]>>ksz= CalcPValueMaria.addWeights(kszTmp);
		LinkedList<Integer> symptoms = FileUtilities.readInSymptoms(symptomsIn);
		int[][]ontology = FileUtilities.readInOntology(ontoIn);
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

		
	}

	public static String resultToString(LinkedList<String[]>res){
		StringBuilder sb = new StringBuilder();
		for(String[]element : res){
			sb.append(element[0]+"\t"+element[1]+"\n");
		}
		return sb.toString();
	}
}


