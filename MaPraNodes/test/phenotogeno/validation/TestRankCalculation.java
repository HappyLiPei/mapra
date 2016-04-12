package phenotogeno.validation;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

import io.FileInputReader;
import phenotogeno.algo.ScoredGene;

public class TestRankCalculation {

	@Test
	public void testRankMethod() {
		
		LinkedList<String> ptgRes = FileInputReader.readAllLinesFrom(
				"../TestData/PhenoToGeno/ExpectedResults/expRes_1.txt");
		//remove header
		ptgRes.remove(0);
		LinkedList<ScoredGene> scoredGenes = new LinkedList<ScoredGene>();
		for(String line:ptgRes){
			String [] split = line.split("\t");
			ScoredGene g = new ScoredGene(split[0], Double.parseDouble(split[1]), "");
			scoredGenes.add(g);
		}
		
		String [] allGenes = new String[50];
		for(int i=1; i<=50; i++){
			allGenes[i-1]="MTG"+i;
		}
		
		String [][] inputs= new String [][]{{"MTG12"}, {"MTG15"}, {"MTG23"}, {"MTG26"}, {"MTG50"}, {"MTG50", "MTG51"},
			{"MTG14", "MTG4", "MTG8"},{"MTG21", "MTG3", "MTG16"},{"MTG22","MTG23","MTG18"}, allGenes};
		double [][] expected = new double[][]{{15},{1},{3.5},{38},{38}, {38, 0}, {6,6,6}, {2,8,17.5}, {3.5,3.5,9},
			{23.5, 23.5, 8, 6, 11.5, 11.5, 11.5, 6, 20, 20,
			23.5, 15 ,23.5, 6, 1, 17.5, 14, 9, 17.5, 16, 
			2, 3.5, 3.5, 11.5, 20, 38, 38, 38, 38, 38, 
			38, 38, 38, 38, 38, 38, 38, 38, 38, 38,
			38, 38, 38, 38, 38, 38, 38, 38, 38, 38}};
		
		for(int i=0; i<inputs.length; i++){
			double [] res =RankCalculator.getRanks(inputs[i], scoredGenes);
			assertArrayEquals("Input "+(i+1)+" is not ranked correctly", expected[i], res, 1E-10); 
		}
	}
	
	@Test
	public void testMinMaxAvg(){
		
		double[] ranks1 = new double []{7,4,2,8,11,42};
		double[] ranks2 = new double []{1,42, 1, 42, 1, 42};
		
		assertEquals("Best rank incorrect for ranks1", 2, RankCalculator.getBestRank(ranks1), 1E-10);
		assertEquals("Worst rank incorrect for ranks1", 42, RankCalculator.getWorstRank(ranks1), 1E-10);
		assertEquals("Average rank incorrect for ranks1", 74d/6d , RankCalculator.getAverageRank(ranks1), 1E-10);
		
		assertEquals("Best rank incorrect for ranks2", 1, RankCalculator.getBestRank(ranks2), 1E-10);
		assertEquals("Worst rank incorrect for ranks2", 42, RankCalculator.getWorstRank(ranks2), 1E-10);
		assertEquals("Average rank incorrect for ranks2", 21.5, RankCalculator.getAverageRank(ranks2), 1E-10);
		
		double [] ranks = new double[2];
		ranks[0] = RankCalculator.getAverageRank(new double[] {7,4,2});
		ranks[1] = RankCalculator.getAverageRank(new double[] {8,11,42});
		assertEquals("Average rank incorrect for ranks1", 74d/6d , RankCalculator.getAverageRank(ranks), 1E-10);
		
		
	}

}
