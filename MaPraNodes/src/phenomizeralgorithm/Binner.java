package phenomizeralgorithm;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import io.FileInputReader;
import io.FileOutputWriter;

public class Binner {
	
	/**
	 * run the binner algorithm:
	 * read in a file with similarity scores and create a new file with binned similarity scores
	 * @param pathIn: file with listed scores
	 * @param pathOut: file with binned scores
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
		FileOutputWriter writer = new FileOutputWriter(pathOut);
		String line;
		while((line=reader.read())!=null){
			String lineOut = createString(line.split("\t"));
			writer.writeFileln(lineOut);
		}
		reader.closer();
		writer.closew();
	}
	
	
	/**
	 * create a binned output line for a given input line
	 * scores are binned with respect to 3 decimal places
	 * @param line: array of scores
	 * @return binned line with disease id, without \n
	 */
	public static String createString(String[] line){
		return createString(line, 3);
	}
	
	
	/**
	 * create a binned output line for a given input line
	 * scores are binned with respect to 3 decimal places
	 * @param line: array of scores
	 * @param bin_width: number of decimal places to consider for binning,
	 * 	Phenomizer requires bin_width=3!
	 * @return binned line with disease id, without \n
	 */
	public static String createString(String[] line, int bin_width){
		LinkedList<String> stringList = new LinkedList<String>();
		int[] bins = createBins(line, bin_width);
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
	 * @param line: line with listing scores
	 * @param bin_width: number of decimal places to consider for binning
	 * @return bins - array of counts
	 */
	private static int[] createBins(String[] line, int bin_width){
		int factor = (int) Math.pow(10, bin_width);
		//adapt if disease number exceeds 10 000!!!
		int[] bins = new int[15*factor];
		for (int i=1; i<line.length;i++){
			double score = Double.parseDouble(line[i]);
			int pos = (int)Math.round(score*factor);
			bins[pos]++;
		}
		return bins;
	}

}
