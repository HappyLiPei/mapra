package phenotogeno.node;

import java.util.HashMap;
import java.util.LinkedList;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;

import phenomizer.node.PhenomizerNodeModel;

public class TableProcessorPhenoToGeno {
	
	public static  LinkedList<String []> getPhenomizerResult(BufferedDataTable tablePheno){
		
		LinkedList<String []> phenoRes = new LinkedList<String[]>();
		
		//get index and type of columns
		DataTableSpec spec = tablePheno.getSpec();
		int indexID = spec.findColumnIndex(PhenomizerNodeModel.DISEASE_ID);
		boolean is_int =false;
		if(spec.getColumnSpec(indexID).getType()==IntCell.TYPE){
			is_int=true;
		}
		int indexPV = spec.findColumnIndex(PhenomizerNodeModel.P_VALUE);
		
		//iterate over rows
		for(DataRow r: tablePheno){
			
			String [] entry = new String[2];
			//get id
			if(is_int){
				IntCell ID_int = (IntCell) r.getCell(indexID);
				entry[0]=String.valueOf(ID_int.getIntValue());
			}else{
				LongCell ID_long = (LongCell) r.getCell(indexID);
				entry[0]=String.valueOf(ID_long.getLongValue());
			}
			//get pvalue
			DoubleCell pvalCell = (DoubleCell) r.getCell(indexPV);
			entry[1] = String.valueOf(pvalCell.getDoubleValue());
			
			phenoRes.add(entry);
		}
		
		return phenoRes;
	}
	
	public static LinkedList<String> getGeneList(BufferedDataTable tableGenes){
		
		LinkedList<String> geneList = new LinkedList<String>();
		//get index and type of columns
		DataTableSpec spec = tableGenes.getSpec();
		int indexGene = spec.findColumnIndex(PhenoToGenoNodeNodeModel.GENE_ID);
		for(DataRow r: tableGenes){
			StringCell geneCell = (StringCell) r.getCell(indexGene);
			String gene_id= geneCell.getStringValue();
			geneList.add(gene_id);
		}
		
		return geneList;
	}
	
	public static HashMap<Integer, LinkedList<String>> getAssociations(BufferedDataTable tableDiseaseGene){
		//map id -> list (empty if gene_id is missing cell)
		//check if gene is also part of the gene list?
		
		return null;
	}
	
	//for output generation
	public static DataTableSpec generateOutputSpec(){
		
    	DataColumnSpec [] specs = new DataColumnSpec[3];
    	specs[0] = new DataColumnSpecCreator(PhenoToGenoNodeNodeModel.GENE_ID, StringCell.TYPE).createSpec();
    	specs[1] = new DataColumnSpecCreator(PhenoToGenoNodeNodeModel.GENE_PROBABILITY, DoubleCell.TYPE).createSpec();
    	specs[2] = new DataColumnSpecCreator(PhenoToGenoNodeNodeModel.CONTRIBUTION, StringCell.TYPE).createSpec();
    	
		return new DataTableSpec(specs);	
	}
	
	public static BufferedDataTable generateOutput(){
		return null;
	}

}
