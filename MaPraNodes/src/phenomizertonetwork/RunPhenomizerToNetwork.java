package phenomizertonetwork;

import java.nio.file.Paths;
import java.util.HashSet;

import network.CytoscapeFileGenerator;
import network.DistanceMatrix;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.IntCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;

import phenomizer.PhenomizerNodeModel;
import execprocess.Executor;

public class RunPhenomizerToNetwork {
	
	protected static void runNetworkGenerator(BufferedDataTable phenores, String matrix, double cutoff,
			String outfolder, boolean run, String cyto, NodeLogger logger, ExecutionContext exec){
		
    	//create HashSet of all nodes that get colored differently
		HashSet<String> hs = getIdsToColor(phenores);
    	
    	//generate output file paths
    	String xgmmlfile=Paths.get(outfolder,"disease_network.xgmml").toString();
    	String scriptfile = Paths.get(outfolder,"script.txt").toString();
    	
    	//generate network file
    	logger.info("Write network file to " +xgmmlfile);
    	CytoscapeFileGenerator.writeSelectedXGMML(xgmmlfile, DistanceMatrix.readDistMatrix(matrix),
    			cutoff, hs);
    	
    	//generate cytoscape script
    	logger.info("Write script file to " +scriptfile);
    	CytoscapeFileGenerator.WriteScript(xgmmlfile, scriptfile);
    	
    	//execute cytoscape
    	if(run){
    		executeCytoscape(scriptfile, outfolder, cyto, logger, exec);
    	}
	}
	
	private static HashSet<String> getIdsToColor(BufferedDataTable phenores){
    	int index = phenores.getDataTableSpec().findColumnIndex(PhenomizerNodeModel.DISEASE_ID);
    	HashSet<String> hs = new HashSet<String>(phenores.getRowCount()*3);
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
	
	//TODO:use for buffered data table, set private
	public static DistanceMatrix generateDistMatrix(BufferedDataTable matrix, NodeLogger logger) throws Exception{
		
		//assume row ids == colum names -> refer to PhenoDis Ids
		
		int rows = matrix.getRowCount();
		DataTableSpec s = matrix.getDataTableSpec();
		String [] colnames=s.getColumnNames();
		
		//check if #cols == #rows
		if(colnames.length!=rows){
			throw new Exception("Invalid format of distance matrix table");
		}
		
		return null;
	}

}
