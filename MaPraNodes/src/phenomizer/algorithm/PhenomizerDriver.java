package phenomizer.algorithm;

import java.util.HashMap;
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
		
		DataTransformer dt = new DataTransformer();
		
		ontology = new Ontology(onto);
		queryIds = dt.prepareQuery(this.ontology, this.query);
		symptoms_and_diseases = dt.generateSymptomDiseaseAssociation(this.ontology, this.symptoms, this.ksz);	
	}
}
