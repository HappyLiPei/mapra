package algorithm;

import java.util.Comparator;

public class MaxPValueComparator implements Comparator<String> {

	@Override
	public int compare(String s1, String s2) {
		String[]parts1 = s1.split(",");
		String[]parts2 = s2.split(",");

		if(Integer.valueOf(parts1[0])<Integer.valueOf(parts2[0])){
			return 1;
		}
		else if(Integer.valueOf(parts1[0])>Integer.valueOf(parts2[0])){
			return -1;
		}
		else{
			if(Double.compare(Double.valueOf(parts1[1]), Double.valueOf(parts2[1]))!=0){
				return -Double.compare(Double.valueOf(parts1[1]), Double.valueOf(parts2[1]));
			}
			else{
				return parts1[2].compareTo(parts2[2]);
			}
		}

	}

}
