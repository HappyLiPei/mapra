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
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;

import nodeutils.ColumnSpecification;
import nodeutils.TableFunctions;
import phenotogeno.node.PhenoToGenoNodeNodeModel;
import togeno.ScoredGene;

public class GeneticNetworkScoreTableProcessor {
	
	/**
	 * method to transform a KNIME table with the results from the PhenoToGeno node into a data structure that is
	 * required for running the network score algorithm,
	 * the node checks for duplicate genes in the results and only uses the first score for each gene id
	 * @param tablePTG table with the results from PhenoToGeno, it should contain 2 columns: one column with gene ids
	 * and one column with scores for each gene
	 * @param logger logger of the GeneticNetworkScoreNode to output warnings to console and log if the data is inconsistent
	 * @return HashMap mapping gene_ids -> scores (probabilities) calculated by PhenoToGeno
	 */
	protected static HashMap<String, Double > getGeneScores (BufferedDataTable tablePTG, NodeLogger logger){
		
		HashMap<String, Double> geneToScore = new HashMap<String, Double>((int) tablePTG.size());
		
		DataTableSpec specPTG = tablePTG.getSpec();
		int indexGeneId = specPTG.findColumnIndex(PhenoToGenoNodeNodeModel.GENE_ID);
		int indexGeneProb = specPTG.findColumnIndex(PhenoToGenoNodeNodeModel.GENE_PROBABILITY);
		
		for(DataRow r: tablePTG){
			String geneId = ((StringCell) r.getCell(indexGeneId)).getStringValue();
			double prob = ((DoubleCell) r.getCell(indexGeneProb)).getDoubleValue();
			
			if(geneToScore.containsKey(geneId)){
				logger.warn("Duplicate entry for gene with id "+geneId+" in the gene scores."
						+ " This node will keep only the first score of each gene.");
			}
			else{
				geneToScore.put(geneId, prob);
			}
		}
		
		return geneToScore;
	}
	
	/**
	 * method to transform a KNIME table with a genetic network (list of undirected edges) that is required for
	 * running the network score algorithm,
	 * the node checks for duplicate edges and for edge pairs of form node1 - node2 vs. node2 - node1, it will output
	 * a warning and the data transformer will take car of those duplicates such that each edge is considered once
	 * in the network score algorithm
	 * @param tableNetwork KNIME table representing the edges of a genetic network
	 * @param weights boolean flag to indicate if edge weights should be extracted from the table
	 * @param logger logger of the GeneitcNetworkScoreNode to output warnings to console and log if the data is inconsistent
	 * @return array of String arrays, each String array represents an edge and has 2 (weights=false) or 3 
	 * 		(weights=true) elements with position 0: id of the first node of the edge, position 1: id of the second 
	 * 		node of the edge, optional position 2: weight of the edge 
	 */
	protected static String [][] getNetworkEdges(BufferedDataTable tableNetwork, boolean weights, NodeLogger logger){
		
		//data structure for recognizing duplicates
		HashSet<String> edgeSet = new HashSet<String>();
		
		String [][] edgeArray = null;
		//decide if read weights from table
		if(weights){
			edgeArray = new String[(int) tableNetwork.size()][3];
		}
		else{
			edgeArray = new String[(int) tableNetwork.size()][2];
		}
		
		DataTableSpec specNetwork = tableNetwork.getDataTableSpec();
		int indexGene1 = specNetwork.findColumnIndex(ColumnSpecification.GENE1);
		int indexGene2 = specNetwork.findColumnIndex(ColumnSpecification.GENE2);
		// indexWeight = -1 if !weights
		int indexWeight = specNetwork.findColumnIndex(ColumnSpecification.EDGEWEIGHT);
		
		int position =0;
		for (DataRow r: tableNetwork){
			
			//extract information from table
			String [] edge = null;
			if(weights){
				edge = new String [3];
				edge[2]= ((IntCell) r.getCell(indexWeight)).getIntValue()+"";
			}
			else{
				edge = new String [2];
			}
			String node1 = ((StringCell) r.getCell(indexGene1)).getStringValue();
			String node2 = ((StringCell) r.getCell(indexGene2)).getStringValue();
			
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
			
			//add edge to array
			edge[0]=node1;
			edge[1]=node2;			
			edgeArray[position]=edge;
			position++;
		}
		return edgeArray;
	}
	
	/**
	 * generates the specification (column names + type) for the output table of the GeneticNetworkScore node
	 * @return DataTableSpec of the output table
	 */
	protected static DataTableSpec generateOutputSpec(){
		
		DataColumnSpec [] specs = new DataColumnSpec [2];
		specs[0] = TableFunctions.makeDataColSpec(PhenoToGenoNodeNodeModel.GENE_ID, StringCell.TYPE);
		specs[1] = TableFunctions.makeDataColSpec(PhenoToGenoNodeNodeModel.GENE_PROBABILITY, DoubleCell.TYPE);
		
		return new DataTableSpec(specs);
	}
	
	/**
	 * generates the actual output table of the GeneticNetworkScore given the results from the NetworkScoreAlgorithm
	 * @param result list of ScoredGenes object produced by the NetworkScoreAlgorithm
	 * @param context ExecutionContext of the GeneticNetworkScore node
	 * @return a KNIME data table representing the information stored in results
	 */
	protected static BufferedDataTable generateOutputTable(LinkedList<ScoredGene> result, ExecutionContext context){
		
		DataTableSpec specOut = generateOutputSpec();
		BufferedDataContainer container = context.createDataContainer(specOut);
		
		int indexId = specOut.findColumnIndex(PhenoToGenoNodeNodeModel.GENE_ID);
		int indexProb = specOut.findColumnIndex(PhenoToGenoNodeNodeModel.GENE_PROBABILITY);
		
		int counter =0;
		for(ScoredGene gene: result){
			counter++;
			RowKey key = new RowKey("Row "+counter);
			DataCell [] cells = new DataCell [specOut.getNumColumns()];
			cells[indexId]=new StringCell(gene.getId());
			cells[indexProb]=new DoubleCell(gene.getScore());
			DataRow row = new DefaultRow(key, cells);
			container.addRowToTable(row);
		}
				
        container.close();
        return container.getTable();
	}

}
