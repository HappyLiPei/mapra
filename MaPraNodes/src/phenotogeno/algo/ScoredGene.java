package phenotogeno.algo;

public class ScoredGene extends Gene {
	
	//final score of the gene
	private double score;
	//string representation of id list
	private String important_disease_ids;
	
	public ScoredGene(String id, double score, String ids) {
		super(id);
		this.score = score;
		this.important_disease_ids =ids;
	}
	
	public double getScore(){
		return this.score;
	}
	
	public String getImportantDiseases(){
		return this.important_disease_ids;
	}
}
