package metabolites;

import static org.junit.Assert.*;

import org.junit.Test;

import metabolites.types.ReferenceMetaboliteBinary;
import metabolites.types.ReferenceMetaboliteConcentration;
import metabolites.types.ScoredMetabolite;

public class TestMetaboliteScoringInReference {

	@Test
	public void testBinaryReferenceScoring() {
		
		// any value for missingness between 0 and 100
		ReferenceMetaboliteBinary b = new ReferenceMetaboliteBinary("Metabo", 88.8);
		ScoredMetabolite sm = b.scoreMeasurement(Double.NaN, 1);
		assertEquals("Type of ScoredMetabolite (NaN,1) is incorrectr","binary" ,sm.getType());
		assertEquals("Score of ScoredMetabolite (NaN,1) is inccorect", 0, sm.getScore(), 1E-10);
		assertEquals("Porbability of ScoredMetabolite (NaN,1) is incorrect", 0.888, sm.getProbability(), 1E-10);
		sm = b.scoreMeasurement(42, 42);
		assertEquals("Type of ScoredMetabolite (42,42) is incorrectr","binary" ,sm.getType());
		assertEquals("Score of ScoredMetabolite (42,42) is inccorect", 1, sm.getScore(), 1E-10);
		assertEquals("Porbability of ScoredMetabolite (42,42) is incorrect", 0.112, sm.getProbability(), 1E-10);
		
		//missingness = 100
		b = new ReferenceMetaboliteBinary("Metabo", 100);
		sm = b.scoreMeasurement(Double.NaN, 1);
		assertEquals("Type of ScoredMetabolite is incorrectr","binary" ,sm.getType());
		assertEquals("Score of ScoredMetabolite is inccorect", 0, sm.getScore(), 1E-10);
		assertEquals("Porbability of ScoredMetabolite is incorrect", 1, sm.getProbability(), 1E-10);
		sm = b.scoreMeasurement(42, 42);
		assertEquals("Type of ScoredMetabolite is incorrectr","binary" ,sm.getType());
		assertEquals("Score of ScoredMetabolite is inccorect", 1, sm.getScore(), 1E-10);
		assertEquals("Porbability of ScoredMetabolite is incorrect", 0, sm.getProbability(), 1E-10);
		
		//missingness = 0
		b = new ReferenceMetaboliteBinary("Metabo", 0);
		sm = b.scoreMeasurement(Double.NaN, 1);
		assertEquals("Type of ScoredMetabolite is incorrectr","binary" ,sm.getType());
		assertEquals("Score of ScoredMetabolite is inccorect", 0, sm.getScore(), 1E-10);
		assertEquals("Porbability of ScoredMetabolite is incorrect", 0, sm.getProbability(), 1E-10);
		sm = b.scoreMeasurement(42, 42);
		assertEquals("Type of ScoredMetabolite is incorrectr","binary" ,sm.getType());
		assertEquals("Score of ScoredMetabolite is inccorect", 1, sm.getScore(), 1E-10);
		assertEquals("Porbability of ScoredMetabolite is incorrect", 1, sm.getProbability(), 1E-10);
	}
	
	@Test
	public void testConcentrationReferenceScoring(){
		 ReferenceMetaboliteConcentration c = new ReferenceMetaboliteConcentration("MID", 11.2, 
				 new int[]{3, 4, 5, 6, 10, 11}, new double[]{3, -2, 0, 1, 10, 1}, new double[]{1, 0.1 ,0.4 ,2, 0.0, 0.0});
		 ScoredMetabolite sm = c.scoreMeasurement(Double.NaN, 3);
		 assertEquals("Type of ScoredMetabolite is incorrect","binary" ,sm.getType());
		 assertEquals("Score of ScoredMetabolite is inccorect", 0, sm.getScore(), 1E-10);
		 assertEquals("Porbability of ScoredMetabolite is incorrect", 0.112, sm.getProbability(), 1E-10);
		 //all probabilities calculated with R: 1-pnorm(z,0,1)
		 sm =c.scoreMeasurement(1, 3);
		 assertEquals("Type of ScoredMetabolite is incorrect","concentration" ,sm.getType());
		 assertEquals("Score of ScoredMetabolite is inccorect", -2, sm.getScore(), 1E-10);
		 assertEquals("Porbability of ScoredMetabolite is incorrect", 0.02275013, sm.getProbability(), 1E-8);
		 sm =c.scoreMeasurement(1, 4);
		 assertEquals("Type of ScoredMetabolite is incorrect","concentration" ,sm.getType());
		 assertEquals("Score of ScoredMetabolite is inccorect", 30, sm.getScore(), 1E-10);
		 assertEquals("Porbability of ScoredMetabolite is incorrect", 0, sm.getProbability(), 1E-8);
		 sm =c.scoreMeasurement(1, 5);
		 assertEquals("Type of ScoredMetabolite is incorrect","concentration" ,sm.getType());
		 assertEquals("Score of ScoredMetabolite is inccorect", 2.5, sm.getScore(), 1E-10);
		 assertEquals("Porbability of ScoredMetabolite is incorrect", 0.006209665, sm.getProbability(), 1E-8);
		 sm =c.scoreMeasurement(1, 6);
		 assertEquals("Type of ScoredMetabolite is incorrect","concentration" ,sm.getType());
		 assertEquals("Score of ScoredMetabolite is inccorect", 0, sm.getScore(), 1E-10);
		 assertEquals("Porbability of ScoredMetabolite is incorrect", 0.5, sm.getProbability(), 1E-8);
		 
		 //test special cases -> should actually never happen on prepared real data!
		 sm =c.scoreMeasurement(1, 1);
		 assertNull(sm);
		 sm =c.scoreMeasurement(1, 10);
		 assertEquals("Type of ScoredMetabolite is incorrectr","concentration" ,sm.getType());
		 assertTrue("Score of ScoredMetabolite is inccorect", Double.isInfinite(sm.getScore()));
		 assertEquals("Porbability of ScoredMetabolite is incorrect", 0.0, sm.getProbability(), 1E-8);
		 sm =c.scoreMeasurement(1, 11);
		 assertEquals("Type of ScoredMetabolite is incorrect","concentration" ,sm.getType());
		 assertTrue("Score of ScoredMetabolite is inccorect", Double.isNaN(sm.getScore()));
		 assertTrue("Porbability of ScoredMetabolite is incorrect", Double.isNaN(sm.getProbability()));
	}

}
