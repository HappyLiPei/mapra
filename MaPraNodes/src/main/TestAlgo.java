package main;

import io.FileUtilities;
import phenomizeralgorithm.AlgoPheno;
import phenomizeralgorithm.FrequencyConverter;
import phenomizeralgorithm.PValueFolder;
import phenomizeralgorithm.PValueGenerator;

import java.util.HashMap;
import java.util.LinkedList;

public class TestAlgo {

	public static void main(String[]args){

		String dataPath = "C:/Users/xxx/Dropbox/Masterpraktikum/";

		/*String dataIn = dataPath+"Datenbank/";
		String ontoIn = dataIn + "isa_HPO_test.csv";
		String kszIn = dataIn + "ksz_HPO_test.csv";
		String symptomsIn = dataIn + "symptoms_HPO_test.csv";*/

		//Test frequency converter
		/*String diseasesFreqIn = dataPath+"ksz_HPO_frequency.csv";
		HashMap<Integer,LinkedList<String[]>>ksz = FileUtilities.frequencyIn(diseasesFreqIn);
		FrequencyConverter.testConvert(ksz);*/

		//Test phenomizer
		String dataIn = dataPath + "Testdatensatz/";
		String kszIn = dataIn + "Krankheiten.txt";
		String symptomsIn = dataIn + "Symptome.txt";
		String ontoIn = dataIn + "Ontology.txt";
		String queryIn = dataIn + "query10.txt";

		/*HashMap<Integer,LinkedList<String[]>>kszTmp = FileUtilities.readInKSZFrequency(diseasesIn);
		HashMap<Integer,LinkedList<Integer[]>>ksz=FrequencyConverter.convertAll(kszTmp);*/
		HashMap<Integer,LinkedList<Integer>>kszTmp = FileUtilities.readInKSZ(kszIn);
		HashMap<Integer,LinkedList<Integer[]>>ksz= FrequencyConverter.addWeights(kszTmp);
		LinkedList<Integer> symptoms = FileUtilities.readInSymptoms(symptomsIn);
		int[][]ontology = FileUtilities.readInOntology(ontoIn);
		LinkedList<Integer> query = FileUtilities.readInQuery(queryIn);
		PValueFolder.setPvalFoder(dataIn+"pvalues_binned/");
		LinkedList<String[]> results = PValueGenerator.phenomizerWithPValues(11, query, symptoms, ksz, ontology,true);
		String res = resultToString(results);
		System.out.println(res);

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
			sb.append(element[0]+"\t"+element[1]+"\t"+element[2]+"\n");
		}
		return sb.toString();
	}
}


