package nodeutils;

import org.knime.core.data.DataType;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.StringCell;

/** class for managing all column names and valid types for them, each column can have multiple valid {@link DataType}s
 * 	stored in an array, the default {@link DataType} is located at position 0 of the array*/
public class ColumnSpecification {
	
	//column names
	//metabolite columns
    /** column name for the column with metabolite ids*/
    public static final String METABOLITE_ID="metabolite_id";
    /** column name for the column with pvalues (probabilities indicating the significance of the scores)*/
    public static final String METABOLITE_SIGNIFICANCE="significance";
    
    //gene columns
    /** column name for the column with gene ids*/
    public static final String GENE_ID = "gene_id";
    /** column name for the column with gene scores (probability that the gene is causal) */
    public static final String GENE_PROBABILITY = "gene_probability";
    /** column name for the column with the major contributors (metabolites or diseases) to the gene score*/
    public static final String CONTRIBUTION= "contribution";
    
    //column types
    //metabolite column types
    /** valid data types for metabolite id*/
    public static final DataType[] METABOLITE_ID_TYPE=new DataType[]{StringCell.TYPE};
    /** valid data types for metabolite pvalue*/
    public static final DataType[] METABOLITE_SIGNIFICANCE_TYPE=new DataType[]{DoubleCell.TYPE};
    
    //gene column types
    /** valid data types for gene id*/
    public static final DataType[] GENE_ID_TYPE = new DataType[]{StringCell.TYPE};
    /** valid data types for gene scores (probabilities) */
    public static final DataType[] GENE_PROBABILITY_TYPE = new DataType[]{DoubleCell.TYPE};
    /** valid data types for the contributors*/
    public static final DataType[] CONTRIBUTION_TYPE = new DataType[]{StringCell.TYPE};

}
