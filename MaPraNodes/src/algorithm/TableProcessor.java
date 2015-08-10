package algorithm;

import java.util.HashMap;
import java.util.LinkedList;

import org.knime.core.data.DataCell;
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

import phenomizer.PhenomizerNodeModel;

public class TableProcessor {
	
	/**
	 * converts BufferedDataTable with query symptoms to phenomizer data structure
	 * @param table: table with query symptoms received at inport of PhenomizerNode
	 * @return LinkedList of symptom ids of the query
	 */
	
	public static LinkedList<Integer> generateQuery(BufferedDataTable table){
		return generateSymptomList(table);
	}
	
	/**
	 * converts BufferedDataTable with symptom dictionary to phenomizer data structure
	 * @param table: table with symptom dictionary received at inport of PhenomizerNode
	 * @return LinkedList of symptom ids of the symptom dictionary
	 */
	
	public static LinkedList<Integer> generateSymptomList(BufferedDataTable table){
		int index = table.getDataTableSpec().findColumnIndex(PhenomizerNodeModel.SYMPTOM_ID);
		LinkedList<Integer> result = new LinkedList<Integer>();
		for (DataRow r: table){
			result.add(((IntCell) r.getCell(index)).getIntValue());
		}
		return result;
	}
	
	/**
	 * converts BufferedDataTable with disease annotation to phenomizer data structure
	 * @param table: table with disease annotation received at inport of PhenomizerNode
	 * @return HashMap which maps a disease id to the annotated symptom ids
	 */
	
	public static HashMap<Integer, LinkedList<Integer>> generateKSZ(BufferedDataTable table){
		
		HashMap<Integer, LinkedList<Integer>> res = new HashMap<Integer, LinkedList<Integer>>(table.getRowCount()*3);
		int index_disease = table.getDataTableSpec().findColumnIndex(PhenomizerNodeModel.DISEASE_ID);
		int index_symptom = table.getDataTableSpec().findColumnIndex(PhenomizerNodeModel.SYMPTOM_ID);
		for(DataRow r : table){
			int disease_id = ((IntCell) r.getCell(index_disease)).getIntValue();
			int symptom_id = ((IntCell) r.getCell(index_symptom)).getIntValue();
			if(res.containsKey(disease_id)){
				res.get(disease_id).add(symptom_id);
			}
			else{
				LinkedList<Integer> tmp = new LinkedList<Integer>();
				tmp.add(symptom_id);
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
		int rowcounter = 0;
		for(DataRow r: table){
			res[rowcounter][0] = ((IntCell) r.getCell(index_child)).getIntValue();
			res[rowcounter][1] = ((IntCell) r.getCell(index_parent)).getIntValue();
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
		for(DataRow r: ksz){
			int id = ((IntCell) r.getCell(pos_disease_id)).getIntValue();
			if(!IdToName.containsKey(id)){
				IdToName.put(id, ((StringCell) r.getCell(pos_disease_name)).getStringValue());
			}
		}
		
		DataTableSpec spec = PhenomizerNodeModel.generateOutputSpec();
		int index_disease_id = spec.findColumnIndex(PhenomizerNodeModel.DISEASE_ID);
		int index_score = spec.findColumnIndex(PhenomizerNodeModel.SCORE);
		int index_disease_name = spec.findColumnIndex(PhenomizerNodeModel.DISEASE_NAME);
		int index_p_value = spec.findColumnIndex(PhenomizerNodeModel.P_VALUE);
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
			cells[index_p_value] = new DoubleCell(0.0);
	    	DataRow row = new DefaultRow(key, cells);
	    	c.addRowToTable(row);
		}
		c.close();

		return c.getTable();
		
	}

}
	



