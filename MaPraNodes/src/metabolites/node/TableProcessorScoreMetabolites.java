package metabolites.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;

import metabolites.types.ScoredMetabolite;
import nodeutils.TableFunctions;

public class TableProcessorScoreMetabolites {
	
	/**
	 * Method to transform a KNIME table with metabolite measurements from a patient into a data structure for running
	 * ScoreMetabolites,
	 * it checks for uniform group annotation and outputs a warning if the group information is inconsistent (will be 
	 * 	handled by DataTransformer),
	 * it checks for duplicate metabolite ids and outputs a warning if a duplicate occurs (will be handled by 
	 * 	DataTransformer),
	 * it checks if a reference metabolite is available for each measurement and outputs a warning if there is no reference
	 * 	(will be handled by DataTransformer)
	 * @param table
	 * 		KNIME table with the metabolite measurements containing the columns metabolite id, concentration and phenotype
	 * 		group
	 * @param l
	 * 		NodeLogger of ScoreMetabolites node
	 * @param reference
	 * 		mapping returned by getReferences, it maps metabolite ids to reference values
	 * 		it is a mapping metabolite id-> list of String arrays with metabolite id (pos 0), type (pos 1), group (pos 2),
	 * 		mean (pos 3), standard deviation (pos 4) and missingness (pos 5)
	 * @return
	 * 		LinkedList of String arrays with metabolite id (pos 0), measured concentration (pos 1) and group number
	 * 		(pos 2)
	 */
	public static LinkedList<String[]> getMeasurements(BufferedDataTable table, NodeLogger l,
			HashMap<String, LinkedList<String[]>> reference){
		
		LinkedList<String[]> patient = new LinkedList<String[]>();
		
		DataTableSpec spec = table.getDataTableSpec();
		int idIndex = spec.findColumnIndex(ScoreMetabolitesNodeModel.METABOLITE_ID);
		int concIndex = spec.findColumnIndex(ScoreMetabolitesNodeModel.METABOLITE_CONCENTRATION);
		int groupIndex = spec.findColumnIndex(ScoreMetabolitesNodeModel.PHENOTYPE_GROUP);
		
		//data structures for checking consistency
		HashSet<String> allIds = new HashSet<String>((int) table.size()*3);
		HashSet<String> allGroups = new HashSet<String> ((int) table.size()*3);
		
		for(DataRow r: table){
			String id = ((StringCell) r.getCell(idIndex)).getStringValue();
			String conc="";
			DataCell concCell = r.getCell(concIndex);
			if(concCell instanceof DoubleCell){
				conc = ((DoubleCell) concCell).getDoubleValue()+"";
			}
			String group= ((IntCell) r.getCell(groupIndex)).getIntValue()+"";
			patient.add(new String[]{id, conc, group});
			
			if(!allIds.add(id)){
				l.warn("Duplicate metabolite id "+id+" in measured metabolites. "
						+ "This node will only use the first measurement.");
			}
			allGroups.add(group);
		}
		
		if(allGroups.size()>1){
			l.warn("Varying group number for the measured metabolites "+"This node will treat all measuredments as "
					+ "if from the majority group");
		}
		
		//check if measurements fit the reference
		for(String [] m: patient){
			if(reference.containsKey(m[0])){
				LinkedList<String[]> metabo = reference.get(m[0]);
				if(metabo.getFirst()[1].equals("concentration")){
					boolean found = false;
					for(String[] m2: metabo){
						if(m[2].equals(m2[2])){
							found = true;
						}
					}
					if(!found){
						l.warn("Group "+m[2]+" not available for metabolite "+m[0]+". The measurement will be ignored.");
					}
				}
			}
			else{
				l.warn("Measured metabolite "+m[0]+" is not part of the reference. It will be ignored.");
			}
		}
		
		
		return patient;
	}
	
	
	/**
	 * Method to transform a KNIME table with reference metabolite data into a data structure for running
	 * ScoreMetabolite,
	 * it check metabolite types and removes types that are not of type and concentration,
	 * it checks if group, mean and standard deviation available for concentration metabolites and removes the row
	 * 	if it does not provide all information
	 * it checks for inconsistencies if there are several entries for a metabolite (varying missingness, 
	 * duplicate group ids, varying types) -> this will be handled by the DataTransformer
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
	 * (4 columns: metabolite id, type, score, significance + 1 optional column: metabolite name)
	 * @return DataTable specification for the table returned by ScoreMetabolites
	 */
	public static DataTableSpec generateOutSpec(DataTableSpec specReference){
		
		boolean hasNameColumn =false;
		if(specReference.findColumnIndex(ScoreMetabolitesNodeModel.METABOLITE_NAME)!=-1){
			hasNameColumn = true;
		}
		
		DataColumnSpec [] spec = null;
		if(hasNameColumn){
			spec = new DataColumnSpec[5]; 
			spec[0]=TableFunctions.makeDataColSpec(ScoreMetabolitesNodeModel.METABOLITE_ID, StringCell.TYPE);
			spec[1]=TableFunctions.makeDataColSpec(ScoreMetabolitesNodeModel.METABOLITE_NAME, StringCell.TYPE);
			spec[2]=TableFunctions.makeDataColSpec(ScoreMetabolitesNodeModel.METABOLITE_TYPE, StringCell.TYPE);
			spec[3]=TableFunctions.makeDataColSpec(ScoreMetabolitesNodeModel.METABOLITE_SCORE, DoubleCell.TYPE);
			spec[4]=TableFunctions.makeDataColSpec(ScoreMetabolitesNodeModel.METABOLITE_SIGNIFICANCE, DoubleCell.TYPE);
		}
		else{
			spec = new DataColumnSpec[4]; 
			spec[0]=TableFunctions.makeDataColSpec(ScoreMetabolitesNodeModel.METABOLITE_ID, StringCell.TYPE);
			spec[1]=TableFunctions.makeDataColSpec(ScoreMetabolitesNodeModel.METABOLITE_TYPE, StringCell.TYPE);
			spec[2]=TableFunctions.makeDataColSpec(ScoreMetabolitesNodeModel.METABOLITE_SCORE, DoubleCell.TYPE);
			spec[3]=TableFunctions.makeDataColSpec(ScoreMetabolitesNodeModel.METABOLITE_SIGNIFICANCE, DoubleCell.TYPE);
		}
		
		return new DataTableSpec(spec);
	}
	
	/**
	 * method to transform a list of scored metabolites into a KNIME table
	 * @param result
	 * 		list of ScoredMetabolite objects returned by ScoreMetabolitesDriver or Algo
	 * @param exec
	 * 		ExecutionContext of ScoreMetabolitesNodeModel
	 * @param tableReference
	 * 		KNIME table containing the reference metabolites (this method requires 2 columns of the table:
	 * 		metabolite id and metabolite name)
	 * @return
	 * 		KNIME table with scores for metabolites with 4 columns: metabolite id, type of the metabolite,
	 * 		score, probability indicating the significance of the score + 1 optional column with metabolite names
	 */
	public static BufferedDataTable generateOutTable(LinkedList<ScoredMetabolite> result, ExecutionContext exec,
			DataTable tableReference){
		
		//generate specification of output table
		DataTableSpec refSpec = tableReference.getDataTableSpec();
		DataTableSpec spec = generateOutSpec(refSpec);
		int indexId = spec.findColumnIndex(ScoreMetabolitesNodeModel.METABOLITE_ID);
		int indexType = spec.findColumnIndex(ScoreMetabolitesNodeModel.METABOLITE_TYPE);
		int indexScore = spec.findColumnIndex(ScoreMetabolitesNodeModel.METABOLITE_SCORE);
		int indexSign = spec.findColumnIndex(ScoreMetabolitesNodeModel.METABOLITE_SIGNIFICANCE);
		int indexName = spec.findColumnIndex(ScoreMetabolitesNodeModel.METABOLITE_NAME);
		
		//build hashmap id-> name from reference table
		HashMap<String,String> idToName = new HashMap<String, String>();
		if(indexName!=-1){
			int indexRefId = refSpec.findColumnIndex(ScoreMetabolitesNodeModel.METABOLITE_ID);
			int indexRefName = refSpec.findColumnIndex(ScoreMetabolitesNodeModel.METABOLITE_NAME);
			for(DataRow r: tableReference){
				String id = ((StringCell) r.getCell(indexRefId)).getStringValue();
				String name =((StringCell) r.getCell(indexRefName)).getStringValue();
				idToName.put(id, name);
			}
		}
		
		//generate output table
		BufferedDataContainer c = exec.createDataContainer(spec);
		int counter=0;
		for(ScoredMetabolite m: result){
        	RowKey key = new RowKey("Row "+counter);
        	DataCell[] data = new DataCell[spec.getNumColumns()];
        	//fill in data
        	data[indexId]= new StringCell(m.getId());
        	data[indexType] = new StringCell(m.getType());
        	data[indexScore]= new DoubleCell(m.getScore());
        	data[indexSign]= new DoubleCell(m.getProbability());
        	//check for name
        	if(indexName!=-1){
        		String name = idToName.get(m.getId());
        		data[indexName] = new StringCell(name);
        	}
        	//add row to table
        	DataRow r = new DefaultRow(key, data);
        	c.addRowToTable(r);
        	counter++;
		}
		c.close();
		
		return c.getTable();
	}

}
