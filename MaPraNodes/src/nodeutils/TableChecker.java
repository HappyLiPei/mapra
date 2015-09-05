package nodeutils;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.node.InvalidSettingsException;

public class TableChecker {
	
    /**
     * checks if input specifications from port port contains a column named colname and 
     * if specifications match data type type1 oder type2
     * @param inSpecs: specifications of incoming table
     * @param port: input port to use
     * @param colname: name of the column to check
     * @param types: allowed types for the column to check
     * @param message: error message
     * @throws InvalidSettingsException
     */
    
    public static void checkColumn(DataTableSpec[] inSpecs, int port, String colname, DataType [] types, String message) throws InvalidSettingsException{
    	//check column name
    	DataColumnSpec s =inSpecs[port].getColumnSpec(colname);
    	if(s==null){
    		if(message==null){
    			throw new InvalidSettingsException("Table at port "+port+" requires column "+colname);
    		}
    		else{
    			throw new InvalidSettingsException("Table at port "+port+" requires column "+colname+"\n"+message);
    		}
    	}
    	//check data type of column
    	boolean correct_type=false;
    	for(DataType t :types){
    		correct_type = correct_type || (s.getType()==t);
    	}
    	if(!correct_type){
    		throw new InvalidSettingsException("Table at port "+port+": Column "+colname+" is not the correct data type");
    	}
    }

}
