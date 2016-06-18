package scorecombination.node;

import java.util.HashMap;
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

import nodeutils.ColumnSpecification;
import nodeutils.TableFunctions;
import scorecombination.algo.CombineScoresDriver;
import togeno.ScoredGene;

/** class with methods to transform KNIME tables for the ScoreCombineation node*/
public class CombineScoresTableProcessor {
	
	/**
	 * method to read a set of gene scores from a KNIME table, 
	 * the method is called by the {@link CombineScoresNodeModel} node and provides data structures 
	 * for running {@link CombineScoresDriver}
	 * the input table should have 2 columns with gene ids and gene scores
	 * the method is able to handle missing entries in thos columns and it checks for duplicate gene entries
	 * in case of a duplicate, the method keeps the first score of the id
	 * @param scoreTable KNIME table with 2 columns gene id and gene probability that contains gene scores
	 * @param port number of the table -> CombineScores is able to read any number of tables with this method
	 * @param logger logger of the CombineScores node to output warnings to KNIME console and log
	 * @return a mapping gene id -> score representing a set of gene scores
	 */
	protected static HashMap<String, Double> getScoreSet(BufferedDataTable scoreTable, int port, NodeLogger logger){
		
		//data structure to return
		HashMap<String, Double> scores = new HashMap<String, Double>((int) scoreTable.size()*3);
		
		//position of relevant columns
		DataTableSpec spec = scoreTable.getSpec();
		int indexId = spec.findColumnIndex(ColumnSpecification.GENE_ID);
		int indexScore = spec.findColumnIndex(ColumnSpecification.GENE_PROBABILITY);
		
		//iterate over all rows and extract data
		for(DataRow row: scoreTable){
			String id = TableFunctions.getStringValue(row, indexId);
			Double score = TableFunctions.getDoubleValue(row, indexScore);
			
			//check valid id and score
			if(id ==null){
				logger.warn("Error parsing gene id of row "+row.getKey()+" in the table at input port "+port+"."
						+ " This node will ignore it.");
			}
			else if(score == null){
				logger.warn("Error parsing gene score of row "+row.getKey()+" in the table at input port "+port+"."
						+ " This node will ignore it.");
			}
			
			//valid data -> fill hashmap
			else{
				//add value to hashmap
				if(!scores.containsKey(id)){
					scores.put(id, score);
				}
				//duplicate gene id, keep only first score
				else{
					logger.warn("Duplicate entry for gene with id "+id+" in the table at input port "+port+"."
							+ " This node will keep only the first score of each gene.");
				}
			}
		}
		
		return scores;
	}
	
	//TODO: additional enrichment column!
	/**
	 * method to generate the specification for the table returned by CombineScores
	 * (2 columns: gene id, probability)
	 * @return DataTable specification for the table returned by CombineScores
	 */
	protected static DataTableSpec generateOutputSpec(){
		DataColumnSpec [] colSpec = new DataColumnSpec[2];
		colSpec[0]=TableFunctions.makeDataColSpec(
				ColumnSpecification.GENE_ID, ColumnSpecification.GENE_ID_TYPE[0]);
		colSpec[1]=TableFunctions.makeDataColSpec(
				ColumnSpecification.GENE_PROBABILITY, ColumnSpecification.GENE_PROBABILITY_TYPE[0]);
				
		return new DataTableSpec(colSpec);
	}
	
	/**
	 * generates the output table for the {@link CombineScoresNodeModel},
	 * the table contains the results produced by {@link CombineScoresDriver} 
	 * @param result list of {@link ScoredGene}s object calculated by the {@link CombineScoresDriver}
	 * @param context ExecutionContext of the CombineScore node
	 * @return a KNIME data table representing the information stored in results
	 */
	protected static BufferedDataTable generateOutputTable(LinkedList<ScoredGene> result, ExecutionContext context){
		
		//prepare table specification
		DataTableSpec specOut = generateOutputSpec();
		BufferedDataContainer container = context.createDataContainer(specOut);
		
		//find positions to store the information
		int indexId = specOut.findColumnIndex(ColumnSpecification.GENE_ID);
		int indexScore = specOut.findColumnIndex(ColumnSpecification.GENE_PROBABILITY);
		
		//iterate over genes and add them to table
		int counter =1;
		for(ScoredGene gene: result){
			
			//generate cells of the table
			DataCell [] cells = new DataCell[specOut.getNumColumns()];
			cells[indexId] = TableFunctions.generateDataCellFor(specOut, indexId, gene.getId());
			cells[indexScore] = TableFunctions.generateDataCellFor(specOut, indexScore, gene.getScore());
			
			//generate key and add row to container
			RowKey key = new RowKey("Row "+counter);
			DataRow row = new DefaultRow(key, cells);
			container.addRowToTable(row);
			counter++;
		}
		
		//extract table from container and return it
		container.close();
		return container.getTable();
	}

}
