package phenomizertoclustering;

import javax.swing.JFileChooser;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "PhenomizerToClustering" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author 
 */

public class PhenomizerToClusteringNodeDialog extends DefaultNodeSettingsPane {
	
	final SettingsModelString dmat = new SettingsModelString(
			PhenomizerToClusteringNodeModel.CFGKEY_DMAT,
			PhenomizerToClusteringNodeModel.DEF_DMAT);
				
	final SettingsModelString ofolder = new SettingsModelString(
			PhenomizerToClusteringNodeModel.CFGKEY_OFOLDER,
			PhenomizerToClusteringNodeModel.DEF_OFOLDER);
	
	final SettingsModelString rscriptexec = new SettingsModelString(
			PhenomizerToClusteringNodeModel.CFGKEY_RSCRIPTEXEC,
			PhenomizerToClusteringNodeModel.DEF_RSCRIPTEXEC);

    /**
     * New pane for configuring PhenomizerToClustering node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected PhenomizerToClusteringNodeDialog() {
        super();
        createNewGroup("Input");
        addDialogComponent(new DialogComponentFileChooser(dmat, "clust_dist_matrix", JFileChooser.OPEN_DIALOG, false));
        createNewGroup("Output");
        addDialogComponent(new DialogComponentFileChooser(ofolder, "clustering_ofolder", JFileChooser.OPEN_DIALOG, true));
        createNewGroup("RScript");
        addDialogComponent(new DialogComponentFileChooser(rscriptexec, "path_rscript_exec",JFileChooser.OPEN_DIALOG, false));        
    }
}
