package phenotogeno;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import phenotogeno.algo.PhenoToGenoDataTransformer;
import phenotogeno.io.FileUtilitiesPTG;
import togeno.AnnotatedGene;
import togeno.AnnotatedGeneMax;
import togeno.AnnotatedGeneMultiple;
import togeno.GeneAssociation;
import togeno.ScoredDiseaseOrMetabolite;

public class TestPhenoToGenoDataStructures {
	
	private LinkedList<String[]> phenomizer_raw;
	private LinkedList<String> genes_raw;
	private HashMap<Integer, LinkedList<String>> mapping;
	
	@Before
	public void readFiles( ){
		
		phenomizer_raw =
				FileUtilitiesPTG.readPhenomizerResult("../TestData/PhenoToGeno/phenores_8.txt");		
		genes_raw =
				FileUtilitiesPTG.readGeneList("../TestData/PhenoToGeno/all_genes.txt");
		mapping =
				FileUtilitiesPTG.readDiseaseGeneAssociation("../TestData/PhenoToGeno/gene_diseases.txt");
	}

	@Test
	public void testPhenomizerResultNormal() {
		
		PhenoToGenoDataTransformer dt = new PhenoToGenoDataTransformer();
		GeneAssociation dga =dt.getDiseaseGeneAssociation(genes_raw, mapping, true);
		LinkedList<ScoredDiseaseOrMetabolite> pheno_res=dt.getPhenomizerResult(phenomizer_raw, dga);
		checkPhenomizerRes(pheno_res);
	}
	
	@Test
	public void testPhenomizerResultRobust(){
		
		//handle diseases that are not part of the associations
		phenomizer_raw.add(new String[]{"111", "0.8"});
		phenomizer_raw.add(new String[]{"112", "0.8"});
		
		PhenoToGenoDataTransformer dt = new PhenoToGenoDataTransformer();
		GeneAssociation dga =dt.getDiseaseGeneAssociation(genes_raw, mapping, false);
		LinkedList<ScoredDiseaseOrMetabolite> pheno_res=dt.getPhenomizerResult(phenomizer_raw, dga);	
		checkPhenomizerRes(pheno_res);
	}
	
	@Test
	public void testDiseaseGeneAssociationMultipleMode(){
		PhenoToGenoDataTransformer dt = new PhenoToGenoDataTransformer();
		GeneAssociation dga =dt.getDiseaseGeneAssociation(genes_raw, mapping, true);
		checkGeneAssociation(dga, true);		
	}
	
	@Test
	public void testDiseaseGeneAssociationRobust(){
		
		//handle duplicate entries in gene list
		genes_raw.add("MTG1");
		genes_raw.add("MTG50");
		
		//add gene that is not in the gene list and add one duplicate entry
		mapping.get(100).add("MTG51");
		mapping.get(108).add("MTG24");
		
		PhenoToGenoDataTransformer dt = new PhenoToGenoDataTransformer();
		GeneAssociation dga =dt.getDiseaseGeneAssociation(genes_raw, mapping, true);
		checkGeneAssociation(dga, true);
	}
	
	@Test
	public void testDiseaseGeneAssociationMaxMode(){
		PhenoToGenoDataTransformer dt = new PhenoToGenoDataTransformer();
		GeneAssociation dga =dt.getDiseaseGeneAssociation(genes_raw, mapping, false);
		checkGeneAssociation(dga, false);
	}
	
	private void checkPhenomizerRes(LinkedList<ScoredDiseaseOrMetabolite> actual){
		
		int[] expected_ids=new int[]{109,108,103,106,105,107,102,101,104};
		//position 0: replace p value 0.0 by 0.001
		double [] expected_scores = new double[]{0.001, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8};
			
		assertEquals("Size of Phenomizer Result is incorrect", expected_ids.length, actual.size());
		int pos=0;
		for(ScoredDiseaseOrMetabolite sd : actual){
			assertEquals("Disease id at position "+pos+" is incorrect", expected_ids[pos]+"", sd.getId());
			assertEquals("Score at position "+pos+" is incorrect", expected_scores[pos], sd.getPval(), 1E-10);
			pos++;
		}
	}
	
	private void checkGeneAssociation(GeneAssociation dga, boolean multiple){
		
		//number of diseases and genes
		assertEquals("Disease number is incorrect", 11,dga.numberOfDiseasesOrMetabolites());
		assertEquals("Total number of genes is incorrect", 50, dga.numberOfGenes());
		
		//test all genes
		AnnotatedGene [] all = dga.getAllGenes();
		for(int i=0; i<50; i++){
			assertEquals("Gene "+(i+1)+" is not represented correctly","MTG"+(i+1), all[i].getId());
			if(multiple){
				assertTrue("Type of gene "+(i+1)+" is incorrect", all[i] instanceof AnnotatedGeneMultiple);
			}
			else{
				assertTrue("Type of gene "+(i+1)+" is incorrect", all[i] instanceof AnnotatedGeneMax);
			}
		}
		
		//test retrieval of single genes
		AnnotatedGene g =dga.getGeneWithID("");
		assertNull(g);
		g =dga.getGeneWithID("xyz");
		assertNull(g);
		g=dga.getGeneWithID("MTG1");
		assertEquals("Gene MTG1 is not retrieved correctly", "MTG1", g.getId());
		g=dga.getGeneWithID("MTG42");
		assertEquals("Gene MTG42 is not retrieved correctly", "MTG42", g.getId());
		//objects in allgenes() and getGene() are identical
		assertTrue("Pointers are not set correctly",g==all[41]);
		
		//test gene retrieval for a certain diseases
		AnnotatedGene [] genes =dga.getGenesForDiseaseMetaboliteWithID("100");
		assertEquals("Genes for disease 100 are incorrect", 0, genes.length);
		genes = dga.getGenesForDiseaseMetaboliteWithID("111");
		assertNull(genes);
		genes = dga.getGenesForDiseaseMetaboliteWithID("103");
		String [] ids = getIds(genes);
		assertArrayEquals("Genes for disease 103 are incorrect",
				new String []{"MTG4", "MTG5", "MTG6", "MTG7", "MTG8", "MTG14"}, ids);
		genes = dga.getGenesForDiseaseMetaboliteWithID("108");
		ids = getIds(genes);
		assertArrayEquals("Genes for disease 108 are incorrect",
				new String []{"MTG15", "MTG21", "MTG24"}, ids);
		//object in genes and allgenes() identical
		assertTrue("Pointers are not set correctly",genes[0]==all[14]);
	}
	
	private String [] getIds(AnnotatedGene[] genes){
		String[] ids = new String [genes.length];
		for(int i=0; i<ids.length; i++){
			ids[i]=genes[i].getId();
		}
		return ids;
	}

}
