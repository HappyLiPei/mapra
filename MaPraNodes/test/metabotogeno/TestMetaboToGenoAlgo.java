package metabotogeno;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Test;

import io.FileInputReader;
import metabotogeno.algo.DataTransformerMTG;
import metabotogeno.algo.MetaboToGenoDriver;
import metabotogeno.io.FileUtilitiesMTG;
import togeno.GeneAssociation;
import togeno.ScoredDiseaseOrMetabolite;
import togeno.ScoredGene;
import togeno.ToGenoAlgo;

public class TestMetaboToGenoAlgo {
	
	//used for driver
	private LinkedList<String> all_raw;
	private HashMap<String, LinkedList<String>> asso_raw;
	private LinkedList<String[]> scores_raw;
	//used without driver
	private GeneAssociation mga;
	private LinkedList<ScoredDiseaseOrMetabolite> scores;
	private LinkedList<ScoredGene> expected;
	
	//TODO: define test cases 2,3,4,5,6 with/without driver
	
	@Test
	public void testCase1NoDriver() {
		prepareForCase(1);
		ToGenoAlgo mtg = new ToGenoAlgo(scores, mga);
		LinkedList<ScoredGene> out = mtg.runToGene();
		
		for(ScoredGene g: out){
			System.out.println(g);
		}
		
		checkResult(out);
	}
	
	@Test
	public void testCase1WithDriver(){
		prepareForCase(1);
		MetaboToGenoDriver driver = new MetaboToGenoDriver(all_raw, asso_raw, scores_raw);
		LinkedList<ScoredGene> out = driver.runMetaboToGeno();
		checkResult(out);
	}
	
	private void prepareForCase(int number){
		
		all_raw = FileUtilitiesMTG.readGeneList("../TestData/PhenoToGeno/all_genes.txt");
		asso_raw = FileUtilitiesMTG.readMetaboliteGeneAssociations("../TestData/MetaboToGeno/gene_metabolites.txt");
		scores_raw = FileUtilitiesMTG.readMetaboliteScoreResult("../TestData/MetaboToGeno/case"+number+".txt");
		
		DataTransformerMTG dt = new DataTransformerMTG();
		mga = dt.getMetaboliteGeneAssociations(all_raw, asso_raw);
		scores = dt.getMetaboliteScoreResult(scores_raw, mga);
		
		expected = new LinkedList<ScoredGene>();
		LinkedList<String> expLines = FileInputReader.readAllLinesFrom("../TestData/MetaboToGeno/ExpectedRes/resCase"+number+".txt");
		expLines.remove(0);
		for (String line:expLines){
			String [] split = line.split("\t");
			ScoredGene gene = new ScoredGene(split[0], Double.parseDouble(split[1]), split[2]);
			expected.add(gene);
		}
	}
	
	private void checkResult(LinkedList<ScoredGene> actual){
		
		assertEquals("Size of the result is incorrect", expected.size(), actual.size());
		
		Iterator<ScoredGene> iterExp=expected.iterator();
		Iterator<ScoredGene> iterAct=actual.iterator();
		int position=1;
		while(iterExp.hasNext()){
			ScoredGene gExp = iterExp.next();
			ScoredGene gAct = iterAct.next();
			assertEquals("Gene id at position "+position+" is incorrect", gExp.getId(), gAct.getId());
			assertEquals("Score at position "+position+" is incorrect", gExp.getScore(), gAct.getScore(),1E-5);
			assertEquals("Contributors at position "+position+" are incorrect", 
					gExp.getImportantContributors(), gAct.getImportantContributors());
		}
	}

}
