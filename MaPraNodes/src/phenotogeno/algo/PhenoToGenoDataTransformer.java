package phenotogeno.algo;

import java.util.HashMap;
import java.util.LinkedList;

public class PhenoToGenoDataTransformer {
	
	/**
	 * Generates a list of ScoredDiseases from a prediction result of Phenomizer
	 * @param phenomizer_input list of String arrays containing PhenoDis disease id (pos 0) and
	 * 			p value of Phenomizer(pos 1)
	 * @return List of ScoredDiseases required for PhenoToGeno Algo
	 */
	public LinkedList<ScoredDisease> getPhenomizerResult(LinkedList<String[]> phenomizer_input){
		
		LinkedList<ScoredDisease> result = new LinkedList<ScoredDisease>();
		for(String[] disease_pval: phenomizer_input){
			ScoredDisease d = new ScoredDisease(Integer.valueOf(disease_pval[0]), Double.valueOf(disease_pval[1]));
			result.add(d);
		}
		return result;
	}
	
	/**
	 * Generates a DiseaseGeneAssociation from a list of genes and a mapping between diseases and genes
	 * @param gene_list LinkedList of gene ids (e.g. ensembl identifier), should not contain any duplicates!
	 * @param association hashmap phenodis disease id -> list of gene ids (e.g ensmebl)
	 * @return a DiseaseGeneAssociation object require for PhenoToGeno algo
	 */
	public DiseaseGeneAssociation getDiseaseGeneAssociation(LinkedList<String> gene_list,
			HashMap<Integer, LinkedList<String>> association){
		
		//array of genes
		AnnotatedGene [] genes = new AnnotatedGene [gene_list.size()];
		int pos=0;
		for(String s: gene_list){
			genes[pos]=new AnnotatedGene(s);
			pos++;
		}
		// association disease - gene
		return new DiseaseGeneAssociation(genes, association);
	}

}
