package geneticnetwork.node;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentNumberEdit;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "GeneticNetworkScore" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Marie-Sophie Friedl
 */
public class GeneticNetworkScoreNodeDialog extends DefaultNodeSettingsPane {
	
    private final SettingsModelBoolean edge_weights =
    		new SettingsModelBoolean(GeneticNetworkScoreNodeModel.CFGKEY_EDGE_WEIGHTS,
    				GeneticNetworkScoreNodeModel.DEFAULT_EDGE_WEIGHTS);
	
    private final SettingsModelDoubleBounded restart_probability =
    		new SettingsModelDoubleBounded(GeneticNetworkScoreNodeModel.CFGKEY_RESTART_PROBABILITY,
    				GeneticNetworkScoreNodeModel.DEFAULT_RESTART_PROBABILITY,
    				GeneticNetworkScoreNodeModel.MIN_RESTART_PROBABILITY,
    				GeneticNetworkScoreNodeModel.MAX_RESTART_PROBABILITY);
    
    private final SettingsModelIntegerBounded number_of_iterations =
    		new SettingsModelIntegerBounded(GeneticNetworkScoreNodeModel.CFGKEY_NUMBER_OF_ITERATIONS,
    				GeneticNetworkScoreNodeModel.DEFAULT_NUMBER_OF_ITERATIONS,
    				GeneticNetworkScoreNodeModel.MIN_NUMBER_OF_ITERATIONS,
    				GeneticNetworkScoreNodeModel.MAX_NUMBER_OF_ITERATIONS);

    /**
     * New pane for configuring GeneticNetworkScore node dialog.
     */
    protected GeneticNetworkScoreNodeDialog() {
        super();
        createNewGroup("Network");
        addDialogComponent(new DialogComponentBoolean(edge_weights, "Use Weighted Edges"));
        createNewGroup("Random Walk with Restart");
        addDialogComponent(new DialogComponentNumberEdit(restart_probability, "Restart Probability", 6));
        addDialogComponent(new DialogComponentNumber(number_of_iterations, "Number of Iterations",1, 6));                    
    }
}

