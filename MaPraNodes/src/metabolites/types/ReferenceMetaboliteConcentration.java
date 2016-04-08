package metabolites.types;

import java.util.HashMap;

public class ReferenceMetaboliteConcentration extends ReferenceMetabolite {
	
	private HashMap<Integer, Integer> groupToPosition;
	private double [] mean;
	private double [] standardDeviation;
	
	/**
	 * generates concentration reference metabolite
	 * @param id
	 * 		metabolite id
	 * @param missingness
	 * 		missingness as fraction of missing values in % before the missing concentrations were imputed
	 * @param group
	 * 		array of all groups
	 * @param mean
	 * 		array of mean values for all groups, position in group corresponds to position in mean
	 * @param std
	 * 		array of standard deviation values for all groups, position in group corresponds to position in std
	 */
	public ReferenceMetaboliteConcentration(String id, double missingness, int[] group, double[] mean, double[] std) {
		super(id,missingness);
		
		groupToPosition = new HashMap<>(group.length*3);
		for(int i=0; i<group.length; i++){
			groupToPosition.put(group[i],i);
		}
		this.mean = mean;
		this.standardDeviation = std;
	}
	
	/**
	 * retrieves the mean and standard deviation of the metabolite
	 * @param group
	 * 		group id
	 * @return
	 * 		array containing mean (position 0) and standard deviation (position 1) or null if the group number is invalid
	 */
	public double[] getMeanAndStdDevForGroup(int group){
		if(groupToPosition.containsKey(group)){
			int position=groupToPosition.get(group);
			double[] stats = new double[2];
			stats[0] = mean[position];
			stats[1] = standardDeviation[position];
			return stats;
		}
		else{
			return null;
		}
	}
	
	//TODO: implement scoring for concentration
	@Override
	public ScoredMetabolite scoreMeasurement(double measurement) {
		// TODO Auto-generated method stub
		return new ScoredMetaboliteConcentration(getId(), 0.0, 0.0);
	}
	
}
