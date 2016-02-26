package phenomizeralgorithm;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import io.FileUtilities;

public class TestFrequencyConverter {

	@Test
	public void testConversionOfAllFrequencies() {
		HashMap<Integer,LinkedList<String[]>> ksz_with_freq_text = FileUtilities.readInKSZFrequency(
				"../TestData/DiseasesAndSymptoms/ksz_freq.txt");
		HashMap<Integer,LinkedList<Integer[]>> ksz_with_freq = FrequencyConverter.convertAll(ksz_with_freq_text);
		
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

}
