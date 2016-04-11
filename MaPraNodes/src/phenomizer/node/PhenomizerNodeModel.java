package phenomizer.node;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import nodeutils.TableFunctions;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
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
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger.getLogger(PhenomizerNodeModel.class);
    
    //inport positions
    private static final int INPORT_SYMPTOM_DICT =0;
    private static final int INPORT_ISA = 1;
    private static final int INPORT_KSZ = 2;
    private static final int INPORT_QUERY = 3;
    
    //column names
    public static final String SYMPTOM_ID ="symptom_id";
    public static final String SYMPTOM_NAME = "symptom_name";
    public static final String DISEASE_ID ="disease_id";
    public static final String DISEASE_NAME = "disease";
    public static final String FREQUENCY ="frequency";
    public static final String CHILD_ID = "child_id";
    public static final String PARENT_ID = "parent_id";
    public static final String SCORE = "score";
    public static final String P_VALUE = "p_value";
    public static final String SIGNIFICANCE = "significance";
    
    //settingsmodels:
    //size of output
    protected static final String CFGKEY_OUTPUTSIZE="outputsize";
    protected static final int DEF_OUTPUTSIZE=20;
    protected static final int MIN_OUTPUTSIZE=1;
    protected static final int MAX_OUTPUTSIZE=Integer.MAX_VALUE;
    private final SettingsModelIntegerBounded m_outputsize = new SettingsModelIntegerBounded(CFGKEY_OUTPUTSIZE, DEF_OUTPUTSIZE, MIN_OUTPUTSIZE, MAX_OUTPUTSIZE);
    //use weights for similarity score
    protected static final String CFGKEY_WEIGHT="weight";
    protected static final boolean DEF_WEIGHT=false;
    private final SettingsModelBoolean m_weight = new SettingsModelBoolean(CFGKEY_WEIGHT, DEF_WEIGHT);
    //calculate p values
    protected static final String CFGKEY_PVALUE="pvalue";
    protected static final boolean DEF_PVALUE=false;
    private final SettingsModelBoolean m_pval = new SettingsModelBoolean(CFGKEY_PVALUE, DEF_PVALUE);
    //folder to pvalues
    protected static final String CFGKEY_FOLDER="pval_folder";
    protected static final String DEF_FOLDER="";
    private final SettingsModelString m_folder = new SettingsModelString(CFGKEY_FOLDER, DEF_FOLDER);
    
    /**
     * Constructor for the node model.
     * 4 inports:
     * 	-symptom dictionary (0)
     * 	-isa table (1)
     * 	-ksz table (2)
     * 	-query table (3)
     */
    protected PhenomizerNodeModel() {
        super(4, 1);
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
    	
    	//read in tables from inports into phenomizer data structure
        LinkedList<Integer> query = TableProcessorPhenomizer.generateQuery(inData[INPORT_QUERY], inData[INPORT_SYMPTOM_DICT], logger);
        LinkedList<Integer> symptoms = TableProcessorPhenomizer.generateSymptomList(inData[INPORT_SYMPTOM_DICT]);
        int [][] edges = TableProcessorPhenomizer.generateEdges(inData[INPORT_ISA]);
        HashMap<Integer,LinkedList<Integer[]>> diseases = TableProcessorPhenomizer.generateKSZ(inData[INPORT_KSZ], m_weight.getBooleanValue());
        
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
    	TableFunctions.checkColumn(inSpecs, INPORT_SYMPTOM_DICT, SYMPTOM_ID, new DataType[]{IntCell.TYPE, LongCell.TYPE}, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_SYMPTOM_DICT, SYMPTOM_NAME, new DataType[]{StringCell.TYPE}, null);
    	//check port 1: isa table
    	TableFunctions.checkColumn(inSpecs, INPORT_ISA, CHILD_ID, new DataType[]{IntCell.TYPE, LongCell.TYPE}, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_ISA, PARENT_ID, new DataType[]{IntCell.TYPE, LongCell.TYPE}, null);
    	//check port 2: ksz table
    	TableFunctions.checkColumn(inSpecs, INPORT_KSZ, DISEASE_ID, new DataType[]{IntCell.TYPE, LongCell.TYPE}, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_KSZ, DISEASE_NAME, new DataType[]{StringCell.TYPE}, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_KSZ, SYMPTOM_ID, new DataType[]{IntCell.TYPE, LongCell.TYPE}, null);
    	if(m_weight.getBooleanValue()){
    		TableFunctions.checkColumn(inSpecs, INPORT_KSZ, FREQUENCY, new DataType[]{StringCell.TYPE},
			"Please uncheck the option \"use frequency weights\" in the node dialog or use another input table with frequency values");
    	}
    	//check port 3: query  
    	TableFunctions.checkColumn(inSpecs, INPORT_QUERY, SYMPTOM_ID, new DataType[]{IntCell.TYPE, LongCell.TYPE},null);

        return new DataTableSpec[]{generateOutputSpec(m_pval.getBooleanValue())};
    }
    
	/**
	 * generates specifications for outport table
	 * @param pvalue: pvalue = true -> displays p values and significance in the out port table
	 * @return: column format of output table
	 * col 0 : disease_id (int)
	 * col 1 : disease_name (string)
	 * col 2 : score (double)
	 * if pvalue=true: 2 additional columns
	 * col 3 : p-value (double)
	 * col 4: significance (string)
	 */
    public static DataTableSpec generateOutputSpec(boolean pvalue){
    	
    	DataColumnSpec [] colspecs = new DataColumnSpec[3];
    	if(pvalue){
    		colspecs= new DataColumnSpec[5];
    	}
    	colspecs[0] = new DataColumnSpecCreator(DISEASE_ID, IntCell.TYPE).createSpec();
    	colspecs[1] = new DataColumnSpecCreator(DISEASE_NAME, StringCell.TYPE).createSpec();
    	colspecs[2] = new DataColumnSpecCreator(SCORE, DoubleCell.TYPE).createSpec();
    	if(pvalue){
        	colspecs[3] = new DataColumnSpecCreator(P_VALUE, DoubleCell.TYPE).createSpec();
        	colspecs[4] = new DataColumnSpecCreator(SIGNIFICANCE, StringCell.TYPE).createSpec();
    	}
    	
    	return new DataTableSpec(colspecs);
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
    		logger.info(settings.getString(CFGKEY_FOLDER));
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

