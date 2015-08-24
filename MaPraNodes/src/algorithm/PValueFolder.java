package algorithm;

import java.nio.file.Files;
import java.nio.file.Paths;

public class PValueFolder {
	
	private static String pvalfolder;
	private static boolean set = false;
	public static String PART1 ="length_";
	public static String PART2 =".txt";
	
	/**
	 * set path to the folder containing the files with the precalculated scores
	 * @param folder
	 */
	public static void setPvalFoder(String folder){
			pvalfolder=folder;
			set=true;
	}
	
	/**
	 * get the file with precalculated scores corresponding to a certain query length
	 * @param query_length
	 * @return file name
	 */
	public static String getPvalFile(int query_length){
		if(set){
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
		else{
			return "";
		}
	}
	
	/**
	 * test if the file for a certain query length exists in the set folder
	 * @param length
	 * @return
	 */
	public static boolean checkFile(int length){
		if(!set|| length<=0||length>10){
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
