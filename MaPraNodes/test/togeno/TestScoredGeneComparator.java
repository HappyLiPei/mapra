package togeno;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import togeno.ScoredGene;
import togeno.ScoredGeneComparator;

public class TestScoredGeneComparator {

	@Test
	public void testComparator5DecimalPlaces() {
		
		ScoredGeneComparator c = new ScoredGeneComparator();
		
		ScoredGene g1 = new ScoredGene("G1", 0.34, "");
		ScoredGene g2 = new ScoredGene("G2", 0.523, "");
		ScoredGene g3 = new ScoredGene("G2", 0.52300001, "");
		ScoredGene g4 = new ScoredGene("G4", 0.111108, "");
		ScoredGene g5 = new ScoredGene("G5", 0.111112,"");
		
		//g1 is bigger than g2 because g1 has lower rank
		assertTrue("Comparison g1-g2 incorrect", c.compare(g1, g2)>0);
		assertTrue("Comparison g2-g1 incorrect", c.compare(g2, g1)<0);
		//g1 is equal to itself
		assertTrue("Comparison g1-g1 incorrect", c.compare(g1, g1)==0);
		//consider only 5 decimal places -> g2 and g3 are equal
		assertTrue("Comparison g2-g3 incorrect", c.compare(g2, g3)==0);
		//consider only 5 decimal places -> g4 and g5 are compared with respect to their ids
		assertTrue("Comparison g4-g5 incorrect", c.compare(g4, g5)<0);
		assertTrue("Comparison g5-g4 incorrect", c.compare(g5, g4)>0);
	}
	
	@Test
	public void testSortingDifferentNumberOfDecimalPlaces(){
		
		ScoredGene g1 = new ScoredGene("G1", 0.34, "");
		ScoredGene g2 = new ScoredGene("G2", 0.523, "");
		ScoredGene g3 = new ScoredGene("G3",0.00001, "");
		ScoredGene g4 = new ScoredGene("G4", 0.111108, "");
		ScoredGene g5 = new ScoredGene("G5", 0.111112,"");
		ScoredGene g6 = new ScoredGene("G6", 0.00002, "");
		
		checkSorting(new ScoredGeneComparator(), new ScoredGene[]{g1,g2,g3,g4,g5,g6},
				new String []{"G2", "G1", "G4", "G5", "G6", "G3"}, 5);
		checkSorting(new ScoredGeneComparator(2), new ScoredGene[]{g1,g2,g3,g4,g5,g6},
				new String []{"G2", "G1", "G4", "G5", "G3", "G6"}, 2);
		checkSorting(new ScoredGeneComparator(10), new ScoredGene[]{g1,g2,g3,g4,g5,g6},
				new String []{"G2", "G1", "G5", "G4", "G6", "G3"}, 10);
	}
	
	private void checkSorting(ScoredGeneComparator c, ScoredGene[] g, String [] expected_order, int places){
		Arrays.sort(g, c);
		for (int i = 0; i<expected_order.length; i++){
			assertEquals("Id at position "+i+" is incorrect (sorting accorind to "+places+" decimal places",
					expected_order[i] , g[i].getId());
		}		
	}

}
