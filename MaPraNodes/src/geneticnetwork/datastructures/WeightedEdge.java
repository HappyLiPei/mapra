package geneticnetwork.datastructures;

/** representation of an unweighted (directed or undirected) edge within a network*/
public class WeightedEdge extends Edge {
	
	/** weight annotated to the edge as integer value*/
	private int weight;

	/**
	 * constructor for generating a weighted edge
	 * @param node1 id of the first node of the edge (starting node)
	 * @param node2 id of the second node of the edge (ending node)
	 * @param weight weight of the edge
	 */
	public WeightedEdge(String node1, String node2, int weight) {
		super(node1, node2);
		this.weight=weight;
	}

	@Override
	public int getWeight() {
		return weight;
	}

}
