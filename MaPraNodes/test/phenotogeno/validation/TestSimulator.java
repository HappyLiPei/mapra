package phenotogeno.validation;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import io.FileInputReader;
import phenomizer.algorithm.DataTransformer;
import phenomizer.algorithm.FrequencyConverter;
import phenomizer.algorithm.Ontology;
import phenomizer.algorithm.SymptomDiseaseAssociations;
import phenomizer.io.FileUtilitiesPhenomizer;

public class TestSimulator {
	
	private SymptomDiseaseAssociations sda;
	
	@Rule
	public TemporaryFolder folder =  new TemporaryFolder();
	
	@Before
	public void getSda(){
		//get SymptomDiseaseAssociations
		LinkedList<Integer> symptoms = FileUtilitiesPhenomizer.readInSymptoms(
				"../TestData/Phenomizer/DiseasesAndSymptoms/symptoms.txt");
		Ontology ontology = new Ontology(FileUtilitiesPhenomizer.readInOntology(
				"../TestData/Phenomizer/DiseasesAndSymptoms/Ontology.txt"));
		HashMap<Integer, LinkedList<Integer[]>> ksz_with_freq = (new FrequencyConverter()).convertAll(
				FileUtilitiesPhenomizer.readInKSZFrequency("../TestData/Phenomizer/DiseasesAndSymptoms/ksz_freq.txt"));
		sda = new DataTransformer().generateSymptomDiseaseAssociation(ontology, symptoms, ksz_with_freq);
	}

	@Test
	public void testSimulatorVeryFrequent() throws IOException {
		
		String file = folder.newFile().getAbsolutePath();
		PatientSimulatorVeryFrequentSymptoms simu = new PatientSimulatorVeryFrequentSymptoms(file);

		assertNull("Patient for disease 100 is incorret", simu.simulatePatient(100, sda.getSymptoms(100)));
		LinkedList<Integer[]> symp = new LinkedList<Integer[]> ();
		symp.add(new Integer[] {50,15});
		symp.add(new Integer[]{51, 15});
		symp.add(new Integer[]{52,15});
		SimulatedPatient p1 = simu.simulatePatient(111, symp);
		symp.add(new Integer[]{53,5});
		SimulatedPatient p2 = simu.simulatePatient(111, symp);
		int count=1;
		for(SimulatedPatient p: new SimulatedPatient[]{p1, p2}){
			assertEquals("Patient id of patient "+count+ " is incorrect", count-1+"", p.getId());
			assertEquals("Disease id of patient "+count+ " is incorrect",111, p.getDisease());
			assertArrayEquals("Symptoms of patient "+ count+" are incorrect", new Integer[]{50,51,52},
					p.getSymptoms().toArray(new Integer[0]));
			count++;
		}
		
		simu.endSimulation();
	}
	
	@Test
	public void testSimulatorVeryFrequentFile() throws IOException {
		
		String file = folder.newFile().getAbsolutePath();
		PatientSimulatorVeryFrequentSymptoms simu = new PatientSimulatorVeryFrequentSymptoms(file);

		for(int disease:sda.getDiseases()){
			simu.simulatePatient(disease, sda.getSymptoms(disease));
		}
		simu.endSimulation();
		
		LinkedList<String> expected = FileInputReader.readAllLinesFrom(
				"../TestData/PhenoToGeno/ExpectedResults/validationVeryFrequentPatient.txt");
		LinkedList<String> actual = FileInputReader.readAllLinesFrom(file);
		assertArrayEquals("Patient file is incorrect", expected.toArray(new String[0]), actual.toArray(new String[0]));
	}

}
