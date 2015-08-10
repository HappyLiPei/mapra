package algorithm;

import java.util.HashMap;
import java.util.LinkedList;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnDomainCreator;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.DataContainer;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.IntCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;

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
	 * 
	 * @param table
	 * @return
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
	 * @return Integer [][] of symptom ids of corresponding to the edges of the ontology: (child, parent) pairs
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

}
	



