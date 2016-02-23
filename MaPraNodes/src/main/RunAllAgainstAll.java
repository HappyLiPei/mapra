package main;

import io.FileUtilities;
import phenomizeralgorithm.AlgoPheno;
import phenomizeralgorithm.FrequencyConverter;
import phenomizeralgorithm.PValueFolder;
import phenomizeralgorithm.PValueGenerator;

import java.util.HashMap;
import java.util.LinkedList;

public class RunAllAgainstAll {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String symptomsIn = args[0];
		String ontoIn = args[1];
		String kszIn = args[2];
		String pvalueFolder = args[3];
		String outputFolder = args[4];
		
		HashMap<Integer,LinkedList<Integer>>kszTmp = FileUtilities.readInKSZ(kszIn);
		HashMap<Integer,LinkedList<Integer[]>>ksz= FrequencyConverter.addWeights(kszTmp);
		LinkedList<Integer> symptoms = FileUtilities.readInSymptoms(symptomsIn);
		int[][]ontology = FileUtilities.readInOntology(ontoIn);

		
		String dataOut = outputFolder+"allAgainstAll_";
		
		String outputDist = dataOut + "dist.txt";
		String outputSim = dataOut+"sim.txt";
		String outputMax = dataOut+"max.txt";
		String outputMin = dataOut+"min.txt";
		String outputAvg = dataOut+"avg.txt";
		
		PValueFolder.setPvalFoder(pvalueFolder);
		
		LinkedList<Integer>query = new LinkedList<Integer>();
		query.add(1);
		AlgoPheno.setInput(query, symptoms, ksz, ontology);
		int[] keys = AlgoPheno.getKeys();
		
		//calculate similarity matrix
		int[][]simMatrixTmp = AlgoPheno.allAgainstAll();
		double[][]simMatrix = AlgoPheno.convertIntToDouble(simMatrixTmp);
		writeResult(simMatrix,keys,outputSim);

		//calculate distance matrix
		int maximum = getMaximum(simMatrixTmp);
		double[][]distMatrix = AlgoPheno.convertAdjacencyToDistance(simMatrixTmp, maximum);
		writeResult(distMatrix,keys,outputDist);
			
		//get asymmetric p value matrix
		double[][] asymmetric = new double[simMatrix.length][simMatrix.length];
		for(int i=0;i<simMatrix.length;i++){
			int disease = keys[i];
			int queryLength = ksz.get(disease).size();
			double []tmp = simMatrix[i];
			double[]line = PValueGenerator.getNextOfAsymmetricMatrix(tmp, keys, queryLength);
			asymmetric[i]=line;
		}
		
		//get symmetric matrix containing in cell (i,j) always the minimum of (i,j) and (j,i)
		double[][]minMatrix = PValueGenerator.getMinimumMatrix(asymmetric);
		writeResult(minMatrix,keys,outputMin);
		
		//get symmetric matrix containing in cell (i,j) always the maximum of (i,j) and (j,i)
		double[][]maxMatrix = PValueGenerator.getMaximumMatrix(asymmetric);
		writeResult(maxMatrix,keys,outputMax);
		
		//get symmetric matrix containing in cell (i,j) always the average of (i,j) and (j,i)
		double[][]avgMatrix = PValueGenerator.getAverageMatrix(asymmetric);
		writeResult(avgMatrix,keys,outputAvg);
	}
	
	private static void writeResult(double[][]array, int[]colNames, String path){
		StringBuilder sb = new StringBuilder();
		sb.append("id,");
		for(int i=0; i<colNames.length-1; i++){
			sb.append(colNames[i]+",");
		}
		sb.append(colNames[colNames.length-1]);
		sb.append("\n");
		FileUtilities.writeStringToExistingFile(path, sb.toString());
		
		for(int i=0;i<array.length; i++){
			sb = new StringBuilder();
			sb.append(colNames[i]+",");
			for(int j=0; j<array[i].length-1; j++){
				sb.append(array[i][j]+",");
			}
			sb.append(array[i][array[i].length-1]);
			sb.append("\n");
			FileUtilities.writeStringToExistingFile(path, sb.toString());
		}
	}
	
	private static int getMaximum(int[][]matrix){
		int maximum = -1;
		for(int i=0; i<matrix.length-1; i++){
			for(int j=(i+1); j<matrix[i].length;j++){
				if(matrix[i][j]>maximum){
					maximum=matrix[i][j];
				}
			}
		}
		return maximum;
	}

}
