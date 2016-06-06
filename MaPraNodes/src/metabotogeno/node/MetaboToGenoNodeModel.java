package metabotogeno.node;

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
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

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
 * This is the model implementation of MetaboToGeno.
 * 
 *
 * @author Marie-Sophie Friedl
 */
public class MetaboToGenoNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(MetaboToGenoNodeModel.class);
        
    /** the settings key which is used to retrieve and 
        store the settings (from the dialog or from a settings file)    
       (package visibility to be usable from the dialog). */
	static final String CFGKEY_COUNT = "Count";

    /** initial default count value. */
    static final int DEFAULT_COUNT = 100;

    // example value: the models count variable filled from the dialog 
    // and used in the models execution method. The default components of the
    // dialog work with "SettingsModels".
    private final SettingsModelIntegerBounded m_count =
        new SettingsModelIntegerBounded(MetaboToGenoNodeModel.CFGKEY_COUNT,
                    MetaboToGenoNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
    
    private static final int INPORT_SCOREMETABOLITES=0;
    private static final int INPORT_METABOLITE_GENE=1;
    private static final int INPORT_ALL_GENES=2;

    /**
     * Constructor for the node model.
     * The node has 3 incoming ports.
     * Port 0: result of ScoreMetablites, Port 1: metabolite-gene associations, Port 2: list of all genes
     */
    protected MetaboToGenoNodeModel() {
        // 3 incoming ports, 1 outgoing port
        super(3, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

        // TODO do something here
        logger.info("Node Model Stub... this is not yet implemented !");

        
        // the data table spec of the single output table, 
        // the table will have three columns:
        DataTableSpec outputSpec = TableProcessorMetaboToGeno.generateOutputSpec();
        // the execution context will provide us with storage capacity, in this
        // case a data container to which we will add rows sequentially
        // Note, this container can also handle arbitrary big data tables, it
        // will buffer to disc if necessary.
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        // let's add m_count rows to it
        for (int i = 0; i < m_count.getIntValue(); i++) {
            RowKey key = new RowKey("Row " + i);
            // the cells of the current row, the types of the cells must match
            // the column spec (see above)
            DataCell[] cells = new DataCell[3];
            cells[0] = new StringCell("String_" + i); 
            cells[1] = new DoubleCell(0.5 * i); 
            cells[2] = new StringCell(i+"");
            DataRow row = new DefaultRow(key, cells);
            container.addRowToTable(row);
            
            // check if the execution monitor was canceled
            exec.checkCanceled();
            exec.setProgress(i / (double)m_count.getIntValue(), 
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
        
    	//check table with metabolite scores for 2 columns: metabolite id and pvalue
    	TableFunctions.checkColumn(inSpecs, INPORT_SCOREMETABOLITES, ColumnSpecification.METABOLITE_ID,
    			ColumnSpecification.METABOLITE_ID_TYPE, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_SCOREMETABOLITES, ColumnSpecification.METABOLITE_SIGNIFICANCE,
    			ColumnSpecification.METABOLITE_SIGNIFICANCE_TYPE, null);
    	
    	//check table with metabolite - gene associations for columns 
    	TableFunctions.checkColumn(inSpecs, INPORT_METABOLITE_GENE, ColumnSpecification.METABOLITE_ID,
    			ColumnSpecification.METABOLITE_ID_TYPE, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_METABOLITE_GENE, ColumnSpecification.GENE_ID,
    			ColumnSpecification.GENE_ID_TYPE, null);
    	
    	//check table with list of all genes
    	TableFunctions.checkColumn(inSpecs, INPORT_ALL_GENES, ColumnSpecification.GENE_ID, 
    			ColumnSpecification.GENE_ID_TYPE, null);

        return new DataTableSpec[]{TableProcessorMetaboToGeno.generateOutputSpec()};
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

