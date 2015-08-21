package algorithm;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import main.FileInputReader;
import main.FileUtilities;

public class Binner {
	
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
		int disease = 1;
		while((line=reader.read())!=null){
			System.out.print("disease "+disease+":\t");
			disease++;
			String lineOut = createString(line.split("\t"));
			FileUtilities.writeStringToExistingFile(pathOut, lineOut+"\n");
		}
	}
	
	
	
	// gibt output-zeile zurück
	private static String createString(String[] line){
		LinkedList<String> stringList = new LinkedList<String>();
		int[] bins = createBins(line);
		int counter = 0;
		boolean maxScore = true; // zum Ausgeben des größten Scores in einer Zeile
		for(int i=10000; i>0; i--){
			counter= counter+bins[i-1];
			if (counter!=0){
				if(maxScore){
					System.out.println(counter+ " mal\t"+(i-1)/1000.0);
					maxScore=false;
				}
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
	
	
	
	// bekommt eine zeile mit id und  10 000 scores aus file
	// gibt int-array mit anzahlen der scores zurück
	private static int[] createBins(String[] line){
		int[] bins = new int[10000];// andere länge?! nur 9 000
		for (int i=1; i<=10000;i++){
			double score = Double.parseDouble(line[i]);
			int pos = (int)Math.round(score*1000);
			bins[pos]++;
		}
		return bins;
	}

}
