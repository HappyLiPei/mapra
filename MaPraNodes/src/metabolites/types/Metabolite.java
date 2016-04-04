package metabolites.types;

public abstract class Metabolite {
	
	/** id of the metabolite*/
	private String id;
	
	/**
	 * generates a Metabolite object with a given id
	 * @param id
	 * 		id of the metabolite
	 */
	public Metabolite(String id){
		this.id = id;
	}
	
	/**
	 * retrieves the id of this metabolite
	 * @return id of the metabolite
	 */
	public String getId(){
		return id;
	}

}
