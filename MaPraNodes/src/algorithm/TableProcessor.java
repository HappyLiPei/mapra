package algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;

import phenomizer.PhenomizerNodeModel;

public class TableProcessor {
	
	/**
	 * converts BufferedDataTable with query symptoms to phenomizer data structure
	 * @param table: table with query symptoms received at inport of PhenomizerNode
	 * @return LinkedList of symptom ids of the query
	 */
	
	private static LinkedList<Integer> generateQuery(BufferedDataTable table){
		return generateSymptomList(table);
	}
	
	/**
	 * converts BufferedDataTable with query symptoms to phenomizer data structure
	 * and removes ids that are not part of the ontology
	 * @param table_query: table with query symptoms received at inport of PhenomizerNode
	 * @param table_symptoms: table with symptom dictionary received at inport of PhenomizerNode
	 * @param l: node logger to make output to KNIME konsole
	 * @return filtered LinkedList of symptom ids of the query
	 */
	
	public static LinkedList<Integer> generateQuery(BufferedDataTable table_query, BufferedDataTable table_symptoms, NodeLogger l){
		LinkedList<Integer> query_complete = generateQuery(table_query);
		
		HashSet<Integer> symptoms_in_dict = new HashSet<Integer>(table_symptoms.getRowCount()*3);
		int index = table_symptoms.getDataTableSpec().findColumnIndex(PhenomizerNodeModel.SYMPTOM_ID);
		boolean is_int =false;
		if(table_symptoms.getDataTableSpec().getColumnSpec(index).getType()==IntCell.TYPE){
			is_int = true;
		}
		for(DataRow r: table_symptoms){
			if(is_int){
				symptoms_in_dict.add(((IntCell) r.getCell(index)).getIntValue());
			}
			else{
				symptoms_in_dict.add((int) ((LongCell) r.getCell(index)).getLongValue());
			}
		}
		
		LinkedList<Integer> res = new LinkedList<Integer>();
		for(Integer i: query_complete){
			if(symptoms_in_dict.contains(i)){
				res.add(i);
			}
			else{
				l.warn("Symptom_id " +i+ " ist not part of the ontology: Phenomizer will ignore this symptom.");
			}
		}
		return res;
	}
	
	/**
	 * converts BufferedDataTable with symptom dictionary to phenomizer data structure
	 * @param table: table with symptom dictionary received at inport of PhenomizerNode
	 * @return LinkedList of symptom ids of the symptom dictionary
	 */
	
	public static LinkedList<Integer> generateSymptomList(BufferedDataTable table){
		int index = table.getDataTableSpec().findColumnIndex(PhenomizerNodeModel.SYMPTOM_ID);
		boolean is_int =false;
		if(table.getDataTableSpec().getColumnSpec(index).getType()==IntCell.TYPE){
			is_int = true;
		}
		LinkedList<Integer> result = new LinkedList<Integer>();
		for (DataRow r: table){
			if(is_int){
				result.add(((IntCell) r.getCell(index)).getIntValue());
			}
			else{
				result.add((int) ((LongCell) r.getCell(index)).getLongValue());
			}
		}
		return result;
	}
	
	/**
	 * converts BufferedDataTable with disease annotation to phenomizer data structure
	 * @param table: table with disease annotation received at inport of PhenomizerNode
	 * @param weight: specifies if algorithm should use weights
	 * @return HashMap which maps a disease id to the annotated symptom ids (pos 0) and to the annotated frequencies (pos 1)
	 */
	public static HashMap<Integer, LinkedList<Integer []>> generateKSZ(BufferedDataTable table, boolean weight){
		
		HashMap<Integer, LinkedList<Integer[]>> res = new HashMap<Integer, LinkedList<Integer[]>>(table.getRowCount()*3);
		int index_disease = table.getDataTableSpec().findColumnIndex(PhenomizerNodeModel.DISEASE_ID);
		int index_symptom = table.getDataTableSpec().findColumnIndex(PhenomizerNodeModel.SYMPTOM_ID);
		int index_frequency = table.getDataTableSpec().findColumnIndex(PhenomizerNodeModel.FREQUENCY);
		
		boolean disease_is_int=false;
		if(table.getDataTableSpec().getColumnSpec(index_disease).getType()==IntCell.TYPE){
			disease_is_int = true;
		}
		boolean symptom_is_int=false;
		if(table.getDataTableSpec().getColumnSpec(index_symptom).getType()==IntCell.TYPE){
			symptom_is_int = true;
		}
		
		for(DataRow r : table){
			int disease_id=-1;
			int symptom_id=-1;
			if(disease_is_int){
				disease_id = ((IntCell) r.getCell(index_disease)).getIntValue();
			}
			else{
				disease_id = (int) ((LongCell) r.getCell(index_disease)).getLongValue();
			}
			if(symptom_is_int){
				symptom_id = ((IntCell) r.getCell(index_symptom)).getIntValue();
			}
			else{
				symptom_id = (int) ((LongCell) r.getCell(index_symptom)).getLongValue();
			}
			
			Integer [] symptom_and_freq = new Integer [2];
			symptom_and_freq[0]=symptom_id;
			if(index_frequency==-1||!weight){
				symptom_and_freq[1]=10;
			}
			else{
				if(r.getCell(index_frequency).getType()!=StringCell.TYPE){
					symptom_and_freq[1]=10;
				}
				else{
					symptom_and_freq[1]= FrequencyConverter.convertFrequency(
						((StringCell) r.getCell(index_frequency)).getStringValue());
				}
			}
			
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
	 * converts BufferedDataTable with ontology edges to phenomizer data structure
	 * @param table: table with ontology edges received at inport of PhenomizerNode
	 * @return Integer [][] of symptom ids corresponding to the edges of the ontology: (child, parent) pairs
	 */
	
	public static int [][] generateEdges (BufferedDataTable table){
		
		int [][] res = new int [table.getRowCount()][2];
		int index_child = table.getDataTableSpec().findColumnIndex(PhenomizerNodeModel.CHILD_ID);
		int index_parent = table.getDataTableSpec().findColumnIndex(PhenomizerNodeModel.PARENT_ID);
		boolean child_is_int=false;
		if(table.getDataTableSpec().getColumnSpec(index_child).getType()==IntCell.TYPE){
			child_is_int = true;
		}
		boolean parent_is_int=false;
		if(table.getDataTableSpec().getColumnSpec(index_parent).getType()==IntCell.TYPE){
			parent_is_int = true;
		}
		
		int rowcounter = 0;
		for(DataRow r: table){
			if(child_is_int){
				res[rowcounter][0] = ((IntCell) r.getCell(index_child)).getIntValue();
			}
			else{
				res[rowcounter][0] = (int)((LongCell) r.getCell(index_child)).getLongValue();
			}
			if(parent_is_int){
				res[rowcounter][1] = ((IntCell) r.getCell(index_parent)).getIntValue();
			}
			else{
				res[rowcounter][1] = (int) ((LongCell) r.getCell(index_parent)).getLongValue();
			}
			rowcounter++;
		}
		return res;
	}
	
	/**
	 * wirtes output of phenomizer algorithm to a buffered data table and adds disease names from ksz inport table
	 * @param output: result of phenomizer algorithm
	 * @param exec: execution context of phenomizer node model
	 * @param ksz: ksz table from inport of PhenomizerNode
	 * @return BufferedDataTable that is passed to the outport of Phenomizer Node
	 */
	public static BufferedDataTable generateOutput(LinkedList<String[]> output, ExecutionContext exec, BufferedDataTable ksz){
		
		HashMap<Integer,String> IdToName = new HashMap<Integer,String>(ksz.getRowCount()*3);
		int pos_disease_name = ksz.getDataTableSpec().findColumnIndex(PhenomizerNodeModel.DISEASE_NAME);
		int pos_disease_id = ksz.getDataTableSpec().findColumnIndex(PhenomizerNodeModel.DISEASE_ID);
		boolean is_int = false;
		if(ksz.getDataTableSpec().getColumnSpec(pos_disease_id).getType()==IntCell.TYPE){
			is_int=true;
		}
		for(DataRow r: ksz){
			int id=-1;
			if(is_int){
				id = ((IntCell) r.getCell(pos_disease_id)).getIntValue();
			}
			else{
				id = (int) ((LongCell) r.getCell(pos_disease_id)).getLongValue();
			}
			if(!IdToName.containsKey(id)){
				IdToName.put(id, ((StringCell) r.getCell(pos_disease_name)).getStringValue());
			}
		}
		
		DataTableSpec spec = new DataTableSpec();
		//lenght = 2 -> no pvalue, length = 3 -> pvalue
		int out_len = output.peek().length;
		if(out_len==2){
			spec = PhenomizerNodeModel.generateOutputSpec(false);
		}
		else{
			spec = PhenomizerNodeModel.generateOutputSpec(true);
		}
		int index_disease_id = spec.findColumnIndex(PhenomizerNodeModel.DISEASE_ID);
		int index_score = spec.findColumnIndex(PhenomizerNodeModel.SCORE);
		int index_disease_name = spec.findColumnIndex(PhenomizerNodeModel.DISEASE_NAME);
		int index_p_value = spec.findColumnIndex(PhenomizerNodeModel.P_VALUE);
		int index_significance = spec.findColumnIndex(PhenomizerNodeModel.SIGNIFICANCE);
		
		BufferedDataContainer c = exec.createDataContainer(spec);
		int counter = 0;
		for(String [] rowdata : output){
			counter++;
			RowKey key = new RowKey("Row "+counter);
			DataCell [] cells = new DataCell [spec.getNumColumns()];
			int id = Integer.parseInt(rowdata[0]);
			cells[index_disease_id] = new IntCell(id);
			cells[index_disease_name] = new StringCell(IdToName.get(id));
			cells[index_score] = new DoubleCell(Double.parseDouble(rowdata[1]));
			if(out_len==3){
				double pval = Double.parseDouble(rowdata[2]);
				cells[index_p_value] = new DoubleCell(pval);
				cells[index_significance] = new StringCell(getSignificance(pval));
			}
	    	DataRow row = new DefaultRow(key, cells);
	    	c.addRowToTable(row);
		}
		c.close();

		return c.getTable();
	}
	
	/** translates p value into star representation indicating the significance of the p value
	 * @param pval : p value
	 * @return : "" if pval >= 0.05
	 * 			* if 0.01 <= pval < 0.05
	 * 			** if 0.001 <= pval < 0.01
	 * 			*** if 0.0001 <= pval < 0.001
	 *			**** if pval <= 0.0001
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
//		if(pval >= 0.0001){
//			return "***";
//		}
//		if(pval < 0.0001){
//			return "****";
//		}
		return "";
	}

}
	



