package phenomizeralgorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

public class DataTransformer {
	
	/**
	 * preparation of the query data structure
	 * function removes symptoms whose successors are also in the list
	 * @param ontology: ontology of symptoms
	 * @param raw_query: query symptoms (as list of PhenoDis ids)
	 * @return list of PhenoDis ids without any ancestors of query terms and without any duplicates
	 */	
	public LinkedList<Integer> prepareQuery(Ontology ontology, LinkedList<Integer> raw_query){
		
		//result= symptoms without duplicates
		LinkedList<Integer>result = removeDuplicates(raw_query);
		
		//result= symptoms without ancestors
		for(int element : raw_query){
			HashSet<Integer>ancestors=ontology.getAllAncestors(element);
			ancestors.remove(element);
			for(int element1 : raw_query){
				//check if element from list is ancestors of another element and remove the element
				if(ancestors.contains(element1)&&result.contains(element1)){
					int index = result.indexOf(element1);
					result.remove(index);
				}
			}
		}
		return result;
	}
	
	/**
	 * creates an object associating symptoms and diseases with each other
	 * @param ontology: ontology of PhenoDis symptoms
	 * @param symptoms: list of PhenoDis symptom ids
	 * @param ksz: maps a disease id to list of symptom ids and weights
	 * @return SymptomDiseaseAssociation object used by PhenomizerAlgorithm
	 */
	public SymptomDiseaseAssociations generateSymptomDiseaseAssociation(
			Ontology ontology, LinkedList<Integer> symptoms, HashMap<Integer, LinkedList<Integer[]>> ksz){
		
		LinkedList<Integer> symptoms_without_dupl = removeDuplicates(symptoms);
		
		//build map disease->symptoms: kszD
		HashMap<Integer,LinkedList<Integer[]>> kszD = new HashMap<Integer,LinkedList<Integer[]>>(ksz.size()*3);
		for(int key : ksz.keySet()){
			LinkedList<Integer[]>value = removeAncestorsFromKSZ(ksz.get(key),ontology);
			kszD.put(key, value);
		}

		//build map symptom->diseases: kszS
		HashMap<Integer,HashSet<Integer>> kszS = new HashMap<Integer,HashSet<Integer>>(symptoms_without_dupl.size()*3);
		for(int symp : symptoms_without_dupl){
			kszS.put(symp, new HashSet<Integer>());
		}
		//iterate over all diseases
		for(int disease : ksz.keySet()){
			LinkedList<Integer[]> tmpSymp = ksz.get(disease);
			//iterate over all symptoms of a disease
			for(Integer[] symp : tmpSymp){
				HashSet<Integer>ancestorsOrSelf = ontology.getAllAncestors(symp[0]);
				//add disease for symptom and its ancestors
				for(int nextSymp : ancestorsOrSelf){
					HashSet<Integer> tmp = kszS.get(nextSymp);
					if(!tmp.contains(disease)){
						tmp.add(disease);
					}
					kszS.put(nextSymp, tmp);
				}
			}
		}
		
		return new SymptomDiseaseAssociations(symptoms_without_dupl, kszD, kszS);
	}
	
	/**
	 * remove symptoms whose successors are also in the list for a list of weighted symptoms
	 * @param list of arrays containing symptom id (index 0) and weights (index 1)
	 * @return list without any ancestors of query terms and without any duplicates
	 */
	private LinkedList<Integer[]> removeAncestorsFromKSZ(LinkedList<Integer[]> symptoms, Ontology ontology){
		
		LinkedList<Integer[]>result = new LinkedList<Integer[]>();
		
		//result= symptoms without duplicates
		for(Integer[] element : symptoms){
			boolean contains=false;
			for(Integer[] entry:result){
				if(entry[0].equals(element[0])){
					contains=true;
					break;
				}
			}
			if(!contains){
				result.add(element);
			}
		}

		for(Integer[] element : symptoms){
			HashSet<Integer>ancestors=ontology.getAllAncestors(element[0]);
			ancestors.remove(element[0]);
			for(Integer[] element1 : symptoms){
				//check if element from list is ancestors of another element and remove the element
				if(ancestors.contains(element1[0])&&result.contains(element1)){
					int index = result.indexOf(element1);
					result.remove(index);
				}
			}
		}
		return result;
	}
	
	private LinkedList<Integer> removeDuplicates(LinkedList<Integer> list_with_dupl){
		LinkedList<Integer> result = new LinkedList<Integer>();
		for(int element : list_with_dupl){
			if(!result.contains(element)){
				result.add(element);
			}
		}
		return result;
	}
	
	public LinkedList<Integer> getRandomQuery(int length, Ontology ontology, int[] symptoms){
		
		LinkedList<Integer> query = new LinkedList<Integer>();
		
		while(query.size()<length){
			Random rnd = new Random();
			int next = rnd.nextInt(symptoms.length);
			int nextId = symptoms[next];
			query.add(nextId);
			query = prepareQuery(ontology, query);
		}
		return query;
		
	}
	
	





}
