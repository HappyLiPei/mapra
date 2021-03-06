package phenotogeno.algo;

import java.util.HashMap;
import java.util.LinkedList;

import togeno.AnnotatedGene;
import togeno.AnnotatedGeneMax;
import togeno.AnnotatedGeneMultiple;
import togeno.GeneAssociation;
import togeno.ScoredDiseaseOrMetabolite;

public class PhenoToGenoDataTransformer {
	
	/**
	 * Generates a list of ScoredDiseases from a prediction result of Phenomizer that was read
	 * form a file or a KNIME table,
	 * removes diseases that are not managed in the diseaes - gene annotation,
	 * replaces the p values 0.0 by the minimum p value 0.001
	 * @param phenomizer_input list of String arrays containing PhenoDis disease id (pos 0) and
	 * 			p value of Phenomizer(pos 1)
	 * @param dga DiseaseGeneAssocation object representing disease - gene associations
	 * @return List of ScoredDiseases required for PhenoToGenoAlgo
	 */
	public LinkedList<ScoredDiseaseOrMetabolite> getPhenomizerResult(LinkedList<String[]> phenomizer_input,
			GeneAssociation dga){
		
		return getPhenomizerResult(phenomizer_input, dga, 0, 1);
	}
	
	/**
	 * Generates a list of ScoredDiseases from a prediction result of Phenomizer that is directly
	 * taken from the PhenomizerAlgorithmWithPval class
	 * removes diseases that are not managed in the diseaes - gene annotation,
	 * replaces the p values 0.0 by the minimum p value 0.001
	 * @param phenomizer_input list of String arrays containing PhenoDis disease id (pos 0) and
	 * 			p value of Phenomizer(pos 2)
	 * @param dga DiseaseGeneAssocation object representing disease - gene associations
	 * @return List of ScoredDiseases required for PhenoToGenoAlgo
	 */
	public LinkedList<ScoredDiseaseOrMetabolite> getPhenomizerResultFromAlgo(LinkedList<String[]> phenomizer_input,
			GeneAssociation dga){
		
		return getPhenomizerResult(phenomizer_input, dga, 0, 2);
	} 
	
	/**
	 * Generates a list of ScoredDiseases from a prediction result of Phenomizer
	 * removes diseases that are not managed in the diseaes - gene annotation,
	 * replaces the p values 0.0 by the minimum p value 0.001
	 * @param phenomizer_input list of String arrays containing PhenoDis disease id (pos posID) and
	 * 			p value of Phenomizer(pos posPval)
	 * @param dga DiseaseGeneAssocation object representing disease - gene associations
	 * @param posId position of the disease id within the arrays of phenomizer_input
	 * @param posPval position of the pvalue within the arrays of phenomizer_input
	 * @return List of ScoredDiseases required for PhenoToGenoAlgo
	 */
	private LinkedList<ScoredDiseaseOrMetabolite> getPhenomizerResult(LinkedList<String[]> phenomizer_input,
			GeneAssociation dga, int posId, int posPval){
		
		LinkedList<ScoredDiseaseOrMetabolite> result = new LinkedList<ScoredDiseaseOrMetabolite>();
		for(String[] disease_pval: phenomizer_input){
			String disease_id = disease_pval[posId];
			//test if disease is part of disease - gene annotation
			if(dga.containsDiseaseOrMetabolite(disease_id)){
				double pvalue = Double.parseDouble(disease_pval[posPval]);
				//test if pvalue is 0, if yes -> replace it by 0.001
				if(pvalue<1E-3){
					pvalue =0.001;
				}
				ScoredDiseaseOrMetabolite d = new ScoredDiseaseOrMetabolite(disease_id, pvalue);
				result.add(d);
			}
		}
		return result;
	}
	
	/**
	 * Generates a DiseaseGeneAssociation from a list of genes and a mapping between diseases and genes,
	 * removes duplicates from the gene list and from the associations and
	 * removes genes from the associations that are not listed in the gene list
	 * @param gene_list LinkedList of gene ids (e.g. ensembl identifier)
	 * @param association hashmap phenodis disease id -> list of gene ids (e.g ensmebl)
	 * @param multiple flag to indicate the mode of annotation, if multiple = true, annotations from all diseases of
	 * 		a gene are combined, if multiple = false, the maximum scoring disease is used to annotate a gene 
	 * @return a DiseaseGeneAssociation object required for PhenoToGeno algo
	 */
	public GeneAssociation getDiseaseGeneAssociation(LinkedList<String> gene_list,
			HashMap<Integer, LinkedList<String>> association, boolean multiple){
		
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
			if(multiple){
				genes[pos]=new AnnotatedGeneMultiple(s);
			}
			else{
				genes[pos]=new AnnotatedGeneMax(s);
			}
			pos++;
		}
		
		//remove all genes from association map that are not in the gene list and remove all duplicates
		HashMap<String, LinkedList<String>> associationsCorrected =
				new HashMap<String, LinkedList<String>>(association.size()*3);
		for(int id:association.keySet()){
			LinkedList<String> genesCorrected = new LinkedList<String>();
			associationsCorrected.put(id+"", genesCorrected);
			for(String entry_gene:association.get(id)){
				// gene is in gene list && gene is no duplicate 
				if(dedup_gene_list.contains(entry_gene) && !genesCorrected.contains(entry_gene)){
					genesCorrected.add(entry_gene);
				}
			}
		}

		// association disease - gene
		return new GeneAssociation(genes, associationsCorrected);
	}

}
