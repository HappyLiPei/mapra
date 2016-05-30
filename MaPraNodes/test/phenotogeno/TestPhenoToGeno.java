package phenotogeno;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import io.FileInputReader;
import phenotogeno.algo.DiseaseGeneAssociation;
import phenotogeno.algo.PhenoToGenoAlgo;
import phenotogeno.algo.PhenoToGenoDataTransformer;
import phenotogeno.algo.PhenoToGenoDriver;
import phenotogeno.algo.ScoredDisease;
import phenotogeno.io.FileUtilitiesPTG;
import togeno.ScoredGene;

public class TestPhenoToGeno {
	
	private LinkedList<String> genes_raw;
	private HashMap<Integer, LinkedList<String>> map_raw;
	
	@Before
	public void readFiles(){
		genes_raw = FileUtilitiesPTG.readGeneList("../TestData/PhenoToGeno/all_genes.txt");
		map_raw = FileUtilitiesPTG.readDiseaseGeneAssociation("../TestData/PhenoToGeno/gene_diseases.txt");
	}
	

	@Test
	public void test() {
		//iterate over 8 test cases
		for(int num=1; num<=8; num++){
			
			//get phenomizer result
			LinkedList<String[] > query = FileUtilitiesPTG.readPhenomizerResult(
					"../TestData/PhenoToGeno/phenores_"+num+".txt");
			PhenoToGenoDriver driver = new PhenoToGenoDriver(query, genes_raw, map_raw);
			LinkedList<ScoredGene> actual = driver.runPhenoToGeno();
			
			//check results
			compareToExpected(num, actual);
		}
	}
	
	@Test
	public void testWithReuse(){
		
		PhenoToGenoDataTransformer dt = new PhenoToGenoDataTransformer();
		DiseaseGeneAssociation dga = dt.getDiseaseGeneAssociation(genes_raw, map_raw);
		
		//iterate over 8 test cases
		for(int num=1; num<=8; num++){
			
			//get phenomizer result
			LinkedList<String[] > query = FileUtilitiesPTG.readPhenomizerResult(
					"../TestData/PhenoToGeno/phenores_"+num+".txt");
			LinkedList<ScoredDisease> phenoRes = dt.getPhenomizerResult(query, dga);
			
			PhenoToGenoAlgo a = new PhenoToGenoAlgo(phenoRes,dga);
			LinkedList<ScoredGene> actual = a.runPhenoToGene();
			
			//check results
			compareToExpected(num, actual);
			
			dga.resetDiseaseScores();
		}	
	}
	
	private void compareToExpected(int num, LinkedList<ScoredGene> actual){
		
		//get expected result
		LinkedList<String> expected = FileInputReader.readAllLinesFrom(
				"../TestData/PhenoToGeno/ExpectedResults/expRes_"+num+".txt");
		//remove header
		expected.remove(0);
		
		//compare
		assertEquals("Result size for result "+num+" is incorrect", expected.size(), actual.size());
		Iterator<ScoredGene> ig = actual.iterator();
		Iterator<String> is = expected.iterator();
		for(int i=0; i<genes_raw.size(); i++){
			ScoredGene gActual = ig.next();
			String[] gExpected = is.next().split("\t");
			assertEquals("Gene id at row "+(i+1)+" of result "+num+" is incorrect",
					gExpected[0],gActual.getId());
			assertEquals("Gene score at row "+(i+1)+" of result "+num+" is incorrect",
					gExpected[1], gActual.getScore()+"");
			if(gExpected.length==2){
				assertEquals("Disease annotation at row "+(i+1)+" of result "+num+" is incorrect",
						"",gActual.getImportantContributors());
			}
			else{
				assertEquals("Disease annotation at row "+(i+1)+" of result "+num+" is incorrect",
						gExpected[2],gActual.getImportantContributors());
			}
		}
	}

}
