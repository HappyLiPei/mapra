package algorithm;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import main.FileInputReader;
import main.FileUtilities;

public class Binner {
	
	/**
	 * run the binner algorithm: read in a file with similarity scores and create a new file with binned similarity scores
	 * @param pathIn
	 * @param pathOut
	 */
	public static void runBinner(String pathIn, String pathOut){
		
		File file = new File(pathOut);
		if (file.exists()) {
			try {
				file.delete();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		FileInputReader reader = new FileInputReader(pathIn);
		String line;
		while((line=reader.read())!=null){
			String lineOut = createString(line.split("\t"));
			FileUtilities.writeStringToExistingFile(pathOut, lineOut+"\n");
		}
	}
	
	
	/**
	 * create a binned output line for a given input line
	 * @param line
	 * @return binned line with disease id, without \n
	 */
	public static String createString(String[] line){
		LinkedList<String> stringList = new LinkedList<String>();
		int[] bins = createBins(line);
		int counter = 0;
		for(int i=bins.length; i>0; i--){
			counter= counter+bins[i-1];
			if (counter!=0){
				stringList.addFirst(counter+"");
			}
		}
		
		String lineOut = line[0];
		Iterator<String> iter = stringList.iterator();
		while (iter.hasNext()){
			lineOut = lineOut+"\t"+iter.next();
		}
		return lineOut;
	}
	
	
	
	/**
	 * count number of scores in one input line
	 * @param line
	 * @return bins - array of counts
	 */
	private static int[] createBins(String[] line){
		int[] bins = new int[15000];
		for (int i=1; i<line.length;i++){
			double score = Double.parseDouble(line[i]);
			int pos = (int)Math.round(score*1000);
			bins[pos]++;
		}
		return bins;
	}

}
