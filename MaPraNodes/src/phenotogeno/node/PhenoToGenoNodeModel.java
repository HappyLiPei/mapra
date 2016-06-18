package phenotogeno.node;

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
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import nodeutils.ColumnSpecification;
import nodeutils.TableFunctions;
import phenotogeno.algo.PhenoToGenoDriver;
import togeno.ScoredGene;


/**
 * This is the model implementation of PhenoToGenoNode.
 * 
 *
 * @author Marie-Sophie Friedl
 */
public class PhenoToGenoNodeModel extends NodeModel {
    
	/** the logger instance for writing to KNIME console and log*/
    private static final NodeLogger logger = NodeLogger
            .getLogger(PhenoToGenoNodeModel.class);
    
    //option for choosing annotation mode
    /** key of the option annotation mode */
    static final String CFGKEY_ANNOTATION_MODE="annotation_mode";
    /** string value coding for annotation mode using only the maximum score */
    static final String ANNOTATION_MODE_MAX="annoMax";
    /** string value coding for annotation mode combining all scores */
    static final String ANNOTATION_MODE_MULTIPLE="annoMultiple";
    /** default value of the option annotation mode */
    static final String DEFAULT_ANNOTATION_MODE=ANNOTATION_MODE_MULTIPLE;
    private final SettingsModelString m_annotation_mode = new SettingsModelString(
    		CFGKEY_ANNOTATION_MODE, DEFAULT_ANNOTATION_MODE);
    
    //inport positions
    /** inport number of the table with the results of Phenomizer */
    private static final int INPORT_PHENOMIZER = 0;
    /** inport number of the table with disease - gene pairs */
    private static final int INPORT_GENE_DISEASE = 1;
    /** inport number of the table with all genes */
    private static final int INPORT_ALL_GENES = 2;
    
    
    /**
     * Constructor for the node model.
     * 3 inports for the results from Phenomizer (0), the associations between genes and diseases (1) and the
     * 		list of all genes (2)
     */
    protected PhenoToGenoNodeModel() {
    	//3 incoming, 1 outgoing port
        super(3, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
        
    	//read in data from KNIME tables at the inPorts
        LinkedList<String> geneList = TableProcessorPhenoToGeno.getGeneList(inData[INPORT_ALL_GENES], logger);
        HashMap<Integer, LinkedList<String>> associations = TableProcessorPhenoToGeno.getAssociations(
        		inData[INPORT_GENE_DISEASE], logger, geneList);
        LinkedList<String []> phenoRes = TableProcessorPhenoToGeno.getPhenomizerResult(
        		inData[INPORT_PHENOMIZER], logger, associations);
        
        //configuration of the PhenoToGeno driver -> choose mode of annotation
        PhenoToGenoDriver d = new PhenoToGenoDriver(phenoRes, geneList, associations);
        if(m_annotation_mode.getStringValue().equals(ANNOTATION_MODE_MULTIPLE)){
        	d.setModeOfAnnotation(true);
        }
        else{
        	d.setModeOfAnnotation(false);
        }
        
        //execution of PhenoToGeno
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
    	TableFunctions.checkColumn(inSpecs, INPORT_PHENOMIZER, ColumnSpecification.DISEASE_ID,
    			ColumnSpecification.DISEASE_ID_TYPE, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_PHENOMIZER, ColumnSpecification.P_VALUE,
    			ColumnSpecification.P_VALUE_TYPE, null);
    	
    	//check disease - gene table: requires gene id and disease id
    	TableFunctions.checkColumn(inSpecs, INPORT_GENE_DISEASE, ColumnSpecification.GENE_ID, 
    			ColumnSpecification.GENE_ID_TYPE, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_GENE_DISEASE, ColumnSpecification.DISEASE_ID,
    			ColumnSpecification.DISEASE_ID_TYPE, null);
    	
    	//check gene table: requires gene id
    	TableFunctions.checkColumn(inSpecs, INPORT_ALL_GENES, ColumnSpecification.GENE_ID, 
    			ColumnSpecification.GENE_ID_TYPE, null);
    	
    	//create table spec for output
        return new DataTableSpec[]{TableProcessorPhenoToGeno.generateOutputSpec()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
    	m_annotation_mode.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_annotation_mode.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	
    	m_annotation_mode.validateSettings(settings);
    	
    	//this should never happen
    	String mode = settings.getString(CFGKEY_ANNOTATION_MODE);
    	if(!mode.equals(ANNOTATION_MODE_MAX) && !mode.equals(ANNOTATION_MODE_MULTIPLE)){
    		throw new InvalidSettingsException(
    				"Gene Annotation Mode \""+mode+"\" is not supported by this node!");
    	}
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

