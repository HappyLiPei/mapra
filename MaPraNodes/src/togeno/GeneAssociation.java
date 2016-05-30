package togeno;

import java.util.HashMap;
import java.util.LinkedList;

/** object representing associations between genes and diseases/metabolites*/
public class GeneAssociation {
	
	/** mapping between disease/metabolite id -> list of associated gene ids*/
	private HashMap<String, LinkedList<String>> disOrMetID_genesID;
	/** mapping of gene ids to {@link AnnotatedGene} objects*/
	private HashMap<String, AnnotatedGene> geneID_gene;
	/** array of all {@link AnnotatedGene} objects managed in this data structure*/
	private AnnotatedGene[] allGenes;
	
	/**
	 * creates object that represents association of diseases/metabolites and genes,
	 * the object allows to add Phenomizer/Metabolite scores to the genes
	 * @param genes array of all genes to consider
	 * @param mapping mapping disease/metabolite -> genes,
	 * 	the disease is represented as a PhenoDis disease id (converted to String)/ the metabolite is represented
	 * 		as a Metabolon id
	 * 	the genes are a list of ids (e.g. ensembl) (String)
	 */
	public GeneAssociation(AnnotatedGene[] genes, HashMap<String, LinkedList<String>> mapping) {
		this.disOrMetID_genesID = mapping;
		this.allGenes = genes;
		buildGeneIDMap();
	}
	
	/**
	 * method to build the map gene id-> gene object,
	 * method is used in the constructor
	 */
	private void buildGeneIDMap(){
		geneID_gene = new HashMap<String, AnnotatedGene>(allGenes.length*3);
		for(AnnotatedGene g: allGenes){
			geneID_gene.put(g.getId(), g);
		}
	}
	
	/**
	 * gets all annotated gene objects that are associated with a disease/metabolite
	 * @param dis_or_met_id PhenoDis id of the disease/ Metabolon id of the metabolite
	 * @return array of all gene objects annotated to the disease/metabolite,
	 * 	array is empty if no genes are annotated,
	 * 	array is null if the disease/metabolite id is invalid 
	 */
	public AnnotatedGene[] getGenesForDiseaseMetaboliteWithID(String dis_or_met_id){
		
		//disease id valid
		if(disOrMetID_genesID.containsKey(dis_or_met_id)){
			
			//get gene ids
			LinkedList<String> gene_ids = disOrMetID_genesID.get(dis_or_met_id);
			//array for holding the corresponding gene objects
			AnnotatedGene [] gene_objects = new AnnotatedGene[gene_ids.size()];
			
			// fill array with annotated genes
			int position=0;
			for(String g_id: gene_ids){
				gene_objects[position] = geneID_gene.get(g_id);
				position++;
			}
			return gene_objects;
		}
		
		//disease/metabolite id invalid
		else{
			return null;
		}	
	}
	
	/**
	 * gets all annotated gene objects that are associated with a disease
	 * @param disease_id PhenoDis id of the disease
	 * @return array of all gene objects annotated to the disease,
	 * 	array is empty if no genes are annotated,
	 * 	array is null if the disease/metabolite id is invalid 
	 */
	public AnnotatedGene[] getGenesForDiseaseWithID(int disease_id){
		return getGenesForDiseaseMetaboliteWithID(disease_id+"");
	}

	
	/**
	 * retrieves the gene object corresponding to a id
	 * @param id id of the gene
	 * @return the gene object with the corresponding id,
	 * 	if there is no gene with the given id, the method returns null
	 */
	public AnnotatedGene getGeneWithID(String id){
		if(geneID_gene.containsKey(id)){
			return geneID_gene.get(id);
		}
		else{
			return null;
		}
	}
	
	/**
	 * retrieves all genes managed by this object
	 * @return array of all annotated genes
	 */
	public AnnotatedGene[] getAllGenes(){
		return allGenes;
	}
	
	/**
	 * gets the number of diseases/metabolites stored in this object
	 * @return number of diseases in PhenoDis/measured metabolites
	 */
	public int numberOfDiseasesOrMetabolites(){
		return disOrMetID_genesID.size();
	}
	
	/**
	 * gets the number of genes stored in this object
	 * @return total number of genes
	 */
	public int numberOfGenes(){
		return allGenes.length;
	}
	
	/**
	 * checks if a certain disease/metabolite is part of this object
	 * @param dis_or_met_id PhenoDis id of the disease/ Metabolon id of the metabolite
	 * @return true if there is an entry for the disease/metabolite in this object, 
	 * 			false if there is no entry for the disease/metabolite
	 */
	public boolean containsDiseaseOrMetabolite(String dis_or_met_id) {
		return disOrMetID_genesID.containsKey(dis_or_met_id+"");
	}
	
	/**
	 * clears all disease/metabolite scores that were annotated so far to the genes of this object
	 * method allows reuse of GeneAssociations for several runs of PhenoToGeno or MetaboToGeno
	 */
	public void resetScores(){
		for(AnnotatedGene ag: allGenes){
			ag.resetAnnotation();
		}
	}
	

}
