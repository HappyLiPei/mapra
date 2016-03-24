package phenotogeno;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import io.FileInputReader;
import phenotogeno.algo.PhenoToGenoDriver;
import phenotogeno.algo.ScoredGene;
import phenotogeno.io.FileUtilitiesPTG;

public class TestPhenoToGeno {
	
	private LinkedList<String> genes_raw;
	private HashMap<Integer, LinkedList<String>> map_raw;
	
	@Before
	public void readFiles(){
		genes_raw = FileUtilitiesPTG.readGeneList("../TestData/PhenoToGeno/all_genes.txt");
		map_raw = FileUtilitiesPTG.readDiseaseGeneAssociation("../TestData/PhenoToGeno/gene_diseases.txt");
	}
	

	@Test
	//TODO: case 5,6,7,8
	public void test() {
		//iterate over 8 test cases
		for(int num=1; num<=4; num++){
			
			//get phenoToGeno result
			LinkedList<String[] > query = FileUtilitiesPTG.readPhenomizerResult(
					"../TestData/PhenoToGeno/phenores_"+num+".txt");
			PhenoToGenoDriver driver = new PhenoToGenoDriver(query, genes_raw, map_raw);
			LinkedList<ScoredGene> actual = driver.runPhenoToGeno();
			
			//get expected result
			LinkedList<String> expected = FileInputReader.readAllLinesFrom(
					"../TestData/PhenoToGeno/ExpectedResults/expRes_"+num+".txt");
			//remove header
			expected.remove(0);
			
			//compare
			assertEquals("Result size is incorrect", expected.size(), actual.size());
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
							"",gActual.getImportantDiseases());
				}
				else{
					assertEquals("Disease annotation at row "+(i+1)+" of result "+num+" is incorrect",
							gExpected[2],gActual.getImportantDiseases());
				}
			}
		}
	}

}
