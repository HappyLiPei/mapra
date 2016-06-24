package phenomizer.node;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import nodeutils.ColumnSpecification;
import nodeutils.TableFunctions;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import phenomizer.algorithm.PValueFolder;
import phenomizer.algorithm.PhenomizerDriver;


/**
 * This is the model implementation of Phenomizer.
 * 
 *
 * @author 
 */
public class PhenomizerNodeModel extends NodeModel {
    
	/** the logger instance for writing to KNIME console and log */
    private static final NodeLogger logger = NodeLogger.getLogger(PhenomizerNodeModel.class);
    
    //settingsmodel for size of output
    /** key of the option "output size" (number of top scoring diseases in the output table) */
    protected static final String CFGKEY_OUTPUTSIZE="outputsize";
    /** default value of the option "output size" */
    protected static final int DEF_OUTPUTSIZE=20;
    /** minimum valid value of the option "output size" */
    protected static final int MIN_OUTPUTSIZE=1;
    /** maximum valid value of the option "output size" */
    protected static final int MAX_OUTPUTSIZE=Integer.MAX_VALUE;
    /** option object (settings model) to adapt the output size */
    private final SettingsModelIntegerBounded m_outputsize = new SettingsModelIntegerBounded(CFGKEY_OUTPUTSIZE, DEF_OUTPUTSIZE, MIN_OUTPUTSIZE, MAX_OUTPUTSIZE);
    
    //settingsmodel for weighted score
    /** key of the option "weights" (calculate weighted similarity score) */
    protected static final String CFGKEY_WEIGHT="weight";
    /** default value of the option "weights" */
    protected static final boolean DEF_WEIGHT=false;
    /** option object (settings model) to calculate weighted scores */
    private final SettingsModelBoolean m_weight = new SettingsModelBoolean(CFGKEY_WEIGHT, DEF_WEIGHT);
    
    //settingsmodel for calculation of p values
    /** key of the option "p value" (calculate p values) */
    protected static final String CFGKEY_PVALUE="pvalue";
    /** default value of the option "p value" */
    protected static final boolean DEF_PVALUE=false;
    /** option object (settings model) to calculate p values */
    private final SettingsModelBoolean m_pval = new SettingsModelBoolean(CFGKEY_PVALUE, DEF_PVALUE);
    
    //settignsmodel for the folder with score distributions
    /** key of the option "p value folder" (path to folder with sampled score distributions) */
    protected static final String CFGKEY_FOLDER="pval_folder";
    /** default value of the option "p value folder" */
    protected static final String DEF_FOLDER="";
    /** option object (settings model) to choose the folder with the score distributions for calculating the p values */
    private final SettingsModelString m_folder = new SettingsModelString(CFGKEY_FOLDER, DEF_FOLDER);
    
    //inport positions
    /** inport of the table containing the symptoms (from PhenoDis)*/
    private static final int INPORT_SYMPTOM_DICT =0;
    /** inport of the table containing ontology of symptoms (from PhenoDis)*/
    private static final int INPORT_ISA = 1;
    /** inport of the table containing the disease-symptom relations (from PhenoDis)*/
    private static final int INPORT_KSZ = 2;
    /** inport of the table containing the symptoms of the query */
    private static final int INPORT_QUERY = 3;
    
    /**
     * Constructor for the node model.
     * 4 inports for symptom dictionary (0), isa table (1), ksz table (2) and query table (3)
     */
    protected PhenomizerNodeModel() {
    	//4 input ports, 1 output port
        super(4, 1);
        //assure that pvalue and pvalue folder have consistent values
        if(!DEF_PVALUE){
        	m_folder.setEnabled(m_pval.getBooleanValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	
    	//read in tables and convert them into Phenomizer data structure
        LinkedList<Integer> query = TableProcessorPhenomizer.generateQuery(
        		inData[INPORT_QUERY], inData[INPORT_SYMPTOM_DICT], logger);
        LinkedList<Integer> symptoms = TableProcessorPhenomizer.generateSymptomList(
        		inData[INPORT_SYMPTOM_DICT], logger, false);
        int [][] edges = TableProcessorPhenomizer.generateEdges(
        		inData[INPORT_ISA], logger);
        HashMap<Integer,LinkedList<Integer[]>> diseases = TableProcessorPhenomizer.generateKSZ(
        		inData[INPORT_KSZ], m_weight.getBooleanValue(), logger);
        
        //run Phenomizer algorithm
    	PhenomizerDriver d = new PhenomizerDriver(query, symptoms, diseases, edges);
    	if(m_weight.getBooleanValue()){
    		//use asymmetric weighting
    		d.setPhenomizerAlgorithm(m_outputsize.getIntValue(), m_pval.getBooleanValue(), 1, m_folder.getStringValue());
    	}
		else{
			//use symmetric weighting
			d.setPhenomizerAlgorithm(m_outputsize.getIntValue(), m_pval.getBooleanValue(), 0, m_folder.getStringValue());
		}
    	LinkedList<String[]> result = d.runPhenomizer(); 
    	
        //generate table for outport
        BufferedDataTable out = TableProcessorPhenomizer.generateOutput(result, exec, inData[INPORT_KSZ]);
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
    	
    	//check port 0: symptom table
    	TableFunctions.checkColumn(inSpecs, INPORT_SYMPTOM_DICT, ColumnSpecification.SYMPTOM_ID, 
    			ColumnSpecification.SYMPTOM_ID_TYPE, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_SYMPTOM_DICT, ColumnSpecification.SYMPTOM_NAME,
    			ColumnSpecification.SYMPTOM_NAME_TYPE, null);
    	
    	//check port 1: isa table
    	TableFunctions.checkColumn(inSpecs, INPORT_ISA, ColumnSpecification.CHILD_ID, 
    			ColumnSpecification.CHILD_ID_TYPE, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_ISA, ColumnSpecification.PARENT_ID, 
    			ColumnSpecification.PARENT_ID_TYPE, null);
    	
    	//check port 2: ksz table
    	TableFunctions.checkColumn(inSpecs, INPORT_KSZ, ColumnSpecification.DISEASE_ID, 
    			ColumnSpecification.DISEASE_ID_TYPE, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_KSZ, ColumnSpecification.DISEASE_NAME, 
    			ColumnSpecification.DISEASE_NAME_TYPE, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_KSZ, ColumnSpecification.SYMPTOM_ID, 
    			ColumnSpecification.SYMPTOM_ID_TYPE, null);
    	//check for frequency annotation if weights are used
    	if(m_weight.getBooleanValue()){
    		TableFunctions.checkColumn(inSpecs, INPORT_KSZ, ColumnSpecification.FREQUENCY, 
    				ColumnSpecification.FREQUENCY_TYPE,
			"Please uncheck the option \"use frequency weights\" in the node dialog or use another input table with frequency values");
    	}
    	
    	//check port 3: query  
    	TableFunctions.checkColumn(inSpecs, INPORT_QUERY, ColumnSpecification.SYMPTOM_ID,
    			ColumnSpecification.SYMPTOM_ID_TYPE, null);
    	
    	//return output specification for table with results
        return new DataTableSpec[]{TableProcessorPhenomizer.generateOutputSpec(m_pval.getBooleanValue())};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
    	m_outputsize.saveSettingsTo(settings);
    	m_weight.saveSettingsTo(settings);
    	m_pval.saveSettingsTo(settings);
    	m_folder.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_outputsize.loadSettingsFrom(settings);
    	m_weight.loadSettingsFrom(settings);
    	m_pval.loadSettingsFrom(settings);
    	m_folder.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_outputsize.validateSettings(settings);
    	m_weight.validateSettings(settings);
    	m_pval.validateSettings(settings);
    	m_folder.validateSettings(settings);
    	//checks if files length_*.txt are available
    	if(settings.getBoolean(CFGKEY_PVALUE)){
	    	PValueFolder p = new PValueFolder(settings.getString(CFGKEY_FOLDER));
	    	for(int i=1; i<=10; i++){
	    		if(!p.checkFile(i)){
	    			throw new InvalidSettingsException("File "+
	    		PValueFolder.PART1+i+PValueFolder.PART2+" is missing in p value folder");
	    		}
	    	}
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

