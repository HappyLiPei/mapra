package metabotogeno.node;

import java.util.HashMap;
import java.util.LinkedList;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.NodeLogger;

import nodeutils.ColumnSpecification;
import nodeutils.TableFunctions;

public class TableProcessorMetaboToGeno {
	
	//TODO: implement
	//check for duplicates in list -> warn only
	protected static LinkedList<String> getGeneList(BufferedDataTable table, NodeLogger logger){
		return null;
	}
	
	//TODO: implement
	//do not check for duplicates -> different sources
	//check for genes that are not part of the gene list -> warn only
	protected static HashMap<String, LinkedList<String>> getAssociationData(BufferedDataTable table, NodeLogger logger){
		return null;
	}
	
	//TODO: implement
	//check for duplicate metabolites -> warn only
	//check for metabolites that are not part of the associations -> warn only
	protected static LinkedList<String[]> getScoreMetabolitesResult(BufferedDataTable table, NodeLogger logger){
		return null;
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

}
