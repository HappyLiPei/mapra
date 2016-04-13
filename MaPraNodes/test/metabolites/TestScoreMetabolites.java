package metabolites;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Test;

import io.FileInputReader;
import metabolites.algo.ScoreMetabolitesDriver;
import metabolites.io.FileUtilitiesMetabolites;
import metabolites.types.ScoredMetabolite;

public class TestScoreMetabolites {
	
	private LinkedList<String []> caseM;
	private HashMap<String, LinkedList<String[]>> controls;
	private LinkedList<String> expected;
	
	private void readRequiredData(int caseNr){
		caseM = FileUtilitiesMetabolites.readMeasurements("../TestData/Metabolites/case"+caseNr+".txt");
		controls = FileUtilitiesMetabolites.readReferences("../TestData/Metabolites/reference.txt");
		expected = FileInputReader.readAllLinesFrom("../TestData/Metabolites/ExpectedResults/resCase"+caseNr+".txt");
		//removes header of table
		expected.remove(0);
	}

	@Test
	public void testWholeMetaboliteScoringProcedure() {
		
		for(int i=1; i<=3; i++){
			readRequiredData(i);
			ScoreMetabolitesDriver driver = new ScoreMetabolitesDriver(caseM, controls);
			LinkedList<ScoredMetabolite> result = driver.runMetaboliteScoring();
			
			assertEquals("Number of scored metabolites is incorrect", expected.size(), result.size());
			Iterator<String> iter_exp = expected.iterator();
			Iterator<ScoredMetabolite> iter_actual = result.iterator();
			while(iter_exp.hasNext()){
				ScoredMetabolite metabo = iter_actual.next();
				String [] line = iter_exp.next().split("\t");
				assertEquals("Id of metabolite "+line[0]+ "in test case "+i+" is incorrect", line[0], metabo.getId());
				assertEquals("Type of metabolite "+line[0]+ "in test case "+i+" is incorrect", line[1], metabo.getType());
				assertEquals("Score of metabolite "+line[0]+ "in test case "+i+" is incorrect",
						Double.parseDouble(line[2]), metabo.getScore(), 1E-10);
				assertEquals("Probability of metabolite "+line[0]+ "in test case "+i+" is incorrect",
						Double.parseDouble(line[3]), metabo.getProbability(), 1E-10);
			}
		}
	}

}
