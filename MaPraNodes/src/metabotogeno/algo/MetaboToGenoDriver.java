package metabotogeno.algo;

import java.util.HashMap;
import java.util.LinkedList;

import togeno.GeneAssociation;
import togeno.ScoredDiseaseOrMetabolite;
import togeno.ScoredGene;
import togeno.ToGenoAlgo;

/** driver for running MetaboToGeno (configures toGenoAlgo to run on metabolites) */
public class MetaboToGenoDriver {
	
	/** list of all genes to score */
	LinkedList<String> genes_raw;
	/** mapping metabolite id -> list of gene ids, representing associations between metabolites and genes */
	HashMap<String, LinkedList<String>> associations_raw;
	/** result of ScoreMetabolitesAlgo */
	LinkedList<String[]> res_metaboliteScore_raw;
	
	/** object representing metabolite-gene associations*/
	GeneAssociation mga;
	/** list of scored metabolite objects */
	LinkedList<ScoredDiseaseOrMetabolite>  metaboliteScores;
	
	//TODO: different rounding?!
	/**
	 * creates a driver for running MetaboToGeno
	 * @param genes_raw list of all gene ids to consider during the scoring
	 * @param associations_raw mapping metabolite id (e.g. Metabolon id) to list of gene ids (e.g. Ensemble id)
	 * @param res_metaboliteScore_raw list of String arrays with 2 elements, 1st element (pos 0): metabolite id,
	 * 2nd element (pos 1): p value of the metabolite as String 
	 */
	public MetaboToGenoDriver(LinkedList<String> genes_raw, HashMap<String, LinkedList<String>> associations_raw,
			LinkedList<String[]> res_metaboliteScore_raw){
		
		this.genes_raw = genes_raw;
		this.associations_raw = associations_raw;
		this.res_metaboliteScore_raw = res_metaboliteScore_raw;
	}
	
	/**
	 * method that executes MetaboToGeno as a {@link ToGenoAlgo}
	 * @return a list of {@link ScoredGene} objects sorted according to their score (in ascending order)
	 */
	public LinkedList<ScoredGene> runMetaboToGeno(){
		prepareData();
		ToGenoAlgo algo = new ToGenoAlgo(metaboliteScores, mga);
		return algo.runToGene();
	}
	
	
	/**
	 * method to prepare the data structures required for running a {@link ToGenoAlgo}
	 */
	private void prepareData(){
		DataTransformerMTG dt = new DataTransformerMTG();
		mga = dt.getMetaboliteGeneAssociations(genes_raw, associations_raw);
		metaboliteScores = dt.getMetaboliteScoreResult(res_metaboliteScore_raw, mga);
	}

}
