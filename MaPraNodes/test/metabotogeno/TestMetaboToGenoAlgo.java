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
	
	@Test
	public void testCase1MultNoDriver() {
		prepareForCase(1,true);
		ToGenoAlgo mtg = new ToGenoAlgo(scores, mga);
		LinkedList<ScoredGene> out = mtg.runToGene();		
		checkResult(out);
	}
	
	@Test
	public void testCase1MaxNoDriver() {
		prepareForCase(1,false);
		ToGenoAlgo mtg = new ToGenoAlgo(scores, mga);
		LinkedList<ScoredGene> out = mtg.runToGene();		
		checkResult(out);
	}
	
	@Test
	public void testCase2MultNoDriver() {
		prepareForCase(2,true);
		ToGenoAlgo mtg = new ToGenoAlgo(scores, mga);
		LinkedList<ScoredGene> out = mtg.runToGene();
		checkResult(out);
	}
	
	@Test
	public void testCase2MaxNoDriver() {
		prepareForCase(2,false);
		ToGenoAlgo mtg = new ToGenoAlgo(scores, mga);
		LinkedList<ScoredGene> out = mtg.runToGene();
		checkResult(out);
	}
	
	@Test
	public void testCase3MultNoDriver() {
		prepareForCase(3, true);
		ToGenoAlgo mtg = new ToGenoAlgo(scores, mga);
		LinkedList<ScoredGene> out = mtg.runToGene();
		checkResult(out);
	}
	
	@Test
	public void testCase3MaxNoDriver() {
		prepareForCase(3, false);
		ToGenoAlgo mtg = new ToGenoAlgo(scores, mga);
		LinkedList<ScoredGene> out = mtg.runToGene();
		checkResult(out);
	}
	
	@Test
	public void testCase4MultNoDriver() {
		prepareForCase(4, true);
		ToGenoAlgo mtg = new ToGenoAlgo(scores, mga);
		LinkedList<ScoredGene> out = mtg.runToGene();
		checkResult(out);
	}
	
	@Test
	public void testCase4MaxNoDriver() {
		prepareForCase(4, false);
		ToGenoAlgo mtg = new ToGenoAlgo(scores, mga);
		LinkedList<ScoredGene> out = mtg.runToGene();
		checkResult(out);
	}
	
	@Test
	public void testCase5MultNoDriver() {
		prepareForCase(5, true);
		ToGenoAlgo mtg = new ToGenoAlgo(scores, mga);
		LinkedList<ScoredGene> out = mtg.runToGene();
		checkResult(out);
	}
	
	@Test
	public void testCase5MaxNoDriver() {
		prepareForCase(5, false);
		ToGenoAlgo mtg = new ToGenoAlgo(scores, mga);
		LinkedList<ScoredGene> out = mtg.runToGene();
		checkResult(out);
	}
	
	@Test
	public void testCase6MultNoDriver() {
		prepareForCase(6, true);
		ToGenoAlgo mtg = new ToGenoAlgo(scores, mga);
		LinkedList<ScoredGene> out = mtg.runToGene();
		checkResult(out);
	}
	
	@Test
	public void testCase6MaxNoDriver() {
		prepareForCase(6, false);
		ToGenoAlgo mtg = new ToGenoAlgo(scores, mga);
		LinkedList<ScoredGene> out = mtg.runToGene();
		checkResult(out);
	}	
	
	@Test
	public void testCase1MultWithDriver(){
		prepareForCase(1, true);
		MetaboToGenoDriver driver = new MetaboToGenoDriver(all_raw, asso_raw, scores_raw);
		LinkedList<ScoredGene> out = driver.runMetaboToGeno();
		checkResult(out);
	}
	
	@Test
	public void testCase1MaxWithDriver(){
		prepareForCase(1, false);
		MetaboToGenoDriver driver = new MetaboToGenoDriver(all_raw, asso_raw, scores_raw);
		driver.setModeOfAnnotation(false);
		LinkedList<ScoredGene> out = driver.runMetaboToGeno();
		checkResult(out);
	}
	
	@Test
	public void testCase2MultWithDriver(){
		prepareForCase(2, true);
		MetaboToGenoDriver driver = new MetaboToGenoDriver(all_raw, asso_raw, scores_raw);
		LinkedList<ScoredGene> out = driver.runMetaboToGeno();
		checkResult(out);
	}
	
	@Test
	public void testCase2MaxWithDriver(){
		prepareForCase(2, false);
		MetaboToGenoDriver driver = new MetaboToGenoDriver(all_raw, asso_raw, scores_raw);
		driver.setModeOfAnnotation(false);
		LinkedList<ScoredGene> out = driver.runMetaboToGeno();
		checkResult(out);
	}
	
	@Test
	public void testCase3MultWithDriver(){
		prepareForCase(3, true);
		MetaboToGenoDriver driver = new MetaboToGenoDriver(all_raw, asso_raw, scores_raw);
		LinkedList<ScoredGene> out = driver.runMetaboToGeno();
		checkResult(out);
	}
	
	@Test
	public void testCase3MaxWithDriver(){
		prepareForCase(3, false);
		MetaboToGenoDriver driver = new MetaboToGenoDriver(all_raw, asso_raw, scores_raw);
		driver.setModeOfAnnotation(false);
		LinkedList<ScoredGene> out = driver.runMetaboToGeno();
		checkResult(out);
	}
	
	@Test
	public void testCase4MultWithDriver(){
		prepareForCase(4, true);
		MetaboToGenoDriver driver = new MetaboToGenoDriver(all_raw, asso_raw, scores_raw);
		LinkedList<ScoredGene> out = driver.runMetaboToGeno();
		checkResult(out);
	}
	
	@Test
	public void testCase4MaxWithDriver(){
		prepareForCase(4, false);
		MetaboToGenoDriver driver = new MetaboToGenoDriver(all_raw, asso_raw, scores_raw);
		driver.setModeOfAnnotation(false);
		LinkedList<ScoredGene> out = driver.runMetaboToGeno();
		checkResult(out);
	}
	
	@Test
	public void testCase5MultWithDriver(){
		prepareForCase(5, true);
		MetaboToGenoDriver driver = new MetaboToGenoDriver(all_raw, asso_raw, scores_raw);
		LinkedList<ScoredGene> out = driver.runMetaboToGeno();
		checkResult(out);
	}
	
	@Test
	public void testCase5MaxWithDriver(){
		prepareForCase(5, false);
		MetaboToGenoDriver driver = new MetaboToGenoDriver(all_raw, asso_raw, scores_raw);
		driver.setModeOfAnnotation(false);
		LinkedList<ScoredGene> out = driver.runMetaboToGeno();
		checkResult(out);
	}
	
	@Test
	public void testCase6MultWithDriver(){
		prepareForCase(6, true);
		MetaboToGenoDriver driver = new MetaboToGenoDriver(all_raw, asso_raw, scores_raw);
		LinkedList<ScoredGene> out = driver.runMetaboToGeno();
		checkResult(out);
	}
	
	@Test
	public void testCase6MaxWithDriver(){
		prepareForCase(6, false);
		MetaboToGenoDriver driver = new MetaboToGenoDriver(all_raw, asso_raw, scores_raw);
		driver.setModeOfAnnotation(false);
		LinkedList<ScoredGene> out = driver.runMetaboToGeno();
		checkResult(out);
	}
	
	private void prepareForCase(int number, boolean multiple){
		
		all_raw = FileUtilitiesMTG.readGeneList("../TestData/PhenoToGeno/all_genes.txt");
		asso_raw = FileUtilitiesMTG.readMetaboliteGeneAssociations("../TestData/MetaboToGeno/gene_metabolites.txt");
		scores_raw = FileUtilitiesMTG.readMetaboliteScoreResult("../TestData/MetaboToGeno/case"+number+".txt");
		
		DataTransformerMTG dt = new DataTransformerMTG();
		mga = dt.getMetaboliteGeneAssociations(all_raw, asso_raw, multiple);
		scores = dt.getMetaboliteScoreResult(scores_raw, mga);
		
		expected = new LinkedList<ScoredGene>();
		String mode="";
		if(!multiple){
			mode="Max";
		}
		LinkedList<String> expLines = FileInputReader.readAllLinesFrom("../TestData/MetaboToGeno/ExpectedRes/resCase"+number+mode+".txt");
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
			position++;
		}
	}

}
