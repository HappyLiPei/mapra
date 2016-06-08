package phenotogeno.algo;

import java.util.HashMap;
import java.util.LinkedList;

import phenotogeno.io.FileUtilitiesPTG;
import phenotogeno.node.TableProcessorPhenoToGeno;
import togeno.GeneAssociation;
import togeno.ScoredDiseaseOrMetabolite;
import togeno.ScoredGene;
import togeno.ToGenoAlgo;

//call this class from node to run PhenoToGeno
/** class for running PhenoToGeno */
public class PhenoToGenoDriver {
	
	/** result of Phenomizer read from a file by {@link FileUtilitiesPTG} 
	 * or from a KNIME table by {@link TableProcessorPhenoToGeno}*/
	private LinkedList<String[]> phenomizer_raw;
	/** associations between genes and diseases read from a file by {@link FileUtilitiesPTG} 
	 * or from a KNIME table by {@link TableProcessorPhenoToGeno}*/
	private LinkedList<String> genes_raw;
	/** list of all genes read from a file by {@link FileUtilitiesPTG} 
	 * or from a KNIME table by {@link TableProcessorPhenoToGeno}*/
	private HashMap<Integer, LinkedList<String>> mapping;
	
	/** variable indicating the mode of annotation, multiple = true -> combine multiple annotation,
	 * multiple = false -> use maximum score that is annotated*/
	private boolean multiple;
	
	/** data structure for ToGeno algo representing the results of Phenomizer */
	private LinkedList<ScoredDiseaseOrMetabolite> phenomizer;
	/** data structure for ToGeno algo representing associations between diseases and genes */
	private GeneAssociation dga;
	
	/**
	 * generates a driver for PhenoToGeno,
	 * 		this objects handles the data needed for PhenoToGeno and runs the PhenoToGenoAlgorithm
	 * @param phenomizer_raw list of String arrays, each array represents one disease with results from Phenomizer
	 * 			array[0]: PhenoDis disease id , array[1]: pvalue
	 * @param genes_raw list of gene ids as Strings (e.g. ensembl ids),
	 * 			assumption: each id occurs exactly once
	 * @param mapping map disease id-> list of gene ids
	 */
	public PhenoToGenoDriver(LinkedList<String[]> phenomizer_raw, LinkedList<String> genes_raw,
			HashMap<Integer, LinkedList<String>> mapping){
		this.phenomizer_raw = phenomizer_raw;
		this.genes_raw = genes_raw;
		this.mapping = mapping;
		this.multiple=true;
	}
	
	/**
	 * method to asjust the mode of annotation used in PhenoToGeno
	 * @param multiple specifies the mode of annotation, if multiple = true, scores from all annotations are combined,
	 * 		if multiple = false, the score equals the maximum score of all annotated diseases
	 */
	public void setModeOfAnnotation(boolean multiple){
		this.multiple = multiple;
	}
	
	/**
	 * method to execute the PhenoToGeno algorithm
	 * @return list of ScoredGenes
	 */
	public LinkedList<ScoredGene> runPhenoToGeno(){
		prepareData();
		ToGenoAlgo ptg = new ToGenoAlgo(phenomizer, dga);
		return ptg.runToGene();
	}
	
	/**
	 * method to handle the input data for PhenoToGenoAlgorithm
	 */
	private void prepareData(){
		
		PhenoToGenoDataTransformer dt = new PhenoToGenoDataTransformer();
		// association disease - gene
		dga = dt.getDiseaseGeneAssociation(genes_raw, mapping, multiple);
		// list of scored diseases
		phenomizer = dt.getPhenomizerResult(phenomizer_raw, dga);
	}

}
