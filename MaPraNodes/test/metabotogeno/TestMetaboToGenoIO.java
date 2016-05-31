package metabotogeno;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import metabotogeno.io.FileUtilitiesMTG;

public class TestMetaboToGenoIO {
	
	@Test
	public void testAssociationReader() {
		
		HashMap<String, LinkedList<String>> read = FileUtilitiesMTG.readMetaboliteGeneAssociations(
				"../TestData/MetaboToGeno/gene_metabolites.txt");
		
		assertEquals("Number of total metabolites is incorrect", 15, read.size());
		
		for(int i=11;i<15; i++){
			assertEquals("Metabolite M"+i+" without associations is parsed correctly",0, read.get("M"+i).size());
		}
		
		assertArrayEquals("Genes of metabolite M01 are not parsed correctly", new String []{"MTG20"},
				read.get("M01").toArray(new String[1]));
		assertArrayEquals("Genes of metabolite M04 are not parsed correctly", new String []{"MTG22", "MTG24", "MTG25", "MTG19"},
				read.get("M04").toArray(new String[4]));
		assertArrayEquals("Genes of metabolite M08 are not parsed correctly", new String []{"MTG15", "MTG15"},
				read.get("M08").toArray(new String[2]));
		assertArrayEquals("Genes of metabolite M10 are not parsed correctly", new String []{"MTG26", "MTG27", "MTG28", "MTG30"},
				read.get("M10").toArray(new String[4]));
	}
	
	@Test
	public void testScoreReader(){
		
		//read case without metabolite name
		LinkedList<String []> readCase1= FileUtilitiesMTG.readMetaboliteScoreResult("../TestData/MetaboToGeno/case1.txt");
		String[][] expected1 = new String[][]{{"M07", "0"}, {"M06", "0"}, {"M09", "0"}, {"M15", "0"},{"M05", "0.0"},
			{"M12", "0.00001"},{"M11", "0.01242"},{"M04", "0.045"},{"M13", "0.0455"},{"M14", "0.06904"},
			{"M02", "0.223"},{"M08", "0.42371"},{"M03", "0.823"},{"M10", "0.94306"},{"M01", "0.972"}};
		String [][] actual1 = readCase1.toArray(new String [0][]);
		assertArrayEquals(expected1, actual1);
		
		//read case with metabolite name
		LinkedList<String []> readCase4= FileUtilitiesMTG.readMetaboliteScoreResult("../TestData/MetaboToGeno/case4.txt");
		String[][] expected4 = new String[][]{{"M15", "0.0"}, {"M14", "0.0"}, {"M13", "0.0"}, {"M12", "0.0"},{"M11", "0.0"},
			{"M10", "0.01"},{"M09", "0.02"},{"M08", "0.1"},{"M07", "0.2"},{"M06", "0.4"},
			{"M05", "0.4"},{"M04", "0.4"},{"M03", "1.0"},{"M02", "1.0"},{"M01", "1.0"}};
		String [][] actual4 = readCase4.toArray(new String [0][]);
		assertArrayEquals(expected4, actual4);
	}
	
	@Test
	public void testGeneListReader(){
		
		LinkedList<String> readGenes = FileUtilitiesMTG.readGeneList("../TestData/PhenoToGeno/all_genes.txt");
		
		assertEquals("Number of genes in parsed gene list is incorrect", 50, readGenes.size());
		
		int counter =1;
		for(String gene: readGenes){
			assertEquals("Gene "+counter+" of gene list is not parsed correctly", "MTG"+counter, gene);
			counter++;
		}
	}

}
