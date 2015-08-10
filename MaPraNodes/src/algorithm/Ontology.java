package algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

//save ontology as data structure
public class Ontology {
	//in ontology ist zu jedem knoten die liste seiner eltern gespeichert
	// wurzel hat leere liste
	private HashMap<Integer,LinkedList<Integer>> ontology;
	
	
	// konstruktor bekommt ein 2d array, das alle kanten aus isa enthält (child_id, parent_id)
	public Ontology(int[][] edges){
		ontology = new HashMap<Integer,LinkedList<Integer>>();
		for(int position=0; position<edges.length; position++){
			addEdge(edges[position][1],edges[position][0]);
		}
	}

	
	//methode, die eine kante zwischen parent und child hinzufügt (falls sie noch nicht existiert)
	private void addEdge(int parent, int child){
		//falls child-knoten schon enthalten, elter evtl in parentlist hinzufügen
		if(ontology.containsKey(child)){
			if(!ontology.get(child).contains(parent)){
				ontology.get(child).add(parent);
			}
		}
		//falls child-knoten noch nicht enthalten, neu hinzufügen
		else{
			LinkedList<Integer> parentList = new LinkedList<Integer>();
			parentList.add(parent);
			ontology.put(child, parentList);
		}
	}
	
	
	//methode, die eine liste aller eltern eines knotens zurückgibt 
	//wurzel gibt leere liste zurück
	public LinkedList<Integer> getParents (int node){
		if(ontology.containsKey(node)){
			LinkedList<Integer> parents = ontology.get(node);
			return parents;
		}
		else{
			return null;
		}
		
	}
	

	// gibt eine Menge (HashSet) aller vorfahren eines knotens zurück (wurzel gibt null zurück)
	public HashSet<Integer> getAllAncestors (int node){
		
		if(ontology.containsKey(node)){
			HashSet<Integer> ancestors = new HashSet<Integer>();
			addAncestors(node, ancestors);
			return ancestors;
		}
		
		else{
			return null;
		}
	}
	
	
	// nötig für getAllAncestors()
	private void addAncestors(int actNode, HashSet<Integer> ancestors){
		if(ontology.containsKey(actNode)){
			if(!ancestors.contains(actNode)){
				//actNode noch nicht enthalten, also hinzufügen und rekursiv für alle parents von actNode aufrufen
				ancestors.add(actNode);
				LinkedList<Integer> parents = ontology.get(actNode);
				Iterator<Integer> iter = parents.iterator();
				while(iter.hasNext()){
					addAncestors(iter.next(), ancestors);
				}
			}
		}
	}
	
	
	//TODO methode, die alle common ancestors zurückgibt
	public HashSet<Integer> getAllCommonAncestors (int node){
		
		
		
		return null;
	}
	
}




