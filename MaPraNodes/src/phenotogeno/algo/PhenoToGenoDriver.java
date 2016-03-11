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
	
	/*
	 * prepare data structures
	 * generate PhenoToGeno object
	 * start algorithm
	 */
	public LinkedList<ScoredGene> runPhenoToGeno(){
		prepareData();
		
		return null;
	}
	

	private void prepareData(){
		
		// list of scored diseases
		phenomizer = new LinkedList<ScoredDisease>();
		for(String[] disease_pval: phenomizer_raw){
			ScoredDisease d = new ScoredDisease(Integer.valueOf(disease_pval[0]),
												Double.valueOf(disease_pval[1]));
			phenomizer.add(d);
		}
		
		//array of genes
		AnnotatedGene [] genes = new AnnotatedGene [genes_raw.size()];
		int pos=0;
		for(String s: genes_raw){
			genes[pos]=new AnnotatedGene(s);
			pos++;
		}
		// association disease - gene
		dga = new DiseaseGeneAssociation(genes, mapping);	
	}

}
