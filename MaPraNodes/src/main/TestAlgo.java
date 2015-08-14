package main;

import java.util.HashMap;
import java.util.LinkedList;
import algorithm.AlgoPheno;

public class TestAlgo {
	
	public static void main(String[]args){
		
		String dataPath = "D:/Dokumente/Masterpraktikum/Datenbank/";
		
		String diseasesIn = dataPath + "ksz_hpo_test.csv";
		String symptomsIn = dataPath + "symptoms_hpo_test.csv";
		String ontologyIn = dataPath + "isa_hpo_test.csv";
		//String queryIn = dataPath + "testQuery.txt";
		
		String output = dataPath + "allAgainstAll.txt";
		
		HashMap<Integer,LinkedList<Integer>>ksz = FileUtilities.readInKSZ(diseasesIn);
		LinkedList<Integer> symptoms = FileUtilities.readInSymptoms(symptomsIn);
		int[][]ontology = FileUtilities.readInOntology(ontologyIn);
		LinkedList<Integer> query = new LinkedList<Integer>();
		query.add(1);
		AlgoPheno.setInput(query, symptoms, ksz, ontology);
		int[] keys = AlgoPheno.getKeys();
		int[][]result = AlgoPheno.allAgainstAll();
		String res = arrayToString(result,keys);
		
		/*LinkedList<String[]>res = AlgoPheno.runPhenomizer(10);
		String tmpRes = resultToString(res);
		System.out.println(tmpRes);*/
		
		//String output = dataPath + "PhenomizerTestResult2.txt";
		/*StringBuilder result = new StringBuilder();
		for(int i=1; i<=10;i++){
			String queryIn = dataPath + "query"+i+".txt";
			LinkedList<Integer> query = FileUtilities.readInQuery(queryIn);
			AlgoPheno.setInput(query, symptoms, ksz, ontology);
			LinkedList<String[]>res=AlgoPheno.runPhenomizer(11);
			String tmpRes = resultToString(res);
			result.append("Query"+i+":\n"+tmpRes);
		}
		
		FileUtilities.writeString(output, result.toString());*/
		FileUtilities.writeString(output, res);
		
		
	}
	
	public static String resultToString(LinkedList<String[]>res){
		StringBuilder sb = new StringBuilder();
		for(String[]element : res){
			sb.append(element[0]+"\t"+element[1]+"\n");
		}
		return sb.toString();
	}
	
	public static String arrayToString(int[][]array, int[] colNames){
		StringBuilder sb = new StringBuilder();
		for(int el : colNames){
			sb.append(el+"\t");
		}
		sb.append("\n");
		for(int i=0;i<array.length; i++){
			sb.append(colNames[i]+"\t");
			for(int j=0; j<array[i].length; j++){
				double element = array[i][j]/100.0;
				sb.append(element+"\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
}


