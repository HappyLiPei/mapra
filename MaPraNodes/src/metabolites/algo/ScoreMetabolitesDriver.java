package metabolites.algo;

import java.util.HashMap;
import java.util.LinkedList;

import metabolites.types.MeasuredMetabolite;
import metabolites.types.ReferenceMetabolite;
import metabolites.types.ScoredMetabolite;

public class ScoreMetabolitesDriver {
	
	/** raw measurements of the case extracted from a file or a table*/
	private LinkedList<String[]> rawMeasurements;
	/** raw reference values of controls extracted from a file or a table*/
	private HashMap<String, LinkedList<String[]>> rawReferences;
	
	/** list of the measured metabolites of a case*/
	private LinkedList<MeasuredMetabolite> measurements;
	/** reference values from the controls*/
	private HashMap<String, ReferenceMetabolite> references;
	
	/**
	 * generates a driver for running a ScoreMetaboliteAlgo
	 * @param rawMeasurements
	 * 		measurements of the case extracted from a file or table as list of String arrays of length 3
	 * 		position 0: metabolite id, position 1: measured concentration or empty string (if missing),
	 * 		position 2: group of the case (identical for all measured metabolites)
	 * @param rawReferences
	 * 		mapping of metabolite ids to lists of String arrays of length 6
	 * 		position 0: metabolite id, position 1: type of reference (binary vs. concentration), position 2:
	 * 		group number, position 3: mean, position 4: standard deviation, position 5: missingness
	 */
	public ScoreMetabolitesDriver(LinkedList<String[]> rawMeasurements,
			HashMap<String, LinkedList<String[]>> rawReferences){
		
		this.rawMeasurements = rawMeasurements;
		this.rawReferences = rawReferences;
	}
	
	/**
	 * method that generates, executes and hands over the results of ScoreMetaboliteAlgo
	 * @return
	 * 		list of ScoredMetabolites containing a probability and score for each measured metabolite
	 */
	public LinkedList<ScoredMetabolite> runMetaboliteScoring(){
		prepareData();
		ScoreMetabolitesAlgo a = new ScoreMetabolitesAlgo(measurements, references);
		return a.scoreMetabolites();
	}
	
	/**
	 * transforms the input data such that it can be used by ScoreMetabolitesAlgo, includes transformation into metabolite
	 * objects and checking of the data for consistency
	 */
	private void prepareData(){
		DataTransformerMetabolites dtm = new DataTransformerMetabolites();
		references = dtm.getMapOfReferenceMetabolites(rawReferences);
		measurements = dtm.getListOfMeasuredMetabolites(rawMeasurements, references);
	}

}
