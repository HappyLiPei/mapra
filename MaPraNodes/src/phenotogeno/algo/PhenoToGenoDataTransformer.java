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
	public LinkedList<ScoredDisease> getPhenomizerResult(LinkedList<String[]> phenomizer_input,
			DiseaseGeneAssociation dga){
		
		LinkedList<ScoredDisease> result = new LinkedList<ScoredDisease>();
		for(String[] disease_pval: phenomizer_input){
			int disease_id = Integer.valueOf(disease_pval[0]);
			if(dga.containsDisease(disease_id)){
				ScoredDisease d = new ScoredDisease(disease_id, Double.valueOf(disease_pval[1]));
				result.add(d);
			}
		}
		return result;
	}
	
	/**
	 * Generates a DiseaseGeneAssociation from a list of genes and a mapping between diseases and genes
	 * @param gene_list LinkedList of gene ids (e.g. ensembl identifier)
	 * @param association hashmap phenodis disease id -> list of gene ids (e.g ensmebl)
	 * @return a DiseaseGeneAssociation object require for PhenoToGeno algo
	 */
	public DiseaseGeneAssociation getDiseaseGeneAssociation(LinkedList<String> gene_list,
			HashMap<Integer, LinkedList<String>> association){
		
		//remove duplicates from gene_list
		LinkedList<String> dedup_gene_list = new LinkedList<String>();
		for(String s: gene_list){
			if(!dedup_gene_list.contains(s)){
				dedup_gene_list.add(s);
			}
		}
		
		//generate array of genes
		AnnotatedGene [] genes = new AnnotatedGene [dedup_gene_list.size()];
		int pos=0;
		for(String s: dedup_gene_list){
			genes[pos]=new AnnotatedGene(s);
			pos++;
		}
		
		//remove all genes from association map that are not in the gene list
		HashMap<Integer, LinkedList<String>> associationsCorrected =
				new HashMap<Integer, LinkedList<String>>(association.size()*3);
		for(int id:association.keySet()){
			LinkedList<String> genesCorrected = new LinkedList<String>();
			associationsCorrected.put(id, genesCorrected);
			for(String entry_gene:association.get(id)){
				if(dedup_gene_list.contains(entry_gene)){
					genesCorrected.add(entry_gene);
				}
			}
		}

		// association disease - gene
		return new DiseaseGeneAssociation(genes, associationsCorrected);
	}

}
