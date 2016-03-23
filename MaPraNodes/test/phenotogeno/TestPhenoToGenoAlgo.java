package phenotogeno;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import phenotogeno.algo.AnnotatedGene;
import phenotogeno.algo.DiseaseGeneAssociation;
import phenotogeno.algo.PhenoToGenoAlgo;
import phenotogeno.algo.PhenoToGenoDataTransformer;
import phenotogeno.algo.ScoredDisease;
import phenotogeno.algo.ScoredGene;
import phenotogeno.algo.ScoredGeneComparator;
import phenotogeno.io.FileUtilitiesPTG;

public class TestPhenoToGenoAlgo {
	
	private DiseaseGeneAssociation dga;
	
	@Before
	public void generateDGA(){
		LinkedList<String> genes_raw =
				FileUtilitiesPTG.readGeneList("../TestData/PhenoToGeno/all_genes.txt");
		HashMap<Integer, LinkedList<String>> mapping =
				FileUtilitiesPTG.readDiseaseGeneAssociation("../TestData/PhenoToGeno/gene_diseases.txt");
		
		//TODO: handle genes in dga that are not in gene list
		//mapping.get(100).add("MTG51");
		
		PhenoToGenoDataTransformer dt = new PhenoToGenoDataTransformer();
		dga =dt.getDiseaseGeneAssociation(genes_raw, mapping);
		
	}

	@Test
	@SuppressWarnings("all")
	public void testAnnotationOfGenes() throws NoSuchMethodException, SecurityException,
	IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		LinkedList<ScoredDisease> pred = new LinkedList<ScoredDisease>();
		pred.add(new ScoredDisease(100, 0.0));
		pred.add(new ScoredDisease(101, 1.0));
		
		PhenoToGenoAlgo algo = new PhenoToGenoAlgo(pred, dga);
		
		Method m = PhenoToGenoAlgo.class.getDeclaredMethod("annotateGenes",null);
		m.setAccessible(true);
		m.invoke(algo, null);

		AnnotatedGene[] genes = dga.getAllGenes();
		HashSet<String> anno = new HashSet<String>();
		for(String s : new String[] {"MTG1", "MTG2", "MTG3", "MTG4", "MTG14"}){
			anno.add(s);
		}
		for (AnnotatedGene g: genes){
			if(anno.contains(g.getId())){
				assertArrayEquals("Disease annotation for "+g.getId()+" is incorrect"
						,new int[]{100,101} ,g.getDiseaseIds());
				assertArrayEquals("Probabilities incorrect for "+g.getId()+" is incorrect",
						new double[]{(double) 1/50, (double) 1/12}, g.getScores(), 1E-10);
			}
			else{
				assertArrayEquals("Disease annotation for "+g.getId()+" is incorrect"
						,new int[]{100} ,g.getDiseaseIds());
				assertArrayEquals("Probabilities incorrect for "+g.getId()+" is incorrect",
						new double[]{(double) 1/50}, g.getScores(), 1E-10);
			}
		}
	}
	
	@Test
	@SuppressWarnings("all")
	public void testGenerationOfResult() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		AnnotatedGene g1= new AnnotatedGene("G1");
		AnnotatedGene g2= new AnnotatedGene("G2");
		g2.add(100, 0.1);
		AnnotatedGene g3= new AnnotatedGene("G3");
		g3.add(101, 1.0);
		g3.add(102, 0.5);
		AnnotatedGene g4= new AnnotatedGene("G4");
		g4.add(103, 0.05);
		g4.add(104, 0.05);
		AnnotatedGene g5= new AnnotatedGene("G5");
		g5.add(105, 0.1);
		g5.add(106, 0.1);
		g5.add(110, 0.01);
		g5.add(107, 0.1);
		g5.add(109, 0.01);
		g5.add(108, 0.1);
		AnnotatedGene g6= new AnnotatedGene("G6");
		g6.add(111, 0.05);
		g6.add(112, 0.05);
		AnnotatedGene[] genes = new AnnotatedGene[]{g1,g2,g3,g4,g5,g6};
		
		PhenoToGenoAlgo algo = new PhenoToGenoAlgo(null, dga);
		Method m = PhenoToGenoAlgo.class.getDeclaredMethod("scoreGenes",AnnotatedGene[].class);
		m.setAccessible(true);
		LinkedList<ScoredGene> res =(LinkedList<ScoredGene>) m.invoke(algo, (Object) genes);
		
		double[] expected_scores = new double[]{0,0.1, 1, 0.0975, 0.35695639, 0.0975};
		String[] expected_origin = new String[]{"", "100", "101", "103,104", "105,106,107,108", "111,112"};
		for(int i=0;i<6; i++){
			assertEquals("Score for gene "+(i+1)+" is incorrect",
					expected_scores[i], res.get(i).getScore(), 1E-5);
			assertEquals("Annotation for gene "+(i+1)+" is incorrect",
					expected_origin[i], res.get(i).getImportantDiseases());
		}
		
		//check sorting function
		Collections.sort(res, new ScoredGeneComparator());
		double[] sorted_scores = new double[] {1, 0.35695639, 0.1, 0.0975, 0.0975, 0.0};
		String[] sorted_ids = new String []{"G3", "G5", "G2", "G4", "G6", "G1"};
		for(int i=0;i<6; i++){
			assertEquals("Id for position "+i+" is incorrect",
					sorted_ids[i], res.get(i).getId());
			assertEquals("Score for gene "+i+" is incorrect",
					sorted_scores[i], res.get(i).getScore(), 1E-5);
		}
		
	}

}
