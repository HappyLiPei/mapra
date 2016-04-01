package phenotogeno;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import phenotogeno.algo.AnnotatedGene;
import phenotogeno.algo.DiseaseGeneAssociation;
import phenotogeno.algo.PhenoToGenoDataTransformer;
import phenotogeno.algo.ScoredDisease;
import phenotogeno.io.FileUtilitiesPTG;

public class TestPhenoToGenoDataStructures {
	
	private LinkedList<ScoredDisease> pheno_res;
	private DiseaseGeneAssociation dga;
	
	private void runDataTransform(boolean testRobustness){
		
		LinkedList<String[]> phenomizer_raw =
				FileUtilitiesPTG.readPhenomizerResult("../TestData/PhenoToGeno/phenores_8.txt");
		//handle diseases that are not part of the associations
		if(testRobustness){
			phenomizer_raw.add(new String[]{"111", "0.8"});
			phenomizer_raw.add(new String[]{"112", "0.8"});
		}
		
		LinkedList<String> genes_raw =
				FileUtilitiesPTG.readGeneList("../TestData/PhenoToGeno/all_genes.txt");
		//handle duplicate entries in gene list
		if(testRobustness){
			genes_raw.add("MTG1");
			genes_raw.add("MTG50");
		}
		HashMap<Integer, LinkedList<String>> mapping =
				FileUtilitiesPTG.readDiseaseGeneAssociation("../TestData/PhenoToGeno/gene_diseases.txt");
		//add gene that is not in the gene list and add one duplicate entry
		if(testRobustness){
			mapping.get(100).add("MTG51");
			mapping.get(108).add("MTG24");
		}
		
		PhenoToGenoDataTransformer dt = new PhenoToGenoDataTransformer();
		dga =dt.getDiseaseGeneAssociation(genes_raw, mapping);
		pheno_res=dt.getPhenomizerResult(phenomizer_raw, dga);
	}

	@Test
	public void testPhenomizerResult() {
		
		boolean [] runs = {false,true};
		for(boolean runMode :runs){
			String mode="";
			if(runMode){
				mode=" (robustness test)";
			}
			runDataTransform(runMode);
			
			int[] expected_ids=new int[]{109,108,103,106,105,107,102,101,104};
			//position 0: replace p value 0.0 by 0.001
			double [] expected_scores = new double[]{0.001, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8};
			
			assertEquals("Size of Phenomizer Result is incorrect"+mode, 9, pheno_res.size());
			int pos=0;
			for(ScoredDisease sd : pheno_res){
				assertEquals("Disease id at position "+pos+" is incorrect"+mode, expected_ids[pos], sd.getId());
				assertEquals("Score at position "+pos+" is incorrect"+mode, expected_scores[pos], sd.getPval(), 1E-10);
				pos++;
			}
		}
	}
	
	@Test
	public void testDiseaseGeneAssociation(){
		
		boolean [] runs = {false,true};
		for(boolean runMode :runs){
			String mode="";
			if(runMode){
				mode=" (robustness test)";
			}
			runDataTransform(runMode);
			
			//test all genes
			AnnotatedGene [] all = dga.getAllGenes();
			for(int i=0; i<50; i++){
				assertEquals("Gene "+(i+1)+" is not represented correctly"+mode,"MTG"+(i+1), all[i].getId());
			}
			
			//test number of genes
			assertEquals("Total number of genes is incorrect"+mode, 50, dga.numberOfGenes());
			
			//test retrieval of single genes
			AnnotatedGene g =dga.getGeneWithID("");
			assertNull(g);
			g =dga.getGeneWithID("xyz");
			assertNull(g);
			g=dga.getGeneWithID("MTG1");
			assertEquals("Gene MTG1 is not retrieved correctly"+mode, "MTG1", g.getId());
			g=dga.getGeneWithID("MTG42");
			assertEquals("Gene MTG42 is not retrieved correctly"+mode, "MTG42", g.getId());
			
			//objects in allgenes() and getGene() are identical
			assertTrue("Pointers are not set correctly"+mode,g==all[41]);
			
			//test gene retrieval for a certain diseases
			AnnotatedGene [] genes =dga.getGenesForDiseaseWithID(100);
			assertEquals("Genes for disease 100 are incorrect"+mode, 0, genes.length);
			genes = dga.getGenesForDiseaseWithID(111);
			assertNull(genes);
			genes = dga.getGenesForDiseaseWithID(103);
			String [] ids = getIds(genes);
			assertArrayEquals("Genes for disease 103 are incorrect"+mode,
					new String []{"MTG4", "MTG5", "MTG6", "MTG7", "MTG8", "MTG14"}, ids);
			genes = dga.getGenesForDiseaseWithID(108);
			ids = getIds(genes);
			assertArrayEquals("Genes for disease 108 are incorrect"+mode,
					new String []{"MTG15", "MTG21", "MTG24"}, ids);
			
			//object in genes and allgenes() identical
			assertTrue("Pointers are not set correctly"+mode,genes[0]==all[14]);
			
			//disease number
			assertEquals("Disease number is incorrect"+mode, 11,dga.numberOfDiseases());
		}
	}
	
	private String [] getIds(AnnotatedGene[] genes){
		String[] ids = new String [genes.length];
		for(int i=0; i<ids.length; i++){
			ids[i]=genes[i].getId();
		}
		return ids;
	}

}
