package metabolites.node;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.knime.core.data.DataTableSpec;
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
import nodeutils.ColumnSpecification;
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
        
       
    /**
     * Constructor for the node model, generates a node with 2 incoming ports and 1 outgoing port
     */
    protected ScoreMetabolitesNodeModel() {
        super(2, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
        
        HashMap<String, LinkedList<String[]>> referenceControls =
        		TableProcessorScoreMetabolites.getReferences(inData[INPORT_REFERENCE], logger);
        LinkedList<String[]> measuredCase = TableProcessorScoreMetabolites.getMeasurements(
        		inData[INPORT_MEASUREMENT], logger, referenceControls);
        
        ScoreMetabolitesDriver driver = new ScoreMetabolitesDriver(measuredCase, referenceControls);
        LinkedList<ScoredMetabolite> result = driver.runMetaboliteScoring();
        
        BufferedDataTable returnTable = TableProcessorScoreMetabolites.generateOutTable(
        		result, exec, inData[INPORT_REFERENCE]);
        
        return new BufferedDataTable[]{returnTable};
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
    	TableFunctions.checkColumn(inSpecs, INPORT_REFERENCE, 
    			ColumnSpecification.METABOLITE_ID, ColumnSpecification.METABOLITE_ID_TYPE, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_REFERENCE, 
    			ColumnSpecification.METABOLITE_TYPE, ColumnSpecification.METABOLITE_TYPE_TYPE, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_REFERENCE, 
    			ColumnSpecification.PHENOTYPE_GROUP, ColumnSpecification.PHENOTYPE_GROUP_TYPE, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_REFERENCE, 
    			ColumnSpecification.METABOLITE_MEAN, ColumnSpecification.METABOLITE_MEAN_TYPE, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_REFERENCE, 
    			ColumnSpecification.METABOLITE_STDEV, ColumnSpecification.METABOLITE_STDEV_TYPE, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_REFERENCE, 
    			ColumnSpecification.METABOLITE_MISSINGNESS, ColumnSpecification.METABOLITE_MISSINGNESS_TYPE, null);
    	
    	//check column for metabolite names
    	if(inSpecs[INPORT_REFERENCE].findColumnIndex(ColumnSpecification.METABOLITE_NAME)!=-1){
    		TableFunctions.checkColumn(inSpecs, INPORT_REFERENCE, 
    				ColumnSpecification.METABOLITE_NAME, ColumnSpecification.METABOLITE_NAME_TYPE, null);
    	}
    	
    	//check measurements: 3 columns id, measured concentration, group
    	TableFunctions.checkColumn(inSpecs, INPORT_MEASUREMENT, 
    			ColumnSpecification.METABOLITE_ID, ColumnSpecification.METABOLITE_ID_TYPE, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_MEASUREMENT, 
    			ColumnSpecification.METABOLITE_CONCENTRATION, ColumnSpecification.METABOLITE_CONCENTRATION_TYPE, null);
        TableFunctions.checkColumn(inSpecs, INPORT_MEASUREMENT, 
        		ColumnSpecification.PHENOTYPE_GROUP, ColumnSpecification.PHENOTYPE_GROUP_TYPE, null);
        
        //generate specification for the table to return
        return new DataTableSpec[]{TableProcessorScoreMetabolites.generateOutSpec(inSpecs[INPORT_REFERENCE])};
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

