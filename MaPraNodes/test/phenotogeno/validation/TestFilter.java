package phenotogeno.validation;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import phenotogeno.algo.DiseaseGeneAssociation;
import phenotogeno.algo.PhenoToGenoDataTransformer;
import phenotogeno.algo.ScoredDisease;
import phenotogeno.io.FileUtilitiesPTG;

public class TestFilter {
	
	private LinkedList<ScoredDisease> phenores2;
	private LinkedList<ScoredDisease> phenores3;
	private LinkedList<ScoredDisease> phenores5;
	
	@Before
	public void prepare(){
		
		LinkedList<String> genes = FileUtilitiesPTG.readGeneList(
				"../TestData/PhenoToGeno/all_genes.txt");
		HashMap<Integer, LinkedList<String>> map = FileUtilitiesPTG.readDiseaseGeneAssociation(
				"../TestData/PhenoToGeno/gene_diseases.txt");
		LinkedList<String[]> pheno_raw2 = FileUtilitiesPTG.readPhenomizerResult("../TestData/PhenoToGeno/phenores_2.txt");
		LinkedList<String[]> pheno_raw3 = FileUtilitiesPTG.readPhenomizerResult("../TestData/PhenoToGeno/phenores_3.txt");
		LinkedList<String[]> pheno_raw5 = FileUtilitiesPTG.readPhenomizerResult("../TestData/PhenoToGeno/phenores_5.txt");
		
		PhenoToGenoDataTransformer dt = new PhenoToGenoDataTransformer();
		DiseaseGeneAssociation dga = dt.getDiseaseGeneAssociation(genes, map);
		
		phenores2 = dt.getPhenomizerResult(pheno_raw2, dga);
		phenores3 = dt.getPhenomizerResult(pheno_raw3, dga);
		phenores5 = dt.getPhenomizerResult(pheno_raw5, dga);
	}

	@Test
	public void testFilterAll() {
		
		PhenomizerFilterAllDiseases filter = new PhenomizerFilterAllDiseases();
		assertEquals("Result size (uninitialized) is incorrect", 0, filter.getResultSize());
		filter.setTotalDiseases(42);
		assertEquals("Result size is incorrect", 42, filter.getResultSize());
		
		compareLists(2, phenores2, phenores2.size(), filter.filter(phenores2));
		compareLists(3, phenores3, phenores3.size(), filter.filter(phenores3));
		compareLists(5, phenores5, phenores5.size(), filter.filter(phenores5));
	}
	
	@Test
	public void testFilterTop20() {
		
		PhenomizerFilterTop20 filter = new PhenomizerFilterTop20();
		assertEquals("Result size (uninitialized) is incorrect", 20, filter.getResultSize());
		filter.setTotalDiseases(42);
		assertEquals("Result size is incorrect", 20, filter.getResultSize());
		
		compareLists(2, phenores2, phenores2.size(), filter.filter(phenores2));
		compareLists(3, phenores3, phenores3.size(), filter.filter(phenores3));
		compareLists(5, phenores5, phenores5.size(), filter.filter(phenores5));
	}
	
	@Test
	public void testFilterSignificant() {
		
		PhenomizerFilterSignificant filter = new PhenomizerFilterSignificant();
		assertEquals("Result size (uninitialized) is incorrect", 0, filter.getResultSize());
		filter.setTotalDiseases(42);
		assertEquals("Result size is incorrect", 42, filter.getResultSize());
		
		compareLists(2, phenores2, 0, filter.filter(phenores2));
		compareLists(3, phenores3, phenores3.size(), filter.filter(phenores3));
		compareLists(5, phenores5, 4, filter.filter(phenores5));
	}
	
	private void compareLists(int number, LinkedList<ScoredDisease> list_expected, int size_expected, LinkedList<ScoredDisease> list_actual){
		
		assertEquals("Size of list "+number+" is incorrect", size_expected, list_actual.size());
		for(int i=0; i<size_expected; i++){
			assertEquals("Disease id of element "+i+" in list "+number+ " is incorrect",
					list_expected.get(i).getId(), list_actual.get(i).getId());
			assertEquals("Pvalue of element "+i+" in list "+number+" is incorrect",
					list_expected.get(i).getPval(), list_actual.get(i).getPval(), 1E-10);
		}
		
	}

}
