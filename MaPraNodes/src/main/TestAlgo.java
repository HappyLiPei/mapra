package main;

import java.util.HashMap;
import java.util.LinkedList;
import algorithm.AlgoPheno;

public class TestAlgo {
	
	public static void main(String[]args){
		
		String dataPath = "D:/Dokumente/Masterpraktikum/Testdatensatz/";
		
		String diseasesIn = dataPath + "Krankheiten.txt";
		String symptomsIn = dataPath + "Symptome.txt";
		String ontologyIn = dataPath + "Ontology.txt";
		
		String output = dataPath + "PhenomizerTestResults.txt";
		

		HashMap<Integer,LinkedList<Integer>>ksz = FileUtilities.readInKSZ(diseasesIn);
		LinkedList<Integer> symptoms = FileUtilities.readInSymptoms(symptomsIn);
		int[][]ontology = FileUtilities.readInOntology(ontologyIn);
		
		StringBuilder result = new StringBuilder();
		for(int i=1; i<=10;i++){
			String queryIn = dataPath + "query"+i+".txt";
			LinkedList<Integer> query = FileUtilities.readInQuery(queryIn);
			AlgoPheno.setInput(query, symptoms, ksz, ontology);
			LinkedList<String[]>res=AlgoPheno.runPhenomizer(11);
			String tmpRes = resultToString(res);
			result.append("Query"+i+":\n"+tmpRes);
		}
		
		FileUtilities.writeString(output, result.toString());
	}
	
	public static String resultToString(LinkedList<String[]>res){
		StringBuilder sb = new StringBuilder();
		for(String[]element : res){
			sb.append(element[0]+"\t"+element[1]+"\n");
		}
		return sb.toString();
	}
	
}


