package phenomizer.algorithm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class PhenomizerAlgorithmNoPvalue extends PhenomizerAlgorithm{
	
	/**
	 * creates a PhenomizerAlgorithmNoPval object
	 * runs the PhenomizerAlgorithm without calculating pvalues
	 * @param num: number of diseases in the output of Phenomizer
	 * @param ontology: Ontology object representing is-a relationship between PhenoDis symptoms
	 * @param queryIds: contains PhenoDis ids of the query symptoms
	 * @param sda: associations between diseases and symptoms annotated in PhenoDis
	 * @param similarityCalculator: calculates the similarity score between a query and a disease
	 */
	public PhenomizerAlgorithmNoPvalue(int num, Ontology ontology, LinkedList<Integer> queryIds,
			SymptomDiseaseAssociations sda, SimilarityCalculator similarityCalculator) {
		
		super(num, ontology, queryIds, sda, similarityCalculator);
		this.comparator = new ComparatorPhenoScore();

	}
	
	/**
	 * creates a PhenomizerAlgorithmNoPval object with reusable ic and similarity hashmap
	 * runs the PhenomizerAlgorithm without calculating pvalues
	 * @param num: number of diseases in the output of Phenomizer
	 * @param ontology: Ontology object representing is-a relationship between PhenoDis symptoms
	 * @param queryIds: contains PhenoDis ids of the query symptoms
	 * @param sda: associations between diseases and symptoms annotated in PhenoDis
	 * @param similarityCalculator: calculates the similarity score between a query and a disease
	 * @param ic: HashMap mapping symptom ids to ic values
	 * @param sim: HashMap mapping pairs of symptoms to their similarity score
	 */
	public PhenomizerAlgorithmNoPvalue(int num, Ontology ontology, LinkedList<Integer> queryIds,
			SymptomDiseaseAssociations sda, SimilarityCalculator similarityCalculator,
			HashMap<Integer, Double> ic, HashMap<String, Double> sim) {
		
		super(num, ontology, queryIds, sda, similarityCalculator, ic, sim);
		this.comparator = new ComparatorPhenoScore();
	}

	@Override
	/**
	 * executes the Phenomizer algorithm
	 * @return: prediction results of Phenomizer for the current query
	 * 		the results are stored in a linked list of String arrays
	 * 		each list entry is an array corresponding to a disease
	 * 		array elements: array[0] disease id, array[1] similariy score
	 */
	public LinkedList<String[]> runPhenomizer() {
		setIC();
		String[][] all_scores = scoreQuery();
		return generateResult(all_scores);
	}
	
	/**
	 * calculates the similariy between the query and all diseases in PhenoDis
	 * the similarity scores are rounded to 3 decimal places and stored in a String matrix
	 * @return matrix, each line represents the a disease with disease id (column 0) and score (column 2)
	 */
	private String[][] scoreQuery(){
		
		//data structure to store scores between all diseases and the query
		String [][] all_scores = new String [sda.numberOfDiseases()][2];
		int position=0;
		
		for(int disease : sda.getDiseases()){
			double similarity = similarityCalculator.calculateSymmetricSimilarity(queryIds,sda.getSymptoms(disease));
			//round similarity to 3 decimal places
			similarity = (double) Math.round(similarity*1000)/1000;
			//add current disease to the array data structure
			all_scores[position][0]=Integer.toString(disease);
			all_scores[position][1]=Double.toString(similarity);
			position++;
		}
		
		//sort the array with all scores
		Arrays.sort(all_scores, comparator);
		
		return all_scores;
	}

}
