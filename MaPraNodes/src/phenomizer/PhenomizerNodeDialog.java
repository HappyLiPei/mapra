package phenomizer;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentNumberEdit;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "Phenomizer" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author 
 */

public class PhenomizerNodeDialog extends DefaultNodeSettingsPane {
	
	private final SettingsModelIntegerBounded outputsize = new SettingsModelIntegerBounded(
			PhenomizerNodeModel.CFGKEY_OUTPUTSIZE,	PhenomizerNodeModel.DEF_OUTPUTSIZE,
			PhenomizerNodeModel.MIN_OUTPUTSIZE, PhenomizerNodeModel.MAX_OUTPUTSIZE);
	private final SettingsModelBoolean weight = new SettingsModelBoolean(
			PhenomizerNodeModel.CFGKEY_WEIGHT, PhenomizerNodeModel.DEF_WEIGHT);
	
    /**
     * New pane for configuring Phenomizer node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected PhenomizerNodeDialog() {
        super();
        
        createNewGroup("Output");
        addDialogComponent(new DialogComponentNumberEdit(outputsize, "Number of diseases in output",5));
        addDialogComponent(new DialogComponentBoolean(weight, "Use frequency weights"));
                    
    }
}

