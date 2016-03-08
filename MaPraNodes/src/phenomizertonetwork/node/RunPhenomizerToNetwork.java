package phenomizertonetwork.node;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;

import phenomizertonetwork.network.CytoscapeFileGenerator;
import phenomizertonetwork.network.DistanceMatrix;
import phenomizertonetwork.network.PhenoResults;
import execprocess.Executor;
import phenomizer.node.PhenomizerNodeModel;

public class RunPhenomizerToNetwork {
	
	/**
	 * method run in the execute procedure of the PhenomizerToNetwork node
	 * @param phenores: input table from inport 0
	 * @param matrix: input table from inport 1
	 * @param comparator: value from node dialog (model m_comparator)
	 * @param cutoff: value from node dialog (model m_edge)
	 * @param outfolder: value from node dialog (model m_out)
	 * @param run: value from node dialog (model m_start_cyto)
	 * @param cyto: value from node dialog (model m_cyto_script)
	 * @param logger: logger of the node
	 * @param exec: execution context of the node 
	 * @throws Exception: if format of table from inport 1 is incorrect
	 */
	
	protected static void runNetworkGenerator(BufferedDataTable phenores, BufferedDataTable matrix,
			String comparator, double cutoff, String outfolder, boolean run, String cyto,
			NodeLogger logger, ExecutionContext exec) throws Exception{
		
    	//read matrix from data table, checks format of matrix (throws exception if wrong format)
    	DistanceMatrix dm = generateDistMatrix(matrix, logger);
		
    	//create HashSet of all nodes that get colored differently
		HashSet<String> hs = getIdsToColor(phenores);
		
		//datastructure for phenomizer scores and pvalues
		PhenoResults res = getPhenoRes(phenores);
	
    	//generate output file paths
    	String xgmmlfile=Paths.get(outfolder,"disease_network.xgmml").toString();
    	String scriptfile = Paths.get(outfolder,"script.txt").toString();
    	
    	//read in disease names, if they are provided in the matrix table, disease names will be displayed as node labels
    	HashMap<Integer,String> idstoname = new HashMap<Integer,String>();
    	if(matrix.getSpec().findColumnIndex(PhenomizerNodeModel.DISEASE_NAME)!=-1){
    		idstoname = getDiseaseNames(matrix);
    	}
    	
    	//generate network file
    	logger.info("Write network file to " +xgmmlfile);
    	//smallerthan = false -> use >, smallerthan = true -> use <
    	boolean smallerthan=false;
    	if(comparator.equals(PhenomizerToNetworkNodeModel.COMPARATOR_VALUES[0])){
    		smallerthan=true;
    	}
    	CytoscapeFileGenerator.writeSelectedXGMML(xgmmlfile, dm, cutoff, smallerthan, hs ,res, idstoname);
    	
    	//generate cytoscape script
    	logger.info("Write script file to " +scriptfile);
    	CytoscapeFileGenerator.WriteScript(xgmmlfile, scriptfile);
    	
    	//execute cytoscape
    	if(run){
    		executeCytoscape(scriptfile, outfolder, cyto, logger, exec);
    	}
	}
	
	/**
	 * transforms table with results of Phenomizer into data structure PhenoResults
	 * @param table: table with results from Phenomizer node (inport 0)
	 * @return: data structure representing the results of Phenomizer (ids, scores and p values (if available) 
	 */
	private static PhenoResults getPhenoRes(BufferedDataTable table){
		//read in data table
		int length = (int)table.size();
		int [] ids = new int[length];
		double [] scores = new double[length];
		double [] pvalues = new double[length];
		boolean pval = (table.getSpec().findColumnIndex(PhenomizerNodeModel.P_VALUE)!=-1);
		int index=0;
		for(DataRow r: table){
			ids[index]=((IntCell) r.getCell(table.getSpec().findColumnIndex(PhenomizerNodeModel.DISEASE_ID))).getIntValue();
			scores[index]=((DoubleCell) r.getCell(table.getSpec().findColumnIndex(PhenomizerNodeModel.SCORE))).getDoubleValue();
			if(pval){
				pvalues[index]=((DoubleCell) r.getCell(table.getSpec().findColumnIndex(PhenomizerNodeModel.P_VALUE))).getDoubleValue();
			}
			index++;
		}
		
		//generate data structure with or without p values
		if(pval){
			return new PhenoResults(ids, scores,pvalues);
		}
		else{
			return new PhenoResults(ids, scores);
		}
	}
	
	/**
	 * reads PhenoDis ids (column disease_id) from data table with results from Phenomizer,
	 * nodes corresponding to those ids will be colored differently in the network
	 * @param phenores: table from input port 0
	 * @return: set of all PhenoDis ids from the table
	 */
	private static HashSet<String> getIdsToColor(BufferedDataTable phenores){
    	int index = phenores.getDataTableSpec().findColumnIndex(PhenomizerNodeModel.DISEASE_ID);
    	HashSet<String> hs = new HashSet<String>((int) phenores.size()*3);
    	if(index!=-1){
    		for(DataRow r: phenores){
    			hs.add( ((IntCell) r.getCell(index)).getIntValue()+"");
    		}
    	}
    	return hs;
	}
	
	private static void executeCytoscape(String scriptfile, String outfolder, String cyto, NodeLogger logger, ExecutionContext exec){
		Executor e = new Executor();
		String cmd = cyto+" -S "+scriptfile;
		logger.info("Running cytoscape...\n"+cmd);
		String stdout = Paths.get(outfolder, "cytoscape_out.txt").toString();
		String stderr = Paths.get(outfolder, "cytoscape_err.txt").toString();
		logger.info("Log files can be found in "+stdout+" and "+stderr);
		
		int exit = e.executeCommand(cmd, "cytoscape", exec, logger, stdout, stderr);
		if(exit==0){
			logger.info("Cytoscape finished successfully");
		}
		else{
			logger.info("Something went wrong while executing Cytoscape, please check out the log files");
		}
	}
	
	/**
	 * method that generates a matrix data structure from the table at inport 1
	 * @param matrix: table from inport 1
	 * @param logger: logger of the node model
	 * @return matrix data structure
	 * @throws Exception: if format of matrix is incorrect
	 */
	private static DistanceMatrix generateDistMatrix(BufferedDataTable matrix, NodeLogger logger) throws Exception{
		
		//check format of matrix
		//assume row ids == column names -> refer to PhenoDis Ids
		int rows = (int) matrix.size();
		DataTableSpec s = matrix.getDataTableSpec();
		String [] colnames=s.getColumnNames();
		int pos_name = s.findColumnIndex(PhenomizerNodeModel.DISEASE_NAME);
		//check if disease names are available
		boolean name = true;
		if(pos_name==-1){
			name=false;
		}
		//check if #cols == #rows (exception: column name disease is not counted)
		if(!name){
			if(colnames.length!=rows){
				throw new Exception("Invalid format of distance matrix table. The number of rows has to be equal to the number of columns");
			}
		}
		else{
			if(colnames.length-1!=rows){
				throw new Exception("Invalid format of distance matrix table. The number of rows has to be equal to the number of columns-1");
			}
		}
		
		//update column names: remove column name disease
		if(name){
			String[] copy = new String [colnames.length-1];
			for(int i=0;i<colnames.length; i++){
				if(i<pos_name){
					copy[i]=colnames[i];
				}
				else if(i>pos_name){
					copy[i-1]=colnames[i];
				}
				else{
					continue;
				}
			}
			colnames=copy;
		}
		
		//read rows from matrix and write score values into array
		String [] rowids = new String [rows];
		double [][] values = new double[rows][rows];
		int index=0;
		for(DataRow r : matrix){
			rowids[index]=r.getKey().getString();
				for(int cell_pos=0; cell_pos<r.getNumCells(); cell_pos++){
					if(cell_pos<pos_name||!name){
						values[index][cell_pos]= ((DoubleCell) r.getCell(cell_pos)).getDoubleValue();
					}
					else if(cell_pos>pos_name){
						values[index][cell_pos-1]= ((DoubleCell) r.getCell(cell_pos)).getDoubleValue();
					}
					else{
						continue;
					}
				}
			index++;
		}
		return new DistanceMatrix(colnames, rowids, values);
	}
	
	/**
	 * reads in PhenoDis disease ids and corresponding names from all against all table to display them in the network
	 * @param matrix: all against all matrix with distance/ similarity scores (inport 1)
	 * @return: HashMap that maps PhenoDis id (row ids of matrix) to disease names (values of column disease) 
	 */
	private static HashMap<Integer,String> getDiseaseNames(BufferedDataTable matrix){
		HashMap<Integer, String> res = new HashMap<Integer,String>((int) (matrix.size()*3));
		int index_name = matrix.getDataTableSpec().findColumnIndex(PhenomizerNodeModel.DISEASE_NAME);
		for(DataRow r: matrix){
			res.put(Integer.valueOf(r.getKey().getString()), ((StringCell) r.getCell(index_name)).getStringValue());
		}
		return res;
	}

}
