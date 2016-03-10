package phenotogeno;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import phenotogeno.io.FileUtilitiesPTG;

public class TestPhenoToGenoFileIO {

	@Test
	public void testGeneReader() {
		LinkedList<String> genes = FileUtilitiesPTG.readGeneList("../TestData/PhenoToGeno/all_genes.txt");
		
		assertEquals("Size of gene list is incorrect", 50, genes.size());
		assertEquals("Gene at position is 0 incorrect", "MTG1", genes.get(0));
		assertEquals("Gene at position is 25 incorrect", "MTG26", genes.get(25));
		assertEquals("Gene at position is 49 incorrect", "MTG50", genes.get(49));
	}
	
	@Test
	public void testPhenomizerReader(){
		LinkedList<String[]> phenores = FileUtilitiesPTG.readPhenomizerResult("../TestData/PhenoToGeno/phenores_8.txt");
		
		assertEquals("Size of gene list is incorrect", 9, phenores.size());
		assertArrayEquals("Phenomizer result for disease 109 is not parsed correctly",
				new String[]{"109","0.0"}, phenores.get(0));
		assertArrayEquals("Phenomizer result for disease 105 is not parsed correctly",
				new String[]{"105","0.4"}, phenores.get(4));
		assertArrayEquals("Phenomizer result for disease 104 is not parsed correctly",
				new String[]{"104","0.8"}, phenores.get(8));
	}
	
	@Test
	public void testDiseaseGeneAssociationReader(){
		HashMap<Integer, LinkedList<String>> sga = FileUtilitiesPTG.readDiseaseGeneAssociation(
				"../TestData/PhenoToGeno/gene_diseases.txt");
		
		assertEquals("Number of diseases is incorrect", 11, sga.size());
		assertArrayEquals("Genes for disease 100 are not parsed correctly",
				new String[]{}, sga.get(100).toArray());
		assertArrayEquals("Genes for disease 104 are not parsed correctly",
				new String[]{"MTG3", "MTG8", "MTG11", "MTG12", "MTG13"}, sga.get(104).toArray());
		assertArrayEquals("Genes for disease 106 are not parsed correctly",
				new String[]{"MTG15", "MTG18", "MTG20"}, sga.get(106).toArray());
		assertArrayEquals("Genes for disease 109 are not parsed correctly",
				new String[]{"MTG15", "MTG21", "MTG22", "MTG23"}, sga.get(109).toArray());
		assertArrayEquals("Genes for disease 110 are not parsed correctly",
				new String[]{}, sga.get(110).toArray());
	}
}
