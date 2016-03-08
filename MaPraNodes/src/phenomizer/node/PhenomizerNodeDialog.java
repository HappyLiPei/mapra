package phenomizer.node;

import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentLabel;
import org.knime.core.node.defaultnodesettings.DialogComponentNumberEdit;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import phenomizer.algorithm.PValueFolder;

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
	
	final SettingsModelIntegerBounded outputsize = new SettingsModelIntegerBounded(
			PhenomizerNodeModel.CFGKEY_OUTPUTSIZE,	PhenomizerNodeModel.DEF_OUTPUTSIZE,
			PhenomizerNodeModel.MIN_OUTPUTSIZE, PhenomizerNodeModel.MAX_OUTPUTSIZE);
	
	final SettingsModelBoolean weight = new SettingsModelBoolean(
			PhenomizerNodeModel.CFGKEY_WEIGHT, PhenomizerNodeModel.DEF_WEIGHT);
	
	final SettingsModelBoolean pval = new SettingsModelBoolean(
			PhenomizerNodeModel.CFGKEY_PVALUE, PhenomizerNodeModel.DEF_PVALUE);
	
	final SettingsModelString folder = new SettingsModelString(
			PhenomizerNodeModel.CFGKEY_FOLDER, PhenomizerNodeModel.DEF_FOLDER);
	
    /**
     * New pane for configuring Phenomizer node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    @SuppressWarnings("deprecation")
	protected PhenomizerNodeDialog() {
        super();
        
        createNewGroup("Output");
        addDialogComponent(new DialogComponentNumberEdit(outputsize, "Number of diseases in output",5));
        createNewGroup("Weights");
        addDialogComponent(new DialogComponentBoolean(weight, "Use frequency weights"));
        createNewGroup("P values");
        addDialogComponent(new DialogComponentBoolean(pval, "Calculate p values"));
        
        final DialogComponentFileChooser dcfc = new DialogComponentFileChooser(folder, "history_pval_folder", JFileChooser.OPEN_DIALOG, true);
        dcfc.setBorderTitle("Choose folder with p value files");
        addDialogComponent(dcfc);
        final DialogComponentLabel l = new DialogComponentLabel("");
        addDialogComponent(l);
        	folder.setEnabled(pval.getBooleanValue());
        	dcfc.setEnabled(pval.getBooleanValue());
        	
        pval.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				folder.setEnabled(pval.getBooleanValue());
				dcfc.setEnabled(pval.getBooleanValue());
			}
		});

        folder.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(pval.getBooleanValue()){
					PValueFolder p = new PValueFolder(folder.getStringValue());
					boolean loop_break = false;
			    	for(int i=1; i<=10; i++){
			    		if(!p.checkFile(i)){
			    			l.setText("File "+PValueFolder.PART1+i+PValueFolder.PART2+" is missing");
			    			loop_break=true;
			    			break;
			    		}
			    	}
			    	if(!loop_break){
			    		l.setText("");
			    	}
				}
			}
		});
                    
    }
}

