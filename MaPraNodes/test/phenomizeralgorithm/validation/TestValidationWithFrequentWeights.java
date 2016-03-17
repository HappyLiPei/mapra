package phenomizeralgorithm.validation;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import io.FileInputReader;
import phenomizer.algorithm.DataTransformer;
import phenomizer.algorithm.FrequencyConverter;
import phenomizer.algorithm.Ontology;
import phenomizer.io.FileUtilitiesPhenomizer;
import phenomizer.validation.PhenomizerWithFrequentSymptoms;
import phenomizer.validation.PhenomizerWithFrequentSymptomsNoPval;
import phenomizer.validation.PhenomizerWithFrequentSymptomsWithPval;

public class TestValidationWithFrequentWeights {
	
	private int [][] ontology;
	private LinkedList<Integer> symptoms;
	private HashMap<Integer, LinkedList<Integer[]>> ksz_freq;
	private LinkedList<Integer>[] queries;
	private int [] query_ids;
	
	@SuppressWarnings("unchecked")
	@Before
	public void prepareTest(){
		ontology = FileUtilitiesPhenomizer.readInOntology("../TestData/Phenomizer/DiseasesAndSymptoms/Ontology.txt");
		symptoms = FileUtilitiesPhenomizer.readInSymptoms("../TestData/Phenomizer/DiseasesAndSymptoms/symptoms.txt");
		ksz_freq = new FrequencyConverter().convertAll(
				FileUtilitiesPhenomizer.readInKSZFrequency("../TestData/Phenomizer/DiseasesAndSymptoms/ksz_freq.txt"));
		
		DataTransformer dt = new DataTransformer();
		Ontology o = new Ontology(ontology);
		queries = new LinkedList[111];
		query_ids = new int[111];
		for (int i=1; i<=10; i++){
			LinkedList<Integer> q = FileUtilitiesPhenomizer.readInQuery("../TestData/Phenomizer/Queries/query"+i+".txt");
			q=dt.prepareQuery(o, q);
			for(int j=0; j<=10; j++){
				queries[(i-1)*11+j]=q;
				query_ids[(i-1)*11+j]=100+j;
			}	
		}
		queries[110] = new LinkedList<Integer>();
		query_ids[110] = 111;
	}
	
	@Rule
	public TemporaryFolder tmp_folder =  new TemporaryFolder();

	@Test
	public void testNoPvalNoWeight() throws IOException, NoSuchFieldException, SecurityException,
		IllegalArgumentException, IllegalAccessException {
		
		File f = tmp_folder.newFile();
		PhenomizerWithFrequentSymptomsNoPval p = new PhenomizerWithFrequentSymptomsNoPval(0, ontology, symptoms, ksz_freq,
				f.getAbsolutePath());
		p.prepareData();
		injectQueries(p);
		p.runValidation();
		compareFiles("../TestData/Phenomizer/ExpectedResults/Validation/frequentweights_noweight_nopval.txt",
				f.getAbsolutePath());
	}
	
	@Test
	public void testNoPvalWithWeight() throws IOException, NoSuchFieldException, SecurityException,
		IllegalArgumentException, IllegalAccessException {
		
		File f = tmp_folder.newFile();
		PhenomizerWithFrequentSymptomsNoPval p = new PhenomizerWithFrequentSymptomsNoPval(1, ontology, symptoms, ksz_freq,
				f.getAbsolutePath());
		p.prepareData();
		injectQueries(p);
		p.runValidation();
		compareFiles("../TestData/Phenomizer/ExpectedResults/Validation/frequentweights_weight_nopval.txt",
				f.getAbsolutePath());
	}
	
	@Test
	public void testPvalNoWeight() throws IOException, NoSuchFieldException, SecurityException,
		IllegalArgumentException, IllegalAccessException {
		
		File f = tmp_folder.newFile();
		PhenomizerWithFrequentSymptomsWithPval p = new PhenomizerWithFrequentSymptomsWithPval(0, ontology, symptoms,
				ksz_freq,f.getAbsolutePath(), "../TestData/Phenomizer/PValues");
		p.prepareData();
		injectQueries(p);
		p.runValidation();
		compareFiles("../TestData/Phenomizer/ExpectedResults/Validation/frequentweights_noweight_pval.txt",
				f.getAbsolutePath());
	}
	
	private void injectQueries(PhenomizerWithFrequentSymptoms p)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		
		Field f_q = PhenomizerWithFrequentSymptoms.class.getDeclaredField("queries");
		f_q.setAccessible(true);
		f_q.set(p, queries);
		Field f_d = PhenomizerWithFrequentSymptoms.class.getDeclaredField("query_ids");
		f_d.setAccessible(true);
		f_d.set(p, query_ids);
	}
	
	private void compareFiles(String path_expected, String path_actual){
		
		LinkedList<String> expected_list = FileInputReader.readAllLinesFrom(path_expected);
		LinkedList<String> actual_list = FileInputReader.readAllLinesFrom(path_actual);
		
		assertEquals("Files do not have equal length", expected_list.size(), actual_list.size());
		
		Iterator<String> ie = expected_list.iterator();
		Iterator<String> ia = actual_list.iterator();
		int count=1;
		while(ia.hasNext()){
			assertEquals("Incorrect result in line "+count, ie.next(), ia.next());
			count++;
		}
	}

}
