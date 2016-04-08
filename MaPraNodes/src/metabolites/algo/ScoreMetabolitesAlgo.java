package metabolites.algo;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import metabolites.types.MeasuredMetabolite;
import metabolites.types.ReferenceMetabolite;
import metabolites.types.ScoredMetabolite;

public class ScoreMetabolitesAlgo {
	
	/** list of the measured metabolites of a casse*/
	private LinkedList<MeasuredMetabolite> measurements;
	/** reference values from the controls*/
	private HashMap<String, ReferenceMetabolite> references;
	
	/**
	 * generates a ScoreMetabolitesAlgo object that is able to score the metabolites measured for a case
	 * @param measurements
	 * 		list of MeasuredMetabolites -> represents the metabolite concentrations measured for a case
	 * @param references
	 * 		map metabolite id -> ReferenceMetabolites for storing the reference values of all metabolites
	 */
	public ScoreMetabolitesAlgo(LinkedList<MeasuredMetabolite> measurements,
			HashMap<String, ReferenceMetabolite> references){
		
		this.measurements = measurements;
		this.references = references;	
	}
	
	/**
	 * runs the calculation of the metabolite scores
	 * @return
	 * 		list of ScoredMetabolites with a score and probability for each measured metabolite
	 */
	public LinkedList<ScoredMetabolite> scoreMetabolites(){
		
		//data structure for returning the metabolite scores
		LinkedList<ScoredMetabolite> scoringResult = new LinkedList<ScoredMetabolite>();
		
		//iterate over all measurements
		for(MeasuredMetabolite m: measurements){
			String mId = m.getId();
			//should not happen if the data is prepared correctly
			if(!references.containsKey(mId)){
				continue;
			}
			//get reference and score
			ReferenceMetabolite r = references.get(mId);
			ScoredMetabolite s = r.scoreMeasurement(m.getConcentration(), m.getGroup());
			//add scoring to result
			scoringResult.add(s);
		}
		
		Collections.sort(scoringResult, new ScoredMetaboliteComparator());		
		return scoringResult;
	}
	
	

}
