package geneticnetwork.datastructures;

/** representation of an edge in the network, it can be considered as directed or indirected*/
public abstract class Edge {
	
	/** starting vertex of the edge (any of the two nodes for undirected edge)*/
	private String startNode;
	/** ending vertex of the edge (any of the two nodes for undirected edge)*/
	private String endNode;
	
	/** construct for generating an edge between two nodes
	 * @param node1 id of the first node of the edge (starting node)
	 * @param node2 id of the second node of the edge (ending node)
	 */
	public Edge(String node1, String node2){
		this.startNode=node1;
		this.endNode=node2;
	}
	
	/** retrieves the weight of this edge, the weight is always 1 in case of an unweighted edge
	 * @return weight of this edge as integer
	 */
	public abstract int getWeight();
	
	/**
	 * gets the first node of the edge (starting node)
	 * @return first node of the edge (starting node)
	 */
	public String getStartNode(){
		return startNode;
	}
	
	/**
	 * gets the second node of the edge (ending node)
	 * @return second node of the edge (ending node)
	 */
	public String getEndNode(){
		return endNode;
	}

}
