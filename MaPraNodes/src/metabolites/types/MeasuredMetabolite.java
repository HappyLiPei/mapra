package metabolites.types;

public class MeasuredMetabolite extends Metabolite {
	
	/** concentration measured for the metabolite, can be any number or naN*/
	private double concentration;
	/** group of the person for which the metabolite was measured*/
	private int group;
	
	/**
	 * generates a MeasuredMetabolite
	 * @param id
	 * 		id of the metabolite
	 * @param concentration
	 * 		measured concentration (naN if missing)
	 */
	public MeasuredMetabolite(String id, double concentration, int group) {
		super(id);
		this.concentration = concentration;
		this.group = group;
	}
	
	/**
	 * retrieves the concentration measured for the metabolie
	 * @return
	 * 		measured concentration or naN (if missing)
	 */
	public double getConcentration(){
		return concentration;
	}
	
	/**
	 * retrieves the group belonging to this metabolite measurement
	 * @return
	 * 		group of the person whose metabolites were measured
	 */
	public int getGroup(){
		return group;
	}

}
