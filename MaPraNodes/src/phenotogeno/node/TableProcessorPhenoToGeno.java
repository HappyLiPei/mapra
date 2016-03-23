package phenotogeno.node;

import java.util.HashMap;
import java.util.LinkedList;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;

import phenomizer.node.PhenomizerNodeModel;
import phenotogeno.algo.ScoredGene;

public class TableProcessorPhenoToGeno {
	
	//TODO: test if Phenomizer result matches disease in disease - gene annotation -> logger output
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
	
	//TODO: test for duplicates -> logger info
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
	
	//TODO: test for genes without entry in gene list, test for duplicate genes for one disease
	public static HashMap<Integer, LinkedList<String>> getAssociations(BufferedDataTable tableDiseaseGene){
		
		HashMap<Integer, LinkedList<String>> mapping = new HashMap<Integer, LinkedList<String>> (
				(int) tableDiseaseGene.size()*3);
		
		//get index and type of columns
		DataTableSpec spec = tableDiseaseGene.getSpec();
		int indexGene = spec.findColumnIndex(PhenoToGenoNodeNodeModel.GENE_ID);
		int indexDis = spec.findColumnIndex(PhenomizerNodeModel.DISEASE_ID);
		boolean is_int =false;
		if(spec.getColumnSpec(indexDis).getType()==IntCell.TYPE){
			is_int=true;
		}
		
		for(DataRow r : tableDiseaseGene){
			//get disease and gene id of current row
			int disease_id = -1;
			if(is_int){
				IntCell c = (IntCell) r.getCell(indexDis);
				disease_id = c.getIntValue();
			}
			else{
				LongCell c = (LongCell) r.getCell(indexDis);
				disease_id = (int) c.getLongValue();
			}
			String gene_id=null;
			DataCell geneCell = r.getCell(indexGene);
			//otherwise it is a MissingCell
			if(geneCell instanceof StringCell){
				gene_id =((StringCell) geneCell).getStringValue();
			}
			//add gene id and disease id to hashmap
			if(mapping.containsKey(disease_id)){
				if(gene_id!=null){
					LinkedList<String> genes = mapping.get(disease_id);
					genes.add(gene_id);
				}
			}
			else{
				LinkedList<String> genes = new LinkedList<String>();
				mapping.put(disease_id, genes);
				if(gene_id!=null){
					genes.add(gene_id);
				}
			}
		}
		
		return mapping;
	}
	
	//for output generation
	public static DataTableSpec generateOutputSpec(){
		
    	DataColumnSpec [] specs = new DataColumnSpec[3];
    	specs[0] = new DataColumnSpecCreator(PhenoToGenoNodeNodeModel.GENE_ID, StringCell.TYPE).createSpec();
    	specs[1] = new DataColumnSpecCreator(PhenoToGenoNodeNodeModel.GENE_PROBABILITY, DoubleCell.TYPE).createSpec();
    	specs[2] = new DataColumnSpecCreator(PhenoToGenoNodeNodeModel.CONTRIBUTION, StringCell.TYPE).createSpec();
    	
		return new DataTableSpec(specs);	
	}
	
	//TODO: add disease names
	public static BufferedDataTable generateOutput(ExecutionContext exec, LinkedList<ScoredGene> genes){
		
        DataTableSpec spec = TableProcessorPhenoToGeno.generateOutputSpec();
        int indexGID = spec.findColumnIndex(PhenoToGenoNodeNodeModel.GENE_ID);
        int indexProb = spec.findColumnIndex(PhenoToGenoNodeNodeModel.GENE_PROBABILITY);
        int indexCon = spec.findColumnIndex(PhenoToGenoNodeNodeModel.CONTRIBUTION);
        
        BufferedDataContainer c = exec.createDataContainer(spec);
        int counter =1;
        for(ScoredGene g : genes){
        	RowKey key = new RowKey("Row "+counter);
        	DataCell[] data = new DataCell[spec.getNumColumns()];
        	data[indexGID] = new StringCell(g.getId());
        	data[indexProb] = new DoubleCell(g.getScore());
        	data[indexCon] = new StringCell(g.getImportantDiseases());
        	DataRow row = new DefaultRow(key, data);
        	c.addRowToTable(row);
        	counter++;
        }
        c.close();
		
		return c.getTable();
	}

}
