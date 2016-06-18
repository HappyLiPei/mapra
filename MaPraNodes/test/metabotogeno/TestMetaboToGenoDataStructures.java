package metabotogeno;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import metabotogeno.algo.DataTransformerMTG;
import metabotogeno.io.FileUtilitiesMTG;
import togeno.AnnotatedGene;
import togeno.AnnotatedGeneMax;
import togeno.AnnotatedGeneMultiple;
import togeno.GeneAssociation;
import togeno.ScoredDiseaseOrMetabolite;

public class TestMetaboToGenoDataStructures {
	
	private LinkedList<String> geneList;
	private HashMap<String, LinkedList<String>> asso;
	private DataTransformerMTG dt;
	
	@Before
	public void readFromFiles(){
		geneList = FileUtilitiesMTG.readGeneList(
				"../TestData/PhenoToGeno/all_genes.txt");
		asso = FileUtilitiesMTG.readMetaboliteGeneAssociations(
				"../TestData/MetaboToGeno/gene_metabolites.txt");
		dt = new DataTransformerMTG();
	}
	
	@Test
	public void testGeneAssociationsNormal() {

		GeneAssociation toTest = dt.getMetaboliteGeneAssociations(geneList, asso, true);
		checkContentGeneAssociation(toTest, true);
	}
	
	@Test
	//duplicates in gene list, genes in asso that are not part of the list
	public void testGeneAssociationsRobust() {
		
		geneList.add("MTG50");
		geneList.addFirst("MTG30");
		geneList.add(8, "MTG2");
		
		asso.get("M15").add("MTG100");
		asso.get("M10").add("MTG100");
		asso.get("M04").add("MTG101");
		
		GeneAssociation toTest = dt.getMetaboliteGeneAssociations(geneList, asso, false);
		checkContentGeneAssociation(toTest,false);
	}
	
	@Test
	public void testScoredMetabolitesNormalCase3(){
		
		LinkedList<String[]> scores = FileUtilitiesMTG.readMetaboliteScoreResult("../TestData/MetaboToGeno/case3.txt");
		GeneAssociation mga = dt.getMetaboliteGeneAssociations(geneList, asso, true);
		LinkedList<ScoredDiseaseOrMetabolite> metabos = dt.getMetaboliteScoreResult(scores, mga);
		
		checkContentScoredMetabos(metabos,3);
	}
	
	@Test
	public void testScoredMetabolitesNormalCase4(){
		
		LinkedList<String[]> scores = FileUtilitiesMTG.readMetaboliteScoreResult("../TestData/MetaboToGeno/case4.txt");
		GeneAssociation mga = dt.getMetaboliteGeneAssociations(geneList, asso, true);
		LinkedList<ScoredDiseaseOrMetabolite> metabos = dt.getMetaboliteScoreResult(scores, mga);
		
		checkContentScoredMetabos(metabos,4);
	}
	
	@Test
	public void testScoredMetabolitesRobustCase4(){
		
		LinkedList<String[]> scores = FileUtilitiesMTG.readMetaboliteScoreResult("../TestData/MetaboToGeno/case4.txt");
		scores.add(new String[]{"MTG10", "5.5"});
		scores.addFirst(new String[]{"MTG17", "-1"});
		
		GeneAssociation mga = dt.getMetaboliteGeneAssociations(geneList, asso, false);
		LinkedList<ScoredDiseaseOrMetabolite> metabos = dt.getMetaboliteScoreResult(scores, mga);
		
		checkContentScoredMetabos(metabos,4);
	}
	
	private void checkContentGeneAssociation(GeneAssociation toTest, boolean multiple){
		
		assertEquals("Length of gene array is incorrect", 50, toTest.getAllGenes().length);
		assertEquals("Number of genes is incorrect", 50, toTest.numberOfGenes());
		assertEquals("Number of metabolites is incorrect", 15, toTest.numberOfDiseasesOrMetabolites());
		
		String[][] associationsByMetabo = new String [][]{{"MTG20"}, {"MTG21"}, {"MTG22", "MTG23", "MTG24"},
			{"MTG22", "MTG24", "MTG25", "MTG19"}, {"MTG25", "MTG19", "MTG18"}, {"MTG16", "MTG17", "MTG15"},
			{"MTG15"}, {"MTG15"}, {"MTG29", "MTG26", "MTG27", "MTG28"}, {"MTG26", "MTG27", "MTG28", "MTG30"},
			{},{},{},{},{}};
			
		for(int i=1; i<10; i++){
			AnnotatedGene [] genes = toTest.getGenesForDiseaseMetaboliteWithID("M0"+i);
			assertArrayEquals("Genes of metabolite M0"+i+" are incorrect", associationsByMetabo[i-1], getGeneIds(genes));
		}
		
		for(int i=11; i<16; i++){
			AnnotatedGene [] genes = toTest.getGenesForDiseaseMetaboliteWithID("M"+i);
			assertArrayEquals("Genes of metabolite M"+i+" are incorrect", associationsByMetabo[i-1], getGeneIds(genes));
		}
		
		for(AnnotatedGene g: toTest.getAllGenes()){
			if(multiple){
				assertTrue("Annotated Gene "+g.getId()+" is not of type multiple", g instanceof AnnotatedGeneMultiple);
			}
			else{
				assertTrue("Annotated Gene "+g.getId()+" is not of type max", g instanceof AnnotatedGeneMax);
			}
		}

	}
	
	private String[] getGeneIds(AnnotatedGene[] genes){
		String [] out = new String[genes.length];
		int pos =0;
		for(AnnotatedGene g:genes){
			out[pos++]=g.getId();
		}
		return out;
	}
	
	private void checkContentScoredMetabos(LinkedList<ScoredDiseaseOrMetabolite> scoredMetabos, int caseNr){
		
		String[] id=null;
		double[] scores = null;
		if(caseNr==3){
			id = new String[]{"M13", "M01", "M07", "M09", "M10", "M12", "M08", "M03", "M11", "M06", "M15", "M14",
					"M02", "M04", "M05"};
			scores = new double[] {1E-5, 0.028, 0.0455, 0.053, 0.06675, 0.13361, 0.16151, 0.177, 0.24335, 0.61708,
					0.61708, 0.64074, 0.777, 0.955, 1.0 };
		}
		else if(caseNr==4){
			id = new String[]{"M15", "M14", "M13", "M12", "M11", "M10", "M09", "M08", "M07", "M06", "M05", "M04",
					"M03", "M02", "M01"};
			scores = new double[] {1E-5,1E-5,1E-5,1E-5,1E-5, 0.01, 0.02, 0.1, 0.2, 0.4, 0.4, 0.4, 1.0, 1.0, 1.0 };
		}
		
		int position=0;
		for(ScoredDiseaseOrMetabolite m : scoredMetabos){
			assertEquals("Id of metabolite at position "+position+" is incorrect", id[position], m.getId());
			assertEquals("Score of metabolite at position "+position+" is incorrect", scores[position], m.getPval(), 1E-5);
			position++;
		}
	}

}
