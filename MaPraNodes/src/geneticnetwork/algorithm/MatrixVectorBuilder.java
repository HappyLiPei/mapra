package geneticnetwork.algorithm;

import java.util.HashMap;

import geneticnetwork.datastructures.Edge;
import geneticnetwork.datastructures.ScoredGenes;
import geneticnetwork.datastructures.SparseMatrix;
import geneticnetwork.datastructures.Vector;

/** MatrixVectorBuilder using data structures that are optimized for random walk with restart*/
public class MatrixVectorBuilder {
	
	/** array of edge objects representing a genetic network */
	private Edge[] network;
	/** scoredGenes object with the results of PhenoToGeno */
	private ScoredGenes scores;
	
	/** auxiliary variable: indicates the position of the first singleton node with the matrix and restart vector,
	 * if critical position == size of matrix/restartVector -> no singleton nodes */
	private int criticalPosition;
	/** mapping gene_id -> position (0-based) in matrix and restartVector for all nodes in the network and the scored genes (union) */
	private HashMap<String, Integer> positionMap;
	/** stochastic matrix representing transition probabilities within the network*/
	private SparseMatrix matrix;
	/** probability vector with initial probabilities for each gene */
	private Vector restartVector;
	
	/**
	 * generates a MatrixVectorBuilder object that produces the matrix and the vector for the random walk with restart
	 * @param network array of edge objects representing an undirected weighted network
	 * @param scores ScoredGenes object storing the scores obtained from PhenoToGeno
	 */
	public MatrixVectorBuilder(Edge [] network, ScoredGenes scores){
		this.network =network;
		this.scores = scores;
		this.criticalPosition =-1;
		this.positionMap = null;
		this.matrix = null;
		this.restartVector = null;
	}
	
	/**
	 * constructor for generating a MatrixVectorBuilder using a pre-calculated matrix from another MatrixVectorBuilder
	 * with a different scoring result from PhenoToGeno (on the same set of genes!!!) 
	 * @param scores ScoredGenes object storing the scores obtained from PhenoToGeno
	 * @param builder MatrixVecotrBuilder whose matrix should be reused
	 */
	public MatrixVectorBuilder(ScoredGenes scores, MatrixVectorBuilder builder){
		//network is not needed
		this.network =null;
		// data calculated by previous object
		this.criticalPosition =builder.getCriticalPosition();
		this.positionMap = builder.getIdPositionMap();
		this.matrix = builder.getStochasticMatrix();
		// is different from previous object
		this.scores = scores;
		this.restartVector = null;
	}
	
	/**
	 * retrieves the critical position (position of first singleton node within the restart vector / transition matrix) 
	 * @return critical position
	 */
	public int getCriticalPosition(){
		if(criticalPosition==-1){
			buildPositionMap();
		}
		return criticalPosition;
	}
	
	/**
	 * retrieves the mapping of gene_id to position within the matrix (column = row) and the restart vector (row)
	 * @return mapping gene_id->position
	 */
	public HashMap<String, Integer> getIdPositionMap(){
		if(positionMap==null){
			buildPositionMap();
		}
		return positionMap;
	}
	
	/**
	 * retrieves the stochastic matrix (transition matrix) for the random walk with restart
	 * @return matrix object with transition probabilities
	 */
	public SparseMatrix getStochasticMatrix(){
		if(positionMap==null){
			buildPositionMap();
		}
		if(matrix==null){
			buildStochasticMatrix();
		}
		return matrix;
	}
	
	/**
	 * retrieves the vector with initial probabilities (elements sum up to 1)
	 * @return vector with the initial probabilities
	 */
	public Vector getRestartVector(){
		if(positionMap==null){
			buildPositionMap();
		}
		if(restartVector==null){
			buildStartVector();
		}
		return restartVector;
	}
	
	/**
	 * method to generate the mapping gene -> position (column, row),
	 * the position is 0-based,
	 * the method combines the genes found in the network and in the scoring and adds the nodes of the scoring that
	 * are not part of the network to the network as singleton nodes with self-loops
	 */
	private void buildPositionMap(){
		
		//initialize data structure
		positionMap = new HashMap<String, Integer>(network.length*4);
		int currentPosition=0;
		
		//map network nodes
		for(Edge e: network){
			for(String node : new String[] {e.getStartNode(), e.getEndNode()}){
				if(!positionMap.containsKey(node)){
					positionMap.put(node, currentPosition);
					currentPosition++;
				}
			}
		}
		criticalPosition = currentPosition;
		
		//get singletons -> scored genes that are not part of the network
		for(String geneId:scores.getAllScoredGenes()){
			if(!positionMap.containsKey(geneId)){
				positionMap.put(geneId, currentPosition);
				currentPosition++;
			}
		}
	}
	
	/**
	 * builds the transition matrix for the random walk with restart,
	 * matrix is column-normalized (columns sum up to one) and represents transition probabilities within
	 * the network
	 */
	private void buildStochasticMatrix(){
		
		//number of entries in matrix= 2*#edges + #singletons
		int nonzeroEntries = 2*network.length+positionMap.size()-criticalPosition;
		matrix = new SparseMatrix(positionMap.size(), positionMap.size(), nonzeroEntries);
		
		//get weight sum for each column
		int [] weightSumPerCol = new int [criticalPosition];
		for(Edge e: network){
			int weight = e.getWeight();
			int pos1 = positionMap.get(e.getStartNode());
			int pos2 = positionMap.get(e.getEndNode());
			weightSumPerCol[pos1]+=weight;
			weightSumPerCol[pos2]+=weight;
		}
		
		//fill in matrix elements for nodes of the network
		for(Edge e: network){
			int weight = e.getWeight();
			int pos1 = positionMap.get(e.getStartNode());
			int pos2 = positionMap.get(e.getEndNode());
			matrix.addEntry(pos1, pos2, (double) weight/weightSumPerCol[pos2]);
			matrix.addEntry(pos2, pos1, (double) weight/weightSumPerCol[pos1]);
		}
		
		//add singleton nodes with self loop
		for(int i=criticalPosition; i<positionMap.size(); i++){
			matrix.addEntry(i, i, 1);
		}

	}
	
	/**
	 * method to generate the vector with the initial probabilities for the random walk with restart,
	 * the entries of the vectors sum up to one, the entries are the normalized scores from ScoredGenes
	 */
	private void buildStartVector(){
		
		//initializes vector with all 0 entries
		restartVector = new Vector(positionMap.size());
		
		//calculate sum of scores
		double sum = 0;
		for(String gene:scores.getAllScoredGenes()){
			sum+=scores.getScoreof(gene);
		}
		
		//set all vector entries
		for(String gene: scores.getAllScoredGenes()){
			int position = positionMap.get(gene);
			double probability =  scores.getScoreof(gene)/sum;
			restartVector.setEntry(position, probability);
		}
	}

}
