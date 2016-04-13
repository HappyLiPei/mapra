package metabolites.types;

import java.util.HashMap;

import org.apache.commons.math3.distribution.NormalDistribution;

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
	
	public double getMean(int group){
		if(groupToPosition.containsKey(group)){
			int position=groupToPosition.get(group);
			return mean[position];
		}
		else{
			return Double.NaN;
		}
	}
	
	public double getStandardDeviation(int group){
		if(groupToPosition.containsKey(group)){
			int position=groupToPosition.get(group);
			return standardDeviation[position];
		}
		else{
			return Double.NaN;
		}
	}
	
	@Override
	public ScoredMetabolite scoreMeasurement(double measurement, int group) {
		if(Double.isNaN(measurement)){
			double probability = getMissingness()/100d;
			probability = Math.round(probability*100000)/100000d;
			return new ScoredMetaboliteBinary(getId(), 0, probability);
		}
		else{
			//get mean and standard deviation for group if available
			double [] distribution = getMeanAndStdDevForGroup(group);
			//should not happen if data is prepared correctly!
			if(distribution == null){
				return null;
			}
			//calculate Z score
			double zScoreSigned = (double) (measurement-distribution[0])/distribution[1];
			double zScoreUnsigned = Math.abs(zScoreSigned);
			NormalDistribution nd = new NormalDistribution(0, 1);
			//calculate probability of observing the Z score
			double probability = 1-nd.cumulativeProbability(zScoreUnsigned);
			//round score and probability
			if(Double.isFinite(zScoreSigned) && !Double.isNaN(zScoreSigned)){
				zScoreSigned = Math.round(zScoreSigned*100)/100d;
			}
			if(!Double.isNaN(probability)){
				probability = Math.round(probability*100000)/100000d;
			}
			return new ScoredMetaboliteConcentration(getId(), zScoreSigned, probability);
		}
	}
	
}
