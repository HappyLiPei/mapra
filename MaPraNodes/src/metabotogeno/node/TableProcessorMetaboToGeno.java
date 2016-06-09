package metabotogeno.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;

import metabotogeno.algo.DataTransformerMTG;
import metabotogeno.algo.MetaboToGenoDriver;
import nodeutils.ColumnSpecification;
import nodeutils.TableFunctions;
import togeno.ScoredGene;

public class TableProcessorMetaboToGeno {
	
	/**
	 * method to read a list of gene ids from a KNIME table, the method is able to handle rows with missing gene id and
	 * to detect duplicate gene ids, if a duplicate is found the method outputs a warning, the duplicate will be removed
	 * by {@link DataTransformerMTG}
	 * @param table KNIME table containing gene ids (column gene_id)
	 * @param logger logger of the MetaboToGeno node for writing warning messages to console
	 * @return list of gene ids for MetaboToGeno
	 */
	protected static LinkedList<String> getGeneList(BufferedDataTable table, NodeLogger logger){
		
		//list of gene ids
		LinkedList<String> genes = new LinkedList<String>();
		//hash set for managing duplicates
		HashSet<String> set = new HashSet<String>((int) table.size()*3);
		
		int index = table.getDataTableSpec().findColumnIndex(ColumnSpecification.GENE_ID);
		for (DataRow row:table){
			//extract gene id
			String gene_id = TableFunctions.getStringValue(row, index);
			//check if gene_id is available
			if(gene_id==null){
				logger.warn("Error parsing gene id of row "+row.getKey()+" in gene list. This node will ignore it.");
			}
			else{
				//add to list
				genes.add(gene_id);
				//check for duplicated gene id
				if(!set.add(gene_id)){
					logger.warn("Duplicate entry with gene id "+gene_id+" in gene list. This node will remove it.");
				}
			}
		}
		return genes;
	}
	
	/**
	 * method to read associations between metabolites and genes from a KNIME table,
	 * the method is able to handle rows with missing metabolite and/or gene id, the method checks 
	 * and outputs a warning if a gene of an association is not part of the reference
	 * gene list, {@link DataTransformerMTG} will remove the associations of those genes
	 * @param table KNIME table with metabolite-gene pairs
	 * @param logger logger of MetaboToGeno node for writing warning messages to console
	 * @param referenceGenes list of all genes to consider (produced by getGeneList())
	 * @return mapping metabolite id-> list of string ids (can be empty or contain duplicates) for MetaboToGeno
	 */
	protected static HashMap<String, LinkedList<String>> getAssociationData(BufferedDataTable table, NodeLogger logger,
			LinkedList<String> referenceGenes){
		
		//hashset of all genes
		HashSet<String> reference = new HashSet<String>(referenceGenes.size());
		for(String id: referenceGenes){
			reference.add(id);
		}
		//mapping metabolite id -> gene id
		HashMap<String, LinkedList<String>> associations = new HashMap<String, LinkedList<String>>((int) table.size()*3);
		
		//get column indices for metabolite and gene id
		DataTableSpec s = table.getDataTableSpec();
		int indexMetabo =s.findColumnIndex(ColumnSpecification.METABOLITE_ID);
		int indexGene = s.findColumnIndex(ColumnSpecification.GENE_ID);
		
		for(DataRow row: table){
			//get metabolite id and gene id
			String metaboliteId = TableFunctions.getStringValue(row, indexMetabo);
			String geneId = TableFunctions.getStringValue(row, indexGene);
			//check if metabolite id is missing
			if(metaboliteId==null){
				logger.warn("Error parsing metabolite id of row "+row.getKey()+" in association data. This node will ignore it.");
			}
			
			//metabolite id not missing
			else{
				//add metabolite id to map keys
				if(!associations.containsKey(metaboliteId)){
					associations.put(metaboliteId, new LinkedList<String>());
				}
				//add gene id to to gene list of current metabolite
				if(geneId!=null){
					if(reference.contains(geneId)){
						associations.get(metaboliteId).add(geneId);
					}
					else{
						logger.warn("Gene "+geneId+" in association data is not part of the reference gene set."
								+ " This node will ignore all associations for this gene.");
					}
				}
			}
		}
		return associations;
	}
	
	/**
	 * method to read scored metabolites from a KNIME table (output of ScoreMetabolites), the method is able to handle
	 * missing scores and/or metabolite ids, the method checks for duplicate metabolite ids in the table with the scores
	 * and for metabolite ids that are not part of the output data, the method outputs a warning and {@link DataTransformerMTG}
	 * will remove the invalid metabolite ids 
	 * @param table KNIME table containing metabolite ids and metabolite scores (represented by probabilities/pvalues
	 * 		 in column significance)
	 * @param logger logger of MetaboToGeno node for writing warning messages to console
	 * @param associations mapping of metabolite id -> list of gene ids produced by getAssociationData()
	 * @return list of String arrays with 2 elements, position 0: metabolite id, position 1: metabolite score 
	 */
	protected static LinkedList<String[]> getScoreMetabolitesResult(BufferedDataTable table, NodeLogger logger,
			HashMap<String, LinkedList<String>> associations){
		
		//set of all metabolites from associations
		Set<String> metaboAsso = associations.keySet();
		//set of all metabolites with scores
		HashSet<String> metaboScore = new HashSet<String>((int) table.size()*3);
		// list of arrays with pos 0: metabolite id, pos 1: metabolite pvalue
		LinkedList<String[]> scores = new LinkedList<String[]>();
		
		//get column indices for metabolite id and pvalue
		DataTableSpec s = table.getDataTableSpec();
		int indexMetabo = s.findColumnIndex(ColumnSpecification.METABOLITE_ID);
		int indexPval = s.findColumnIndex(ColumnSpecification.METABOLITE_SIGNIFICANCE);
		
		for(DataRow r: table){
			String metabolite = TableFunctions.getStringValue(r, indexMetabo);
			String pval = TableFunctions.getStringValue(r, indexPval);
			//check extracted data
			if(metabolite==null){
				logger.warn("Error parsing metabolite id of row "+r.getKey()+" in metabolite scores. "
						+ "This node will ignore it.");
			}
			else if(pval==null){
				logger.warn("Error parsing probability (significanc)e of row "+r.getKey()+" in metabolite scores. "
						+ "This node will ignore it.");
			}
			//add to data structure
			else{
				if(!metaboAsso.contains(metabolite)){
					logger.warn("Scored Metabolite "+metabolite+" is not part of the association data. "
							+ "This node will ignore it.");
				}
				else if(metaboScore.contains(metabolite)){
					logger.warn("Duplicate entry for scored metabolite "+metabolite+" .This node will ignore it.");
				}
				else{
					metaboScore.add(metabolite);
					scores.add(new String[]{metabolite, pval});
				}
			}
		}
		return scores;
	}
	
	/**
	 * method to create the {@link DataTableSpec} for the output table of the MetaboToGeno node
	 * @return specification for the output table of MetaboToGeno with 3 columns:
	 * 	 metabolite_id (String), gene_probability (Double), contribution (String)
	 */
	protected static DataTableSpec generateOutputSpec(){
		DataColumnSpec[] colSpecs = new DataColumnSpec[3];
		colSpecs[0]=TableFunctions.makeDataColSpec(
				ColumnSpecification.GENE_ID, ColumnSpecification.GENE_ID_TYPE[0]);
		colSpecs[1]=TableFunctions.makeDataColSpec(
				ColumnSpecification.GENE_PROBABILITY, ColumnSpecification.GENE_PROBABILITY_TYPE[0]);
		colSpecs[2]=TableFunctions.makeDataColSpec(
				ColumnSpecification.CONTRIBUTION, ColumnSpecification.CONTRIBUTION_TYPE[0]);
		return new DataTableSpec(colSpecs);
	}
	
	/**
	 * method for transforming the output of {@link MetaboToGenoDriver} (list of {@link ScoredGene}s) into a KNIME table
	 * the output table contains 3 columns for gene id, gene score and major contributors, the method is able to add
	 * metabolite names to the list of major contributors is they are specified in the input table with metabolite scores
	 * @param exec execution context of MetaboToGeno
	 * @param result list of scored genes returned by the MetaboToGeno algorithm
	 * @param tableNames KNIME table with containing a columns with metabolite ids and metabolite names 
	 * @return a KNIME table containing genes, their scores from MetaboToGeno and the metabolites with highest contribution
	 * 		to the score
	 */
	protected static BufferedDataTable generateMTGOutputTable(ExecutionContext exec, LinkedList<ScoredGene> result,
			BufferedDataTable tableNames){
		
		//mapping metabolite id -> name
		HashMap<String, String> idToName = new HashMap<String, String>((int) tableNames.size()*3);
		
		DataTableSpec specIn = tableNames.getDataTableSpec();
		int indexName = specIn.findColumnIndex(ColumnSpecification.METABOLITE_NAME);
		int indexId = specIn.findColumnIndex(ColumnSpecification.METABOLITE_ID);
		//read names from metabolite scores and fill hashmap
		if(indexName !=-1){
			for(DataRow r : tableNames){
				String name = TableFunctions.getStringValue(r, indexName);
				String id = TableFunctions.getStringValue(r, indexId);
				if(!idToName.containsKey(id)){
					idToName.put(id, name);
				}
			}
		}
		
		//process table specification
		DataTableSpec specOut = generateOutputSpec();
		int indexGeneId = specOut.findColumnIndex(ColumnSpecification.GENE_ID);
		int indexGeneScore = specOut.findColumnIndex(ColumnSpecification.GENE_PROBABILITY);
		int indexContributor = specOut.findColumnIndex(ColumnSpecification.CONTRIBUTION);
		BufferedDataContainer container = exec.createDataContainer(specOut);
		
		//generate table data
		int counter =1;
		for(ScoredGene gene: result){
			
			//parse contributors
			String contr = gene.getImportantContributors();
			String [] split = contr.split(",");
			for(int i=0; i<split.length; i++){
				//get metabolite id
				String id = split[i];
				if(id.endsWith("...")){
					id = id.substring(0, id.length()-3);
				}
				//if name for id available, exchange id by name
				if(idToName.containsKey(id)){
					String name = idToName.get(id);
					if(split[i].endsWith("...")){
						split[i]=name+" ("+id+")...";
					}
					else{
						split[i]=name+" ("+id+")";
					}
				}
			}
			contr = String.join(", ", split);
			
			 //generate content of one row
			 DataCell [] cells = new DataCell[specOut.getNumColumns()];
			 cells[indexGeneId]=
					 TableFunctions.generateDataCellFor(specOut, indexGeneId, gene.getId());
			 cells[indexContributor]=
					 TableFunctions.generateDataCellFor(specOut, indexContributor, contr);
			 cells[indexGeneScore]=
					 TableFunctions.generateDataCellFor(specOut, indexGeneScore, gene.getScore());
			 //generate row object
			 RowKey key = new RowKey("Row " + counter);
			 DataRow row = new DefaultRow(key, cells);
			 container.addRowToTable(row);
			 counter++;
		}
		
		//return table
		container.close();
		return container.getTable();
	}

}
