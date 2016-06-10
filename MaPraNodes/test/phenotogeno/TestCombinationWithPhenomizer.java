package phenotogeno;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import phenomizer.algorithm.FrequencyConverter;
import phenomizer.algorithm.PhenomizerDriver;
import phenomizer.io.FileUtilitiesPhenomizer;
import phenotogeno.algo.PhenoToGenoDataTransformer;
import phenotogeno.io.FileUtilitiesPTG;
import togeno.GeneAssociation;
import togeno.ScoredDiseaseOrMetabolite;

public class TestCombinationWithPhenomizer {

	@Test
	public void testOutputToInput() {
		
		LinkedList<String []> res = new LinkedList<String[]>();
		res.add(new String[]{"110", "1.228", "0.0"});
		res.add(new String[]{"108","1.125","0.016"});
		res.add(new String[]{"105","0.765","0.646"});
		res.add(new String[]{"107","0.512","1.0"});
		
		int [] expectedID = new int []{110, 108, 105, 107};
		double [] expectedPV = new double[] {0.001, 0.016, 0.646, 1.0};
		transformAndCompare(res, expectedID, expectedPV);
	}
	
	@Test
	public void testPhenomizerToInput(){
		
		LinkedList<Integer> query = FileUtilitiesPhenomizer.readInQuery(
				"../TestData/Phenomizer/Queries/query10.txt");
		LinkedList<Integer> symptoms = FileUtilitiesPhenomizer.readInSymptoms(
				"../TestData/Phenomizer/DiseasesAndSymptoms/symptoms.txt");
		int [][] ontology =FileUtilitiesPhenomizer.readInOntology(
				"../TestData/Phenomizer/DiseasesAndSymptoms/Ontology.txt");
		HashMap<Integer,LinkedList<Integer[]>> ksz_no_freq = (new FrequencyConverter()).addWeights(
				FileUtilitiesPhenomizer.readInKSZ("../TestData/Phenomizer/DiseasesAndSymptoms/ksz.txt"));
		
		PhenomizerDriver driver = new PhenomizerDriver(query,symptoms,ksz_no_freq,ontology);
		driver.setPhenomizerAlgorithm(11, true, 0, "../TestData/Phenomizer/PValues");
		LinkedList<String[]> res = driver.runPhenomizer();
		
		int [] expectedID = new int []{110, 108, 105, 106, 109, 101, 103, 107, 104, 102, 100};
		double [] expectedPV = new double[] {0.001, 0.01595, 0.6457, 0.6457, 0.74617, 0.74617, 0.91536,1.0,1.0,1.0,1.0};
		transformAndCompare(res, expectedID, expectedPV);

	}
	
	private void transformAndCompare(LinkedList<String[]> res, int[] expectedID, double[] expectedPV){
		
		LinkedList<String> genes_raw =
				FileUtilitiesPTG.readGeneList("../TestData/PhenoToGeno/all_genes.txt");
		HashMap<Integer, LinkedList<String>> mapping =
				FileUtilitiesPTG.readDiseaseGeneAssociation("../TestData/PhenoToGeno/gene_diseases.txt");
		PhenoToGenoDataTransformer dt = new PhenoToGenoDataTransformer();
		GeneAssociation dga =dt.getDiseaseGeneAssociation(genes_raw, mapping, true);
		LinkedList<ScoredDiseaseOrMetabolite> transformed = dt.getPhenomizerResultFromAlgo(res, dga);
		
		assertEquals("Size of parsed data is incorrect", res.size(), transformed.size());
		for(int pos =0; pos<res.size(); pos++){
			assertEquals("Id of Result "+(pos+1)+" is not parsed correctly", expectedID[pos]+"",
					transformed.get(pos).getId());
			assertEquals("Pvalue of Result "+(pos+1)+" is not parsed correctly", expectedPV[pos],
					transformed.get(pos).getPval(),1E-5);
		}
	}


}
