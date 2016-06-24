package phenomizer.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.MissingCell;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;

import nodeutils.ColumnSpecification;
import nodeutils.TableFunctions;
import phenomizer.algorithm.FrequencyConverter;
import phenomizer.algorithm.PhenomizerDriver;

/** class for converting the KNIME tables of the Phenomizer node */
public class TableProcessorPhenomizer {
	
	/**
	 * converts a KNIME table with query symptoms into a data structure for {@link PhenomizerDriver},
	 * the method recognizes and removes symptom ids that are not part of the list of all symptoms
	 * @param table_query table with the query symptoms received at the input port of the Phenomizer node
	 * @param table_symptoms table with the symptom dictionary received at the input port of the Phenomizer node
	 * @param logger logger of the Phenomizer node to make output to the KNIME console
	 * @return filtered list of symptom ids representing the query symptoms for Phenomizer
	 */
	protected static LinkedList<Integer> generateQuery(BufferedDataTable table_query, BufferedDataTable table_symptoms, NodeLogger logger){
		
		//unfiltered query
		LinkedList<Integer> query_complete = generateSymptomList(table_query, logger, true);
		
		//read in set of all symptoms
		HashSet<Integer> symptoms_in_dict = new HashSet<Integer>(((int)table_symptoms.size())*3);
		int index = table_symptoms.getDataTableSpec().findColumnIndex(ColumnSpecification.SYMPTOM_ID);
		for(DataRow r: table_symptoms){
			Integer symptomId = TableFunctions.getIntegerValue(r, index);
			if(symptomId!=null){
				symptoms_in_dict.add(symptomId);
			}
		}
		
		//filter query
		LinkedList<Integer> res = new LinkedList<Integer>();
		for(Integer i: query_complete){
			if(symptoms_in_dict.contains(i)){
				res.add(i);
			}
			else{
				logger.warn("Symptom_id " +i+ " of the query is not part of the list of all symptoms:"
						+ " Phenomizer will ignore this symptom.");
			}
		}
		return res;
	}
	
	/**
	 * converts a KNIME table with symptoms into a data structure for {@link PhenomizerDriver},
	 * the method is able to skip rows with missing symptom ids
	 * @param table table with symptom dictionary (list of all symptoms) received at the input port of the Phenomizer node
	 * @param logger logger of the Phenomizer node to make output to the KNIME console
	 * @return list of symptom ids of the symptom dictionary
	 */
	protected static LinkedList<Integer> generateSymptomList(BufferedDataTable table, NodeLogger logger, boolean query){
		
		//data structures for reading symptom ids
		int index = table.getDataTableSpec().findColumnIndex(ColumnSpecification.SYMPTOM_ID);
		LinkedList<Integer> result = new LinkedList<Integer>();
		
		//extract symptom id of every row
		for (DataRow r: table){
			Integer symptomId = TableFunctions.getIntegerValue(r, index);
			if(symptomId==null){
				if(query){
					logger.warn("Error parsing symptom id of row "+r.getKey()+" in the table containing the query symptoms."
							+ " This node will ignore it.");
				}
				else{
					logger.warn("Error parsing symptom id of row "+r.getKey()+" in the table containing the list of all symptoms."
						+ " This node will ignore it.");
				}
			}
			else{
				result.add(symptomId);
			}
		}
		return result;
	}
	
	/**
	 * converts a KNIME table with symptom-disease annotation into a data structure for {@link PhenomizerDriver},
	 * @param table table with the symptom-disease annotation received at input port of the Phenomizer node
	 * @param weight flag that specifies if algorithm uses weights
	 * @return HashMap which maps a disease id to an int array containing one annotated symptom id (pos 0) 
	 * 		and the annotated frequency (pos 1)
	 */
	protected static HashMap<Integer, LinkedList<Integer []>> generateKSZ(BufferedDataTable table, boolean weight, NodeLogger logger){
		
		HashMap<Integer, LinkedList<Integer[]>> res = new HashMap<Integer, LinkedList<Integer[]>>(((int)table.size())*3);
		int index_disease = table.getDataTableSpec().findColumnIndex(ColumnSpecification.DISEASE_ID);
		int index_symptom = table.getDataTableSpec().findColumnIndex(ColumnSpecification.SYMPTOM_ID);
		int index_frequency = table.getDataTableSpec().findColumnIndex(ColumnSpecification.FREQUENCY);
		
		FrequencyConverter f = new FrequencyConverter();
		for(DataRow r : table){
			//read disease id and symptom id, check for missing values
			Integer disease_id= TableFunctions.getIntegerValue(r, index_disease);
			Integer symptom_id= TableFunctions.getIntegerValue(r, index_symptom);
			if(disease_id==null){
				logger.warn("Error parsing disease id of row "+r.getKey()+" in the table containing the symptoms-disease associations."
						+ " This node will ignore it.");
				continue;
			}
			if(symptom_id==null){
				logger.warn("Error parsing symptom id of row "+r.getKey()+" in the table containing the symptoms-disease associations."
						+ " This node will ignore it.");
				continue;
			}
			
			//get correct weighting factor
			Integer [] symptom_and_freq = new Integer [2];
			symptom_and_freq[0]=symptom_id;
			//do not use weight
			if(index_frequency==-1||!weight){
				symptom_and_freq[1]=FrequencyConverter.NO_WEIGHT;
			}
			//use weight -> parse frequency annotation
			else{
				String frequency = TableFunctions.getStringValue(r, index_frequency);
				//missing frequency annotation
				if(frequency==null){
					//symptom is weighted as frequent
					symptom_and_freq[1]=f.convertFrequency("");
				}
				else{
					symptom_and_freq[1]= f.convertFrequency(frequency);
				}
			}
			
			//add disease, symptom and weight to result
			if(res.containsKey(disease_id)){
				res.get(disease_id).add(symptom_and_freq);
			}
			else{
				LinkedList<Integer[]> tmp = new LinkedList<Integer[]>();
				tmp.add(symptom_and_freq);
				res.put(disease_id, tmp);
			}
		}
		
		return res;
	}
	
	/**
	 * converts a KNIME table with ontology edges into a data structure for {@link PhenomizerDriver}
	 * @param table KNIME table with ontology edges received at the input port of the Phenomizer node
	 * @param logger logger of the Phenomizer node to make output to the KNIME console
	 * @return int [][] of symptom ids corresponding to the edges of the ontology: (child, parent) pairs
	 */
	
	protected static int [][] generateEdges (BufferedDataTable table, NodeLogger logger){
		
		//data structures for reading the edge of the ontology
		LinkedList<int[]> resList = new LinkedList<int[]>();
		int index_child = table.getDataTableSpec().findColumnIndex(ColumnSpecification.CHILD_ID);
		int index_parent = table.getDataTableSpec().findColumnIndex(ColumnSpecification.PARENT_ID);
		
		for(DataRow r: table){
			//extract symptom ids of parent and child and check if they are missing
			Integer childId = TableFunctions.getIntegerValue(r, index_child);
			Integer parentId = TableFunctions.getIntegerValue(r, index_parent);
			if(childId!=null && parentId!=null){
				resList.add(new int[]{childId, parentId});
			}
			if(childId==null){
				logger.warn("Error parsing child id of row "+r.getKey()+" in the table containing the ontology."
						+ " This node will ignore it.");
			}
			if(parentId==null){
				logger.warn("Error parsing parent id of row "+r.getKey()+" in the table containing the ontology."
						+ " This node will ignore it.");
			}
		}
		
		//convert list into array
		return resList.toArray(new int[0][]);
	}
	
	/**
	 * generates specifications for output table of Phenomizer
	 * @param pvalue pvalue = true -> displays p values and significance in the out port table
	 * @return specification format of output table:
	 * 		column 0 with disease_id (int), column 1 with disease_name (string),
	 * 		column 2 with similarity score (double),
	 * 		if pvalue=true -> 2 additional columns: column 3 with p-value (double) and column 4 with significance (string)
	 */
    protected static DataTableSpec generateOutputSpec(boolean pvalue){
    	
    	//initialize specification according to p value option
    	DataColumnSpec [] colspecs = new DataColumnSpec[3];
    	if(pvalue){
    		colspecs= new DataColumnSpec[5];
    	}
    	
    	//add column specifications
    	colspecs[0] = TableFunctions.makeDataColSpec(ColumnSpecification.DISEASE_ID,
    			ColumnSpecification.DISEASE_ID_TYPE[0]);
    	colspecs[1] = TableFunctions.makeDataColSpec(ColumnSpecification.DISEASE_NAME, 
    			ColumnSpecification.DISEASE_NAME_TYPE[0]);
    	colspecs[2] = TableFunctions.makeDataColSpec(ColumnSpecification.SCORE, 
    			ColumnSpecification.SCORE_TYPE[0]);
    	//columns for p values (optional)
    	if(pvalue){
        	colspecs[3] = TableFunctions.makeDataColSpec(ColumnSpecification.P_VALUE, 
        			ColumnSpecification.P_VALUE_TYPE[0]);
        	colspecs[4] = TableFunctions.makeDataColSpec(ColumnSpecification.SIGNIFICANCE, 
        			ColumnSpecification.SIGNIFICANCE_TYPE[0]);
    	}
    	
    	return new DataTableSpec(colspecs);
    }
	
	/**
	 * writes the output of the {@link PhenomizerDriver} to a KNIME table and adds disease names extracted
	 * from the symptom-disease table
	 * @param output result of the Phenomizer algorithm
	 * @param exec execution context of the Phenomizer node
	 * @param ksz symptom-disease table from the input port of the Phenomizer node
	 * @return a KNIME table that is passed to the output port of the Phenomizer node
	 */
	protected static BufferedDataTable generateOutput(LinkedList<String[]> output, ExecutionContext exec, BufferedDataTable ksz){
		
		//read disease names from ksz table and store them in a map id -> name
		HashMap<Integer,String> IdToName = new HashMap<Integer,String>(((int)ksz.size())*3);
		int pos_disease_name = ksz.getDataTableSpec().findColumnIndex(ColumnSpecification.DISEASE_NAME);
		int pos_disease_id = ksz.getDataTableSpec().findColumnIndex(ColumnSpecification.DISEASE_ID);
		for(DataRow r: ksz){
			Integer id=TableFunctions.getIntegerValue(r, pos_disease_id);
			String name=TableFunctions.getStringValue(r, pos_disease_name);
			if(id!=null && name!=null && !IdToName.containsKey(id)){
				IdToName.put(id, name);
			}
		}
		
		//get and read specifications of the table to return
		DataTableSpec spec = new DataTableSpec();
		//lenght = 2 -> no pvalue, length = 3 -> pvalue
		int out_len = output.peek().length;
		if(out_len==2){
			spec = generateOutputSpec(false);
		}
		else{
			spec = generateOutputSpec(true);
		}
		int index_disease_id = spec.findColumnIndex(ColumnSpecification.DISEASE_ID);
		int index_score = spec.findColumnIndex(ColumnSpecification.SCORE);
		int index_disease_name = spec.findColumnIndex(ColumnSpecification.DISEASE_NAME);
		int index_p_value = spec.findColumnIndex(ColumnSpecification.P_VALUE);
		int index_significance = spec.findColumnIndex(ColumnSpecification.SIGNIFICANCE);
		
		//generate table content
		BufferedDataContainer c = exec.createDataContainer(spec);
		int counter = 1;
		for(String [] rowdata : output){
			//get data for current row
			//disease id
			DataCell [] cells = new DataCell [spec.getNumColumns()];
			cells[index_disease_id] = TableFunctions.generateDataCellFor(spec, index_disease_id, rowdata[0]);
			//disease name
			int id = Integer.parseInt(rowdata[0]);
			if(IdToName.containsKey(id)){
				cells[index_disease_name] = TableFunctions.generateDataCellFor(spec, index_disease_name,IdToName.get(id));
			}
			else{
				cells[index_disease_name] = new MissingCell(null);
			}
			//score
			cells[index_score] = TableFunctions.generateDataCellFor(spec, index_score, rowdata[1]);
			//pvalue
			if(out_len==3){
				double pval = Double.parseDouble(rowdata[2]);
				cells[index_p_value] = TableFunctions.generateDataCellFor(spec, index_p_value, pval);
				cells[index_significance] = TableFunctions.generateDataCellFor(spec, index_significance, getSignificance(pval));
			}
			//add row to table
			RowKey key = new RowKey("Row "+counter);
	    	DataRow row = new DefaultRow(key, cells);
	    	c.addRowToTable(row);
	    	counter++;
		}
		c.close();
		
		//return table
		return c.getTable();
	}
	
	/** translates the p value into star representation indicating the significance of the p value
	 * @param pval p value
	 * @return "" if pval >= 0.05,
	 * 			* if 0.01 <= pval < 0.05,
	 * 			** if 0.001 <= pval < 0.01,
	 * 			*** if 0.0001 <= pval < 0.001
	 */
	private static String getSignificance(Double pval){
		if(pval >= 0.05){
			return "ns";
		}
		if(pval >= 0.01){
			return "*";
		}
		if(pval >= 0.001){
			return "**";
		}
		if(pval<0.001){
			return "***";
		}
		return "";
	}

}
	



