package metabolites.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.NodeLogger;

import nodeutils.TableFunctions;

public class TableProcessorScoreMetabolites {
	
	//TODO: implement!
	public static LinkedList<String[]> getMeasurements(BufferedDataTable table){
		return null;
	}
	
	
	/**
	 * Method to transform a KNIME table with reference metabolite data into a data structure for running
	 * ScoreMetabolite,
	 * it check metabolite types and removes types that are not of type and concentration,
	 * it checks if group, mean and standard deviation available for concentration metabolites,
	 * it checks for inconsistencies if there are several entries for a metabolite (varying missingness, 
	 * duplicate group ids, varying types)
	 * @param table
	 * 		KNIME data table with columns metabolite id, metabolite type, phenotype group, mean, standard deviation and
	 * 		missingness
	 * @param l
	 * 		NodeLogger of the ScoreMetabolites node
	 * @return
	 * 		a mapping metabolite id-> list of String arrays with metabolite id (pos 0), type (pos 1), group (pos 2),
	 * 		mean (pos 3), standard deviation (pos 4) and missingness (pos 5)
	 */
	public static HashMap<String, LinkedList<String[]>> getReferences(BufferedDataTable table, NodeLogger l){
		
		HashMap<String, LinkedList<String[]>> references = new HashMap<String, LinkedList<String[]>>((int) table.size()*3);
		
		DataTableSpec spec = table.getSpec();
		int idIndex = spec.findColumnIndex(ScoreMetabolitesNodeModel.METABOLITE_ID);
		int typeIndex = spec.findColumnIndex(ScoreMetabolitesNodeModel.METABOLITE_TYPE);
		int groupIndex = spec.findColumnIndex(ScoreMetabolitesNodeModel.PHENOTYPE_GROUP);
		int meanIndex = spec.findColumnIndex(ScoreMetabolitesNodeModel.METABOLITE_MEAN);
		int stdevIndex = spec.findColumnIndex(ScoreMetabolitesNodeModel.METABOLITE_STDEV);
		int missIndex = spec.findColumnIndex(ScoreMetabolitesNodeModel.METABOLITE_MISSINGNESS);
		
		for(DataRow r: table){
			String id = ((StringCell)r.getCell(idIndex)).getStringValue();
			String type= ((StringCell)r.getCell(typeIndex)).getStringValue();
			String group="";
			DataCell groupCell = r.getCell(groupIndex);
			if(groupCell instanceof IntCell){
				group = ((IntCell) groupCell).getIntValue()+"";
			}
			String mean="";
			DataCell meanCell = r.getCell(meanIndex);
			if(meanCell instanceof DoubleCell){
				mean =((DoubleCell) meanCell).getDoubleValue()+"";
			}
			String stdev="";
			DataCell stdevCell = r.getCell(stdevIndex);
			if(stdevCell instanceof DoubleCell){
				stdev =((DoubleCell) stdevCell).getDoubleValue()+"";
			}
			String miss=((DoubleCell) r.getCell(missIndex)).getDoubleValue()+"";
			
			//incorrect type
			if(!type.equals("binary") && !type.equals("concentration")){
				l.warn("Unkown type "+type+" for metabolite " +id+". "+
						"This node can only handle the types \"binary\" and \"concentration\" and will ignore this row.");
				continue;
			}
			//missing group, mean or stedv for concentration type
			if(type.equals("concentration") && (group.equals("") || mean.equals("") || stdev.equals(""))){
				l.warn("Missing value for metabolite " +id+" of type \"concentration\". "+
						"This node will ignore this row.");
				continue;
			}
			
			//add everything to hashmap
			String [] entry = new String [] {id, type, group, mean, stdev, miss};
			if(references.containsKey(id)){
				references.get(id).add(entry);
			}
			else{
				LinkedList<String[]> list = new LinkedList<String[]>();
				list.add(entry);
				references.put(id, list);
			}
		}
		
		//check several entries of one metabolite for consistency
		for(String key: references.keySet()){
			LinkedList<String[]> metabo= references.get(key);
			if(metabo.size()>1){
				HashSet<String> groups = new HashSet<String>(metabo.size());
				HashSet<String> types = new HashSet<String>(metabo.size());
				HashSet<String> missingness = new HashSet<String>(metabo.size());
				for(String[] entry: metabo){
					groups.add(entry[2]);
					types.add(entry[1]);
					missingness.add(entry[5]);
				}
				if(groups.size()!=metabo.size()){
					l.warn("Metabolite "+key+" has duplicate group. The second entry for the group will be ignored.");
				}
				if(types.size()>1){
					l.warn("Metabolite "+key+" has binary and concentration annotation. "
							+ "The type of the first entry will be used.");
				}
				if(missingness.size()>1){
					l.warn("Metabolite "+key+" has varying missingness annotation. "+
							"The first missigness will be used.");
				}
			}
		}
		
		return references;
	}
	
	/**
	 * method to generate the specification for the table returned by ScoreMetabolites
	 * (3 columns: metabolite id, score, significance)
	 * @return DataTable specification for the table returned by ScoreMetabolites
	 */
	public static DataTableSpec generateOutSpec(){
		
		DataColumnSpec [] spec = new DataColumnSpec[3]; 
		spec[0]=TableFunctions.makeDataColSpec(ScoreMetabolitesNodeModel.METABOLITE_ID, StringCell.TYPE);
		spec[1]=TableFunctions.makeDataColSpec(ScoreMetabolitesNodeModel.METABOLITE_SCORE, DoubleCell.TYPE);
		spec[2]=TableFunctions.makeDataColSpec(ScoreMetabolitesNodeModel.METABOLITE_SIGNIFICANCE, DoubleCell.TYPE);
		
		return new DataTableSpec(spec);
	}
	
	//TODO: implement!
	public static BufferedDataTable generateOutTable(){
		return null;
	}

}
