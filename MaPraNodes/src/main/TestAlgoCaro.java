package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import algorithm.AlgoPheno;
import algorithm.Binner;
import algorithm.Ontology;
import algorithm.PValueFolder;
import algorithm.PValueGenerator;

public class TestAlgoCaro {

	public static void main(String[]args) throws IOException{
		
		/*
		// files mit empirischen sim-scores umwandeln (binnen)
		String file = "length_2.txt";
		String pathIn = "C:/Users/Carolin/Downloads/pvalues/"+file;
		String pathOut = "C:/Users/Carolin/Downloads/pvalues_binned/"+file;
		Binner.runBinner(pathIn, pathOut);
		*/
		
		// testdaten mit compressed p-value files
		String dataPath = "C:/Users/Carolin/Dropbox/Masterpraktikum/Testdatensatz/";
		String diseasesIn = dataPath + "Krankheiten.txt";
		String symptomsIn = dataPath + "Symptome.txt";
		String ontologyIn = dataPath + "Ontology.txt";
		String queryIn = dataPath + "query10.txt";
		
		HashMap<Integer,LinkedList<Integer>> ksz = FileUtilities.readInKSZ(diseasesIn);
		HashMap<Integer, LinkedList<Integer []>> diseases = addWeights(ksz);
		LinkedList<Integer> symptoms = FileUtilities.readInSymptoms(symptomsIn);
		int[][] ontology = FileUtilities.readInOntology(ontologyIn);
		LinkedList<Integer> query = FileUtilities.readInQuery(queryIn);
		
		
		String m_folder = "C:/Users/Carolin/Dropbox/Masterpraktikum/Testdatensatz/pvalues_binned";
    	PValueFolder.setPvalFoder(m_folder);
    	LinkedList<String[]>result =PValueGenerator.phenomizerWithPValues(20, query, symptoms, diseases, ontology);
    	for (String[] array : result){
    		for(String field : array){
    			System.out.print(field+"\t");
    		}
    		System.out.print("\n");
    	}
   		
		
		
		/*
		// for classification of diseases:
		String dataPathDisease = "C:/Users/Carolin/Downloads/disease_classification.txt";
		String outDisease = "C:/Users/Carolin/Downloads/disease_classification_parsed.txt";
		List<String> content = FileUtilities.readLines(dataPathDisease);
		FileWriter fw = new FileWriter(outDisease);
		BufferedWriter bw = new BufferedWriter(fw);
		for(String line : content){
			String[] a = line.split("\t");
			if(a.length>=3){
				String[] c = a[2].split("<");
				String lineOut = a[0]+"\t"+a[1]+"\t<"+c[c.length-1];
				System.out.println(lineOut);
				bw.append(lineOut);
				bw.newLine();
			}
		}
		bw.close();
		*/
		
		
		/*
		// for parsing all-against-all (for disease network):
		String dataPathIn = "C:/Users/Carolin/Dropbox/Masterpraktikum/Clustering/allAgainstAll_complete.txt";
		String pathOut = "C:/Users/Carolin/Downloads/allAgainstAll_complete_parsed.txt";
		List<String> content = FileUtilities.readLines(dataPathIn);
		FileWriter fw = new FileWriter(pathOut);
		BufferedWriter bw = new BufferedWriter(fw);
		
		String[] ids = null;
		for(String line : content){
			if(line.startsWith("id")){
				ids = line.split(",");//erste id in ids[1] usw... bis ids[7554] mit letzter disease-id
				//System.out.println(ids[7554]);
			}
			else{
				String[] l = line.split(",");
				for(int i=1; i<=7554; i++){
					String lineOut = l[0]+"\t"+ids[i]+"\t"+l[i];
					System.out.println(lineOut);
					bw.append(lineOut);
					bw.newLine();
				}
			}
		}
		bw.close();
		*/
		
		/*
		String dataPath = "C:/Users/Carolin/Dropbox/Masterpraktikum/Testdatensatz/";
		
		String diseasesIn = dataPath + "Krankheiten.txt";
		String symptomsIn = dataPath + "Symptome.txt";
		String ontologyIn = dataPath + "Ontology.txt";
		String queryIn = dataPath + "query1.txt";		
		*/
		
    	/*
		String dataPath = "C:/Users/Carolin/Dropbox/Masterpraktikum/Datenbank/";
		
		String diseasesIn = dataPath + "ksz_HPO_test.csv";
		String symptomsIn = dataPath + "symptoms_HPO_test.csv";
		String ontologyIn = dataPath + "isa_HPO_test.csv";
		//String queryIn = dataPath + "query1.txt";
		
		

		HashMap<Integer,LinkedList<Integer>>ksz = FileUtilities.readInKSZ(diseasesIn);
		LinkedList<Integer> symptoms = FileUtilities.readInSymptoms(symptomsIn);
		int[][]ontology = FileUtilities.readInOntology(ontologyIn);
		LinkedList<Integer> query = FileUtilities.readInQuery(queryIn);
		
		//AlgoPheno.setInput(query, symptoms, ksz, ontology);
		Ontology onto = new Ontology(ontology);
		*/
		
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
		
		/*
		System.out.println("AllCommonAncestors");
		int node1 = 34;
		int[] node2 = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40}; 
		for(int i = 0; i<40; i++){
			System.out.println(node1+"\t"+node2[i]+"\t"+onto.getAllCommonAncestors(node1, node2[i]).toString());
		}
		*/
		
		
    	/*
		System.out.println("RelevantCommonAncestors");
		int node1b = 34;
		int[] node2b = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40}; 
		for(int i = 0; i<40; i++){
			System.out.println(node1+"\t"+node2[i]+"\t"+onto.getRelevantCommonAncestors(node1b, node2b[i]).toString());
		}
		*/
		

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
	
	
	private static HashMap<Integer, LinkedList<Integer []>> addWeights (HashMap<Integer, LinkedList<Integer>> ksz){
		
		HashMap<Integer, LinkedList<Integer[]>> res = new HashMap<Integer, LinkedList<Integer []>>(ksz.size()*3);
		for(Integer k: ksz.keySet()){
			LinkedList<Integer []> list = new LinkedList<Integer[]>();
			res.put(k, list);
			for(int i: ksz.get(k)){
				Integer [] symp_and_weight = new Integer [2];
				symp_and_weight[0]=i;
				symp_and_weight[1]=10;
				list.add(symp_and_weight);
			}			
		}
		return res;
	}
}
