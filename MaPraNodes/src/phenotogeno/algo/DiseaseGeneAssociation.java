package phenotogeno.algo;

import java.util.HashMap;
import java.util.LinkedList;

public class DiseaseGeneAssociation {
	
	private HashMap<Integer, LinkedList<String>> diseaseID_genesID;
	private HashMap<String, AnnotatedGene> geneID_gene;
	private AnnotatedGene[] allGenes;
	
	/**
	 * creates object that represents association of diseases and genes
	 * the object allows to add Phenomizer scores to the genes
	 * @param genes: array of all genes to consider
	 * @param mapping: mapping disease -> genes
	 * 	the disease is represented as a PhenoDis disease id (integer)
	 * 	the genes are a list of ids (e.g. ensembl) (String)
	 */
	public DiseaseGeneAssociation(AnnotatedGene[] genes, HashMap<Integer, LinkedList<String>> mapping) {
		this.diseaseID_genesID = mapping;
		this.allGenes = genes;
		buildGeneIDMap();
	}
	
	/**
	 * method to build the map gene id-> gene object
	 * method is used in the constructor
	 */
	private void buildGeneIDMap(){
		geneID_gene = new HashMap<String, AnnotatedGene>(allGenes.length);
		for(AnnotatedGene g: allGenes){
			geneID_gene.put(g.getId(), g);
		}
	}
	
	/**
	 * gets all annotated gene objects that are associated with a disease
	 * @param disease_id: PhenoDis id of the disease
	 * @return: array of all gene objects annotated to the disease
	 * 	array is empty if no genes are annotated
	 * 	array is null if the disease id is invalid 
	 */
	public AnnotatedGene[] getGenesForDiseaseWithID(int disease_id){
		
		//disease id valid
		if(diseaseID_genesID.containsKey(disease_id)){
			
			//get gene ids
			LinkedList<String> gene_ids = diseaseID_genesID.get(disease_id);
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
		
		//disease id invalid
		else{
			return null;
		}	
	}
	
	/**
	 * retrieves the gene object corresponding to a id
	 * @param id: id of the gene
	 * @return: the gene object with the corresponding id,
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
	 * @return: array of all annotated genes
	 */
	public AnnotatedGene[] getAllGenes(){
		return allGenes;
	}
	
	/**
	 * gets the number of diseases stored in this object
	 * @return: number of diseases in PhenoDis
	 */
	public int numberOfDiseases(){
		return diseaseID_genesID.size();
	}
	

}
