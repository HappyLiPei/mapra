package phenomizertonetwork;

import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentNumberEdit;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "PhenomizerToNetwork" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author 
 */

public class PhenomizerToNetworkNodeDialog extends DefaultNodeSettingsPane {
	
	final SettingsModelString comparator = new SettingsModelString(
			PhenomizerToNetworkNodeModel.CFGKEY_COMPARATOR,
			PhenomizerToNetworkNodeModel.DEF_COMPARATOR);
	
	final SettingsModelDoubleBounded edge = new SettingsModelDoubleBounded(
			PhenomizerToNetworkNodeModel.CFGKEY_EDGE,
			PhenomizerToNetworkNodeModel.DEF_EDGE,
			PhenomizerToNetworkNodeModel.MIN_EDGE,
			PhenomizerToNetworkNodeModel.MAX_EDGE);
	
	final SettingsModelString out = new SettingsModelString(
			PhenomizerToNetworkNodeModel.CFGKEY_OUT,
			PhenomizerToNetworkNodeModel.DEF_OUT);
	
	final SettingsModelBoolean start_cyto = new SettingsModelBoolean(
			PhenomizerToNetworkNodeModel.CFGKEY_START_CYTO,
			PhenomizerToNetworkNodeModel.DEF_START_CYTO);
	
	final SettingsModelString cyto_script = new SettingsModelString(
			PhenomizerToNetworkNodeModel.CFGKEY_CYTO_SCRIPT,
			PhenomizerToNetworkNodeModel.DEF_CYTO_SCRIPT);

    /**
     * New pane for configuring PhenomizerToNetwork node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    @SuppressWarnings("deprecation")
	protected PhenomizerToNetworkNodeDialog() {
        super();
        
        createNewGroup("Edges");
        setHorizontalPlacement(true);
        addDialogComponent(new DialogComponentStringSelection(comparator, "comparator", PhenomizerToNetworkNodeModel.COMPARATOR_VALUES));
        addDialogComponent(new DialogComponentNumberEdit(edge, "Edge threshold"));
        setHorizontalPlacement(false);
        createNewGroup("Output directory");
        addDialogComponent(new DialogComponentFileChooser(out, "network_out_folder", JFileChooser.OPEN_DIALOG, true));
        createNewGroup("Cytoscape");
        addDialogComponent(new DialogComponentBoolean(start_cyto, "Run Cytoscape"));
        
        final DialogComponentFileChooser dcfc = new DialogComponentFileChooser(cyto_script, "cyto_script",JFileChooser.OPEN_DIALOG, false);
        dcfc.setEnabled(start_cyto.getBooleanValue());
        cyto_script.setEnabled(start_cyto.getBooleanValue());
        addDialogComponent(dcfc);
        
        start_cyto.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				cyto_script.setEnabled(start_cyto.getBooleanValue());
				dcfc.setEnabled(start_cyto.getBooleanValue());
			}
		});
    }
}

