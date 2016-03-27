package phenotogeno.node;

import java.util.HashMap;
import java.util.HashSet;
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
import org.knime.core.node.NodeLogger;

import phenomizer.node.PhenomizerNodeModel;
import phenotogeno.algo.ScoredGene;

public class TableProcessorPhenoToGeno {
	
	/**
	 * method to transform table with results from Phenomizer into the data structure required for PhenoToGeno,
	 * the method checks for diseases without annotations in the disease - gene associations
	 * and outputs a warning if such a disease is found,
	 * the diseases without annotations are removed automatically by PhenoToGenoDriver
	 * @param tablePheno
	 * 		table containing the results of Phenomizer (column with disease ids and column with pvalues)
	 * @param tableAssociations
	 * 		table with the mapping between diseases and genes
	 * @param logger
	 * 		logger of PhenoToGeno node to produce warnings
	 * @return
	 * 		list of String arrays with disease ids (pos 0) and pvalues (pos 1)
	 */
	public static  LinkedList<String []> getPhenomizerResult(BufferedDataTable tablePheno,
			BufferedDataTable tableAssociations, NodeLogger logger){
		
		//build hashset of all diseases in the disease - gene association
		HashSet<Integer> allDiseases = new HashSet<Integer>((int)tableAssociations.size()*3);
		DataTableSpec spec2 = tableAssociations.getDataTableSpec();
		int colPos= spec2.findColumnIndex(PhenomizerNodeModel.DISEASE_ID);
		boolean is_int2 =false;
		if(spec2.getColumnSpec(colPos).getType()==IntCell.TYPE){
			is_int2=true;
		}
		for(DataRow r: tableAssociations){
			int currId =-1;
			if(is_int2){
				IntCell cell = (IntCell) r.getCell(colPos);
				currId = cell.getIntValue();
			}
			else{
				LongCell cell = (LongCell) r.getCell(colPos);
				currId = (int) cell.getLongValue();
			}
			allDiseases.add(currId);			
		}
		
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
			
			//check if current disease is part of disease - gene associations
			int diseaseID = Integer.valueOf(entry[0]);
			if(!allDiseases.contains(diseaseID)){
				logger.warn("Disease with id "+diseaseID+" from Phenomizer result "
						+ "is not part of the disease - gene associations. "
						+ "This node will remove it from the result of Phenomizer!");
			}
			
			phenoRes.add(entry);
		}
		
		return phenoRes;
	}
	
	/**
	 * method to transform table with gene ids into data structure required for PhenoToGeno,
	 * the method checks for duplicates and outputs a warning if a duplicate is found
	 * the duplicates are removed automatically by PhenoToGenoDriver
	 * @param tableGenes
	 * 		table containing a column with gene ids
	 * @param logger
	 * 		logger of PhenoToGeno node to produce warnings
	 * @return
	 * 		list of gene ids (may contain duplicates!)
	 */
	public static LinkedList<String> getGeneList(BufferedDataTable tableGenes, NodeLogger logger){
		
		LinkedList<String> geneList = new LinkedList<String>();
		HashSet<String> testForDuplicates = new HashSet<String>((int) tableGenes.size()*3);
		
		//get index and type of columns
		DataTableSpec spec = tableGenes.getSpec();
		int indexGene = spec.findColumnIndex(PhenoToGenoNodeNodeModel.GENE_ID);
		for(DataRow r: tableGenes){
			StringCell geneCell = (StringCell) r.getCell(indexGene);
			String gene_id= geneCell.getStringValue();
			
			//check for duplicate
			if(!testForDuplicates.add(gene_id)){
				logger.warn("Duplicate gene id \'"+gene_id+"\' in gene list. This node will remove it!");
			}
			
			geneList.add(gene_id);
		}
		
		return geneList;
	}
	
	/**
	 * method to transform table with associations between gene ids  and disease ids
	 * into the data structure required for PhenoToGeno,
	 * the method checks for duplicates and for genes that are not part of the gene list,
	 * the method outputs a warning if a duplicate or a gene without list entry is found
	 * the duplicates and genes without list entry are removed automatically by PhenoToGenoDriver
	 * @param tableDiseaseGene
	 * 			table with disease ids and gene ids representing disease - gene associations
	 * @param tableGenes
	 * 			table containing gene ids of all genes to score
	 * @param logger
	 * 			logger of PhenoToGeno node to produce warnings
	 * @return
	 * 			mapping disease id -> list of gene ids representing associations between genes and diseases
	 */
	public static HashMap<Integer, LinkedList<String>> getAssociations(BufferedDataTable tableDiseaseGene,
			BufferedDataTable tableGenes, NodeLogger logger){
		
		//build hasmap with all gene ids
		HashSet<String> allGenes = new HashSet<String>((int)tableGenes.size()*3);
		DataTableSpec spec2 = tableGenes.getDataTableSpec();
		int colPos = spec2.findColumnIndex(PhenoToGenoNodeNodeModel.GENE_ID);
		for(DataRow r: tableGenes){
			StringCell cell = (StringCell) r.getCell(colPos);
			allGenes.add(cell.getStringValue());
		}
		
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
				
				//if gene id is part of the gene list
				if(!allGenes.contains(gene_id)){
					logger.warn("Gene "+gene_id+" is not part of the gene list. "
							+ "This node will remove it from the disease - gene associations!");
				}
				
			}
			//add gene id and disease id to hashmap
			if(mapping.containsKey(disease_id)){
				if(gene_id!=null){
					LinkedList<String> genes = mapping.get(disease_id);
					
					//check for duplicate gene-disease association
					if(genes.contains(gene_id)){
						logger.warn("Duplicate association between disease "+disease_id+" and gene "+gene_id+
								". This node will remove it!");
					}
					
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
	
	/**
	 * method to generate the specification for the table returned by PhenoToGeno
	 * (3 columns: gene id, probability, contribution)
	 * @return DataTable specification for the table returned by PhenoToGeno
	 */
	public static DataTableSpec generateOutputSpec(){
		
    	DataColumnSpec [] specs = new DataColumnSpec[3];
    	specs[0] = new DataColumnSpecCreator(PhenoToGenoNodeNodeModel.GENE_ID, StringCell.TYPE).createSpec();
    	specs[1] = new DataColumnSpecCreator(PhenoToGenoNodeNodeModel.GENE_PROBABILITY, DoubleCell.TYPE).createSpec();
    	specs[2] = new DataColumnSpecCreator(PhenoToGenoNodeNodeModel.CONTRIBUTION, StringCell.TYPE).createSpec();
    	
		return new DataTableSpec(specs);	
	}
	
	/**
	 * method to transform the result of PhenoToGeno into a DataTable for the PhenoToGeno node
	 * @param exec
	 * 			Execution Context of the calling node model (execute method)
	 * @param genes
	 * 			List of ScoredGenes return by PhenoToGenoAlgo
	 * @param tablePhenomizer
	 * 			Table with the prediction results of Phenomizer (required for extracting disease names)
	 * @return
	 * 			DataTable with a row for each gene containing gene id, gene score and diseases (ids and names)
	 * 			contributing to the score
	 */
	public static BufferedDataTable generateOutput(ExecutionContext exec, LinkedList<ScoredGene> genes,
			BufferedDataTable tablePhenomizer){
		
		//map disease id to disease name
		HashMap<Integer, String> idToName = new HashMap<Integer, String>((int) tablePhenomizer.size());
		DataTableSpec specPheno = tablePhenomizer.getDataTableSpec();
		int indexDiseaseName = specPheno.findColumnIndex(PhenomizerNodeModel.DISEASE_NAME);
		//build mapping disease id -> name if name is available
		if(indexDiseaseName!=-1){
			int indexDiseaseID = specPheno.findColumnIndex(PhenomizerNodeModel.DISEASE_ID);
			boolean is_int =false;
			if(specPheno.getColumnSpec(indexDiseaseID).getType()==IntCell.TYPE){
				is_int=true;
			}
			for(DataRow r: tablePhenomizer){
				DataCell cell = r.getCell(indexDiseaseName);
				String diseaseName=((StringCell) cell).getStringValue();
				DataCell cell2 = r.getCell(indexDiseaseID);
				int diseaseId = -1;
				if(is_int){
					diseaseId = ((IntCell) cell2).getIntValue();
				}
				else{
					diseaseId = (int) ((LongCell) cell2).getLongValue();
				}
				idToName.put(diseaseId, diseaseName);
			}
		}
		
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
        	
        	//add major contribution to score either as list of ids or list of disease names
        	String importantDiseases = g.getImportantDiseases();
        	//list of disease names and ids
        	if(idToName.size()!=0 && !importantDiseases.equals("")){
        		String [] components = importantDiseases.split(",");
        		String annotation="";
        		for(int i=0; i<components.length; i++){
        			if(i!=0){
        				annotation+=",";
        			}
        			String currId = components[i];
        			boolean addDots =false;
        			if(components[i].endsWith("...")){
        				currId =currId.substring(0, currId.length()-3);
        				addDots=true;
        			}
        			annotation+=idToName.get(Integer.valueOf(currId))+" ("+currId+")";
        			if(addDots){
        				annotation+="...";
        			}
        		}
        		data[indexCon] = new StringCell(annotation);
        	}
        	//just list of ids or no contribution at all
        	else{
        		data[indexCon] = new StringCell(importantDiseases);
        	}
        	
        	DataRow row = new DefaultRow(key, data);
        	c.addRowToTable(row);
        	counter++;
        }
        c.close();
		
		return c.getTable();
	}

}
