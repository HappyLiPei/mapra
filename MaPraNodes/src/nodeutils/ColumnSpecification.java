package nodeutils;

import org.knime.core.data.DataType;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;

/** class for managing all column names and valid types for them, each column can have multiple valid {@link DataType}s
 * 	stored in an array, the default {@link DataType} is located at position 0 of the array*/
public class ColumnSpecification {
	
	//column names
	//disease columns
	/** column name for the column with symptom ids*/
    public static final String SYMPTOM_ID ="symptom_id";
    /** column name for the column with symptom names*/
    public static final String SYMPTOM_NAME = "symptom_name";
    /** column name for the column with frequency terms for symptom-disease associations*/
    public static final String FREQUENCY ="frequency";
    /** column name for the column with symptom ids representing children in the ontoloy of symptoms*/
    public static final String CHILD_ID = "child_id";
    /** column name for the column with symptom ids representing parents in the ontoloy of symptoms*/
    public static final String PARENT_ID = "parent_id";
    /** column name for the column with disease ids*/
	public static final String DISEASE_ID ="disease_id";
	/** column name for the column with disease names */
	public static final String DISEASE_NAME = "disease";
	/** column name for the column with disease scores (similarity between disease and query) */
	public static final String SCORE = "score";
	/** column name for the column with pvalues of the diseases */
	public static final String P_VALUE = "p_value";
	/** column name for the column with symbolic pvalues of the diseases (indicating significance at different niveaus)*/
	public static final String SIGNIFICANCE = "significance";
	
	//metabolite columns
    /** column name for the column with metabolite ids*/
    public static final String METABOLITE_ID="metabolite_id";
    /** column name for the column with metabolite names */
    public static final String METABOLITE_NAME="metabolite_name";
    /** column name for the column with metabolite type (binary vs. concentration)*/
    public static final String METABOLITE_TYPE="type";
    /** column name for the column with mean concentrations of the metabolites*/
    public static final String METABOLITE_MEAN="mean";
    /** column name for the column with standard deviation of the metabolite concentrations*/
    public static final String METABOLITE_STDEV="stdev";
    /** column name for the column with the missingness of the reference metabolites*/
    public static final String METABOLITE_MISSINGNESS="missingness";
    /** column name for the column with metabolite concentrations*/
    public static final String METABOLITE_CONCENTRATION="concentration";
    /** column name for the column with phenotype groups (age and fasting) */
    public static final String PHENOTYPE_GROUP = "group";
    /** column name for the column with metabolite scores (z-scores, missingness-based score)*/
    public static final String METABOLITE_SCORE ="metabolite_score";
    /** column name for the column with pvalues (probabilities indicating the significance of the metabolite scores)*/
    public static final String METABOLITE_SIGNIFICANCE="significance";
    
    //gene columns
    /** column name for the column with gene ids*/
    public static final String GENE_ID = "gene_id";
    /** column name for the column with gene scores (probability that the gene is causal) */
    public static final String GENE_PROBABILITY = "gene_probability";
    /** column name for the column with the major contributors (metabolites or diseases) to the gene score*/
    public static final String CONTRIBUTION= "contribution";
    /** column name for the column with the enrichment score of a gene*/
    public static final String GENE_ENRICHMENT="enrichment_score";
    /** column name for the column with gene ids corresponding to nodes of a genetic network, the node is start point of an (undirected) edge) */
    public static final String GENE1 ="gene1";
    /** column name for the column with gene ids corresponding to nodes of a genetic network, the node is end point of an (undirected) edge) */
    public static final String GENE2 ="gene2";
    /** column name for the column with edge weights = integer edge weights for each weight (required only if the edge weight option of NetworkScore is used )*/
    public static final String EDGEWEIGHT="weight";
    
    //column types
    //disease column types
	/** valid data types for symptom id */
    public static final DataType[] SYMPTOM_ID_TYPE =new DataType[]{IntCell.TYPE, LongCell.TYPE};
    /** valid data types for symptom name*/
    public static final DataType[] SYMPTOM_NAME_TYPE = new DataType[]{StringCell.TYPE};
    /** valid data type for frequency */
    public static final DataType[] FREQUENCY_TYPE = new DataType[]{StringCell.TYPE};
    /** valid data types for child id */
    public static final DataType[] CHILD_ID_TYPE = new DataType[]{IntCell.TYPE, LongCell.TYPE};
    /** valid data types for parent id */
    public static final DataType[] PARENT_ID_TYPE = new DataType[]{IntCell.TYPE, LongCell.TYPE};
    /** valid data types for disease id*/
    public static final DataType[] DISEASE_ID_TYPE=new DataType[]{IntCell.TYPE, LongCell.TYPE};
    /** valid data types for disease name */
    public static final DataType[] DISEASE_NAME_TYPE = new DataType[]{StringCell.TYPE};
	/** valid data types for score */
	public static final DataType[] SCORE_TYPE = new DataType[]{DoubleCell.TYPE};
    /** valid data types for disease pvalues */
    public static final DataType[] P_VALUE_TYPE = new DataType[]{DoubleCell.TYPE};
	/** valid data types for significance string*/
	public static final DataType[] SIGNIFICANCE_TYPE = new DataType[]{StringCell.TYPE};
    
    //metabolite column types
    /** valid data types for metabolite id*/
    public static final DataType[] METABOLITE_ID_TYPE=new DataType[]{StringCell.TYPE};
    /** valid data types for metabolite name*/
    public static final DataType[] METABOLITE_NAME_TYPE=new DataType[]{StringCell.TYPE};
    /** valid data types for metabolite type*/
    public static final DataType[] METABOLITE_TYPE_TYPE=new DataType[]{StringCell.TYPE};
    /** valid data types for metabolite mean*/
    public static final DataType[] METABOLITE_MEAN_TYPE=new DataType[]{DoubleCell.TYPE};
    /** valid data types for metabolite standard deviation*/
    public static final DataType[] METABOLITE_STDEV_TYPE=new DataType[]{DoubleCell.TYPE};
    /** valid data types for metabolite missingness*/
    public static final DataType[] METABOLITE_MISSINGNESS_TYPE=new DataType[]{DoubleCell.TYPE};
    /** valid data types for metabolite concentrations*/
    public static final DataType[] METABOLITE_CONCENTRATION_TYPE= new DataType[]{DoubleCell.TYPE};
    /** valid data types for phenotype group */
    public static final DataType[] PHENOTYPE_GROUP_TYPE = new DataType[]{IntCell.TYPE};
    /** valid data types for metabolite scores*/
    public static final DataType[] METABOLITE_SCORE_TYPE =new DataType[]{DoubleCell.TYPE};
    /** valid data types for metabolite pvalue*/
    public static final DataType[] METABOLITE_SIGNIFICANCE_TYPE=new DataType[]{DoubleCell.TYPE};
    
    //gene column types
    /** valid data types for gene id*/
    public static final DataType[] GENE_ID_TYPE = new DataType[]{StringCell.TYPE};
    /** valid data types for gene scores (probabilities) */
    public static final DataType[] GENE_PROBABILITY_TYPE = new DataType[]{DoubleCell.TYPE};
    /** valid data types for the contributors*/
    public static final DataType[] CONTRIBUTION_TYPE = new DataType[]{StringCell.TYPE};
    /** valid data types for the enrichment score*/
    public static final DataType[] GENE_ENRICHMENT_TYPE= new DataType[]{DoubleCell.TYPE};
    /** valid data types for gene1*/
    public static final DataType[] GENE1_TYPE = new DataType[]{StringCell.TYPE};
    /** valid data types for gene2*/
    public static final DataType[] GENE2_TYPE = new DataType[]{StringCell.TYPE};
    /** valid data types for edge weight (optional column)*/
    public static final DataType[] EDGEWEIGHT_TYPE = new DataType[]{IntCell.TYPE};

}
