package phenomizeralgorithm;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import io.FileInputReader;
import phenomizer.algorithm.DataTransformer;
import phenomizer.algorithm.FrequencyConverter;
import phenomizer.algorithm.Ontology;
import phenomizer.algorithm.PhenomizerAlgorithm;
import phenomizer.algorithm.PhenomizerAlgorithmSampling;
import phenomizer.algorithm.ScoreDistributionSampling;
import phenomizer.algorithm.SimilarityCalculatorNoWeight;
import phenomizer.io.FileUtilitiesPhenomizer;

public class TestSampling {

	@Test
	public void testICCalculation()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		LinkedList<Integer> s = FileUtilitiesPhenomizer.readInSymptoms("../TestData/Phenomizer/DiseasesAndSymptoms/symptoms.txt");
		int [][] o = FileUtilitiesPhenomizer.readInOntology("../TestData/Phenomizer/DiseasesAndSymptoms/Ontology.txt");
		HashMap<Integer, LinkedList<Integer> > k = FileUtilitiesPhenomizer.readInKSZ("../TestData/Phenomizer/DiseasesAndSymptoms/ksz.txt");
		HashMap<Integer, LinkedList<Integer[]> > ksz = (new FrequencyConverter()).addWeights(k);
		
		DataTransformer dt = new DataTransformer();
		Ontology onto = new Ontology(o);
		PhenomizerAlgorithm pheno = new PhenomizerAlgorithmSampling(onto,
				dt.generateSymptomDiseaseAssociation(onto, s, ksz),
				new SimilarityCalculatorNoWeight());
		
		pheno.runPhenomizer();
		
		//reflection pattern :)
		Field f = PhenomizerAlgorithm.class.getDeclaredField("ic");
		f.setAccessible(true);
		@SuppressWarnings("unchecked")
		HashMap<Integer, Double> ic =(HashMap<Integer, Double>) f.get(pheno);
		
		int[] result ={
				11,3,10,8,9,8,5,2,10,7,
				7,4,9,3,1,9,1,5,5,4,
				8,6,4,5,2,2,0,2,0,1,
				0,0,2,3,1,4,5,5,2,1
		};
		
		for(int i=0; i<40; i++){
			assertEquals("IC of symptom "+(i+1)+" is incorrect",
					-Math.log((double)result[i]/11), ic.get(i+1), 1E-10);
		}
	}
	
	@Rule
	public TemporaryFolder folder =  new TemporaryFolder();
	
	@Test
	public void testSamplingProcedure()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException{
		
		String out = folder.newFile().getAbsolutePath();
		
		LinkedList<Integer> s = FileUtilitiesPhenomizer.readInSymptoms("../TestData/Phenomizer/DiseasesAndSymptoms/symptoms.txt");
		int [][] o = FileUtilitiesPhenomizer.readInOntology("../TestData/Phenomizer/DiseasesAndSymptoms/Ontology.txt");
		HashMap<Integer, LinkedList<Integer> > k = FileUtilitiesPhenomizer.readInKSZ("../TestData/Phenomizer/DiseasesAndSymptoms/ksz.txt");
		HashMap<Integer, LinkedList<Integer[]> > ksz = (new FrequencyConverter()).addWeights(k);
		
		ScoreDistributionSampling sds = new ScoreDistributionSampling(1, 10, 0,
				o, ksz, s, folder.newFile().getAbsolutePath(), out);
		sds.prepareData();
		
		//reflection pattern :)
		Field f = ScoreDistributionSampling.class.getDeclaredField("dt");
		f.setAccessible(true);
		f.set(sds, new MockDatatTransformer());	
		
		sds.startSampling(1);
		
		LinkedList<String> res = FileInputReader.readAllLinesFrom(out);
		LinkedList<String> expected = FileInputReader.readAllLinesFrom(
				"../TestData/Phenomizer/ExpectedResults/sampling.txt");
		
		for(int i=0; i<11; i++){
			assertEquals("Line "+(i+1)+" is not correct", expected.get(i), res.get(i));
		}
		
	}
	
	//mock class for generating non-random queries
	class MockDatatTransformer extends DataTransformer{
		private int position=0;
		private LinkedList<Integer>[] queries;
		
		@SuppressWarnings("unchecked")
		public MockDatatTransformer(){
			queries = new LinkedList[10];
			for(int i=1; i<=10; i++){
				queries[i-1]=FileUtilitiesPhenomizer.readInQuery("../TestData/Phenomizer/Queries/query"+i+".txt");
			}
			
			
		}
		
		public LinkedList<Integer> getRandomQuery(int length, Ontology ontology, int[] symptoms){
			LinkedList<Integer> result = prepareQuery(ontology, queries[position]);
			position=(position+1)%10;
			return result;
		}
	}

}
