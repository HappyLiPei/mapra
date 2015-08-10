package algorithm;

import java.util.HashMap;
import java.util.LinkedList;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

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
	 * 
	 * @param table
	 * @return
	 */
	
	public static LinkedList<Integer> generateQuery(BufferedDataTable table){
		
		DataTableSpec s = table.getDataTableSpec();
		int index = s.findColumnIndex(PhenomizerNodeModel.SYMPTOM_ID);
		
		LinkedList<Integer> result = new LinkedList<Integer>();
		
		for (DataRow r: table){
			IntCell c = (IntCell) r.getCell(index);
			result.add(c.getIntValue());
		}
		return result;
	}
	
	/**
	 * 
	 * @param table
	 * @return
	 */
	
	public static LinkedList<Integer> generateSymptoms(BufferedDataTable table){
		

		
		return null;
	}
	
	/**
	 * 
	 * @param table
	 * @return
	 */
	public static HashMap<Integer, LinkedList<Integer>> generateKSZ(BufferedDataTable table){
		return null;
	}
	
	/**
	 * 
	 * @param table
	 * @return
	 */
	public static int [][] generateEdges (BufferedDataTable table){
		return null;
	}

}
	



