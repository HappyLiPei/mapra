package phenotogeno.node;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import nodeutils.TableFunctions;
import phenomizer.node.PhenomizerNodeModel;
import phenotogeno.algo.PhenoToGenoDriver;
import togeno.ScoredGene;


/**
 * This is the model implementation of PhenoToGenoNode.
 * 
 *
 * @author Marie-Sophie Friedl
 */
public class PhenoToGenoNodeNodeModel extends NodeModel {
    
	/** the logger instance for writing to KNIME console and log*/
    private static final NodeLogger logger = NodeLogger
            .getLogger(PhenoToGenoNodeNodeModel.class);
    
    //inport positions
    private static final int INPORT_PHENOMIZER = 0;
    private static final int INPORT_GENE_DISEASE = 1;
    private static final int INPORT_ALL_GENES = 2;
    
    //column names
    public static final String GENE_ID = "gene_id";
    public static final String GENE_PROBABILITY = "gene_probability";
    public static final String CONTRIBUTION= "contribution";
    

    /**
     * Constructor for the node model.
     * 3 inports: 
     * 		results from Phenomizer (0)
     * 		associations between genes and diseases (1)
     * 		list of all genes (2)
     */
    protected PhenoToGenoNodeNodeModel() {
    	//3 incoming, 1 outgoing port
        super(3, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
        
        LinkedList<String> geneList = TableProcessorPhenoToGeno.getGeneList(inData[INPORT_ALL_GENES], logger);
        HashMap<Integer, LinkedList<String>> associations =
        		TableProcessorPhenoToGeno.getAssociations(inData[INPORT_GENE_DISEASE], inData[INPORT_ALL_GENES], logger);
        LinkedList<String []> phenoRes = TableProcessorPhenoToGeno.getPhenomizerResult(inData[INPORT_PHENOMIZER],
        		inData[INPORT_GENE_DISEASE], logger);
        
        PhenoToGenoDriver d = new PhenoToGenoDriver(phenoRes, geneList, associations);
        LinkedList<ScoredGene> res = d.runPhenoToGeno();
        BufferedDataTable tab = TableProcessorPhenoToGeno.generateOutput(exec, res, inData[INPORT_PHENOMIZER]);
        
        return new BufferedDataTable[]{tab};
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
        
    	//check Phenomizer table: requires disease id and p value
    	TableFunctions.checkColumn(inSpecs, INPORT_PHENOMIZER, PhenomizerNodeModel.DISEASE_ID,
    			new DataType[]{IntCell.TYPE, LongCell.TYPE}, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_PHENOMIZER, PhenomizerNodeModel.P_VALUE,
    			new DataType[]{DoubleCell.TYPE}, null);
    	
    	//check disease - gene table: requires gene id and disease id
    	TableFunctions.checkColumn(inSpecs, INPORT_GENE_DISEASE, GENE_ID, new DataType[]{StringCell.TYPE}, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_GENE_DISEASE, PhenomizerNodeModel.DISEASE_ID,
    			new DataType[]{IntCell.TYPE, LongCell.TYPE}, null);
    	
    	//check gene table: requires gene id
    	TableFunctions.checkColumn(inSpecs, INPORT_ALL_GENES, GENE_ID, new DataType[]{StringCell.TYPE}, null);
    	
    	//create table spec for output
        return new DataTableSpec[]{TableProcessorPhenoToGeno.generateOutputSpec()};
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

