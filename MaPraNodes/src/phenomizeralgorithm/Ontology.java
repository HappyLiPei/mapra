package phenomizeralgorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class Ontology {
	
	//save for each node a list of its parents where the root has an empty list
	private HashMap<Integer,LinkedList<Integer>> ontology;
	
	
	/**
	 * construct ontology from a 2d array with columns child-id, parent-id
	 * @param edges
	 */
	public Ontology(int[][] edges){
		ontology = new HashMap<Integer,LinkedList<Integer>>();
		for(int position=0; position<edges.length; position++){
			addEdge(edges[position][1],edges[position][0]);
		}
	}

	/**
	 * add an edge between parent and child by adding the parent to the parentlist of the child if it doesn't already exist
	 * @param parent
	 * @param child
	 */
	private void addEdge(int parent, int child){

		//child node already exists -> maybe add parent to parentlist
		if(ontology.containsKey(child)){
			if(!ontology.get(child).contains(parent)){
				ontology.get(child).add(parent);
			}	
		}
		//add child node if it doesn't already exist
		else{
			LinkedList<Integer> parentList = new LinkedList<Integer>();
			parentList.add(parent);
			ontology.put(child, parentList);
		}
		//if parent node doesn't already exist, add it with an empty parentlist
		if(!ontology.containsKey(parent)){
			LinkedList<Integer> parentList = new LinkedList<Integer>();
			ontology.put(parent, parentList);
		}
	}
	

	/**
	 * calculate all ancestors of a node including the node itself
	 * @param node
	 * @return list of ancestors and the node itself
	 */
	public HashSet<Integer> getAllAncestors (int node){
		HashSet<Integer> ancestors = new HashSet<Integer>();
		if(ontology.containsKey(node)){
			addAncestors(node, ancestors);
			return ancestors;
		}
		else{
			ancestors.add(node);
			return ancestors;
		}
	}
	
	
	/**
	 * recursively determines all ancestors of the given node
	 * @param actNode
	 * @param ancestors
	 */
	private void addAncestors(int actNode, HashSet<Integer> ancestors){
		if(ontology.containsKey(actNode)){
			if(!ancestors.contains(actNode)){
				ancestors.add(actNode);
				LinkedList<Integer> parents = ontology.get(actNode);
				Iterator<Integer> iter = parents.iterator();
				while(iter.hasNext()){
					addAncestors(iter.next(), ancestors);
				}
			}
		}
	}
	
	/**
	 * generates all common ancestors of 2 given nodes
	 * @param node1
	 * @param node2
	 * @return list of common ancestors, empty if no common ancestors exist
	 */
	public HashSet<Integer> getAllCommonAncestors (int node1, int node2){
		HashSet<Integer> commonAncestors = new HashSet<Integer>();
		HashSet<Integer> ancestors1 = getAllAncestors(node1);
		HashSet<Integer> ancestors2 = getAllAncestors(node2);
		
		//return empty list if one of the nodes is not in the ontology
		if(ancestors1==null || ancestors2==null){
			return commonAncestors;
		}
		
		Iterator<Integer> iter1 = ancestors1.iterator();
		while(iter1.hasNext()){
			int actNode = iter1.next();
			if(ancestors2.contains(actNode) && !commonAncestors.contains(actNode)){
				commonAncestors.add(actNode);
			}
		}
		return commonAncestors;
	}

	/**
	 * generates list of only the common ancestors of 2 given nodes which could be a most informative common ancestor
	 * @param node1
	 * @param node2
	 * @return list of common ancestors relevant for ic calculation
	 */
	public HashSet<Integer> getRelevantCommonAncestors (int node1, int node2){
		HashSet<Integer> relevantAncestors = new HashSet<Integer>();
		HashSet<Integer> ancestors1 = getAllAncestors(node1);
		
		//not all ancestors of node2 needed, terminate if a common ancestor was found -> no consideration of the parents
		addRelevantCommonAncestors(node2, relevantAncestors, ancestors1);
		return relevantAncestors;
	}
	
	private void addRelevantCommonAncestors(int actNode, HashSet<Integer> relevantAncestors, HashSet<Integer> ancestors1){
		if(ancestors1.contains(actNode)){
			//common ancestor was found -> add to common ancestor and don't consider parents of this node
			relevantAncestors.add(actNode);
		}
		else{
			//no common ancestor found -> consider parents of this node
			if(ontology.containsKey(actNode)){
				LinkedList<Integer> parents = ontology.get(actNode);
				Iterator<Integer> iter = parents.iterator();
				while(iter.hasNext()){
					addRelevantCommonAncestors(iter.next(), relevantAncestors, ancestors1);
				}
			}
		}
		
	}
}
