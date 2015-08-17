package main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import algorithm.AlgoPheno;
import algorithm.Ontology;

public class TestAlgoCaro {

	public static void main(String[]args){
		
		String a = "11 of 21 [HPO]";
		String[] ar = a.split(" ");
		System.out.println(ar[2]);
		
		
		/*
		String dataPath = "C:/Users/Carolin/Dropbox/Masterpraktikum/Testdatensatz/";
		
		String diseasesIn = dataPath + "Krankheiten.txt";
		String symptomsIn = dataPath + "Symptome.txt";
		String ontologyIn = dataPath + "Ontology.txt";
		String queryIn = dataPath + "query1.txt";		
		*/
		
		String dataPath = "C:/Users/Carolin/Dropbox/Masterpraktikum/Datenbank/";
		
		String diseasesIn = dataPath + "ksz_HPO_test.csv";
		String symptomsIn = dataPath + "symptoms_HPO_test.csv";
		String ontologyIn = dataPath + "isa_HPO_test.csv";
		//String queryIn = dataPath + "query1.txt";

		HashMap<Integer,LinkedList<Integer>>ksz = FileUtilities.readInKSZ(diseasesIn);
		LinkedList<Integer> symptoms = FileUtilities.readInSymptoms(symptomsIn);
		int[][]ontology = FileUtilities.readInOntology(ontologyIn);
		//LinkedList<Integer> query = FileUtilities.readInQuery(queryIn);
		
		//AlgoPheno.setInput(query, symptoms, ksz, ontology);
		Ontology onto = new Ontology(ontology);
		
		/*
		System.out.println("Query");
		System.out.println(listToString(AlgoPheno.queryIds));
		System.out.println("Symptoms");
		System.out.println(listToString(AlgoPheno.symptomIds));
		System.out.println("kszD");
		System.out.println(hashMapToString(AlgoPheno.kszD));
		System.out.println("kszS");
		System.out.println(hashSetToString(AlgoPheno.kszS));
		*/
		
		
		System.out.println("AllCommonAncestors");
		int node1 = 34;
		int[] node2 = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40}; 
		for(int i = 0; i<40; i++){
			System.out.println(node1+"\t"+node2[i]+"\t"+onto.getAllCommonAncestors(node1, node2[i]).toString());
		}
		
		
		System.out.println("RelevantCommonAncestors");
		int node1b = 34;
		int[] node2b = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40}; 
		for(int i = 0; i<40; i++){
			System.out.println(node1+"\t"+node2[i]+"\t"+onto.getRelevantCommonAncestors(node1b, node2b[i]).toString());
		}
		

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
	
	
	public static String hashSetToString(HashSet<Integer> list){
		StringBuilder sb = new StringBuilder();
		Iterator<Integer> iter = list.iterator();
		while(iter.hasNext()){
			iter.next();
			
		}
		return "";
		
		
		
	}
	
}
