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
		String queryIn = dataPath + "query1.txt";

		HashMap<Integer,LinkedList<Integer>>ksz = FileUtilities.readInKSZ(diseasesIn);
		LinkedList<Integer> symptoms = FileUtilities.readInSymptoms(symptomsIn);
		int[][]ontology = FileUtilities.readInOntology(ontologyIn);
		LinkedList<Integer> query = FileUtilities.readInQuery(queryIn);
		
		//AlgoPheno.setInput(query, symptoms, ksz, ontology);

	}
	
}


