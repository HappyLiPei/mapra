package main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import algorithm.AlgoPheno;

public class TestAlgo {
	
	public static void main(String[]args){
		
		String dataPath = "D:/Dokumente/Masterpraktikum/Testdatensatz/";
		
		String diseasesIn = dataPath + "Krankheiten.txt";
		String symptomsIn = dataPath + "Symptome.txt";
		String ontologyIn = dataPath + "Ontology.txt";
		String queryIn = dataPath + "query1.txt";

		HashMap<Integer,LinkedList<Integer>>ksz = FileUtilities.readInKSZ(diseasesIn);
		LinkedList<Integer> symptoms = FileUtilities.readInSymptoms(symptomsIn);
		int[][]ontology = FileUtilities.readInOntology(ontologyIn);
		LinkedList<Integer> query = FileUtilities.readInQuery(queryIn);
		
		AlgoPheno.setInput(query, symptoms, ksz, ontology);
		/*System.out.println("Query");
		System.out.println(listToString(AlgoPheno.queryIds));
		System.out.println("Symptoms");
		System.out.println(listToString(AlgoPheno.symptomIds));
		System.out.println("kszD");
		System.out.println(hashMapToString(AlgoPheno.kszD));
		System.out.println("kszS");*/
		System.out.println(hashSetToString(AlgoPheno.kszS));	
	}
	
	public static String listToString(LinkedList<Integer>list){
		StringBuilder sb = new StringBuilder();
		for(int el : list){
			sb.append(el+"\n");
		}
		return sb.toString();
	}
	
	public static String hashMapToString(HashMap<Integer,LinkedList<Integer>>list){
		StringBuilder sb = new StringBuilder();
		for(int key : list.keySet()){
			LinkedList<Integer> values = list.get(key);
			sb.append(key+"\t");
			for(int val : values){
				sb.append(val+"\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public static String hashSetToString(HashMap<Integer,HashSet<Integer>>list){
		StringBuilder sb = new StringBuilder();
		for(int key : list.keySet()){
			HashSet<Integer> values = list.get(key);
			sb.append(key+"\t");
			for(int val : values){
				sb.append(val+"\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
}


