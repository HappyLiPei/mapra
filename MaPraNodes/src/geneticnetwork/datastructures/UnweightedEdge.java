package geneticnetwork.datastructures;

/** representation of an unweighted (directed or undirected) edge within a network*/
public class UnweightedEdge extends Edge {
	
	/**
	 * constructor for an unweighted edge between two nodes
	 * @param node1 id of the first node of the edge (starting node)
	 * @param node2 id of the second node of the edge (ending node)
	 */
	public UnweightedEdge(String node1, String node2) {
		super(node1, node2);
	}

	@Override
	public int getWeight() {
		return 1;
	}

}
