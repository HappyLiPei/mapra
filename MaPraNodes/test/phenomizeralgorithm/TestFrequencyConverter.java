package phenomizeralgorithm;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import phenomizer.algorithm.FrequencyConverter;
import phenomizer.io.FileUtilitiesPhenomizer;

public class TestFrequencyConverter {

	@Test
	public void testConversionOfAllFrequencies() {
		HashMap<Integer,LinkedList<String[]>> ksz_with_freq_text = FileUtilitiesPhenomizer.readInKSZFrequency(
				"../TestData/Phenomizer/DiseasesAndSymptoms/ksz_freq.txt");
		HashMap<Integer,LinkedList<Integer[]>> ksz_with_freq = (new FrequencyConverter()).convertAll(ksz_with_freq_text);
		
		int[] expected= new int[]{5, 15, 15, 5, 10, 10, 5, 5, 15, 5, 15, 5, 5, 10, 15, 10, 5, 10, 10, 15,
				5, 10, 5, 5, 15, 10, 5, 10, 15, 5, 10, 15, 5, 5, 10, 15, 10, 5, 5, 10, 15, 10, 5, 5, 10};
		
		int pos =0;
		for(int i=100; i<=110; i++){
			LinkedList<Integer[]> l = ksz_with_freq.get(i);
			for(Integer[]a :l){
				assertEquals("Frequency for disease "+i+" and symptom "+a[0]+" does not match frequency at position "+pos,
						new Integer(expected[pos]), a[1]);
				pos++;
			}
		}
		assertEquals("Number of parsed frequencies is incorrect", expected.length, pos);
	}
	
	@Test
	public void testConversionwithDifferentWeights() {
		HashMap<Integer,LinkedList<String[]>> ksz_with_freq_text = FileUtilitiesPhenomizer.readInKSZFrequency(
				"../TestData/Phenomizer/DiseasesAndSymptoms/ksz_freq.txt");
		FrequencyConverter f = new FrequencyConverter(2,8,42);
		HashMap<Integer,LinkedList<Integer[]>> ksz_with_freq = f.convertAll(ksz_with_freq_text);
		
		int[] expected= new int[]{2, 42, 42, 2, 8, 8, 2, 2, 42, 2, 42, 2, 2, 8, 42, 8, 2, 8, 8, 42,
				2, 8, 2, 2, 42, 8, 2, 8, 42, 2, 8, 42, 2, 2, 8, 42, 8, 2, 2, 8, 42, 8, 2, 2, 8};
		
		int pos =0;
		for(int i=100; i<=110; i++){
			LinkedList<Integer[]> l = ksz_with_freq.get(i);
			for(Integer[]a :l){
				assertEquals("Frequency for disease "+i+" and symptom "+a[0]+" does not match frequency at position "+pos,
						new Integer(expected[pos]), a[1]);
				pos++;
			}
		}
		assertEquals("Number of parsed frequencies is incorrect", expected.length, pos);
	}
	
	@Test
	public void testTermConversion() {
		
		FrequencyConverter f = new FrequencyConverter();
		
		assertEquals("Term \"frequent [Orphanet]\" is not parsed correctly",
				10, f.convertFrequency("frequent [Orphanet]") );
		assertEquals("Term \"very frequent [Orphanet]\" is not parsed correctly",
				15, f.convertFrequency("very frequent [Orphanet]") );
		assertEquals("Term \"frequent [Orphanet], 5% [HPO]\" is not parsed correctly",
				10, f.convertFrequency("frequent [Orphanet], 5% [HPO]") );
		assertEquals("Term \"5% [HPO:koehler]\" is not parsed correctly",
				5, f.convertFrequency("5% [HPO:koehler]") );
		assertEquals("Term \"hallmark[HPO]; frequent[IBIS]\" is not parsed correctly",
				15, f.convertFrequency("hallmark[HPO]; frequent[IBIS]") );
		assertEquals("Term \"1/4 [HPO:curators]\" is not parsed correctly",
				5, f.convertFrequency("1/4 [HPO:curators]"));
		assertEquals("Term \"9 of 10 [HPO]\" is not parsed correctly",
				15, f.convertFrequency("9 of 10 [HPO]") );
		assertEquals("Term \"rare\" is not parsed correctly",
				10, f.convertFrequency("rare") );
	}

}
