package phenotogeno.algo;

import java.util.HashMap;
import java.util.LinkedList;

public class PhenoToGenoDriver {
	
	private LinkedList<String[]> phenomizer_raw;
	private LinkedList<String> genes_raw;
	private HashMap<Integer, LinkedList<String>> mapping;
	
	private LinkedList<ScoredDisease> phenomizer;
	private DiseaseGeneAssociation dga;
	
	//call this class from node to run PhenoToGeno
	
	public PhenoToGenoDriver(LinkedList<String[]> phenomizer_raw, LinkedList<String> genes_raw,
			HashMap<Integer, LinkedList<String>> mapping){
		this.phenomizer_raw = phenomizer_raw;
		this.genes_raw = genes_raw;
		this.mapping = mapping;
	}
	
	public LinkedList<ScoredGene> runPhenoToGeno(){
		prepareData();
		PhenoToGenoAlgo ptg = new PhenoToGenoAlgo(phenomizer, dga);
		return ptg.runPhenoToGene();
	}
	

	private void prepareData(){
		
		PhenoToGenoDataTransformer dt = new PhenoToGenoDataTransformer();
		// list of scored diseases
		phenomizer = dt.getPhenomizerResult(phenomizer_raw);
		// association disease - gene
		dga = dt.getDiseaseGeneAssociation(genes_raw, mapping);
	}

}
