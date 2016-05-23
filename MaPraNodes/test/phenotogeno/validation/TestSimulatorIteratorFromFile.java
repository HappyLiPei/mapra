package phenotogeno.validation;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

import io.FileInputReader;

public class TestSimulatorIteratorFromFile {
	
	@Test
	public void testTransformationPatientString() {
		LinkedList<String> patientsFromFile = FileInputReader.readAllLinesFrom(
				"../TestData/PhenoToGeno/PatientFiles/PatientsQueries.txt");
		int counter =1;
		for(String line: patientsFromFile){
			SimulatedPatient p = SimulatedPatient.StringToPatient(line);
			String patientString =p.toString();
			assertEquals("Line "+counter+" is not parsed correctly", line, patientString);
		}
	}
	
	@Test
	public void testIterator(){
		DiseaseIterator iterator = new SimulatorIteratorFromFile(
				"../TestData/PhenoToGeno/PatientFiles/PatientsQueries.txt");
		assertEquals("Total number of iterations is incorrect",11, iterator.totalIterations());
		assertTrue("Check for next iteration failed (beginning)", iterator.hasNextId());
		for(int i=0; i<11; i++){
			assertTrue("Check for next iteration failed (iteration "+i+")", iterator.hasNextId());
			assertEquals("",100+i,iterator.getNextDiseaseId());
		}
		assertFalse("Check for next iteration failed (end)", iterator.hasNextId());
	}
	
	@Test
	public void testSimulator(){
		PatientSimulator simulator  = new SimulatorIteratorFromFile(
				"../TestData/PhenoToGeno/PatientFiles/PatientsQueries.txt");
		Integer[][] symptoms = {{23,24,11,34,2,33,39,30},{35,8,17,19,24,25,11,12},{23,11,34,28,2},
				{37,38,33,25,15,2},{13},{37,38,33,25,15,2,26,31},{10,9},{23,34,27,40},{23,11,33,34,39,30},
				{20,34,8,14,39},{}};
		for(int i=0; i<11; i++){
			SimulatedPatient p = simulator.simulatePatient(-1, null);
			assertEquals("Patient id of patient "+i+" is incorrect", i+"", p.getId());
			assertEquals("Disease id of patient "+i+" is incorrect", 100+i, p.getDisease());
			assertArrayEquals("Symptoms of patient "+i+" are incorrect", symptoms[i],p.getSymptoms().toArray(new Integer[0]));
		}
	}

}
