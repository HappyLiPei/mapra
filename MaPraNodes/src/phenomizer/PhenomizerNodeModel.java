package phenomizer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

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
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import algorithm.AlgoPheno;
import algorithm.TableProcessor;


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
    public static final String CHILD_ID = "child_id";
    public static final String PARENT_ID = "parent_id";
    public static final String SCORE = "score";
    public static final String P_VALUE = "p_value";
    
    //settingsmodels
    protected static final String CFGKEY_OUTPUTSIZE="outputsize";
    protected static final int DEF_OUTPUTSIZE=20;
    protected static final int MIN_OUTPUTSIZE=1;
    protected static final int MAX_OUTPUTSIZE=Integer.MAX_VALUE;
    private final SettingsModelIntegerBounded m_outputsize = new SettingsModelIntegerBounded(CFGKEY_OUTPUTSIZE, DEF_OUTPUTSIZE, MIN_OUTPUTSIZE, MAX_OUTPUTSIZE);

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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

    	logger.info("generateQuery");
        LinkedList<Integer> query = TableProcessor.generateQuery(inData[INPORT_QUERY], inData[INPORT_SYMPTOM_DICT], logger);
        logger.info("generateSymptomList");
        LinkedList<Integer> symptoms = TableProcessor.generateSymptomList(inData[INPORT_SYMPTOM_DICT]);
        logger.info("generateEdges");
        int [][] edges = TableProcessor.generateEdges(inData[INPORT_ISA]);
        logger.info("generateKSZ");
        HashMap<Integer,LinkedList<Integer>> diseases = TableProcessor.generateKSZ(inData[INPORT_KSZ]);
        
//        logger.info("Test generateQuery()");
//        for(Integer i: query){
//        	logger.info(""+i);
//        }
//        
//        logger.info("Test generateQSymptomList()");
//        for(Integer i: symptoms){
//        	logger.info(""+i);
//        }
//        
//        logger.info("Test generateEdges()");
//        for(int [] edge:edges){
//        	logger.info("child "+edge[0]+" parent "+edge[1]);
//        }
//        
//        logger.info("Test generateKSZ()");
//        for(Integer i: diseases.keySet()){
//        	logger.info("Disease "+i);
//        	for(Integer j: diseases.get(i)){
//        		logger.info("Symptom "+j);
//        	}
//        }
//        logger.info(m_outputsize.getIntValue());
        
        AlgoPheno.setInput(query, symptoms, diseases, edges);
        LinkedList<String[]> result = AlgoPheno.runPhenomizer(m_outputsize.getIntValue());
        
//        for(String [] entry: result){
//        	for(String s : entry){
//        		logger.info(s);
//        	}
//        }
        
        logger.info("generate output");
        BufferedDataTable out = TableProcessor.generateOutput(result, exec, inData[INPORT_KSZ]);
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
    	
    	logger.info("configure");
    	//check port 0: symptom table
    	checkColumn(inSpecs, INPORT_SYMPTOM_DICT, SYMPTOM_ID, IntCell.TYPE, LongCell.TYPE);
    	checkColumn(inSpecs, INPORT_SYMPTOM_DICT, SYMPTOM_NAME, StringCell.TYPE, null);
    	//check port 1: isa table
    	checkColumn(inSpecs, INPORT_ISA, CHILD_ID, IntCell.TYPE, LongCell.TYPE);
    	checkColumn(inSpecs, INPORT_ISA, PARENT_ID, IntCell.TYPE, LongCell.TYPE);
    	//TODO: check source if required
    	//check port 2: ksz table
    	checkColumn(inSpecs, INPORT_KSZ, DISEASE_ID, IntCell.TYPE, LongCell.TYPE);
    	checkColumn(inSpecs, INPORT_KSZ, DISEASE_NAME, StringCell.TYPE, null);
    	checkColumn(inSpecs, INPORT_KSZ, SYMPTOM_ID, IntCell.TYPE, LongCell.TYPE);
    	//check port 3: query  
    	checkColumn(inSpecs, INPORT_QUERY, SYMPTOM_ID, IntCell.TYPE, LongCell.TYPE);

        return new DataTableSpec[]{generateOutputSpec()};
    }
    
    /**
     * checks if input specifications from port port contains a column named colname and 
     * if specifications match data type type1 oder type2
     * @param inSpecs
     * @param port
     * @param colname
     * @param type1
     * @param type2
     * @throws InvalidSettingsException
     */
    
    private void checkColumn(DataTableSpec[] inSpecs, int port, String colname, DataType type1, DataType type2) throws InvalidSettingsException{
    	
    	DataColumnSpec s =inSpecs[port].getColumnSpec(colname);
    	if(s==null){
    		throw new InvalidSettingsException("Table at port "+port+" requires column "+colname);
    	}
    	if((s.getType() != type1 && s.getType() != type2)){
    		throw new InvalidSettingsException("Table at port "+port+": Column "+colname+" is not the correct data type");
    	}
    }
    
	/**
	 * generates specifications for outport table
	 * col 0 : disease_id (int)
	 * col 1 : disease_name (string)
	 * col 2 : score (double)
	 * col 3 : p-value (double)
	 */
    public static DataTableSpec generateOutputSpec(){
    	DataColumnSpec [] colspecs = new DataColumnSpec [4];
    	colspecs[0] = new DataColumnSpecCreator(DISEASE_ID, IntCell.TYPE).createSpec();
    	colspecs[1] = new DataColumnSpecCreator(DISEASE_NAME, StringCell.TYPE).createSpec();
    	colspecs[2] = new DataColumnSpecCreator(SCORE, DoubleCell.TYPE).createSpec();
    	colspecs[3] = new DataColumnSpecCreator(P_VALUE, DoubleCell.TYPE).createSpec();
    	
    	return new DataTableSpec(colspecs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
    	m_outputsize.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_outputsize.loadSettingsFrom(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_outputsize.validateSettings(settings);
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

