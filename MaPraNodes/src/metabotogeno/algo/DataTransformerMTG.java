package metabotogeno.algo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import togeno.AnnotatedGene;
import togeno.GeneAssociation;
import togeno.ScoredDiseaseOrMetabolite;

public class DataTransformerMTG {
	
	//TODO: pass results directly from MetaboliteScore vs. pass results from file/KNIME table
	
	/**
	 * method to convert the results of ScoreMetabolites from a file or KNIME table into a list of {@link ScoredDiseaseOrMetabolite}
	 * objects that is required for running MetaboToGeno,
	 * the method removes duplicate metabolite ids and metabolite ids that are not stored in the 
	 * corresponding {@link GeneAssociation} object,
	 * the method replaces pvalues equal to 0 by the minimum value of 1E-5=0.00001
	 * @param metabolite_res list of String arrays with 2 entrys per array: position 0 contains the metabolite id,
	 * position 1 contains the pvalue obtained for that metabolite
	 * @param mga GeneAssociation object (e.g. created by getMetaboliteGeneAssociations())
	 * @return a list of ScoredMetabolite objects storing ids and pvalues
	 */
	public LinkedList<ScoredDiseaseOrMetabolite> getMetaboliteScoreResult(LinkedList<String []> metabolite_res,
			GeneAssociation mga){
		
		LinkedList<ScoredDiseaseOrMetabolite> scoredMetabos = new LinkedList<ScoredDiseaseOrMetabolite>();
		HashSet<String> metabosSeen = new HashSet<String>(metabolite_res.size()*3);
		
		for(String[] result: metabolite_res){
			//remove duplicates and metabolites that are not part of the associations
			if(metabosSeen.add(result[0]) && mga.containsDiseaseOrMetabolite(result[0])){
				//handle pvalues that are nearly 0 (p values are rounded to 5 decimal places)
				double pvalue = Double.parseDouble(result[1]);
				if(pvalue<1E-5){
					pvalue=1E-5;
				}
				ScoredDiseaseOrMetabolite scoredMetabolite = new ScoredDiseaseOrMetabolite(result[0], pvalue);
				scoredMetabos.add(scoredMetabolite);
			}
		}
		
		return scoredMetabos;
	}
	
	/**
	 * method to convert associations between metabolites and genes from a file or KNIME table into a {@link GeneAssociation}
	 * datastructure that is required for running MetaboToGeno
	 * the method removes duplicate metabolite-gene pairs and genes that are not part of the reference list
	 * @param allGenes: list with ids of all genes (e.g. ENSG identifier)
	 * @param associations: mapping metabolite id (e.g. Metabolon id) -> list of gene ids (e.g ENSG id)
	 * @return GeneAssociation object for storing the metabolite-gene associations
	 */
	public GeneAssociation getMetaboliteGeneAssociations(LinkedList<String> allGenes,
			HashMap<String, LinkedList<String>> associations){
		
		//remove duplicates from gene list
		LinkedList<String> genesNoDupl = new LinkedList<String>();
		for(String geneId: allGenes){
			if(!genesNoDupl.contains(geneId)){
				genesNoDupl.add(geneId);
			}
		}
		
		//remove duplicates in associated genes of a metabolite (e.g. same association from different source)
		//remove genes that are not part of the gene list
		HashMap<String, LinkedList<String>> associationsNoDupl = new HashMap<String, LinkedList<String>>(associations.size()*3);
		for(String metaboId: associations.keySet()){
			LinkedList<String> genesNew = new LinkedList<String>();
			for(String geneId: associations.get(metaboId)){
				if(!genesNew.contains(geneId) && genesNoDupl.contains(geneId)){
					genesNew.add(geneId);
				}
			}
			associationsNoDupl.put(metaboId, genesNew);
		}
		
		//transform gene list into array of annotated genes
		AnnotatedGene[] annoArray = new AnnotatedGene[genesNoDupl.size()];
		int counter=0;
		for(String geneId: genesNoDupl){
			annoArray[counter]=new AnnotatedGene(geneId);
			counter++;
		}
		
		//object representing the associations between metabolites and genes		
		return new GeneAssociation(annoArray, associationsNoDupl);
	}
}
