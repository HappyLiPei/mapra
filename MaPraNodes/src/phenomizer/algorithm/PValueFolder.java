package phenomizer.algorithm;

import java.nio.file.Files;
import java.nio.file.Paths;

public class PValueFolder {
	
	public static final String PART1 ="length_";
	public static final String PART2 =".txt";
	
	private String pvalfolder;
	
	/**
	 * Constructor for object representing a folder with files length_x.txt with x in range of 1 to 10
	 * each file contains a sampled score distribution for each PhenoDis disease
	 * @param pvalfolder: path to the folder
	 */
	public PValueFolder(String pvalfolder){
		this.pvalfolder=pvalfolder;
	}
	
	/**
	 * get the file with pre-calculated score distributions for a certain query length
	 * @param query_length: length of the query
	 * @return name of the file with the score distributions
	 */
	public String getPvalFile(int query_length){
		if(0<query_length && query_length<=10){
			return Paths.get(pvalfolder, PART1+query_length+PART2).toString();
		}
		else if(query_length>10){
			return Paths.get(pvalfolder, PART1+10+PART2).toString();
		}
		else{
			return "";
		}
	}
	
	/**
	 * test if the file with score distributions for a certain query length exists in the p value folder
	 * @param length: length of the query
	 * @return true, if file exits; false if file does not exist
	 */
	public boolean checkFile(int length){
		if(length<=0||length>10){
			return false;
		}
		else{
			if(Files.exists(Paths.get(pvalfolder, PART1+length+PART2))){
				return true;
			}
			else{
				return false;
			}
		}
	}
	
	

}
