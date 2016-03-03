package phenomizeralgorithm;

import java.util.HashMap;
import java.util.LinkedList;

public class PhenomizerAlgorithmSampling extends PhenomizerAlgorithm{
	
	public PhenomizerAlgorithmSampling(Ontology ontology, SymptomDiseaseAssociations sda,
			SimilarityCalculator similarityCalculator) {
		
		super(0, ontology, null, sda, similarityCalculator,
				new HashMap<Integer,Double>(sda.numberOfSymptoms()*3),
				new HashMap<String, Double>(sda.numberOfSymptoms()*sda.numberOfSymptoms()));	
	}

	@Override
	//this version of Phenomizer does not use a query -> no return result, just calculate IC
	public LinkedList<String[]> runPhenomizer() {
		setIC();
		return null;
	}

}
