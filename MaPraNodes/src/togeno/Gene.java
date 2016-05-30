package togeno;

/** object representing a gene*/
public class Gene {
	
	/** unqiue identifier of the gene e.g Ensembl id*/
	private String id;
	
	/**
	 * generates an object representing a gene
	 * @param id id (e.g Ensembl) of the gene
	 */
	public Gene(String id){
		this.id=id;
	}
	
	/**
	 * retrieves the id of the gene
	 * @return gene id
	 */
	public String getId(){
		return id;
	}

}
