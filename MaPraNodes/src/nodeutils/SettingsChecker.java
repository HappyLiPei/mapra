package nodeutils;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

public class SettingsChecker {
	
	/**
	 * checks if file was specified in the node dialog
	 * @param settings
	 * @param key
	 * @param def
	 * @param message
	 * @throws InvalidSettingsException
	 */
	
	public static void checkFileDialog(NodeSettingsRO settings, String key, String def, String message) throws InvalidSettingsException{
    	if(settings.getString(key).equals(def)){
    		throw new InvalidSettingsException(message);
    	}
	}
	
	/**
	 * checks if file was specified, method is called in the configure procedure to check if node dialog was called before execution
	 * @param model
	 * @param def
	 * @param message
	 * @throws InvalidSettingsException
	 */
	
	public static void checkFileModel(SettingsModelString model, String def, String message) throws InvalidSettingsException{
    	if((model.getStringValue()).equals(def)){
    		throw new InvalidSettingsException(message);
    	}
	}

}
