package geneticnetwork.node;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


/**
 * This is the model implementation of GeneticNetworkScore.
 * 
 *
 * @author Marie-Sophie Friedl
 */
public class GeneticNetworkScoreNodeModel extends NodeModel {
    
	/** the logger instance for writing to KNIME console and log */
    private static final NodeLogger logger = NodeLogger
            .getLogger(GeneticNetworkScoreNodeModel.class);
      
    //TODO: option iterate until convergence???
    
    //weighting
    /** key of the option "edge weights" */
    static final String CFGKEY_EDGE_WEIGHTS="edge_weights";
    /** default value of the option "edge weights" */
    static final boolean DEFAULT_EDGE_WEIGHTS=false;
    /** option object (settings model) to use unweighted/weighted edges */
    private final SettingsModelBoolean m_edge_weights =
    		new SettingsModelBoolean(CFGKEY_EDGE_WEIGHTS, DEFAULT_EDGE_WEIGHTS);
    
    //restart probability
    /** key of the option "restart probability" */
    static final String CFGKEY_RESTART_PROBABILITY="restart_probability";
    /** default value of the option "restart probability" */
    static final double DEFAULT_RESTART_PROBABILITY=0.9;
    /** minimum valid value of the option "restart probability" */
    static final double MIN_RESTART_PROBABILITY=0.0;
    /** maximum valid value of the option "restart probability" */
    static final double MAX_RESTART_PROBABILITY=1.0;
    /** option object (settings model) to adjust the restart probability */
    private final SettingsModelDoubleBounded m_restart_probability =
    		new SettingsModelDoubleBounded(CFGKEY_RESTART_PROBABILITY, DEFAULT_RESTART_PROBABILITY,
    				MIN_RESTART_PROBABILITY, MAX_RESTART_PROBABILITY);
    
    //number of iterations
    /** key of the option "number of iterations" */
    static final String CFGKEY_NUMBER_OF_ITERATIONS = "number_of_iterations";
    /** default value of the option "number of iterations" */
    static final int DEFAULT_NUMBER_OF_ITERATIONS = 2;
    /** minimum valid value of the option "number of iterations" */
    static final int MIN_NUMBER_OF_ITERATIONS=0;
    /** maximum valid value of the option "number of iterations" */
    static final int MAX_NUMBER_OF_ITERATIONS=Integer.MAX_VALUE;
    /** option object (settings model) to specify the number of iterations */
    private final SettingsModelIntegerBounded m_number_of_iterations =
    		new SettingsModelIntegerBounded(CFGKEY_NUMBER_OF_ITERATIONS, DEFAULT_NUMBER_OF_ITERATIONS,
    				MIN_NUMBER_OF_ITERATIONS, MAX_NUMBER_OF_ITERATIONS);
    
    //input ports
    private static final int INPORT_GENESCORES=0;
    private static final int INPORT_NETWORK=1;
    
    //column names
    private static final String NODE1 ="gene1";
    private static final String NODE2 ="gene2";
    private static final String EDGEWEIGHT="weight";

    /**
     * Constructor for the node model.
     */
    protected GeneticNetworkScoreNodeModel() {
        //2 incoming, 1 outgoing port
        super(2, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

        // TODO do something here
        logger.info("Node Model Stub... this is not yet implemented !");
        
        System.out.println("Weights "+m_edge_weights.getBooleanValue());
        System.out.println("Restart probability "+m_restart_probability.getDoubleValue());
        System.out.println("Iterations "+m_number_of_iterations.getIntValue());

        
        // the data table spec of the single output table, 
        // the table will have three columns:
        DataColumnSpec[] allColSpecs = new DataColumnSpec[3];
        allColSpecs[0] = 
            new DataColumnSpecCreator("Column 0", StringCell.TYPE).createSpec();
        allColSpecs[1] = 
            new DataColumnSpecCreator("Column 1", DoubleCell.TYPE).createSpec();
        allColSpecs[2] = 
            new DataColumnSpecCreator("Column 2", IntCell.TYPE).createSpec();
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
        // the execution context will provide us with storage capacity, in this
        // case a data container to which we will add rows sequentially
        // Note, this container can also handle arbitrary big data tables, it
        // will buffer to disc if necessary.
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        // let's add m_count rows to it
        for (int i = 0; i < 100; i++) {
            RowKey key = new RowKey("Row " + i);
            // the cells of the current row, the types of the cells must match
            // the column spec (see above)
            DataCell[] cells = new DataCell[3];
            cells[0] = new StringCell("String_" + i); 
            cells[1] = new DoubleCell(0.5 * i); 
            cells[2] = new IntCell(i);
            DataRow row = new DefaultRow(key, cells);
            container.addRowToTable(row);
            
            // check if the execution monitor was canceled
            exec.checkCanceled();
            exec.setProgress(i / (double)100, 
                "Adding row " + i);
        }
        // once we are done, we close the container and return its table
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
        
        // TODO: check if user settings are available, fit to the incoming
        // table structure, and the incoming types are feasible for the node
        // to execute. If the node can execute in its current state return
        // the spec of its output data table(s) (if you can, otherwise an array
        // with null elements), or throw an exception with a useful user message

        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {

        m_edge_weights.saveSettingsTo(settings);
        m_restart_probability.saveSettingsTo(settings);
        m_number_of_iterations.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        
        m_edge_weights.loadSettingsFrom(settings);
        m_restart_probability.loadSettingsFrom(settings);
        m_number_of_iterations.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
       
        m_edge_weights.validateSettings(settings);
        m_restart_probability.validateSettings(settings);
        m_number_of_iterations.validateSettings(settings);
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

