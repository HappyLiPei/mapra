package phenotogeno.validation;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import geneticnetwork.algorithm.RandomWalkWithRestart;
import geneticnetwork.algorithm.RandomWalkWithRestartFixedIterations;
import geneticnetwork.io.FileUtilitiesGeneticNetwork;
import io.FileInputReader;
import phenomizer.algorithm.FrequencyConverter;
import phenomizer.algorithm.SymptomDiseaseAssociations;
import phenomizer.io.FileUtilitiesPhenomizer;
import phenotogeno.io.FileUtilitiesPTG;

public class TestValidationProcedure {
	
	private LinkedList<Integer> symptoms;
	private int [][] ontology;
	private HashMap<Integer, LinkedList<Integer[]>> ksz_with_freq;
	private String Pvalfolder;
	private LinkedList<String> genes_raw;
	private HashMap<Integer,LinkedList<String>> map_raw;
	private String [][] network_raw;
	
	@Before
	public void readInputData(){
		symptoms = FileUtilitiesPhenomizer.readInSymptoms(
				"../TestData/Phenomizer/DiseasesAndSymptoms/symptoms.txt");
		ontology = FileUtilitiesPhenomizer.readInOntology(
				"../TestData/Phenomizer/DiseasesAndSymptoms/Ontology.txt");
		ksz_with_freq = (new FrequencyConverter()).convertAll(
				FileUtilitiesPhenomizer.readInKSZFrequency("../TestData/Phenomizer/DiseasesAndSymptoms/ksz_freq.txt"));
		//use unweighted score distribution because weighted not available!
		Pvalfolder = "../TestData/Phenomizer/PValues";
		
		genes_raw = FileUtilitiesPTG.readGeneList(
				"../TestData/PhenoToGeno/all_genes.txt");
		map_raw = FileUtilitiesPTG.readDiseaseGeneAssociation(
				"../TestData/PhenoToGeno/gene_diseases.txt");
		network_raw = FileUtilitiesGeneticNetwork.readEdges("../TestData/GeneticNetwork/MTGNetwork.txt", true);
	}

	@Rule
	public TemporaryFolder folder =  new TemporaryFolder();
	
	@Test
	public void testPTGValidationProcedure() throws IOException {
		
		String outfile = folder.newFile().getAbsolutePath();
		MockSimulator s = new MockSimulator();
		ValidateGeneRanking val = new ValidateGeneRanking(ontology, symptoms, ksz_with_freq, genes_raw, map_raw,
				Pvalfolder, s, s, outfile);
		val.prepareData();
		val.simulateAndRank();
		
		LinkedList<String> actual = FileInputReader.readAllLinesFrom(outfile);
		LinkedList<String> expected = FileInputReader.readAllLinesFrom(
				"../TestData/PhenoToGeno/ExpectedResults/validationMock_expectedPTG.txt");
		assertEquals("Size of the validation result is incorrect", expected.size(), actual.size());
		assertArrayEquals("Validation result is incorrect", expected.toArray(new String[0]), actual.toArray(new String[0]));

	}
	
	@Test
	public void testNWValidationProcedure() throws IOException{
		
		String outfile = folder.newFile().getAbsolutePath();
		MockSimulator s = new MockSimulator();
		RandomWalkWithRestart rwwr = new RandomWalkWithRestartFixedIterations(0.9, 1);
		ValidateGeneRanking val = new ValidateGeneRanking(				
				ontology, symptoms, ksz_with_freq, genes_raw, map_raw, Pvalfolder, network_raw, rwwr, s, s, outfile);
		val.prepareData();
		val.simulateAndRank();
		
		LinkedList<String> actual = FileInputReader.readAllLinesFrom(outfile);
		LinkedList<String> expected = FileInputReader.readAllLinesFrom(
				"../TestData/PhenoToGeno/ExpectedResults/validationMock_expectedNW.txt");
		assertEquals("Size of the validation result is incorrect", expected.size(), actual.size());
		assertArrayEquals("Validation result is incorrect", expected.toArray(new String[0]), actual.toArray(new String[0]));

	}
	
	@Test
	public void testValidationProcedureFromFile() throws IOException{
		
		String outfile = folder.newFile().getAbsolutePath();
		SimulatorIteratorFromFile simiter =new SimulatorIteratorFromFile(
				"../TestData/PhenoToGeno/PatientFiles/PatientsMock.txt");
		RandomWalkWithRestart rwwr = new RandomWalkWithRestartFixedIterations(0.9, 1);
		ValidateGeneRanking val = new ValidateGeneRanking(				
				ontology, symptoms, ksz_with_freq, genes_raw, map_raw, Pvalfolder, network_raw,
				rwwr, simiter, simiter, outfile);
		val.prepareData();
		val.simulateAndRank();
		
		LinkedList<String> actual = FileInputReader.readAllLinesFrom(outfile);
		LinkedList<String> expected = FileInputReader.readAllLinesFrom(
				"../TestData/PhenoToGeno/ExpectedResults/validationMock_expectedNW.txt");
		assertEquals("Size of the validation result is incorrect", expected.size(), actual.size());
		assertArrayEquals("Validation result is incorrect", expected.toArray(new String[0]), actual.toArray(new String[0]));
		
	}
	
	@Test
	public void testValidationProcedureDiseasesFromFile() throws IOException{
		
		String outfile = folder.newFile().getAbsolutePath();
		DiseaseIteratorFile iter = new DiseaseIteratorFile(2, "../TestData/PhenoToGeno/ValidationDiseaseIdList.txt");
		MockSimulator sim = new MockSimulator();
		RandomWalkWithRestart rwwr = new RandomWalkWithRestartFixedIterations(0.9, 1);
		
		ValidateGeneRanking val = new ValidateGeneRanking(				
				ontology, symptoms, ksz_with_freq, genes_raw, map_raw, Pvalfolder, network_raw,
				rwwr, sim, iter, outfile);
		val.prepareData();
		val.simulateAndRank();
		
		LinkedList<String> actual = FileInputReader.readAllLinesFrom(outfile);
		LinkedList<String> expected = FileInputReader.readAllLinesFrom(
				"../TestData/PhenoToGeno/ExpectedResults/validationMock_expectedNW_2Patients.txt");
		assertEquals("Size of the validation result is incorrect", expected.size(), actual.size());
		assertArrayEquals("Validation result is incorrect", expected.toArray(new String[0]), actual.toArray(new String[0]));
		
	}
	
	@Test
	public void testValidationTop20() throws IOException{
		
		String outfile = folder.newFile().getAbsolutePath();
		MockSimulator s = new MockSimulator();
		RandomWalkWithRestart rwwr = new RandomWalkWithRestartFixedIterations(0.9, 1);
		
		ValidateGeneRanking val = new ValidateGeneRanking(				
				ontology, symptoms, ksz_with_freq, genes_raw, map_raw, Pvalfolder, network_raw, rwwr,
				new PhenomizerFilterTop20(), s, s, outfile);
		val.prepareData();
		val.simulateAndRank();
		
		LinkedList<String> actual = FileInputReader.readAllLinesFrom(outfile);
		LinkedList<String> expected = FileInputReader.readAllLinesFrom(
				"../TestData/PhenoToGeno/ExpectedResults/validationMock_expectedNW.txt");
		assertEquals("Size of the validation result is incorrect", expected.size(), actual.size());
		assertArrayEquals("Validation result is incorrect", expected.toArray(new String[0]), actual.toArray(new String[0]));
	}
	
	@Test
	public void testValidationSignificant() throws IOException{
		
		String outfile = folder.newFile().getAbsolutePath();
		MockSimulator s = new MockSimulator();
		RandomWalkWithRestart rwwr = new RandomWalkWithRestartFixedIterations(0.9, 1);
		
		ValidateGeneRanking val = new ValidateGeneRanking(				
				ontology, symptoms, ksz_with_freq, genes_raw, map_raw, Pvalfolder, network_raw, rwwr,
				new PhenomizerFilterSignificant(), s, s, outfile);
		val.prepareData();
		val.simulateAndRank();
		
		LinkedList<String> actual = FileInputReader.readAllLinesFrom(outfile);
		LinkedList<String> expected = FileInputReader.readAllLinesFrom(
				"../TestData/PhenoToGeno/ExpectedResults/validationMock_expectedSignificant.txt");
		assertEquals("Size of the validation result is incorrect", expected.size(), actual.size());
		assertArrayEquals("Validation result is incorrect", expected.toArray(new String[0]), actual.toArray(new String[0]));
	}
	
	@Test
	public void testValidationTopP() throws IOException{
		
		String outfile = folder.newFile().getAbsolutePath();
		MockSimulator s = new MockSimulator();
		RandomWalkWithRestart rwwr = new RandomWalkWithRestartFixedIterations(0.9, 1);
		
		ValidateGeneRanking val = new ValidateGeneRanking(				
				ontology, symptoms, ksz_with_freq, genes_raw, map_raw, Pvalfolder, network_raw, rwwr,
				new PhenomizerFilterSignificant(), s, s, outfile);
		val.prepareData();
		val.simulateAndRank();
		
		LinkedList<String> actual = FileInputReader.readAllLinesFrom(outfile);
		//same result as for significant diseases because there is only one significant disease (phenomizer NoWeightP, query4)
		LinkedList<String> expected = FileInputReader.readAllLinesFrom(
				"../TestData/PhenoToGeno/ExpectedResults/validationMock_expectedSignificant.txt");
		assertEquals("Size of the validation result is incorrect", expected.size(), actual.size());
		assertArrayEquals("Validation result is incorrect", expected.toArray(new String[0]), actual.toArray(new String[0]));
	}

}

class MockSimulator implements DiseaseIterator, PatientSimulator{
	
	private int [] diseaseIds=new int[]{100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110};
	private int position = 0;
	private int id =0;
	private int [] query4 = new int []{37,38,33,25,15,2};

	@Override
	public SimulatedPatient simulatePatient(int diseaseId, LinkedList<Integer[]> diseaseSymptoms) {
		LinkedList<Integer> symp = new LinkedList<Integer>();
		for(int i: query4){
			symp.add(i);
		}
		int pId = id++;
		return new SimulatedPatient(pId+"", diseaseId, symp);
	}

	@Override
	public int getNextDiseaseId() {
		return diseaseIds[position++];
	}

	@Override
	public boolean hasNextId() {
		return position<diseaseIds.length;
	}

	@Override
	public void setSDA(SymptomDiseaseAssociations sda) {		
	}

	@Override
	public int totalIterations() {
		return 11;
	}
	
}
