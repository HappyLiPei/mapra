package geneticnetwork.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

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

import geneticnetwork.algorithm.DataTransformerGeneticNetwork;
import geneticnetwork.algorithm.NetworkScoreDriver;
import nodeutils.ColumnSpecification;
import nodeutils.TableFunctions;
import togeno.ScoredGene;

public class GeneticNetworkScoreTableProcessor {
	
	/**
	 * method to transform a KNIME table with the results from the PhenoToGeno node or MetaboToGeno node
	 * into a data structure that is required for running the {@link NetworkScoreDriver}
	 * the node checks for duplicate genes in the results and only uses the first score for each gene id
	 * furthermore the method is able to recognize rows with missing values and skips that rows
	 * @param tablePTG table with the results from PhenoToGeno or MetaboToGebo, it should contain 2 columns:
	 * 		one column with gene ids and one column with scores for each gene
	 * @param logger logger of the GeneticNetworkScoreNode to output warnings to console and log if the data is inconsistent
	 * @return HashMap mapping gene_ids -> scores (probabilities) calculated by PhenoToGeno or MetaboToGeno
	 */
	protected static HashMap<String, Double > getGeneScores (BufferedDataTable tablePTG, NodeLogger logger){
		
		//data structure to return
		HashMap<String, Double> geneToScore = new HashMap<String, Double>((int) tablePTG.size());
		
		//extract column positions
		DataTableSpec specPTG = tablePTG.getSpec();
		int indexGeneId = specPTG.findColumnIndex(ColumnSpecification.GENE_ID);
		int indexGeneProb = specPTG.findColumnIndex(ColumnSpecification.GENE_PROBABILITY);
		
		for(DataRow r: tablePTG){
			
			//extraxt gene id and score
			String geneId = TableFunctions.getStringValue(r, indexGeneId);
			Double prob = TableFunctions.getDoubleValue(r, indexGeneProb);
			
			//check for missing values
			if(geneId ==null){
				logger.warn("Error parsing gene id of row "+r.getKey()+" in the table containing the gene scores."
						+ " This node will ignore it.");
			}
			else if(prob == null){
				logger.warn("Error parsing gene probability of row "+r.getKey()+" in the table containing the gene scores."
						+ " This node will ignore it.");
			}
			
			//add gene and score to hashmap if they are not already part of the map
			else{
				if(geneToScore.containsKey(geneId)){
					logger.warn("Duplicate entry for gene with id "+geneId+" in the gene scores."
							+ " This node will keep only the first score of each gene.");
				}
				else{
					geneToScore.put(geneId, prob);
				}
			}
		}
		
		return geneToScore;
	}
	
	/**
	 * method to transform a KNIME table with a genetic network (list of undirected edges) that is required for
	 * running the {@link NetworkScoreDriver},
	 * the node checks for duplicate edges and for edge pairs of form node1 - node2 vs. node2 - node1, it will output
	 * a warning and the {@link DataTransformerGeneticNetwork} will take car of those duplicates such that each edge
	 * is considered exactly once in the network score algorithm
	 * furthermore the method is able to handle rows with missing values, in case of a missing value it skips the row
	 * and outputs a warning
	 * @param tableNetwork KNIME table representing the edges of a genetic network
	 * @param weights boolean flag to indicate if the edge weights should be extracted from the table
	 * @param logger logger of the GeneticNetworkScoreNode to output warnings to console and log if the data is inconsistent
	 * @return array of String arrays, each String array represents an edge and has 2 (weights=false) or 3 
	 * 		(weights=true) elements with position 0: id of the first node of the edge, position 1: id of the second 
	 * 		node of the edge, optional position 2: weight of the edge 
	 */
	protected static String [][] getNetworkEdges(BufferedDataTable tableNetwork, boolean weights, NodeLogger logger){
		
		//data structure for recognizing duplicates
		HashSet<String> edgeSet = new HashSet<String>();
		
		LinkedList<String[]> edgeArray = new LinkedList<String[]>();
		
		DataTableSpec specNetwork = tableNetwork.getDataTableSpec();
		int indexGene1 = specNetwork.findColumnIndex(ColumnSpecification.GENE1);
		int indexGene2 = specNetwork.findColumnIndex(ColumnSpecification.GENE2);
		// indexWeight = -1 if !weights
		int indexWeight = specNetwork.findColumnIndex(ColumnSpecification.EDGEWEIGHT);
		
		for (DataRow r: tableNetwork){
			
			//extract edge weights (if weight==true) and check for valid content 
			String [] edge = null;
			if(weights){
				edge = new String [3];
				edge[2]= TableFunctions.getStringValue(r, indexWeight);
				if(edge[2]==null){
					logger.warn("Error parsing edge weight of row "+r.getKey()+" of the edge list."
							+ " This node will ignore it.");
					continue;
				}
			}
			
			//extract node of the edge and check for valid cells
			else{
				edge = new String [2];
			}
			String node1 = TableFunctions.getStringValue(r, indexGene1);
			if(node1==null){
				logger.warn("Error parsing node1 of row "+r.getKey()+" of the edge list."
						+ " This node will ignore it.");
				continue;
			}
			String node2 = TableFunctions.getStringValue(r, indexGene2);
			if(node2==null){
				logger.warn("Error parsing node2 of row "+r.getKey()+" of the edge list."
						+ " This node will ignore it.");
				continue;
			}
			
			//check for duplicate edge
			String key="";
			if(node1.compareTo(node2)<0){
				key=node1+"-"+node2;
			}
			else{
				key=node2+"-"+node1;
			}
			if(!edgeSet.add(key)){
				logger.warn("Duplicate undirected edge of nodes "+node1+" and "+node2+". "+
						"This node will ignore all duplicate edges.");
			}
			
			//add edge to list
			edge[0]=node1;
			edge[1]=node2;			
			edgeArray.add(edge);
		}
		
		//transform list into array and return it
		return edgeArray.toArray(new String[edgeArray.size()][]);
	}
	
	//TODO: adapt node manual
	/**
	 * generates the specification (column names + type) for the output table of the GeneticNetworkScore node
	 * @return DataTableSpec of the output table
	 */
	protected static DataTableSpec generateOutputSpec(){
		
		DataColumnSpec [] specs = new DataColumnSpec [3];
		specs[0] = TableFunctions.makeDataColSpec(
				ColumnSpecification.GENE_ID, ColumnSpecification.GENE_ID_TYPE[0]);
		specs[1] = TableFunctions.makeDataColSpec(
				ColumnSpecification.GENE_PROBABILITY, ColumnSpecification.GENE_PROBABILITY_TYPE[0]);
		specs[2] = TableFunctions.makeDataColSpec(
				ColumnSpecification.GENE_ENRICHMENT, ColumnSpecification.GENE_ENRICHMENT_TYPE[0]);
		
		return new DataTableSpec(specs);
	}
	
	/**
	 * generates the actual output table of the GeneticNetworkScore node given the results from the NetworkScoreAlgorithm
	 * @param result list of {@link ScoredGene}s object produced by the {@link NetworkScoreDriver}
	 * @param context ExecutionContext of the GeneticNetworkScore node
	 * @return a KNIME data table representing the information stored in results
	 */
	protected static BufferedDataTable generateOutputTable(LinkedList<ScoredGene> result, ExecutionContext context){
		
		//prepare table specification
		DataTableSpec specOut = generateOutputSpec();
		BufferedDataContainer container = context.createDataContainer(specOut);
		
		//get column positions
		int indexId = specOut.findColumnIndex(ColumnSpecification.GENE_ID);
		int indexProb = specOut.findColumnIndex(ColumnSpecification.GENE_PROBABILITY);
		int indexEnrich = specOut.findColumnIndex(ColumnSpecification.GENE_ENRICHMENT);
		
		//iterate over scoredGenes and generate a row for each gene
		int counter =1;
		for(ScoredGene gene: result){
			//generate data for row
			DataCell [] cells = new DataCell [specOut.getNumColumns()];
			cells[indexId]=TableFunctions.generateDataCellFor(specOut, indexId, gene.getId());
			cells[indexProb]=TableFunctions.generateDataCellFor(specOut, indexProb, gene.getScore());
			cells[indexEnrich]=TableFunctions.generateDataCellFor(specOut, indexEnrich, 
					gene.getEnrichmentScore(result.size()));
			//add row to table
			RowKey key = new RowKey("Row "+counter);
			DataRow row = new DefaultRow(key, cells);
			container.addRowToTable(row);
			counter++;
		}
		
		//generate and return final table
        container.close();
        return container.getTable();
	}

}
