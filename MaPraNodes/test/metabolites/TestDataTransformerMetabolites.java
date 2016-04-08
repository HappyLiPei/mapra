package metabolites;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Test;

import metabolites.algo.DataTransformerMetabolites;
import metabolites.io.FileUtilitiesMetabolites;
import metabolites.types.MeasuredMetabolite;
import metabolites.types.ReferenceMetabolite;
import metabolites.types.ReferenceMetaboliteBinary;
import metabolites.types.ReferenceMetaboliteConcentration;

public class TestDataTransformerMetabolites {
	
	private LinkedList<MeasuredMetabolite> measurements;
	private HashMap<String, ReferenceMetabolite> reference;
	

	@Test
	public void testDataStructureCases() {
		
		String[] measuredIdExp = new String[]{"M01", "M02", "M03", "M04", "M05", "M06", "M07", "M08", "M09",
				"M10","M11", "M12", "M13", "M14", "M15"};
		double[] measuredConExp= new double[]{0.4, Double.NaN, Double.NaN, Double.NaN, 1.0, Double.NaN, 0.0,
				Double.NaN, 0.8, Double.NaN, 1.1, Double.NaN, 4.2, Double.NaN, -1.0};
			
		for(int mode: new int[] {0,1,2}){
			//prepare for current mode
			prepareCases(mode);
			String modeName="";
			if(mode==1){
				modeName=" (robustness test)";
			}
			else if(mode==2){
				modeName=" (invalid group test)";
			}
			int numMetabo=15;
			int group=2;
			if(mode==2){
				numMetabo=5;
				group=4;
			}
			//test case
			assertEquals("Number of measured metabolites is incorrect"+modeName, numMetabo, measurements.size());
			int pos =0;
			for(MeasuredMetabolite m:measurements){
				assertEquals("Metabolite id of measured metabolite at position "+pos+" is incorrect"+modeName, 
						measuredIdExp[pos], m.getId());
				assertEquals("Metabolite concentration of measured metabolite at position "+pos+" is incorrect"+modeName, 
						measuredConExp[pos], m.getConcentration(), 1E-10);
				assertEquals("Group of measured metabolite at position "+pos+" is incorrect"+modeName, group, m.getGroup());
				pos++;
			}
		}
	}
	
	@Test
	public void testDataStructureControls(){
		
		String[] IdExp = new String[]{"M01", "M02", "M03", "M04", "M05", "M06", "M07", "M08", "M09",
				"M10","M11", "M12", "M13", "M14", "M15"};
		double[] missingnessExp= new double[]{97.2, 77.7, 82.3, 95.5, 100.0, 10.0, 2.2, 0.0, 5.3,
				1.7, 25.0, 40.0, 8.5, 16.6, 4.0};
		double[][] stats = new double[][]{{0.0, 0.5}, {0.2, 0.1}, {-0.4, 0.1}, {0.7, 1.0}, {-2.0, 1.3},
			{0.9, 0.6}, {-0.8, 0.9}, {3.1, 0.7}, {-0.6, 1.4}, {-1.2, 0.1}};
		
		for(boolean robust: new boolean[]{false, true}){
			prepareControls(robust);
			String mode="";
			if(robust){
				mode+=" (robustness test)";
			}
			
			assertEquals("Number of reference metabolites is incorrect"+mode, 15, reference.size());
			for(int i=0; i<IdExp.length; i++){
				String curId = IdExp[i];
				assertTrue("Reference metabolite "+curId +" is missing"+mode,
						reference.containsKey(curId));
				ReferenceMetabolite m = reference.get(curId);
				assertEquals("Missingness of metabolite "+curId+" is incorrect"+mode, missingnessExp[i],
						m.getMissingness(), 1E-10);
				if(i<=4){
					assertTrue("Type of metabolite "+curId+" is incorrect"+mode, 
							m instanceof ReferenceMetaboliteBinary);
				}
				else{
					assertTrue("Type of metabolite "+curId+" is incorrect"+mode, 
							m instanceof ReferenceMetaboliteConcentration);
					ReferenceMetaboliteConcentration c = (ReferenceMetaboliteConcentration) m;
					assertArrayEquals("Statistics of metabolite "+curId+" are incorrect"+mode, stats[i-5],
							c.getMeanAndStdDevForGroup(2), 1E-10);
				}
			}
		}
	}
	
	private void prepareControls(boolean robust){
		
		HashMap<String, LinkedList<String[]>> controlMetabol = FileUtilitiesMetabolites.readReferences(
				"/home/marie-sophie/Uni/master/mapra/TestData/Metabolites/reference.txt");
		
		if(robust){
			//add metabolite with unknown type -> is ignored
			LinkedList<String[]> l16 = new LinkedList<String[]>();
			l16.add(new String[]{"M16", "abcd"});
			controlMetabol.put("M16", l16);
			// duplicate
			LinkedList<String[]> l10 = controlMetabol.get("M10");
			l10.add(new String[]{"M10", "concentration", "3", "-4.2", "-4.2", "-4.2"});
		}
		
		DataTransformerMetabolites dtm = new DataTransformerMetabolites();
		reference = dtm.getMapOfReferenceMetabolites(controlMetabol);
	}
	
	private void prepareCases(int mode){
		
		HashMap<String, LinkedList<String[]>> controlMetabol = FileUtilitiesMetabolites.readReferences(
				"/home/marie-sophie/Uni/master/mapra/TestData/Metabolites/reference.txt");
		LinkedList<String[]> caseMetabol = FileUtilitiesMetabolites.readMeasurements(
				"/home/marie-sophie/Uni/master/mapra/TestData/Metabolites/case2.txt");
		//manipulate ids and groups
		if(mode==1){
			//duplicate id
			caseMetabol.add(new String[]{"M03", "3.3", "2"});
			caseMetabol.add(new String[]{"M03", "3.3", "2"});
			caseMetabol.add(new String[]{"M07", "3.3", "2"});
			//id not in reference
			caseMetabol.add(new String[]{"M16", "3.3", "2"});
			caseMetabol.add(new String[]{"", "3.3", "2"});
			caseMetabol.add(new String[]{null, "3.3", "2"});
			//group does not correspond to consensus
			caseMetabol.get(4)[2]="3";
			caseMetabol.get(5)[2]="7";
			caseMetabol.get(1)[2]="3";
		}
		//invalid group
		if(mode==2){
			for(String[] array:caseMetabol){
				array[2]="4";
			}
		}
		
		DataTransformerMetabolites dtm = new DataTransformerMetabolites();
		reference = dtm.getMapOfReferenceMetabolites(controlMetabol);
		measurements = dtm.getListOfMeasuredMetabolites(caseMetabol,reference);
		
	}
}
