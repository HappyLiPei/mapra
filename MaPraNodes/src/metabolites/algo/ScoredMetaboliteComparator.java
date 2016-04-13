package metabolites.algo;

import java.util.Comparator;

import metabolites.types.ScoredMetabolite;

public class ScoredMetaboliteComparator implements Comparator<ScoredMetabolite> {

	//TODO: implement ranking according to score and probability
	@Override
	/**
	 * sorts the ScoredMetabolites according to their id -> first probability, then  id
	 */
	public int compare(ScoredMetabolite arg0, ScoredMetabolite arg1) {
		String id0 = arg0.getId();
		String id1 = arg1.getId();
		return id0.compareTo(id1);
	}
	

}
