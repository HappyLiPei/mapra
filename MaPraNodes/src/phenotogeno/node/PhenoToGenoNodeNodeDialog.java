package phenotogeno.node;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentButtonGroup;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
/**
 * <code>NodeDialog</code> for the "PhenoToGenoNode" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Marie-Sophie Friedl
 */
public class PhenoToGenoNodeNodeDialog extends DefaultNodeSettingsPane {
	
	/** settings model for the option annotation mode used in the node dialog*/
	private final SettingsModelString annotation_mode = new SettingsModelString(
			PhenoToGenoNodeNodeModel.CFGKEY_ANNOTATION_MODE, PhenoToGenoNodeNodeModel.DEFAULT_ANNOTATION_MODE);

    /**
     * New pane for configuring PhenoToGenoNode node dialog.
     */
    protected PhenoToGenoNodeNodeDialog() {
        super();
        
        addDialogComponent(new DialogComponentButtonGroup(annotation_mode, "Gene Annotation Mode", true, 
        		new String[]{"Combination of all disease scores", "Maximum disease score"}, 
        		new String[]{PhenoToGenoNodeNodeModel.ANNOTATION_MODE_MULTIPLE,
        				PhenoToGenoNodeNodeModel.ANNOTATION_MODE_MAX}));
    }
}

