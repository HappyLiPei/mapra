package metabolites.node;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import metabolites.algo.ScoreMetabolitesDriver;
import metabolites.types.ScoredMetabolite;
import nodeutils.TableFunctions;


/**
 * This is the model implementation of ScoreMetabolites.
 * 
 *
 * @author Marie-Sophie Friedl
 */
public class ScoreMetabolitesNodeModel extends NodeModel {
    
    /** the logger instance for writing to KNIME console and log*/
    private static final NodeLogger logger = NodeLogger
            .getLogger(ScoreMetabolitesNodeModel.class);
    
    /** position of the inport receiving the reference values (position 0)*/
    private static final int INPORT_REFERENCE=0;
    /** position of the inport receiving the measured values (position 1)*/
    private static final int INPORT_MEASUREMENT=1;
    
    /** column name for the column with metabolite ids*/
    public static final String METABOLITE_ID="metabolite_id";
    /** column name for the column with metabolite concentrations*/
    public static final String METABOLITE_CONCENTRATION="concentration";
    /** column name for the column with phenotype groups (age and fasting) */
    public static final String PHENOTYPE_GROUP = "group";
    /** column name for the column with metabolite type (binary vs. concentration)*/
    public static final String METABOLITE_TYPE="type";
    /** column name for the column with mean concentrations of the metabolites*/
    public static final String METABOLITE_MEAN="mean";
    /** column name for the column with standard deviation of the metabolite concentrations*/
    public static final String METABOLITE_STDEV="stdev";
    /** column name for the column with the missingness of the reference metabolites*/
    public static final String METABOLITE_MISSINGNESS="missingness";
    /** column name for the column with metabolite scores*/
    public static final String METABOLITE_SCORE ="metabolite_score";
    /** column name for the column with probabilities indicating the significance of the scores*/
    public static final String METABOLITE_SIGNIFICANCE="significance";
    
       
    /**
     * Constructor for the node model, generates a node with 2 incoming ports and 1 outgoing port
     */
    //TODO: 2 outports ?!
    protected ScoreMetabolitesNodeModel() {
        super(2, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

        // TODO generate output
        logger.info("Node Model Stub... this is not yet implemented !");
        
        HashMap<String, LinkedList<String[]>> referenceControls =
        		TableProcessorScoreMetabolites.getReferences(inData[INPORT_REFERENCE], logger);
        LinkedList<String[]> measuredCase = TableProcessorScoreMetabolites.getMeasurements(
        		inData[INPORT_MEASUREMENT], logger, referenceControls);
        
        ScoreMetabolitesDriver driver = new ScoreMetabolitesDriver(measuredCase, referenceControls);
        LinkedList<ScoredMetabolite> result = driver.runMetaboliteScoring();
        
        for(ScoredMetabolite m: result){
        	System.out.println(m);
        }


        DataTableSpec outputSpec = TableProcessorScoreMetabolites.generateOutSpec();
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        container.close();
        BufferedDataTable out = container.getTable();
        return new BufferedDataTable[]{out};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
    	
    	//check reference metabolite table: 6 columns metabolite id, type, group, mean, stdev, missingness
    	TableFunctions.checkColumn(inSpecs, INPORT_REFERENCE, METABOLITE_ID, new DataType[]{StringCell.TYPE}, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_REFERENCE, METABOLITE_TYPE, new DataType[]{StringCell.TYPE}, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_REFERENCE, PHENOTYPE_GROUP, new DataType[]{IntCell.TYPE}, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_REFERENCE, METABOLITE_MEAN, new DataType[]{DoubleCell.TYPE}, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_REFERENCE, METABOLITE_STDEV, new DataType[]{DoubleCell.TYPE}, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_REFERENCE, METABOLITE_MISSINGNESS, new DataType[]{DoubleCell.TYPE}, null);
    	
    	//check measurements: 3 columns id, measured concentration, group
    	TableFunctions.checkColumn(inSpecs, INPORT_MEASUREMENT, METABOLITE_ID, new DataType[]{StringCell.TYPE}, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_MEASUREMENT, METABOLITE_CONCENTRATION, new DataType[]{DoubleCell.TYPE}, null);
        TableFunctions.checkColumn(inSpecs, INPORT_MEASUREMENT, PHENOTYPE_GROUP, new DataType[]{IntCell.TYPE}, null);
        
        //generate specification for the table to return
        return new DataTableSpec[]{TableProcessorScoreMetabolites.generateOutSpec()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {

    }

}

