package metabolites.algo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import metabolites.types.MeasuredMetabolite;
import metabolites.types.ReferenceMetabolite;
import metabolites.types.ReferenceMetaboliteBinary;
import metabolites.types.ReferenceMetaboliteConcentration;

public class DataTransformerMetabolites {
	
	/**
	 * method to transform the data read from the table with measured metabolites from a patient into a data structure
	 * for metabolite scoring,
	 * the method checks for duplicate metabolite ids and keeps only the first measurement of the metabolite,
	 * the method compares the measured metabolite and the group to the ReferenceMetabolites and removes the measurement
	 * if the group or the metabolite is not part of the references,
	 * the method checks if all measurements are annotated with the same group, if not the measurements are set to the
	 * majority group
	 * @param caseMetabol
	 * 		list of String arrays read from a file or KNIME table containing the information about the measured
	 * 		metabolite:
	 * 		position 0: metabolite id, position 1: measured concentration or empty String if missing,
	 * 		position 2: group number of case (should be identical for each array in the list)
	 * @param reference
	 * 		mapping metabolite id -> ReferenceMetabolite object, required for checking the data for consistency
	 * @return
	 * 		a list of MeasuredMetabolite objects ready for scoring
	 */
	public LinkedList<MeasuredMetabolite> getListOfMeasuredMetabolites (LinkedList<String[]> caseMetabol,
			HashMap<String,ReferenceMetabolite> reference){
		
		//to recognize duplicates
		HashSet<String> metaboliteIds = new HashSet<String>(caseMetabol.size()*3);
		//group(s) of the patient -> final group: majority group annotation
		HashMap<Integer, Integer> groupCount = new HashMap<Integer, Integer>(caseMetabol.size()*3);
		for(String[] array: caseMetabol){
			int curGroup = Integer.parseInt(array[2]);
			if(groupCount.containsKey(curGroup)){
				Integer c =groupCount.get(curGroup);
				c++;
			}
			else{
				groupCount.put(curGroup, 1);
			}
		}
		int finalgroup =0;
		int count=0;
		for(int group: groupCount.keySet()){
			int curCount = groupCount.get(group);
			if(curCount>count){
				finalgroup=group;
				count=curCount;
			}
		}

		LinkedList<MeasuredMetabolite> list = new LinkedList<MeasuredMetabolite>();
		for(String[] array: caseMetabol){
			//get id and check for duplicates
			String id = array[0];
			if(!metaboliteIds.add(id)){
				continue;
			}
			//check if there is a reference metabolite
			if(!reference.containsKey(id)){
				continue;
			}
			//ignore metabolites for which there is no reference group
			else{
				ReferenceMetabolite r = reference.get(id);
				if(r instanceof ReferenceMetaboliteConcentration){
					double [] testForNull = ((ReferenceMetaboliteConcentration) r).getMeanAndStdDevForGroup(finalgroup);
					if(testForNull==null){
						continue;
					}
				}
			}
			
			MeasuredMetabolite entry=null;
			//case missing
			if(array[1].equals("")){
				entry= new MeasuredMetabolite(id, Double.NaN, finalgroup);
			}
			//case present
			else{
				entry = new MeasuredMetabolite(id, Double.parseDouble(array[1]),finalgroup);
			}
			list.add(entry);
		}
		return list;
	}

	/**
	 * method to transform the data read from the table with reference metabolites into a data structure for metabolite
	 * scoring,
	 * the method checks the type annotation of each metabolite (binary vs. concentration), it uses the annotation
	 * of the first row of the metabolite,
	 * the method checks for duplicate rows with identical metabolite id and group number, only the first row is kept
	 * @param controlMetabol
	 * 		map metabolite id-> list of String arrays of length 5 with the following elements
	 * 		position 0: id, position 1: type, position 2: group, position 3: mean, position 4: standard deviation,
	 * 		position 5: missingness in %
	 * 		the map is either read from a file or a KNIME table
	 * @return
	 * 		map metabolite id -> referenceMetabolite object required for metabolite scoring
	 */
	public HashMap<String, ReferenceMetabolite> getMapOfReferenceMetabolites(
			HashMap<String, LinkedList<String[]>> controlMetabol){
		
		HashMap<String, ReferenceMetabolite> map = new HashMap<String, ReferenceMetabolite>(controlMetabol.size()*3);
		for(String key: controlMetabol.keySet()){
			LinkedList<String[]> list =controlMetabol.get(key);
			String [] firstRow = list.getFirst();
			//binary metabolite
			if(firstRow[1].equals("binary")){
				ReferenceMetaboliteBinary bin = new ReferenceMetaboliteBinary(key, Double.parseDouble(firstRow[5]));
				map.put(key, bin);
			}
			//concentration metabolite
			else if(firstRow[1].equals("concentration")){
				
				//build hashset to remove duplicate entries (duplicate group numbers)
				HashSet<Integer> groups = new HashSet<Integer>();
				for(String[] array:list){
					int group = Integer.parseInt(array[2]);
					if(!groups.add(group)){
						list.remove(array);
					}
				}
				
				//get relevant data from each list entry
				int [] group = new int [list.size()];
				double[] mean = new double[list.size()];
				double[] std = new double[list.size()];
				double missing = Double.parseDouble(firstRow[5]);
				int position=0;
				for(String[] array: list){
					group[position]=Integer.parseInt(array[2]);
					mean[position]=Double.parseDouble(array[3]);
					std[position]=Double.parseDouble(array[4]);
					position++;
				}
				ReferenceMetaboliteConcentration conc = new ReferenceMetaboliteConcentration(
						key, missing, group, mean, std);
				map.put(key, conc);
			}
			//incorrect type annotation
			else{
				continue;
			}
		}
		
		return map;
	}
	

}
