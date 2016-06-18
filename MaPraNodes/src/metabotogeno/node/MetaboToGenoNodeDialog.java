package metabotogeno.node;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentButtonGroup;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "MetaboToGeno" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Marie-Sophie Friedl
 */
public class MetaboToGenoNodeDialog extends DefaultNodeSettingsPane {
	
	private final SettingsModelString annoMode = new SettingsModelString(
			MetaboToGenoNodeModel.CFGKEY_ANNOTATION_MODE, MetaboToGenoNodeModel.DEFAULT_ANNOTATION_MODE);

    /**
     * New pane for configuring MetaboToGeno node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected MetaboToGenoNodeDialog() {
        super();
        
        addDialogComponent(new DialogComponentButtonGroup(annoMode, "Gene Annotation Mode", true, 
        		new String[]{"Combination of all metabolite scores", "Maximum metabolite score"}, 
        		new String[]{MetaboToGenoNodeModel.ANNOTATION_MODE_MULTIPLE,
        				MetaboToGenoNodeModel.ANNOTATION_MODE_MAX}));
    }
}

