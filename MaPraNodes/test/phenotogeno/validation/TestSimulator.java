package phenotogeno.validation;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

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
	
	@Test
	public void testSimulatorDrawSymptom() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		String file = folder.newFile().getAbsolutePath();
		PatientSimulatorDrawSymptoms simu = new PatientSimulatorDrawSymptoms(file);
		Field f = PatientSimulatorDrawSymptoms.class.getDeclaredField("randomNumberGenerator");
		f.setAccessible(true);
		Random r = (Random) f.get(simu);
		
		SimulatedPatient p1 = simu.simulatePatient(104, sda.getSymptoms(104));
		assertNull("Patient 1 (draw symptoms) is incorrect", p1);
		
		int[] symptoms = new int[]{50, 51, 52, 53, 54, 55, 56, 57, 58, 59};
		LinkedList<Integer[]> anno = new LinkedList<Integer[]>();
		for(int i=0; i<5; i++){
			anno.add(new Integer[] {symptoms[i],2});
		}
		
		SimulatedPatient p2 = simu.simulatePatient(111, anno);
		assertEquals("Patient id of Patient 2 is incorrect", 0+"", p2.getId());
		assertEquals("Disease id of Patient 2 is incorrect", 111, p2.getDisease());
		assertArrayEquals("Symptoms of Patient 2 are incorrect", new Integer[]{50,51,52,53,54},
				p2.getSymptoms().toArray(new Integer[0]));
		
		//6 symptoms with mixed frequency -> always keep
		int[] weights = new int []{15,15,10,5,10,15};
		anno = new LinkedList<Integer[]>();
		for(int i=0; i<6; i++){
			anno.add(new Integer[]{symptoms[i], weights[i]});
		}
		r.setSeed(42);
		SimulatedPatient p3 = simu.simulatePatient(112, anno);
		assertEquals("Patient id of Patient 3 is incorrect", 1+"", p3.getId());
		assertEquals("Disease id of Patient 3 is incorrect", 112, p3.getDisease());
		assertArrayEquals("Symptoms of Patient 3 are incorrect", new Integer[]{50,51,52,54,55},
				p3.getSymptoms().toArray(new Integer[0]));
		
		anno = new LinkedList<Integer[]>();
		for(int i=0; i<10; i++){
			if(i<5){
				anno.add(new Integer[]{symptoms[9-i], 10});
			}
			else{
				anno.add(new Integer[]{symptoms[9-i], 5});
			}
		}
		r.setSeed(42);
		SimulatedPatient p4 = simu.simulatePatient(113, anno);
		assertEquals("Patient id of Patient 4 is incorrect", 2+"", p4.getId());
		assertEquals("Disease id of Patient 2 is incorrect", 113, p4.getDisease());
		assertArrayEquals("Symptoms of Patient 2 are incorrect", new Integer[]{57,56,58,59,50},
				p4.getSymptoms().toArray(new Integer[0]));
		
		/* first 25 random numbers for seed 42
		 * 0.7275636800328681, 0.6832234717598454, 0.30871945533265976, 0.27707849007413665, 0.6655489517945736
		 * 0.9033722646721782, 0.36878291341130565, 0.2757480694417024, 0.46365357580915334, 0.7829017787900358
		 * 0.9193277828687169, 0.43649097442328655, 0.7499061812554475, 0.38656687435934867, 0.17737847790937833
		 * 0.5943499108896841, 0.20976756886633208, 0.825965871887821, 0.17221793768785243, 0.5874273817862956
		 * 0.7512804067674601, 0.5710403484148672, 0.5800248845020607, 0.752509948590651, 0.03141823882658079
		*/
	}

}
