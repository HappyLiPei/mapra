package geneticnetwork.algorithm;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.math3.linear.ArrayRealVector;

import phenotogeno.algo.ScoredGene;
import phenotogeno.algo.ScoredGeneComparator;

public class NetworkScoreAlgorithm {
	
	private MatrixVectorBuilder mvb;
	private RandomWalkWithRestart rwwr;
	
	//pass onto mvb and rwwr
	public NetworkScoreAlgorithm(MatrixVectorBuilder mvb, RandomWalkWithRestart rwwr){
		this.mvb = mvb;
		this.rwwr=rwwr;
	}
	
	//TODO: implement and test
	//coordinate action of mvb and rwwr
	public LinkedList<ScoredGene> runNetowrkScoreAlgorithm(){
		
		rwwr.setVector(mvb.getRestartVector());
		rwwr.setMatrix(mvb.getStochasticMatrix());
		
		ArrayRealVector res = rwwr.doRandomWalkWithRestart();
		
		return generateResult(res);
	}
	
	//TODO: rounding of decimal places->10??! encoding as integer??
	private LinkedList<ScoredGene> generateResult(ArrayRealVector res){
		
		LinkedList<ScoredGene> geneList = new LinkedList<ScoredGene>();
		HashMap<String, Integer> idsToPos = mvb.getIdPositionMap();
		for(String gene:idsToPos.keySet()){
			int pos = idsToPos.get(gene);
			double score = (double) Math.round(res.getEntry(pos)*1E10)/1E10;
			ScoredGene sg = new ScoredGene(gene, score, "");
			geneList.add(sg);
		}
		Collections.sort(geneList, new ScoredGeneComparator(10));
		return geneList;
	}

}