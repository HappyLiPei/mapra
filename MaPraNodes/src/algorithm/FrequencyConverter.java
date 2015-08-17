package algorithm;

import java.util.HashMap;
import java.util.LinkedList;

public class FrequencyConverter {

	//default values for weights
	static int occasional=5;
	static int frequent=10;
	static int veryFrequent=15;

	/**
	 * set weights
	 * @param occ
	 * @param freq
	 * @param very
	 */
	public static void setWeights(int occ, int freq, int very){
		occasional = occ;
		frequent = freq;
		veryFrequent = very;
	}

	/**
	 * convert a frequency given as string into the corresponding weight
	 * @param term to convert
	 * @return corresponding weight
	 */
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

	/**
	 * convert all terms in a given hash map
	 * @param terms
	 * @return hash map with integer weights instead of string frequencies
	 */
	public static HashMap<Integer,LinkedList<Integer[]>>convertAll(HashMap<Integer,LinkedList<String[]>>terms){

		HashMap<Integer,LinkedList<Integer[]>>result = new HashMap<Integer,LinkedList<Integer[]>>();
		for(int key : terms.keySet()){
			LinkedList<String []> values = terms.get(key);
			LinkedList<Integer[]>symptoms = new LinkedList<Integer[]>();
			for(String[] val : values){
				int weight = convertFrequency(val[1]);
				//System.out.println(weight);
				
				/*if(weight==0){
					System.out.println(val[1]);
					System.out.println(weight);
				}*/
				Integer[]nextEl = new Integer[2];
				nextEl[0]=Integer.valueOf(val[0]);
				nextEl[1]=weight;
				symptoms.add(nextEl);
			}
			result.put(key, symptoms);
		}
		
		return result;
	}

	/**
	 * get the weight corresponding to a certain interval
	 * @param value
	 * @return
	 */
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
