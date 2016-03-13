package phenomizeralgorithm;

import static org.junit.Assert.*;

import org.junit.Test;

import phenomizer.algorithm.ComparatorPhenoPval;
import phenomizer.algorithm.ComparatorPhenoScore;

public class TestComparatorPheno {

	@Test
	public void testComparatorScore() {
		
		String[] res1= new String[]{"101", "1.2"};
		String[] res2 = new String[]{"102", "1.2"};
		String[] res3 = new String[]{"103", "4.2"};
		String[] res4 = new String[]{"104", "-0.01"};
		
		ComparatorPhenoScore c = new ComparatorPhenoScore();
		assertEquals("Comparison of result 1 and result 1 failed",0 ,c.compare(res1, res1));
		assertTrue("Comparison of result 1 and result 2 failed",c.compare(res1, res2)<0);
		assertTrue("Comparison of result 1 and result 3 failed",c.compare(res1, res3)>0);
		assertTrue("Comparison of result 1 and result 4 failed",c.compare(res1, res4)<0);
		
		assertEquals("Score comparison of result 1 and result 1 failed",0 ,c.compareWithoutID(res1, res1));
		assertEquals("Score comparison of result 1 and result 2 failed",0 ,c.compareWithoutID(res1, res2));
		assertTrue("Score comparison of result 1 and result 3 failed",c.compareWithoutID(res1, res3)>0);
		assertTrue("Score comparison of result 1 and result 4 failed",c.compareWithoutID(res1, res4)<0);
	}
	
	@Test
	public void testComparatorPval() {
		
		String[] res1= new String[]{"101", "1.2", "0.05"};
		String[] res2 = new String[]{"102", "1.2", "0.05"};
		String[] res3 = new String[]{"103", "3", "0.05"};
		String[] res4 = new String[]{"104", "0.5", "0.05"};
		String[] res5 = new String[]{"105", "2.2", "0.1"};
		String[] res6 = new String[]{"106", "4.2", "0.0"};
		
		ComparatorPhenoPval c = new ComparatorPhenoPval();
		assertEquals("Comparison of result 1 and result 1 failed",0 ,c.compare(res1, res1));
		assertTrue("Comparison of result 1 and result 2 failed",c.compare(res1, res2)<0);
		assertTrue("Comparison of result 1 and result 3 failed",c.compare(res1, res3)>0);
		assertTrue("Comparison of result 1 and result 4 failed",c.compare(res1, res4)<0);
		assertTrue("Comparison of result 1 and result 5 failed",c.compare(res1, res5)<0);
		assertTrue("Comparison of result 1 and result 6 failed",c.compare(res1, res6)>0);
		
		assertEquals("Score comparison of result 1 and result 1 failed",0 ,c.compareWithoutID(res1, res1));
		assertEquals("Score comparison of result 1 and result 2 failed",0 ,c.compareWithoutID(res1, res2));
		assertTrue("Score comparison of result 1 and result 3 failed",c.compareWithoutID(res1, res3)>0);
		assertTrue("Score comparison of result 1 and result 4 failed",c.compareWithoutID(res1, res4)<0);
		assertTrue("Score comparison of result 1 and result 5 failed",c.compareWithoutID(res1, res5)<0);
		assertTrue("Score comparison of result 1 and result 6 failed",c.compareWithoutID(res1, res6)>0);
	}

}
