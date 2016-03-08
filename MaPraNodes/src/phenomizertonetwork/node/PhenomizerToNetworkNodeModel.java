package phenomizertonetwork.node;

import java.io.File;
import java.io.IOException;

import nodeutils.SettingsChecker;
import nodeutils.TableChecker;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
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
 * This is the model implementation of PhenomizerToNetwork.
 * 
 *
 * @author 
 */
public class PhenomizerToNetworkNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(PhenomizerToNetworkNodeModel.class);
    
    // inport positions
    private static final int IN_PHENO=0;
    private static final int IN_MATRIX=1;
    
    //settingsmodels:
    //comparator for edge cutoff
    protected static final String CFGKEY_COMPARATOR = "comparator";
    protected static final String [] COMPARATOR_VALUES=new String[]{"<",">"};
    protected static final String DEF_COMPARATOR=COMPARATOR_VALUES[0];
    private final SettingsModelString m_comparator = new SettingsModelString(CFGKEY_COMPARATOR, DEF_COMPARATOR);    
    //distance cutoff for edge
    protected static final String CFGKEY_EDGE="edge";
    protected static final double DEF_EDGE=4;
    protected static final double MIN_EDGE=0;
    protected static final double MAX_EDGE = Double.MAX_VALUE;
    private final SettingsModelDoubleBounded m_edge = new SettingsModelDoubleBounded(CFGKEY_EDGE, DEF_EDGE,
    		MIN_EDGE, MAX_EDGE);
    //output folder
    protected static final String CFGKEY_OUT="out";
    protected static final String DEF_OUT="";
    private final SettingsModelString m_out = new SettingsModelString(CFGKEY_OUT, DEF_OUT);
    //start cytoscpe
    protected static final String CFGKEY_START_CYTO="start_cyto";
    protected static final boolean DEF_START_CYTO=false;
    private final SettingsModelBoolean m_start_cyto = new SettingsModelBoolean(CFGKEY_START_CYTO, DEF_START_CYTO);
    //cytoscape script
    protected static final String CFGKEY_CYTO_SCRIPT = "cyto_script";
    protected static final String DEF_CYTO_SCRIPT = "";
    private final SettingsModelString m_cyto_script = new SettingsModelString(CFGKEY_CYTO_SCRIPT, DEF_CYTO_SCRIPT);

    /**
     * Constructor for the node model.
     * 2 inports
     *  -results from phenomizer(0)
     * 	-all against all matrix (1)
     */
    protected PhenomizerToNetworkNodeModel(){
        super(2,0);
        m_cyto_script.setEnabled(m_start_cyto.getBooleanValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	
    	RunPhenomizerToNetwork.runNetworkGenerator(
    			inData[IN_PHENO],
    			inData[IN_MATRIX],
    			m_comparator.getStringValue(),
    			m_edge.getDoubleValue(),
    			m_out.getStringValue(),
    			m_start_cyto.getBooleanValue(),
    			m_cyto_script.getStringValue(), logger, exec);
    	
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
    	SettingsChecker.checkFileModel(m_out, DEF_OUT,
    			"Output folder is missing\nPlease choose a folder in the node dialog");
    	if(m_start_cyto.getBooleanValue()){
    		SettingsChecker.checkFileModel(m_cyto_script, DEF_CYTO_SCRIPT,
    				"Cytoscape script is missing\nPlease choose a file in the node dialog");
    	}
    	
    	//check port 0: results from phenomizer
    	TableChecker.checkColumn(inSpecs, IN_PHENO, PhenomizerNodeModel.DISEASE_ID, new DataType[]{IntCell.TYPE}, "");
    	TableChecker.checkColumn(inSpecs, IN_PHENO, PhenomizerNodeModel.SCORE, new DataType[]{DoubleCell.TYPE}, "");
    	//check of port 1: done in RunPhenomizerToNetwork -> format depends on number of rows
    	
        return new DataTableSpec[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_comparator.saveSettingsTo(settings);
        m_edge.saveSettingsTo(settings);
        m_out.saveSettingsTo(settings);
        m_start_cyto.saveSettingsTo(settings);
        m_cyto_script.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_comparator.loadSettingsFrom(settings);
    	m_edge.loadSettingsFrom(settings);
    	m_out.loadSettingsFrom(settings);
    	m_start_cyto.loadSettingsFrom(settings);
    	m_cyto_script.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_comparator.validateSettings(settings);
    	m_edge.validateSettings(settings);
    	m_out.validateSettings(settings);
    	m_start_cyto.validateSettings(settings);
    	m_cyto_script.validateSettings(settings);
    	//check if files are set (not empty default values anymore)
    	SettingsChecker.checkFileDialog(settings, CFGKEY_OUT, DEF_OUT, "Output folder is missing");
    	if(settings.getBoolean(CFGKEY_START_CYTO)){
    		SettingsChecker.checkFileDialog(settings, CFGKEY_CYTO_SCRIPT, DEF_CYTO_SCRIPT, "Cytoscape script is missing");
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

