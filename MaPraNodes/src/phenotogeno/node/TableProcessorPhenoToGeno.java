package phenotogeno.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;

import nodeutils.ColumnSpecification;
import nodeutils.TableFunctions;
import phenotogeno.algo.PhenoToGenoDriver;
import togeno.ScoredGene;

public class TableProcessorPhenoToGeno {
	
	/**
	 * method to transform table with results from Phenomizer into the data structure required for PhenoToGeno,
	 * the method is able to handle missing disease ids and pvalue,
	 * it checks for duplicate disease ids and for disease ids without annotations in the disease - gene associations
	 * and outputs a warning if such a disease is found,
	 * the diseases without annotations are removed automatically by {@link PhenoToGenoDriver}
	 * @param tablePheno table containing the results of Phenomizer (column with disease ids and column with pvalues)
	 * @param associations mapping disease id -> gene id, read by getAssociations()
	 * @param logger logger of PhenoToGeno node to produce warnings
	 * @return list of String arrays with disease ids (pos 0) and pvalues (pos 1)
	 */
	protected static  LinkedList<String []> getPhenomizerResult(BufferedDataTable tablePheno,
			NodeLogger logger, HashMap<Integer, LinkedList<String>> associations){
		
		//set of all disease ids -> check for diseases that are not part of the associations
		Set<Integer> allDiseases = associations.keySet();
		//set of disease ids with scores -> check for duplicate 
		HashSet<String> diseaseScore = new HashSet<String>((int) tablePheno.size()*3);
		
		LinkedList<String []> phenoRes = new LinkedList<String[]>();
		
		//get index and type of columns
		DataTableSpec spec = tablePheno.getSpec();
		int indexID = spec.findColumnIndex(ColumnSpecification.DISEASE_ID);
		int indexPV = spec.findColumnIndex(ColumnSpecification.P_VALUE);
		//iterate over rows
		for(DataRow r: tablePheno){
			String disease_id = TableFunctions.getStringValue(r, indexID);
			String pval = TableFunctions.getStringValue(r, indexPV);
			if(disease_id!=null && pval !=null){
				String [] entry = new String[]{disease_id, pval};
				//check if current disease is part of disease - gene associations
				int diseaseID = Integer.valueOf(entry[0]);
				if(!allDiseases.contains(diseaseID)){
					logger.warn("Disease with id "+diseaseID+" from Phenomizer result is not part of the disease - gene associations. "
							+ "This node will remove it from the result of Phenomizer!");
				}
				else if(diseaseScore.contains(disease_id)){
					logger.warn("Duplicate entry for disease "+disease_id+" in Phenomizer table. This node will ignore it.");
				}
				else{
					phenoRes.add(entry);
					diseaseScore.add(disease_id);
				}
			}
			//check if any cell is missing
			else if(disease_id == null){
				logger.warn("Error parsing disease id of row "+r.getKey()+" in Phenomizer result. This node will ignore it.");
			}
			else{
				logger.warn("Error parsing the pvalue of row "+r.getKey()+" in Phenomizer result. This node will ignore it.");
			}
		}
		
		return phenoRes;
	}
	
	/**
	 * method to transform table with gene ids into data structure required for PhenoToGeno,
	 * the method is able to rows with missing gene ids,
	 * the method checks for duplicates and outputs a warning if a duplicate is found
	 * the duplicates are removed automatically by {@link PhenoToGenoDriver}
	 * @param tableGenes table containing a column with gene ids
	 * @param logger logger of PhenoToGeno node to produce warnings
	 * @return list of gene ids (may contain duplicates!)
	 */
	protected static LinkedList<String> getGeneList(BufferedDataTable tableGenes, NodeLogger logger){
		
		LinkedList<String> geneList = new LinkedList<String>();
		HashSet<String> testForDuplicates = new HashSet<String>((int) tableGenes.size()*3);
		
		//get index and type of columns
		DataTableSpec spec = tableGenes.getSpec();
		int indexGene = spec.findColumnIndex(ColumnSpecification.GENE_ID);
		for(DataRow r: tableGenes){
			//get gene id
			String gene_id= TableFunctions.getStringValue(r, indexGene);
			if(gene_id!=null){
				//add to list
				geneList.add(gene_id);
				//check for duplicate
				if(!testForDuplicates.add(gene_id)){
					logger.warn("Duplicate gene id \'"+gene_id+"\' in gene list. This node will remove it!");
				}
			}
			//gene id is missing
			else{
				logger.warn("Error parsing gene id of row "+r.getKey()+" in gene list. This node will ignore it.");
			}
		}
		return geneList;
	}
	
	/**
	 * method to transform table with associations between gene ids  and disease ids
	 * into the data structure required for PhenoToGeno,
	 * it is able to handle missing disease ids and gene ids,
	 * the method checks for duplicates and for genes that are not part of the gene list,
	 * the method outputs a warning if a duplicate or a gene without list entry is found
	 * the duplicates and genes without list entry are removed automatically by PhenoToGenoDriver
	 * @param tableDiseaseGene table with disease ids and gene ids representing disease - gene associations
	 * @param geneList list of the gene ids of all genes to score, read by getGeneList()
	 * @param logger logger of PhenoToGeno node to produce warnings
	 * @return mapping disease id -> list of gene ids representing associations between genes and diseases
	 */
	protected static HashMap<Integer, LinkedList<String>> getAssociations(BufferedDataTable tableDiseaseGene,
			NodeLogger logger, LinkedList<String> geneList){
		
		//build hasmap with all gene ids
		HashSet<String> allGenes = new HashSet<String>(geneList.size()*3);
		for(String gene: geneList){
			allGenes.add(gene);
		}
		
		//hashmap disease id -> gene id
		HashMap<Integer, LinkedList<String>> mapping = new HashMap<Integer, LinkedList<String>> (
				(int) tableDiseaseGene.size()*3);
		
		//get index and type of columns
		DataTableSpec spec = tableDiseaseGene.getSpec();
		int indexGene = spec.findColumnIndex(ColumnSpecification.GENE_ID);
		int indexDis = spec.findColumnIndex(ColumnSpecification.DISEASE_ID);
		
		for(DataRow r : tableDiseaseGene){
			//get disease id and gene id of current row
			Integer disease_id = TableFunctions.getIntegerValue(r, indexDis);
			String gene_id=TableFunctions.getStringValue(r, indexGene);
			//check if disease id is missing
			if(disease_id!=null){
				//add disease id
				if(!mapping.containsKey(disease_id)){
					LinkedList<String> emptyList = new LinkedList<String>();
					mapping.put(disease_id,emptyList);
				}
				//check gene id
				if(gene_id !=null){
					
					//if gene id is part of the gene list
					if(allGenes.contains(gene_id)){
						LinkedList<String> genes= mapping.get(disease_id);
						//duplicate association gene_id/ disease_id
						if(genes.contains(gene_id)){
							logger.warn("Duplicate association \'"+gene_id+"\'-\'"+disease_id+"\' in association table. This node will remove it!");
						}
						mapping.get(disease_id).add(gene_id);
					}
					else{
						logger.warn("Gene "+gene_id+" is not part of the gene list. This node will remove it from the disease - gene associations!");
					}
				}
			}
			//disease id is missing -> skip row
			else{
				logger.warn("Error parsing disease id of row "+r.getKey()+" in association data. This node will ignore it.");
			}
		}
		return mapping;
	}
	
	/**
	 * method to generate the specification for the table returned by PhenoToGeno
	 * (3 columns: gene id, probability, contribution)
	 * @return DataTable specification for the table returned by PhenoToGeno
	 */
	protected static DataTableSpec generateOutputSpec(){
		
    	DataColumnSpec [] specs = new DataColumnSpec[3];
    	specs[0] = TableFunctions.makeDataColSpec(ColumnSpecification.GENE_ID, 
    			ColumnSpecification.GENE_ID_TYPE[0]);
    	specs[1] = TableFunctions.makeDataColSpec(ColumnSpecification.GENE_PROBABILITY, 
    			ColumnSpecification.GENE_PROBABILITY_TYPE[0]);
    	specs[2] = TableFunctions.makeDataColSpec(ColumnSpecification.CONTRIBUTION, 
    			ColumnSpecification.CONTRIBUTION_TYPE[0]);
    	
		return new DataTableSpec(specs);	
	}
	
	/**
	 * method to transform the result of PhenoToGeno into a DataTable for the PhenoToGeno node
	 * @param exec Execution Context of the calling node model (execute method)
	 * @param genes List of ScoredGenes return by PhenoToGenoAlgo
	 * @param tablePhenomizer Table with the prediction results of Phenomizer (required for extracting disease names)
	 * @return DataTable with a row for each gene containing gene id, gene score and diseases (ids and names)
	 * 			contributing to the score
	 */
	protected static BufferedDataTable generateOutput(ExecutionContext exec, LinkedList<ScoredGene> genes,
			BufferedDataTable tablePhenomizer){
		
		//map disease id to disease name
		HashMap<String, String> idToName = new HashMap<String, String>((int) tablePhenomizer.size());
		DataTableSpec specPheno = tablePhenomizer.getDataTableSpec();
		int indexDiseaseName = specPheno.findColumnIndex(ColumnSpecification.DISEASE_NAME);
		//build mapping disease id -> name if name is available
		if(indexDiseaseName!=-1){
			int indexDiseaseID = specPheno.findColumnIndex(ColumnSpecification.DISEASE_ID);
			for(DataRow r: tablePhenomizer){
				String diseaseName=TableFunctions.getStringValue(r, indexDiseaseName);
				String diseaseId = TableFunctions.getStringValue(r, indexDiseaseID);
				if(!idToName.containsKey(diseaseId)){
					idToName.put(diseaseId, diseaseName);
				}
			}
		}
		
		//get indices for filling the table
        DataTableSpec spec = TableProcessorPhenoToGeno.generateOutputSpec();
        int indexGID = spec.findColumnIndex(ColumnSpecification.GENE_ID);
        int indexProb = spec.findColumnIndex(ColumnSpecification.GENE_PROBABILITY);
        int indexCon = spec.findColumnIndex(ColumnSpecification.CONTRIBUTION);
        BufferedDataContainer c = exec.createDataContainer(spec);
        int counter =1;
        for(ScoredGene g : genes){
        	
        	//add major contribution to score as comma-separated list of ids
        	String importantDiseases = g.getImportantContributors();
    		String [] split = importantDiseases.split(",");
    		for(int i=0; i<split.length; i++){
				//get disease id
				String id = split[i];
				if(id.endsWith("...")){
					id = id.substring(0, id.length()-3);
				}
				//if name for id available, exchange id by name
				if(idToName.containsKey(id)){
					String name = idToName.get(id);
					if(split[i].endsWith("...")){
						split[i]=name+" ("+id+")...";
					}
					else{
						split[i]=name+" ("+id+")";
					}
				}
			}
			importantDiseases = String.join(", ", split);
        	
			//add new row to table
        	RowKey key = new RowKey("Row "+counter);
        	DataCell[] data = new DataCell[spec.getNumColumns()];
        	data[indexGID] = TableFunctions.generateDataCellFor(spec, indexGID, g.getId());
        	data[indexProb] = TableFunctions.generateDataCellFor(spec, indexProb, g.getScore());
        	data[indexCon] = TableFunctions.generateDataCellFor(spec, indexGID, importantDiseases);
        	DataRow row = new DefaultRow(key, data);
        	c.addRowToTable(row);
        	counter++;
        }
        c.close();
		
		return c.getTable();
	}

}
