package phenomizer.algorithm;

import java.util.HashMap;
import java.util.LinkedList;

public class FrequencyConverter {
	
	/**
	 * this class parses frequency annotations from the PhenoDis table ksz
	 * each frequency term is converted into a weighting factor for the Phenomizer algorithm
	 * the weighting factors are stored as ints -> division by 10 gives the actual weighting factor 
	 */
	
	/**
	 * default values for weights
	 */
	public static final int DEFAULT_OCCASIONAL=5;
	public static final int DEFAULT_FREQUENT=10;
	public static final int DEFAULT_VERYFREQUENT=15;
	public static final int NO_WEIGHT=10;
	
	private int occasional;
	private int frequent;
	private int veryFrequent;
	
	/**
	 * constructor for frequency converter
	 * uses default values for weighting
	 */
	public FrequencyConverter(){
		occasional=DEFAULT_OCCASIONAL;
		frequent=DEFAULT_FREQUENT;
		veryFrequent=DEFAULT_VERYFREQUENT;
	}
	
	/**
	 * constructor for frequency converter with individual weights
	 * @param occ: weighting factor *10 for occasional symptoms
	 * @param freq: weighting factor *10 for frequent symptoms
	 * @param very: weighting factor *10 for frequent symptoms
	 */
	public FrequencyConverter(int occ, int freq, int very){
		occasional = occ;
		frequent = freq;
		veryFrequent = very;
	}

	/**
	 * convert a frequency given as string into the corresponding weight
	 * parses the frequency annotations of the ksz table from PhenoDis
	 * @param term to convert
	 * @return corresponding weight
	 */
	public int convertFrequency(String term){
		int weight = 0;
		//empty field
		if(term.equals("")){
			weight=frequent;
		}
		//MIPS annotations: always present + very frequent
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
			else if(term.toLowerCase().contains("rare")){
				weight = occasional;
			}
		}
		//terms with frequency annotation e.g. rare, frequent, very frequent, but without any source
		else{
			weight=frequent;
		}
		return weight;
	}

	/**
	 * get the weight for an absolute frequency value
	 * [0,0.25] : weighted as occasional
	 * ]0.25, 0.75[ : weighted as frequent
	 * [0.75, 1]: weighted as very frequent
	 * @param absolute frequency value
	 * @return corresponding weight
	 */
	private int getWeight(double value){
		if(Double.compare(value, 0.25)<=0){
			return occasional;
		}
		else if(Double.compare(value, 0.75)<0){
			return frequent;
		}
		else{
			return veryFrequent;
		}
	}
	
	/**
	 * converts the the output of FileUtilities.readInKSZFrequency into the ksz data structure required for Phenomizer
	 * convert all terms in a given hash map
	 * @param terms: maps a disease id to a list, each list element contains an String array
	 * 				array[0]: symptom id, array[1]: frequency term
	 * @return hash map with integer weights instead of string frequencies
	 */
	public HashMap<Integer,LinkedList<Integer[]>>convertAll(HashMap<Integer,LinkedList<String[]>>terms){

		HashMap<Integer,LinkedList<Integer[]>>result = new HashMap<Integer,LinkedList<Integer[]>>();
		for(int key : terms.keySet()){
			LinkedList<String []> values = terms.get(key);
			LinkedList<Integer[]>symptoms = new LinkedList<Integer[]>();
			for(String[] val : values){
				int weight = convertFrequency(val[1]);
				
				//code to detect new frequency annotations that could not be parsed ->should not happen
				if(weight==0){
//					System.out.println(val[1]);
//					System.out.println(weight);
					weight =frequent;
				}
				
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
	 * converts the the output of FileUtilities.readInKSZ into the ksz data structure required for Phenomizer
	 * the input contains no frequency information -> everything is weighted as frequent (factor 10)
	 * @param terms: maps a disease id to a list, each list element contains a symptomg id
	 * @return hash map with uniform integer weights
	 */
	public HashMap<Integer, LinkedList<Integer []>> addWeights (HashMap<Integer, LinkedList<Integer>> ksz){

		HashMap<Integer, LinkedList<Integer[]>> res = new HashMap<Integer, LinkedList<Integer []>>(ksz.size()*3);
		for(Integer k: ksz.keySet()){
			LinkedList<Integer []> list = new LinkedList<Integer[]>();
			res.put(k, list);
			for(int i: ksz.get(k)){
				Integer [] symp_and_weight = new Integer [2];
				symp_and_weight[0]=i;
				//everything gets weight 10 -> evaluates to weighting factor 1 during Phenomizer
				symp_and_weight[1]=NO_WEIGHT;
				list.add(symp_and_weight);
			}			
		}
		return res;
	}
}
