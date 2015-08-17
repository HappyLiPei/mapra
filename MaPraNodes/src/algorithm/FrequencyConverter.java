package algorithm;

import java.util.HashMap;
import java.util.LinkedList;

public class FrequencyConverter {

	static int occasional=5;
	static int frequent=10;
	static int veryFrequent=15;

	public static void setWeights(int occ, int freq, int very){
		occasional = occ;
		frequent = freq;
		veryFrequent = very;
	}

	public static int convertFrequency(String term){
		int weight = 0;
		if(term.equals("")){
			weight=frequent;
		}
		else if(term.contains("MIPS")){
			weight=veryFrequent;
		}
		else if(term.contains("Orphanet")){
			String[]parts = term.split(";|,");
			for(int i=0;i<parts.length;i++){
				if(parts[i].contains("Orphanet")){
					if(parts[i].toLowerCase().contains("occasional")){
						weight=occasional;
					}
					else if(parts[i].toLowerCase().contains("very frequent")){
						weight=veryFrequent;
					}
					else if(parts[i].toLowerCase().contains("frequent")){
						weight=frequent;
					}
				}
			}
		}
		else if(term.contains("HPO")){
			String[]parts = term.split(";|,");
			for(int i=0;i<parts.length;i++){
				if(parts[i].contains("HPO")){
					if(parts[i].toLowerCase().contains("rare")|parts[i].toLowerCase().contains("occasional")){
						weight = occasional;
					}
					else if(parts[i].toLowerCase().contains("hallmark")|parts[i].toLowerCase().contains("obligate")){
						weight=veryFrequent;
					}
					else if(parts[i].toLowerCase().contains("common")|parts[i].toLowerCase().contains("typical")
							|parts[i].toLowerCase().contains("frequent")||parts[i].toLowerCase().contains("variable")){
						weight=frequent;
					}
					else if(parts[i].toLowerCase().contains("/")){
						String [] numbers = parts[i].split("/| ");
						double val = Double.valueOf(numbers[0])/Double.valueOf(numbers[1]);
						weight = getWeight(val);
					}
					else if(parts[i].toLowerCase().contains("%")){
						String [] numbers = parts[i].split("%|-| ");
						double val = Double.valueOf(numbers[0])/100;
						weight = getWeight(val);
					}
					else if(parts[i].toLowerCase().contains("of")){
						String [] numbers = parts[i].split(" ");
						double val = Double.valueOf(numbers[0])/Double.valueOf(numbers[2]);
						weight = getWeight(val);
					}
				}
			}
		}
		else if(term.contains("IBIS")){
			if(term.toLowerCase().contains("occasional")){
				weight = occasional;
			}
			else if(term.toLowerCase().contains("very")){
				weight = veryFrequent;
			}
			else if(term.toLowerCase().contains("frequent")){
				weight = frequent;
			}
		}
		else{
			weight=frequent;
		}
		return weight;
	}

	public static void testConvert(HashMap<Integer,LinkedList<String[]>>test){

		for(int key : test.keySet()){
			LinkedList<String []> values = test.get(key);
			for(String[] val : values){
				int weight = convertFrequency(val[1]);

				if(weight==0){
					System.out.println(val[1]);
					System.out.println(weight);
				}
			}
		}
	}

	private static int getWeight(double value){
		if(Double.compare(value, 0.075)<=0){
			return occasional;
		}
		else if(Double.compare(value, 0.9)<0){
			return frequent;
		}
		else{
			return veryFrequent;
		}
	}
}
