package metabolites;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import org.junit.Test;

import metabolites.io.FileUtilitiesMetabolites;

public class TestFileUtilitiesMetabolites {

	@Test
	public void testCaseReader() {
		
		LinkedList<String[]> caseMetabol = FileUtilitiesMetabolites.readMeasurements(
				"/home/marie-sophie/Uni/master/mapra/TestData/Metabolites/case1.txt");
		
		assertEquals("Number of measured metabolites is incorrect", 15, caseMetabol.size());
		assertArrayEquals("Line 2 of measured metabolites is not parsed correctly",
				new String [] {"M01", "", "1"}, caseMetabol.get(0));
		assertArrayEquals("Line 4 of measured metabolites is not parsed correctly",
				new String [] {"M03", "", "1"}, caseMetabol.get(2));
		assertArrayEquals("Line 12 of measured metabolites is not parsed correctly",
				new String [] {"M11", "0.0", "1"}, caseMetabol.get(10));
		assertArrayEquals("Line 16 of measured metabolites is not parsed correctly",
				new String [] {"M15", "-0.7", "1"}, caseMetabol.get(14));
	}
	
	@Test
	public void testControlReader() {
		
		HashMap<String, LinkedList<String[]>> controlMetabol = FileUtilitiesMetabolites.readReferences(
				"/home/marie-sophie/Uni/master/mapra/TestData/Metabolites/reference.txt");
		
		Set<String> mids = controlMetabol.keySet();
		assertEquals("Number of reference metabolites is incorrect", 15, mids.size());
		String [] ids = new String []{"M01", "M02", "M03", "M04", "M05", "M06", "M07", "M08", "M09",
				"M10", "M11", "M12", "M13", "M14", "M15"};
		for(String i:ids){
			assertTrue("Reference metabolite "+i+" is missing", mids.contains(i));
		}
		
		//M01+M05
		String [] binId = new String[]{"M01", "M05"};
		String [] miss = new String[]{"97.2", "100.0"};
		for(int p=0; p<binId.length; p++){
			LinkedList<String[]> l1 = controlMetabol.get(binId[p]);
			assertEquals("Reference Metabolite "+binId[p]+" is not parsed correctly", 1, l1.size());
			assertArrayEquals("Binary Metabolite data of "+binId[p]+" is not parsed correctly",
					new String[]{binId[p], "binary", "","","","", miss[p]}, l1.get(0));
		}
		
		//M06 + M10 + M15
		String[][][] concData = new String [][][]{
			{{"M06", "concentration", "1", "-1.0", "0.2", "-1.5", "0.0"},
				{"M06", "concentration", "2", "0.0", "0.5", "-1.5", "0.0"},
				{"M06", "concentration", "3", "1.0", "0.2", "-1.5", "0.0"}},
			{{"M10", "concentration", "1", "0.2", "1.4", "-4.0", "0.0"},	
				{"M10", "concentration", "2", "-2.0", "1.3", "-4.0", "0.0"},
				{"M10", "concentration", "3", "2.5", "1.2", "-4.0", "0.0"}},
			{{"M15", "concentration", "1", "-1.8", "0.2", "-2.8", "0.0"},
				{"M15", "concentration", "2", "-1.2", "0.1", "-2.8", "0.0"},
				{"M15", "concentration", "3", "-1.4", "0.2", "-2.8", "0.0"}}}; 
		
		for(int q=0; q<3; q++){
			LinkedList<String []> l2 = controlMetabol.get(concData[q][0][0]);
			assertEquals("Reference Metabolite "+concData[q][0][0]+" is not parsed correctly", 3, l2.size());
			for(int p=0; p<3; p++){
				assertArrayEquals("Reference metabolite "+concData[q][0][0]+" group "+(p+1)+" data is not parsed correctly",
						concData[q][p], l2.get(p));
			}
		}
	}

}
