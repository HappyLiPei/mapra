package nodeutils;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.InvalidSettingsException;

public class TableFunctions {
	
    /**
     * checks if the input specifications from port "port" contain a column named "colname" and 
     * and checks if the specifications match any of the data types in "types"
     * @param inSpecs specifications of incoming table
     * @param port input port to use
     * @param colname name of the column to check
     * @param types allowed types for the column to check
     * @param message error message
     * @throws InvalidSettingsException
     */
    
    public static void checkColumn(DataTableSpec[] inSpecs, int port, String colname, DataType [] types, String message)
    		throws InvalidSettingsException{
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
    	String allowedTypes="";
    	for(DataType t :types){
    		correct_type = correct_type || (s.getType()==t);
    		if(!allowedTypes.equals("")){
    			allowedTypes+="/";
    		}
    		allowedTypes+=t.toString();
    	}
    	if(!correct_type){
    		throw new InvalidSettingsException("Table at port "+port+": Column "+colname+" is not the correct data type"+
    				" (requires "+allowedTypes+" column)");
    	}
    }
    
    /**
     * method to generate a data column specification for a KNIME table
     * @param name name of the column
     * @param type data type of the column
     * @return DataColumnSpec object specifying a column of a KNIME table
     */
    public static DataColumnSpec makeDataColSpec(String name, DataType type){
    	//generate factory for column specification
    	DataColumnSpecCreator creator = new DataColumnSpecCreator(name, type);    	
    	return creator.createSpec();
    }
    
    /**
     * method to extract a String value from a row of a KNIME table
     * @param row DataRow of a KNIME table
     * @param index index of the cell with a String value,
     * 		the corresponding cell can be of type {@link StringCell}, {@link DoubleCell}, {@link IntCell} and {@link LongCell}
     * @return String value stored in row "row" at position "index"
     */
    public static String getStringValue(DataRow row, int index){

    	DataCell cellWithString = row.getCell(index);
    	
    	//String cell
    	if(cellWithString instanceof StringCell){
    		String res = ((StringCell) cellWithString).getStringValue();
    		return res;
    	}
    	//Double cell
    	else if(cellWithString instanceof DoubleCell){
    		double res = ((DoubleCell) cellWithString).getDoubleValue();
    		return Double.toString(res);
    	}
    	//Int cell
    	else if(cellWithString instanceof IntCell){
    		int res  = ((IntCell) cellWithString).getIntValue();
    		return Integer.toString(res);
    	}
    	//Long cell
    	else if(cellWithString instanceof LongCell){
    		long res = ((LongCell) cellWithString).getLongValue();
    		return Long.toString(res);
    	}
    	//invalid cell type
    	else{
    		return null;
    	}
    }
    
    /**
     * method to extract an integer value from a row of a KNIME table
     * @param row DataRow of a KNIME table
     * @param index index of the cell with a String value,
     * 		the corresponding cell can be of type {@link StringCell}, {@link LongCell} or {@link DoubleCell}
     * @return Integer value stored in row "row" at position "index"
     */
    public static Integer getIntegerValue(DataRow row, int index){

    	DataCell cellWithInteger = row.getCell(index);
    	
    	//int cell
    	if(cellWithInteger instanceof IntCell){
    		Integer res = ((IntCell) cellWithInteger).getIntValue();
    		return res;
    	}
    	//long cell
    	else if(cellWithInteger instanceof LongCell){
    		long longValue = ((LongCell) cellWithInteger).getLongValue();
    		try{
    			Integer res = Math.toIntExact(longValue);
    			return res;
    		}
    		// arithmetic exception -> long cannot be parsed into int
    		catch(ArithmeticException e){
    			return null;
    		}
    	}
    	//invalid cell type
    	else{
    		return null;
    	}
    }
    
    /**
     * method to extract a double value from a row of a KNIME table
     * @param row DataRow of a KNIME table
     * @param index index of the cell with a String value,
     * 		the corresponding cell has to be of type {@link DoubleCell}
     * @return Double value stored in row "row" at position "index"
     */
    public static Double getDoubleValue(DataRow row, int index){
    	
    	DataCell cellWithDouble = row.getCell(index);
    	
    	//double cell
    	if(cellWithDouble instanceof DoubleCell){
    		double res = ((DoubleCell) cellWithDouble).getDoubleValue();
    		return res;
    	}
    	//invalid cell type
    	else{
    		return null;
    	}
    }
    
    /**
     * method to create a data cell from a {@link String} for a specific column of a KNIME table
     * @param spec column specification of the target table
     * @param index position of the column in the target table
     * @param content String that specifies the cell content
     * @return {@link DataCell} for the column at position "index" that fits to the specifications in "spec" and that
     * 		stores the information "content"
     */
    public static DataCell generateDataCellFor( DataTableSpec spec, int index, String content){
    	
    	DataType type = spec.getColumnSpec(index).getType();
    	if(type==StringCell.TYPE){
    		return new StringCell(content);
    	}
    	return null;
    }
    
    /**
     * method to create a data cell from a {@link Double} for a specific column of a KNIME table
     * @param spec column specification of the target table
     * @param index position of the column in the target table
     * @param content String that specifies the cell content
     * @return {@link DataCell} for the column at position "index" that fits to the specifications in "spec" and that
     * 		stores the information "content"
     */
    public static DataCell generateDataCellFor( DataTableSpec spec, int index, double content){
    	
    	DataType type = spec.getColumnSpec(index).getType();
    	if(type==DoubleCell.TYPE){
    		return new DoubleCell(content);
    	}
    	return null;
    }

}
