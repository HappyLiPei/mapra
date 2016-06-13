package scorecombination.node;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

/**
 * <code>NodeDialog</code> for the "CombineScores" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Marie-Sophie Friedl
 */
public class CombineScoresNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring CombineScores node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected CombineScoresNodeDialog() {
        super();                    
    }
}

