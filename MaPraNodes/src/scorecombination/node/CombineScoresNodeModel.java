package scorecombination.node;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;

import nodeutils.ColumnSpecification;
import nodeutils.TableFunctions;
import scorecombination.algo.CombineScoresDriver;
import togeno.ScoredGene;

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
    	
    	//driver for running the algorithm
    	CombineScoresDriver driver = new CombineScoresDriver();
    	
    	//generate input data and add it to the driver
    	for(int i=0; i<numberOfInPorts; i++){
    		HashMap<String,Double> scores = CombineScoresTableProcessor.getScoreSet(inData[i], i, logger);
    		System.out.println(i);
    		for(String key:scores.keySet()){
    			System.out.println(key+"\t"+scores.get(key));
    		}
    		driver.addInput(scores);
    	}
    	
    	//run the algorithm
    	LinkedList<ScoredGene> result = driver.runCombineScores();
    	
    	//return a KNIME table with the calculated gene scores
        BufferedDataTable out = CombineScoresTableProcessor.generateOutputTable(result, exec);
        return new BufferedDataTable[]{out};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
    }

    /**
     * method to check the tables at all input Ports, dependent on the variable numberOfInPorts     *
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

