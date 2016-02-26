package phenomizeralgorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class PhenomizerDriver {
	
	//input data
	private LinkedList<Integer> query;
	private LinkedList<Integer> symptoms;
	private HashMap<Integer, LinkedList<Integer[]>> ksz;
	private int [][] onto;
	
	//processed data used for the PhenomizerAlgorithm
	private Ontology ontology;
	private LinkedList<Integer> queryIds;
	private SymptomDiseaseAssociations symptoms_and_diseases;
	
	//phenomizer algorithm strategy pattern
	private PhenomizerAlgorithm pheno;
	

	
	public PhenomizerDriver(LinkedList<Integer> query, LinkedList<Integer>symptoms,
			HashMap<Integer,LinkedList<Integer[]>> ksz, int[][]onto){
		
		this.query=query;
		this.symptoms=symptoms;
		this.ksz = ksz;
		this.onto=onto;
		
	}
	
	public void setPhenomizerAlgorithm(int num, boolean pvalue, int weight, String pvalfolder){
		
		prepareData();

		// output cannot be larger than the number of all diseases
		if(num>symptoms_and_diseases.numberOfDiseases()){
			num=symptoms_and_diseases.numberOfDiseases();
		}
		
		SimilarityCalculator sc = null;
		if(weight==0){
			sc = new SimilarityCalculatorNoWeight();
		}
		else if(weight==1){
			sc = new SimilarityCalculatorOneSidedWeight();			
		}
		else if (weight==2){
			sc = new SimilarityCalculatorTwoSidedWeight();
		}
		
		if(!pvalue){
			this.pheno = new PhenomizerAlgorithmNoPvalue(num, ontology, queryIds, symptoms_and_diseases, sc);
		}
		else{
			PValueFolder p = new PValueFolder(pvalfolder);
			this.pheno = new PhenomizerAlgorithmWithPval(num, ontology, queryIds, symptoms_and_diseases,
					sc, p, new BenjaminiHochbergCorrector());
		}
	}
	
	public LinkedList<String[]> runPhenomizer(){
		if(pheno==null){
			System.out.println("Phenomizer algorithm is not set. Do nothing");
		}

		return pheno.runPhenomizer();
		
		
	}
	
	private void prepareData(){
		
		ontology = new Ontology(onto);
		queryIds = removeAncestorsFromQuery(query);

		//build disease-symptom map without duplicates and without ancestors whose successors are also in the annotation
		HashMap<Integer,LinkedList<Integer[]>> kszD = new HashMap<Integer,LinkedList<Integer[]>>();
		for(int key : ksz.keySet()){
			LinkedList<Integer[]>value = removeAncestorsFromKSZ(ksz.get(key));
			kszD.put(key, value);
		}

		//build symptom-disease map listing for each symptom the diseases it is annotated to
		HashMap<Integer,HashSet<Integer>> kszS = new HashMap<Integer,HashSet<Integer>>();
		for(int symp : symptoms){
			kszS.put(symp, new HashSet<Integer>());
		}
		for(int disease : ksz.keySet()){
			LinkedList<Integer[]> tmpSymp = ksz.get(disease);
			for(Integer[] symp : tmpSymp){
				HashSet<Integer>ancestorsOrSelf = ontology.getAllAncestors(symp[0]);
				for(int nextSymp : ancestorsOrSelf){
					HashSet<Integer> tmp = kszS.get(nextSymp);
					if(!tmp.contains(disease)){
						tmp.add(disease);
					}
					kszS.put(nextSymp, tmp);
				}
			}
		}
		
		symptoms_and_diseases = new SymptomDiseaseAssociations(symptoms, kszD, kszS);
	}
	
	/**
	 * remove symptoms whose successor(s) are also in the list
	 * @param symptoms
	 * @return list without any ancestors of query terms
	 */
	private LinkedList<Integer>removeAncestorsFromQuery(LinkedList<Integer>symptoms){

		LinkedList<Integer>result = new LinkedList<Integer>();
		
		//result= symptoms without duplicates
		for(int element : symptoms){
			if(!result.contains(element)){
				result.add(element);
			}
		}
		
		//result= symptoms without ancestors
		for(int element : symptoms){
			HashSet<Integer>ancestors=ontology.getAllAncestors(element);
			ancestors.remove(element);
			for(int element1 : symptoms){
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
	 * remove symptoms whose successor(s) are also in the list for a list of weighted symptoms
	 * @param symptoms with weights
	 * @return list without any ancestors of query terms
	 */
	private LinkedList<Integer[]> removeAncestorsFromKSZ(LinkedList<Integer[]> symptoms){
		
		LinkedList<Integer[]>result = new LinkedList<Integer[]>();
		
		//result= symptoms without duplicates
		for(Integer[] element : symptoms){
			if(!result.contains(element)){
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

}
