package algorithm;

import java.util.HashMap;
import java.util.LinkedList;

//save ontology as data structure
public class Ontology {
	//in ontology ist zu jedem knoten die liste seiner eltern gespeichert
	private HashMap<Integer,LinkedList<Integer>> ontology;
	
	// konstruktor bekommt ein 2d array, das alle kanten aus isa enth�lt (child_id, parent_id)
	public Ontology(int[][] edges){
		for(int child=0; child<edges.length; child++){
			for (int parent=0; parent<edges[child].length; parent++){
				addEdge(parent,child);
			}
		}
	}

	
	//methode, die eine kante zwischen parent und child hinzuf�gt (falls sie noch nicht existiert)
	private void addEdge(int parent, int child){
		//falls child-knoten schon enthalten, elter evtl in parentlist hinzuf�gen
		if(ontology.containsKey(child)){
			if(!ontology.get(child).contains(parent)){
				ontology.get(child).add(parent);
			}
		}
		//falls child-knoten noch nicht enthalten, neu hinzuf�gen
		else{
			LinkedList<Integer> parentList = new LinkedList<Integer>();
			parentList.add(parent);
			ontology.put(child, parentList);
		}
	}
	
	
	//methode, die eine liste aller eltern eines knotens zur�ckgibt
	//wurzel gibt null zur�ck
	public LinkedList<Integer> getParents (int node){
		LinkedList<Integer> parents = ontology.get(node);
		return parents;
	}
}




