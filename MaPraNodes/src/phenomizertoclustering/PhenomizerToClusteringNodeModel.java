package phenomizertoclustering;

import java.io.File;
import java.io.IOException;

import nodeutils.SettingsChecker;
import nodeutils.TableChecker;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.IntCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import phenomizer.PhenomizerNodeModel;


/**
 * This is the model implementation of PhenomizerToClustering.
 * 
 *
 * @author 
 */
public class PhenomizerToClusteringNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(PhenomizerToClusteringNodeModel.class);
    
    private static final int IN_PHENO=0;
        
    //file with distance matrix
    protected static final String CFGKEY_DMAT = "dmat";
    protected static final String DEF_DMAT="";
    private final SettingsModelString m_dmat = new SettingsModelString(CFGKEY_DMAT, DEF_DMAT);
    
    //output folder
    protected static final String CFGKEY_OFOLDER = "ofolder";
    protected static final String DEF_OFOLDER = "";
    private final SettingsModelString m_ofolder = new  SettingsModelString(CFGKEY_OFOLDER, DEF_OFOLDER);
    
    //Rscript executable
    protected static final String CFGKEY_RSCRIPTEXEC ="rscript_exec";
    protected static final String DEF_RSCRIPTEXEC ="";
    private final SettingsModelString m_rscripexec = new SettingsModelString(CFGKEY_RSCRIPTEXEC, DEF_RSCRIPTEXEC);

    /**
     * Constructor for the node model.
     */
    protected PhenomizerToClusteringNodeModel() {
        super(1, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	
    	//TODO: remove labels in plot, change dimensions of plot
    	//TODO: command line arguments in script ?
    	//TODO: distance matrix via input port ? -> code in script
    	
    	//TODO: TableChecker in Phenomizer
        
    	RunPhenomizerToClustering.runClustering(inData[IN_PHENO],
    			m_dmat.getStringValue(),
    			m_ofolder.getStringValue(),
    			m_rscripexec.getStringValue(),
    			logger,
    			exec);

        return new BufferedDataTable[0];
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
    	
    	//check if node dialog was called
    	SettingsChecker.checkFileModel(m_dmat,DEF_DMAT,
    			"Distance matrix file is missing\nPlease choose a file in the node dialog");
    	SettingsChecker.checkFileModel(m_ofolder, DEF_OFOLDER,
    			"Output folder is missing\nPlease choose a folder in the node dialog");
    	SettingsChecker.checkFileModel(m_rscripexec, DEF_RSCRIPTEXEC,
    			"RScript executable is missing\nPlease choose a file in the node dialog");
    	
    	//check disease id
    	TableChecker.checkColumn(inSpecs, IN_PHENO, PhenomizerNodeModel.DISEASE_ID, new DataType[]{IntCell.TYPE}, "");
    	
        return new DataTableSpec[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {        
        m_dmat.saveSettingsTo(settings);
        m_ofolder.saveSettingsTo(settings);
        m_rscripexec.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_dmat.loadSettingsFrom(settings);
        m_ofolder.loadSettingsFrom(settings);
        m_rscripexec.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_dmat.validateSettings(settings);
    	m_ofolder.validateSettings(settings);
    	m_rscripexec.validateSettings(settings);
    	
    	SettingsChecker.checkFileDialog(settings, CFGKEY_DMAT, DEF_DMAT, "Distance matrix file is missing");
    	SettingsChecker.checkFileDialog(settings, CFGKEY_OFOLDER, DEF_OFOLDER, "Outputfolder is missing");
    	SettingsChecker.checkFileDialog(settings, CFGKEY_RSCRIPTEXEC, DEF_RSCRIPTEXEC, "No Rscript executable is missing");
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

