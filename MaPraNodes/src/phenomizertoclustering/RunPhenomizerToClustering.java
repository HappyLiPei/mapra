package phenomizertoclustering;

import java.nio.file.Paths;

import org.knime.core.data.DataRow;
import org.knime.core.data.def.IntCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;

import phenomizer.PhenomizerNodeModel;
import clustering.RscriptClustGenerator;
import execprocess.Executor;

public class RunPhenomizerToClustering {
	
	protected static void runClustering(BufferedDataTable table, String matrix, String outfolder, String rscript, NodeLogger logger, ExecutionContext exec ){
		
		//readIds from table
		int [] ids = getIds(table);
        
        //create R script
        String scriptfile=Paths.get(outfolder, "disease_clustering.R").toString();
        logger.info("Write R script file to "+scriptfile);
        RscriptClustGenerator.writeClusteringScript(scriptfile);
        
        //create command to run R script
        String command = createCommand(ids, scriptfile, matrix, outfolder, rscript);
        
        //run R script
        executeRScript(command, outfolder, logger, exec);
		
	}
	
	private static int [] getIds(BufferedDataTable table){
        int [] ids = new int [table.getRowCount()];
        int counter =0;
        int index = table.getSpec().findColumnIndex(PhenomizerNodeModel.DISEASE_ID);
        for(DataRow r : table){
        	ids[counter++]=((IntCell) r.getCell(index)).getIntValue();
        }
        return ids;
	}
	
	private static String createCommand(int [] ids, String scriptfile, String matrix, String outfolder, String rscript){
        StringBuilder command = new StringBuilder();
        command.append(rscript);
        command.append(" "+scriptfile+" "+matrix);
        String picture = Paths.get(outfolder, "clustering.jpeg").toString();
        command.append(" "+picture);
        for(int i: ids){
        	command.append(" "+i);
        }
        return command.toString();
	}
	
	private static void executeRScript(String command, String outfolder, NodeLogger logger, ExecutionContext exec){
    	Executor e = new Executor();
		logger.info("Running R script...\n"+command.toString());
		String stdout = Paths.get(outfolder, "clustering_out.txt").toString();
		String stderr = Paths.get(outfolder, "clustering_err.txt").toString();
		logger.info("Log files can be found in "+stdout+" and "+stderr);
		
		int exit = e.executeCommand(command.toString(), "R_clustering", exec, logger, stdout, stderr);
		if(exit==0){
			logger.info("RScript finished successfully");
		}
		else{
			logger.info("Something went wrong while executing RScript, please check out the log files");
		}
	}

}
