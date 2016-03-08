package phenomizeralgorithm;

import static org.junit.Assert.*;

import org.junit.Test;

import phenomizer.algorithm.PValueFolder;

public class TestPValueFolder {

	@Test
	public void testGetFile() {
		
		String folder = "../TestData/PValues";
		PValueFolder p = new PValueFolder(folder);
		
		assertEquals("Incorrect pvalue file for query length 1",
				"../TestData/PValues/length_1.txt", p.getPvalFile(1));
		assertEquals("Incorrect pvalue file for query length 11",
				"../TestData/PValues/length_10.txt", p.getPvalFile(11));
		assertEquals("Incorrect pvalue file for query length 0",
				"", p.getPvalFile(0));
		assertEquals("Incorrect pvalue file for query length -42",
				"", p.getPvalFile(-42));
	}
	
	@Test
	public void testCheckFile(){
		String folder1="../TestData/PValues";
		String folder2="../TestData/Queries";
		PValueFolder p = new PValueFolder(folder1);
		PValueFolder p2 = new PValueFolder(folder2);
		
		assertTrue("File for length 5 not found in "+folder1, p.checkFile(5));
		assertFalse("File for length 11 should not be in "+folder1, p.checkFile(11));
		assertFalse("File for length -1 should not be in "+folder1, p.checkFile(-1));
		assertFalse("File for length 5 should not be in "+folder2, p2.checkFile(5));
	}

}
