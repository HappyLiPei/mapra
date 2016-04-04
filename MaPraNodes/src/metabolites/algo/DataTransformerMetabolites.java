package metabolites.algo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import metabolites.types.MeasuredMetabolite;
import metabolites.types.ReferenceMetabolite;
import metabolites.types.ReferenceMetaboliteBinary;
import metabolites.types.ReferenceMetaboliteConcentration;

public class DataTransformerMetabolites {
	
	//TODO: comment
	//checks for duplicate metabolite id -> remove duplicated (second appearance)
	//check if there is a reference for the measured metabolite -> remove metabolite without reference
	//check group: group identical for all metabolites -> set to majority group id found
	//check if group for reference metabolite available -> else remove metabolite
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

	//TODO: Comment!!!
	//check type binary/ concentration -> annotation of first row of the metabolite
	//check for duplicates id+group -> ignore duplicate, take first values
	//check uniform minimum
	public HashMap<String, ReferenceMetabolite> getMapOfReferenceMetabolites(
			HashMap<String, LinkedList<String[]>> controlMetabol){
		
		HashMap<String, ReferenceMetabolite> map = new HashMap<String, ReferenceMetabolite>(controlMetabol.size()*3);
		for(String key: controlMetabol.keySet()){
			LinkedList<String[]> list =controlMetabol.get(key);
			String [] firstRow = list.getFirst();
			//binary metabolite
			if(firstRow[1].equals("binary")){
				ReferenceMetaboliteBinary bin = new ReferenceMetaboliteBinary(key, Double.parseDouble(firstRow[6]));
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
				
				int [] group = new int [list.size()];
				double[] mean = new double[list.size()];
				double[] std = new double[list.size()];
				double min = Double.parseDouble(firstRow[5]);
				int position=0;
				for(String[] array: list){
					group[position]=Integer.parseInt(array[2]);
					mean[position]=Double.parseDouble(array[3]);
					std[position]=Double.parseDouble(array[4]);
					//check if current minimum is global min for the group
					double curMin = Double.parseDouble(array[5]);
					if(curMin<min){
						min=curMin;
					}
					position++;
				}
				ReferenceMetaboliteConcentration conc = new ReferenceMetaboliteConcentration(key, group, mean, std, min);
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
