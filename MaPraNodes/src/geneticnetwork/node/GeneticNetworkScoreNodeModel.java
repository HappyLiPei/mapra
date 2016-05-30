package geneticnetwork.node;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

import geneticnetwork.algorithm.NetworkScoreDriver;
import nodeutils.TableFunctions;
import phenotogeno.node.PhenoToGenoNodeNodeModel;
import togeno.ScoredGene;

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
    
    //option for iteration until convergence
    /** key of the option "iterate until convergence" */
    static final String CFGKEY_ITERATION_CONVERGENCE = "iteration_convergence";
    /** default value of the option "iterate until convergence" */
    static final boolean DEFAULT_ITERATION_CONVERGENCE =false;
    /** option object (settings model) to choose iteration until convergence*/
    private final SettingsModelBoolean m_iteration_convergence =
    		new SettingsModelBoolean(CFGKEY_ITERATION_CONVERGENCE, DEFAULT_ITERATION_CONVERGENCE);
    
    //inports
    /** inport of the table containing the gene scores */
    private static final int INPORT_GENESCORES=0;
    /** inport of the table containing the genetic network (table of edges)*/
    private static final int INPORT_NETWORK=1;
    
    //column names
    /** name of the column gene1 = gene id corresponding to a node of the network, the node is end point of an (undirected) edge) */
    public static final String GENE1 ="gene1";
    /** name of the column gene2 = gene id corresponding to a node of the network, the node is end point of an (undirected) edge) */
    public static final String GENE2 ="gene2";
    /** name of the column edgeweight = column with an integer edge weight for each weight, only if the edge weight option is used*/
    public static final String EDGEWEIGHT="weight";

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
        
        HashMap<String, Double> scores = GeneticNetworkScoreTableProcessor.
        		getGeneScores(inData[INPORT_GENESCORES], logger);
        String [][] network = GeneticNetworkScoreTableProcessor.
        		getNetworkEdges(inData[INPORT_NETWORK], m_edge_weights.getBooleanValue(), logger);
        
        NetworkScoreDriver driver = new NetworkScoreDriver(network, scores);
        driver.SetNetworkScoreAlgorithm(m_restart_probability.getDoubleValue(),
        		m_iteration_convergence.getBooleanValue(), m_number_of_iterations.getIntValue());
        LinkedList<ScoredGene> result = driver.runNetworkScoreAlgorithm();
        
        logger.info("Random Walk finished after "+driver.getNumberOfIterationsDone()+" iterations. "
        		+ "Convergence is "+driver.getConvergenceNorm()+".");
        
        BufferedDataTable resTable = GeneticNetworkScoreTableProcessor.generateOutputTable(result, exec);
        return new BufferedDataTable[]{resTable};
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
        
    	//check port 0: gene scores from phenotogeno
    	TableFunctions.checkColumn(inSpecs, INPORT_GENESCORES, PhenoToGenoNodeNodeModel.GENE_ID,
    			new DataType[]{StringCell.TYPE}, null);
    	TableFunctions.checkColumn(inSpecs, INPORT_GENESCORES, PhenoToGenoNodeNodeModel.GENE_PROBABILITY,
    			new DataType[]{DoubleCell.TYPE}, null);
    	//check port 1: genetic network
    	TableFunctions.checkColumn(inSpecs, INPORT_NETWORK, GENE1, 
    			new DataType[]{StringCell.TYPE}, null);
       	TableFunctions.checkColumn(inSpecs, INPORT_NETWORK, GENE2, 
    			new DataType[]{StringCell.TYPE}, null);
       	if(m_edge_weights.getBooleanValue()){
       		TableFunctions.checkColumn(inSpecs, INPORT_NETWORK, EDGEWEIGHT, new DataType[] {IntCell.TYPE}, 
       				"Please uncheck the option \"Use Weighted Edges\" in the node dialog or use another network table with edge weights");
       	}

        return new DataTableSpec[]{GeneticNetworkScoreTableProcessor.generateOutputSpec()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {

        m_edge_weights.saveSettingsTo(settings);
        m_restart_probability.saveSettingsTo(settings);
        m_number_of_iterations.saveSettingsTo(settings);
        m_iteration_convergence.saveSettingsTo(settings);
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
        m_iteration_convergence.loadSettingsFrom(settings);
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
        m_iteration_convergence.validateSettings(settings);
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

