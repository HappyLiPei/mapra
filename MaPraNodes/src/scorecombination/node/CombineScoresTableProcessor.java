package scorecombination.node;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;

import nodeutils.ColumnSpecification;
import nodeutils.TableFunctions;

public class CombineScoresTableProcessor {
	
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

}
