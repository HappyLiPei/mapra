package metabolites.node;

import java.util.HashMap;
import java.util.LinkedList;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;

import nodeutils.TableFunctions;

public class TableProcessorScoreMetabolites {
	
	//TODO: implement!
	public static LinkedList<String[]> getMeasurements(BufferedDataTable table){
		return null;
	}
	
	//TODO: implement!
	public static HashMap<String, LinkedList<String[]>> getReferences(BufferedDataTable table){
		return null;
	}
	
	/**
	 * method to generate the specification for the table returned by ScoreMetabolites
	 * (3 columns: metabolite id, score, significance)
	 * @return DataTable specification for the table returned by ScoreMetabolites
	 */
	public static DataTableSpec generateOutSpec(){
		
		DataColumnSpec [] spec = new DataColumnSpec[3]; 
		spec[0]=TableFunctions.makeDataColSpec(ScoreMetabolitesNodeModel.METABOLITE_ID, StringCell.TYPE);
		spec[1]=TableFunctions.makeDataColSpec(ScoreMetabolitesNodeModel.METABOLITE_SCORE, DoubleCell.TYPE);
		spec[2]=TableFunctions.makeDataColSpec(ScoreMetabolitesNodeModel.METABOLITE_SIGNIFICANCE, DoubleCell.TYPE);
		
		return new DataTableSpec(spec);
	}
	
	//TODO: implement!
	public static BufferedDataTable generateOutTable(){
		return null;
	}

}
