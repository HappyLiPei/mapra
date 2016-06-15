package scorecombination.node;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;

import nodeutils.ColumnSpecification;
import nodeutils.TableFunctions;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


/**
 * This is the model implementation of CombineScores.
 * 
 *
 * @author Marie-Sophie Friedl
 */
public class CombineScoresNodeModel extends NodeModel {
    
    /** logger of the node to write to KNIME console and log file*/
    private static final NodeLogger logger = NodeLogger.getLogger(CombineScoresNodeModel.class);
    
    /** number of input ports of this node*/
    private int numberOfInPorts;
    

    /**
     * Constructor for the node model.
     * @param numberOfInPorts number of inports to create
     */
    protected CombineScoresNodeModel(int numberOfInPorts) {
    	
    	super(numberOfInPorts, 1);
    	this.numberOfInPorts=numberOfInPorts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

        // TODO do something here
        logger.info("Node Model Stub... this is not yet implemented !");

        DataTableSpec outputSpec = CombineScoresTableProcessor.generateOutputSpec();
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        for (int i = 0; i < 100; i++) {
            RowKey key = new RowKey("Row " + i);
            DataCell[] cells = new DataCell[2];
            cells[0] = new StringCell("String_" + i); 
            cells[1] = new DoubleCell(0.5 * i); 
            DataRow row = new DefaultRow(key, cells);
            container.addRowToTable(row);
            exec.checkCanceled();
            exec.setProgress(i / (double)100, 
                "Adding row " + i);
        }
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
     * method to check the tables at all input Ports, dependent on the variable numberOfInPorts
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        
    	//check gene ids and scores for all input ports
    	for(int i=0; i<numberOfInPorts; i++){
    		TableFunctions.checkColumn(inSpecs, i, ColumnSpecification.GENE_ID,
    				ColumnSpecification.GENE_ID_TYPE, null);
    		TableFunctions.checkColumn(inSpecs, i, ColumnSpecification.GENE_PROBABILITY,
    				ColumnSpecification.GENE_PROBABILITY_TYPE, null);
    	}
    	
        return new DataTableSpec[]{CombineScoresTableProcessor.generateOutputSpec()};
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

